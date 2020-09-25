package com.laacompany.travelplanner.ModelClass;

import java.util.ArrayList;
import java.util.Date;

public class PlanMaster {

    private String planMasterId,eventTitle;
    private Date eventDate;
    private int timeStart;
    private Plan origin;
    private ArrayList<Plan> plans;


    public PlanMaster(String planMasterId, String eventTitle, Date eventDate, int timeStart, Plan origin) {
        this.planMasterId = planMasterId;
        this.eventTitle = eventTitle;
        this.eventDate = eventDate;
        this.timeStart = timeStart;
        this.origin = origin;
        this.plans = new ArrayList<>();
    }

    public PlanMaster(){
        plans = new ArrayList<>();
    }

    public void setPlanMaster(PlanMaster planMaster) {
        this.planMasterId = planMaster.getPlanMasterId();
        this.eventTitle = planMaster.getEventTitle();
        this.eventDate = planMaster.getEventDate();
        this.timeStart = planMaster.getTimeStart();
        this.origin = planMaster.getOrigin();
        this.plans = planMaster.getPlans();
    }

    public String getPlanMasterId() {
        return planMasterId;
    }

    public void setPlanMasterId(String planMasterId) {
        this.planMasterId = planMasterId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public int getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
    }

    public Plan getOrigin() {
        return origin;
    }

    public void setOrigin(Plan origin) {
        this.origin = origin;
    }

    public ArrayList<Plan> getPlans() {
        return plans;
    }

    public void setPlans(ArrayList<Plan> plans) {
        this.plans = plans;
    }
}
