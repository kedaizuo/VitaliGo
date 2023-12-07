package com.androidapp.vitaligo;

public class HistoryData {
    private String title;
    private String content;

    // 必须有一个无参构造函数
    public HistoryData(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // getters 和 setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
