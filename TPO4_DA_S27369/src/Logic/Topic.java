package Logic;

import java.time.LocalDateTime;
import java.util.List;

public class Topic {
    String name;
    List<News> news;

    public Topic(String name) {
        this.name = name;
    }
    public void addNews(String s){
        this.news.add(new News(s));
    }
    public void addNews(String s, LocalDateTime time){
        this.news.add(new News(time, s));
    }


}
