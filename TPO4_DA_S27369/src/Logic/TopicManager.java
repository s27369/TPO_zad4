package Logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.channels.SocketChannel;

public class TopicManager {
    Map<Topic, List<SocketChannel>> subscriptions;

    public TopicManager(Map<Topic, List<SocketChannel>> subscriptions) {
        this.subscriptions = subscriptions;
    }
    public TopicManager() {
        this.subscriptions = new HashMap<>();
    }

    public Map<Topic, List<SocketChannel>> getSubscriptions() {
        return subscriptions;
    }
    public List<SocketChannel> getSubscribers(Topic t) {
        return subscriptions.get(t);
    }
    public List<SocketChannel> getSubscribers(String s) {
        for (Map.Entry<Topic, List<SocketChannel>> p : subscriptions.entrySet()) {
            if (p.getKey().getName().equals(s)) return subscriptions.get(p.getValue());
        }
        return new ArrayList<>();
    }
    public List<String> getTopics(){
        ArrayList<String> result = new ArrayList();
        for (Topic t: subscriptions.keySet()){
            result.add(t.getName());
        }
        return result;
    }
    private boolean containsIgnoreCase(String s){
        for(Topic t: subscriptions.keySet()){
            if (t.getName().equalsIgnoreCase(s)){
                return true;
            }
        }
        return false;
    }
    public boolean addTopic(String name){
        if (containsIgnoreCase(name)){
            System.out.println("Topic '"+name+"' already exists");
            return false;
        }
        subscriptions.put(new Topic(name), new ArrayList<>());
        return true;
    }
    public boolean removeTopic(String s){
        Topic temp=null;
        for (Topic t: subscriptions.keySet()){
            if(t.getName().equalsIgnoreCase(s)) temp=t;
        }
        if (temp==null) return false;
        subscriptions.remove(temp);
        return true;
    }
    public String getTopicsAsString(){
        StringBuilder topicList = new StringBuilder();
        for (String topic : getTopics()) {
            topicList.append(topic).append(",");
        }
        return topicList.toString().substring(0, topicList.length()-1);
    }
    public void addSubscriber(Topic topic, SocketChannel clientChannel) {
        subscriptions.computeIfAbsent(topic, k -> new ArrayList<>()).add(clientChannel);
    }
    public void addSubscriber(String topic, SocketChannel clientChannel) {
        subscriptions.computeIfAbsent(getTopicByName(topic), k -> new ArrayList<>()).add(clientChannel);
    }
    public void removeSubscriber(Topic topic, SocketChannel clientChannel) {
        List<SocketChannel> subscribers = subscriptions.get(topic);
        if (subscribers != null) {
            subscribers.remove(clientChannel);
        }
    }
    public void removeSubscriber(String topic, SocketChannel clientChannel) {
        removeSubscriber(getTopicByName(topic), clientChannel);
    }
    public Topic getTopicByName(String name) {
        for (Topic t : subscriptions.keySet()) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }
}


