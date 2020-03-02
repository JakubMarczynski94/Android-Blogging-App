package com.example.app;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
// comments added

public class BlogPost extends BlogPostId {

    // these are the fiels from the database which we save upon storage
    public String user_id, image_url, desc, image_thumb;
    public Date timestamp;

    // we implement an empty constructor
    public BlogPost() {}

    // with this constructor you specify each attribute of this class that we are considering
    public BlogPost(String user_id, String image_url, String desc, String image_thumb, Date timestamp) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    // we set the user_id parameter of this instance of this class
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage_thumb() {
        return image_thumb;
    }

    public void setImage_thumb(String image_thumb) {
        this.image_thumb = image_thumb;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
