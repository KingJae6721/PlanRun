package com.yj.planrun;

import java.util.HashMap;
import java.util.Map;

public class RunningData {

    private  String date;
    private  String date_time;
    private  String time;
    private  String distance;
    private  String calories;
    private  String pace;

    public RunningData(){}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate_time() {
        return date_time;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getPace() {
        return pace;
    }

    public void setPace(String pace) {
        this.pace = pace;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("date", date);
        result.put("date_time", date_time);
        result.put("time", time);
        result.put("distance", distance);
        result.put("calories", calories);
        result.put("pace", pace);
        return result;
    }

}
