package Logic;

import java.time.LocalDateTime;

public class News {
    LocalDateTime time;
    String text;

    public News(LocalDateTime time, String text) {
        this.time = time;
        this.text = text;
    }

    public News(String text) {
        this.time = LocalDateTime.now();
        this.text = text;
    }

    @Override
    public String toString() {
        return this.time+": "+this.text;
    }
}
