package com.laacompany.travelplanner.ModelClass;

public class Destination {

    private String destinationId, name, address, country, previewUrl, flagUrl;
    private double rating, latitude, longitude;
    private int totalVisitor, visitorEachDay, bestTimeStart, bestTimeEnd, openTime, closeTime;

    public Destination(String destinationId){
        this.destinationId = destinationId;
        name = address = country = previewUrl = flagUrl = "";
        rating = latitude = longitude = 0;
        totalVisitor = visitorEachDay = bestTimeStart = bestTimeEnd = openTime = closeTime = 0;
    }

    public Destination(String destinationId, String name, String address, String country, String previewUrl, String flagUrl, double rating, double latitude, double longitude, int totalVisitor, int visitorEachDay, int bestTimeStart, int bestTimeEnd, int openTime, int closeTime) {
        this.destinationId = destinationId;
        this.name = name;
        this.address = address;
        this.country = country;
        this.previewUrl = previewUrl;
        this.flagUrl = flagUrl;
        this.rating = rating;
        this.latitude = latitude;
        this.longitude = longitude;
        this.totalVisitor = totalVisitor;
        this.visitorEachDay = visitorEachDay;
        this.bestTimeStart = bestTimeStart;
        this.bestTimeEnd = bestTimeEnd;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getTotalVisitor() {
        return totalVisitor;
    }

    public void setTotalVisitor(int totalVisitor) {
        this.totalVisitor = totalVisitor;
    }

    public int getBestTimeStart() {
        return bestTimeStart;
    }

    public void setBestTimeStart(int bestTimeStart) {
        this.bestTimeStart = bestTimeStart;
    }

    public int getBestTimeEnd() {
        return bestTimeEnd;
    }

    public void setBestTimeEnd(int bestTimeEnd) {
        this.bestTimeEnd = bestTimeEnd;
    }

    public int getVisitorEachDay() {
        return visitorEachDay;
    }

    public void setVisitorEachDay(int visitorEachDay) {
        this.visitorEachDay = visitorEachDay;
    }

    public int getOpenTime() {
        return openTime;
    }

    public void setOpenTime(int openTime) {
        this.openTime = openTime;
    }

    public int getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(int closeTime) {
        this.closeTime = closeTime;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public String getFlagUrl() {
        return flagUrl;
    }

    public void setFlagUrl(String flagUrl) {
        this.flagUrl = flagUrl;
    }
}
