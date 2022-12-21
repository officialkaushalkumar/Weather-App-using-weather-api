package com.example.weatherapp;

public class WeatherRVModel {

    private String time;
    private String temprature;
    private String icon;
    private String windspeed;

    public WeatherRVModel(String time, String temprature, String icon, String windspeed) {
        this.time = time;
        this.temprature = temprature;
        this.icon = icon;
        this.windspeed = windspeed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemprature() {
        return temprature;
    }

    public void setTemprature(String temprature) {
        this.temprature = temprature;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWindspeed() {
        return windspeed;
    }

    public void setWindspeed(String windspeed) {
        this.windspeed = windspeed;
    }
}
