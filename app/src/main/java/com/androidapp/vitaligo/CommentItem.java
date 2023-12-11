package com.androidapp.vitaligo;

public class CommentItem {
    private String authorID;
    private String comment;
    public CommentItem(){

    }

    public CommentItem(String authoriD, String comment) {
        this.authorID = authoriD;
        this.comment = comment;
    }

    public String getAuthorID() {
        return authorID;
    }

    public String getComment() {
        return comment;
    }
}