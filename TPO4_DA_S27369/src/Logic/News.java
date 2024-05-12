package Logic;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static News getNewsFromString(String s){
        String regex = "\\[(.*?)\\]:\\s+(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);

        if (matcher.find()) {
            String timeString = matcher.group(1);
            String text = matcher.group(2);
            LocalDateTime time = LocalDateTime.parse(timeString);
            return new News(time, text);
        }
        return null;
    }

    @Override
    public String toString() {
        return "["+this.time+"]: "+this.text;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getText() {
        return text;
    }
}
