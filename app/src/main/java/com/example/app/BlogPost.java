package com.example.app;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;
// comments added

public class BlogPost extends BlogPostId {

    // these are the fiels from the database which we save upon storage
    public String user_id, image_url, desc, image_thumb, title, location, text;
    public Date timestamp;
    // only missing data here are the tags

    // we implement an empty constructor
    public BlogPost() {}

    // with this constructor you specify each attribute of this class that we are considering
    public BlogPost(String user_id, String image_url, String title, String desc, String image_thumb, Date timestamp, String location, String text) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
        this.image_thumb = image_thumb;
        this.timestamp = timestamp;
        this.location = location;
        this.title = title;
        this.text = text;
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

    public String get_title() {return title;}

    public void set_title(String title) {this.title = title;}

    public String get_location() {return location;}

    public void set_location(String location) {this.location = location;}

    public String get_text() {return text;}

    public void set_text(String text) {this.text = text;}
}
