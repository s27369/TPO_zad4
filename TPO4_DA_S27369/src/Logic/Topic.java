package Logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Topic {
    String name;
    List<News> news;

    public Topic(String name) {
        this.name = name;
        this.news = new ArrayList<>();
    }
    public void addNews(String s){
        this.news.add(new News(s));
    }
    public void addNews(String s, LocalDateTime time){
        this.news.add(new News(time, s));
    }

    public String getName() {
        return name;
    }

    public List<News> getNews() {
        return news;
    }
    public News getLatestNews(){
        return news.get(news.size()-1);
    }
}
