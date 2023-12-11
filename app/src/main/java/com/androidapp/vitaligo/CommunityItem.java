package com.androidapp.vitaligo;

import java.util.List;

public class CommunityItem {
    private String topic;
    private String publisher;
    private String content;
    private String publisherID;
    private String postID;

    private List<CommentItem> comments;

    public CommunityItem() {
    }

    public CommunityItem(String publisher, String topic, String content, String publisherID, String postID) {
        this.publisher = publisher;
        this.topic = topic;
        this.content = content;
        this.publisherID = publisherID;
        this.postID = postID;
    }

    public String getTopic() {
        return topic;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getContent() {
        return content;
    }
    public String getPublisherID(){
        return publisherID;
    }

    public List<CommentItem> getComments() {
        return comments;
    }

    public void setComments(List<CommentItem> comments) {
        this.comments = comments;
    }

    public void setPostID(String postID){
        this.postID = postID;
    }
    public String getPostID(){
        return postID;
    }
}
