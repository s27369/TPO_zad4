package Logic;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TopicManager {
    Map<Topic, List<Client>> subscriptions;

    public TopicManager(Map<Topic, List<Client>> subscriptions) {
        this.subscriptions = subscriptions;
    }
    public TopicManager() {
        this.subscriptions = new HashMap<>();
    }

    public Map<Topic, List<Client>> getSubscriptions() {
        return subscriptions;
    }
    public List<Client> getSubscribers(Topic t) {
        return subscriptions.get(t);
    }
    public List<Client> getSubscribers(String s) {
        for (Map.Entry<Topic, List<Client>> p : subscriptions.entrySet()) {
            if (p.getKey().getName().equals(s)) return subscriptions.get(p.getValue());
        }
        return new ArrayList<>();
    }
}
