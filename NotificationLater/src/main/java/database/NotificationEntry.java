package database;

/**
 * Created by kimcy on 18/08/2015.
 */
public class NotificationEntry extends AppEntry{
    private int id;
    private String ntContentTitle;
    private String ntContentText;
    private String ntTimePost;
    private int isRead = 0;
    private int color = -1;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getNtContentTitle() {
        return ntContentTitle;
    }

    public void setNtContentTitle(String ntContentTitle) {
        this.ntContentTitle = ntContentTitle;
    }

    public String getNtContentText() {
        return ntContentText;
    }

    public void setNtContentText(String ntContentText) {
        this.ntContentText = ntContentText;
    }

    public String getNtTimePost() {
        return ntTimePost;
    }

    public void setNtTimePost(String ntTimePost) {
        this.ntTimePost = ntTimePost;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
