package com.emperor95.era.pojo;

public class EmDetails {
    private String description;
    private int stepNum;

    public EmDetails() {
    }

    public EmDetails(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStepNum() {
        return stepNum;
    }

    public void setStepNum(int stepNum) {
        this.stepNum = stepNum;
    }
}
