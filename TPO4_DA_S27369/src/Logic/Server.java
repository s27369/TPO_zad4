package Logic;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//via Admin:
//update topics
//add news to a topic
public class Server {
    ServerSocketChannel serverChannel;
    Selector selector;
    String host = "localhost";
    int port;
    private TopicManager topicManager;
    private List<SocketChannel> clients = new ArrayList<>();

    public Server(int port) throws IOException {
        this.port = port;
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(this.host, this.port));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        this.topicManager = new TopicManager();
        topicManager.addTopic("Sport");
        topicManager.addTopic("Science");
        topicManager.addTopic("Film");
        System.out.println("Server started at port: "+this.port);

    }

    public void start() throws IOException{
        while (true){
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();

            while(it.hasNext()){
                SelectionKey key = it.next();
                it.remove();

                if(key.isAcceptable()){
                    acceptConnection(key);
                }
                else if (key.isReadable()){
                    readRequest(key);
                }else if(key.isWritable()){
                    writeToClient(key);
                }
            }
        }
    }

    public void acceptConnection(SelectionKey key){
        System.out.println("Accepting connection: "+key);

        SocketChannel clientChannel;
        try {
            clientChannel = serverChannel.accept();
            clientChannel.configureBlocking(false);
            clientChannel.register(this.selector, SelectionKey.OP_READ);
            clients.add(clientChannel);
            System.out.println("New client connected: " + clientChannel.getRemoteAddress());

        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private void broadcastTopicChange(){
        String topics = topicManager.getTopicsAsString();
        topics = "topics:::"+topics;
        try {
            for (SocketChannel c : clients) {
                ByteBuffer responseBuffer = ByteBuffer.wrap(topics.getBytes());
                c.write(responseBuffer);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void readRequest(SelectionKey key){
        System.out.println("Reading request");
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketChannel clientChannel = (SocketChannel) key.channel();
        try {
            int bytesRead = clientChannel.read(buffer);
            if (bytesRead == -1) {
                System.out.println("Client disconnected: " + clientChannel.getRemoteAddress());
                clientChannel.close();
                key.cancel();
                return;
            }

            buffer.flip();
            byte[] data = new byte[bytesRead];
            buffer.get(data);

            String message = new String(data).trim();
            System.out.println("Received message from client " + clientChannel.getRemoteAddress() + ": " + message);

            handleRequest(message, clientChannel);

            buffer.clear();
        }catch (IOException e){
//            e.printStackTrace();
            System.out.println("Connection terminated");
            key.cancel();
        }
    }

    public void writeToClient(SelectionKey key){
        System.out.println("Writing to client");
    }

    public void handleRequest(String request, SocketChannel clientChannel) throws IOException{
        String[] parts = request.split(":::");
        if (parts.length == 2){
            String name = parts[0], command= parts[1];
            if(command.equalsIgnoreCase("gettopics")) {
                System.out.println("Sending topic list");
                String topics = topicManager.getTopicsAsString();
                topics = "topics:::"+topics;
                ByteBuffer responseBuffer = ByteBuffer.wrap(topics.getBytes());
                clientChannel.write(responseBuffer);
            }
        }
        else if (parts.length == 3){
            String name = parts[0], command= parts[1], content= parts[2];
            if(name.equals("admin")) {
                if(command.equalsIgnoreCase("addtopic")){
                    System.out.println("Adding topic: "+ content);
                    topicManager.addTopic(content);
//                    String topics = topicManager.getTopicsAsString();
//                    topics = "topics:::"+topics;
//                    ByteBuffer responseBuffer = ByteBuffer.wrap(topics.getBytes());
//                    clientChannel.write(responseBuffer);
                    broadcastTopicChange();
                }

                else if (command.equalsIgnoreCase("removetopic")){
                    System.out.println("Removing topic: "+ content);
                    ArrayList<SocketChannel> subscribers = (ArrayList<SocketChannel>)topicManager.subscriptions.get(topicManager.getTopicByName(content));
                    //remove topic
                    topicManager.removeTopic(content);
                    //notify subscribers
                    for (SocketChannel subscriber : subscribers) {
                        String notification = "Topic " + content + " has been removed.";
                        ByteBuffer notificationBuffer = ByteBuffer.wrap(notification.getBytes());
                        subscriber.write(notificationBuffer);
                    }
                    broadcastTopicChange();
                }
            }
            else if (name.equals("client")) {
                if(command.equalsIgnoreCase("subscribe")){
                    topicManager.addSubscriber(content, clientChannel);
                    System.out.println("Client " + clientChannel.getRemoteAddress() + " subscribed to topic: " + content);
                }

                else if(command.equalsIgnoreCase("unsubscribe")) {
                    topicManager.removeSubscriber(content, clientChannel);
                    System.out.println("Client " + clientChannel.getRemoteAddress() + " unsubscribed from topic: " + content);
                }

                else if (command.equalsIgnoreCase("getlatestnews")){
                    System.out.println("Sending latest news from topic: "+content);
                    String latestNews = topicManager.getTopicByName(content).getLatestNews().toString();
                    ByteBuffer responseBuffer = ByteBuffer.wrap(latestNews.getBytes());
                    clientChannel.write(responseBuffer);
                }
            }

        }
        else if (parts.length==4){
            String name = parts[0], command= parts[1], topic= parts[2], news = parts[3];
            if (name.equalsIgnoreCase("admin")){
                if(command.equalsIgnoreCase("postnews")){
                    System.out.println("Posting news on topic '"+topic+"': "+news);
                    for (SocketChannel subscriber : topicManager.subscriptions.get(topicManager.getTopicByName(topic))) {
                        String msg = "news:::"+topic+":::"+news;
                        ByteBuffer notificationBuffer = ByteBuffer.wrap(msg.getBytes());
                        subscriber.write(notificationBuffer);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try{
            Server server = new Server(50000);
            server.start();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
