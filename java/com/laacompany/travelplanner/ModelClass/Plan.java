package com.laacompany.travelplanner.ModelClass;

public class Plan {

    private String destinationId, name, address, previewUrl;
    private int arrivedTime, duration;

    public Plan(String destinationId, String name, String address, String previewUrl, int arrivedTime, int duration) {
        this.destinationId = destinationId;
        this.name = name;
        this.address = address;
        this.previewUrl = previewUrl;
        this.arrivedTime = arrivedTime;
        this.duration = duration;
    }

    public Plan(Plan plan){
        this.destinationId = plan.getDestinationId();
        this.name = plan.getName();
        this.address = plan.getAddress();
        this.previewUrl = plan.getPreviewUrl();
        this.arrivedTime = plan.getArrivedTime();
        this.duration = plan.getDuration();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public int getArrivedTime() {
        return arrivedTime;
    }

    public void setArrivedTime(int arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
