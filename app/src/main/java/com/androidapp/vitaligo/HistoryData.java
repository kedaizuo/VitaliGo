package com.androidapp.vitaligo;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class HistoryData {
    private String title;
    private String content;
    private List<LatLng> pathPointsList;
    private String dataBaseId;
    private String speed;
    private String runningDuration;

    // 必须有一个无参构造函数
    public HistoryData(String title, String content, String runningDuration, String speed, List<LatLng> pathPointsList, String dataBaseId) {
        this.title = title;
        this.content = content;
        this.pathPointsList = pathPointsList;
        this.dataBaseId = dataBaseId;
        this.runningDuration = runningDuration;
        this.speed = speed;
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
    public String getDataBaseId() {
        return dataBaseId;
    }

    public void setDataBaseId(String dataBaseId) {
        this.dataBaseId = dataBaseId;
    }
    public String getRunningDuration() {
        return runningDuration;
    }
    public String getSpeed() {
        return speed;
    }
}
