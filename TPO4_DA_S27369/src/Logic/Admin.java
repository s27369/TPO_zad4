package Logic;

import GUI.AdminGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class Admin {
    private SocketChannel socketChannel;
    AdminGUI gui;
    String prefix="admin";

    public Admin(String serverAddress, int serverPort) {
        try{
            this.socketChannel = SocketChannel.open();
            this.socketChannel.connect(new java.net.InetSocketAddress(serverAddress, serverPort));

            this.gui = new AdminGUI();
            this.configureActionListeners();
            receive();
        }catch (UnknownHostException err){
            System.out.println("Unknown host: "+serverAddress);
        }catch (Exception e){
            e.printStackTrace();
        }



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

    public void handle(String msg) {
        String[] parts = msg.split(":::");
        String command = parts[0];
        if (parts.length == 2) {
            if (command.equalsIgnoreCase("topics")) {
                String topicsString = parts[1];
                String[] topics = topicsString.split(",");
                //use topics
//                System.out.print("Topics: ");
//                for (String s : topics) System.out.print(s + "|");
                this.gui.updateTopics(topics);
            }
        }
    }

    public void postNews(String topic, String newsText) throws IOException {
        sendMessage(this.prefix+":::postnews:::" + topic+":::"+new News(newsText));
    }
    public void addTopic(String topic) throws IOException {
        sendMessage(this.prefix+":::addtopic:::"+topic);
    }
    public void removeTopic(String topic) throws IOException {
        sendMessage(this.prefix+":::removetopic:::" + topic+":::");
    }
    public void getTopics() throws IOException {
        sendMessage(this.prefix+":::gettopics");
    }



    private void sendMessage(String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        socketChannel.write(buffer);
    }

    private void configureActionListeners(){
        //submit
        this.gui.submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTopic = gui.getSelectedTopic();
                String text = gui.textField.getText();
                if (selectedTopic != null && !text.isEmpty()) {
                    try{
                        postNews(selectedTopic, text);
                    }catch (IOException err){
                        err.printStackTrace();
                    }
                    gui.textField.setText(""); // Clear text field after submitting
                }
            }
        });
        //addtopic
        this.gui.addTopicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newTopic = gui.textField.getText().trim();
                if (!newTopic.isEmpty()) {
                    //send addtopic msg to server
                    try{
                        addTopic(newTopic);
//                        wait(400);
//                        getTopics();
//                        receive();
                    }catch (IOException err){
                        err.printStackTrace();
                    }
//                    gui.updateTopics()
                    gui.textField.setText(""); // Clear text field after adding topic
                }
            }
        });
        //removetopic
        this.gui.removeTopicButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle remove topic button action
                String selectedTopic = gui.getSelectedTopic();
                if (selectedTopic != null) {
                    try{
                        removeTopic(selectedTopic);
                    }catch (IOException err){
                        err.printStackTrace();
                    }
                }
            }
        });

    }

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int serverPort = 50000;

        Admin admin = new Admin(serverAddress, serverPort);

    }
}
