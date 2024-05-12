package Logic;

import GUI.ClientGUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

//(un)subscribe to a topic
//update topics
public class Client {
    String prefix = "client";
    private SocketChannel socketChannel;
    private ClientGUI gui;
    ArrayList<String> subscriptions = new ArrayList<>();

    public Client(String serverAddress, int serverPort) throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.connect(new java.net.InetSocketAddress(serverAddress, serverPort));

        this.gui = new ClientGUI();
        this.configureActionListeners();
        receive();
    }
    private void configureActionListeners(){
        //removetopic
        for(JCheckBox cb: this.gui.checkBoxList){
            cb.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (cb.isSelected()) {
                        try{
                            subscribeToTopic(cb.getText());
                        }catch(IOException er){
                            er.printStackTrace();
                        }
                    } else {
                        try{
                            unsubscribeFromTopic(cb.getText());
                        }catch(IOException er){
                            er.printStackTrace();
                        }
                    }
                }
            });
        }


    }

    public void subscribeToTopic(String topic) throws IOException {
        sendMessage(this.prefix+":::subscribe:::" + topic);
        subscriptions.add(topic);
    }

    public void unsubscribeFromTopic(String topic) throws IOException {
        sendMessage(this.prefix+":::unsubscribe:::" + topic);
        subscriptions.remove(topic);
    }

    public void getLatestNews(String topic) throws IOException {
        sendMessage(this.prefix+":::getlatestnews:::" + topic);
    }
    public void getTopics() throws IOException {
        sendMessage(this.prefix+":::gettopics");
    }
    public void receive() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        getTopics();
        while (true) {
            buffer.clear();
            int bytesRead = socketChannel.read(buffer);
            if (bytesRead == -1) {
                break;
            }
            buffer.flip();
            byte[] data = new byte[bytesRead];
            buffer.get(data);
            String msg = new String(data);
            System.out.println("Received news: " + msg);
            handle(msg);
        }
    }


    public void handle(String msg){
        String[] parts = msg.split(":::");
        String command = parts[0];
        if(parts.length==2){
            if(command.equalsIgnoreCase("topics")){
                String topicsString = parts[1];
                String[] topics = topicsString.split(",");
                //use topics
                this.gui.updateCheckBoxes(topics, this.subscriptions);
                configureActionListeners();
            }
        }
        else if(parts.length==3){
            if(command.equalsIgnoreCase("news")){
                String topic= parts[1], newsString= parts[2];
                News news = News.getNewsFromString(newsString);
                if (news==null){
                    System.out.println("Error: can't create news instance out of "+newsString);
                }else{
                    this.gui.appendText(news+"\n");
                }
            }

        }
    }


    private void sendMessage(String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        socketChannel.write(buffer);
    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 50000;

        try {
            Client client = new Client(serverAddress, serverPort);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
