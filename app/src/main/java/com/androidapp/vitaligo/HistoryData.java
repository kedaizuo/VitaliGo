package com.androidapp.vitaligo;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class HistoryData {
    private String title;
    private String content;
    private List<LatLng> pathPointsList;

    // 必须有一个无参构造函数
    public HistoryData(String title, String content, List<LatLng> pathPointsList) {
        this.title = title;
        this.content = content;
        this.pathPointsList = pathPointsList;
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

    public List<LatLng> getPathPointsList() {
        return pathPointsList;
    }

    public void setPathPointsList(List<LatLng> pathPointsList) {
        this.pathPointsList = pathPointsList;
    }
}
