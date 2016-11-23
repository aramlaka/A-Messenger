package com.aramlaka.a_messenger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Message {

    public String uid;
    public String author;
    public String body;
    public String imageUrl;
    public Date date;
    public String key;

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String uid, String author, String body, Date date, String imageUrl) {
        this.uid = uid;
        this.author = author;
        this.body = body;
        this.date = date;
        this.imageUrl = imageUrl;
        this.key = null;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("body", body);
        result.put("imageUrl", imageUrl);

        return result;
    }

    public String getUid() {
        return uid;
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Date getDate() {
        return date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
