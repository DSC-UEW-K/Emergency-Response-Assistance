package com.emperor95.era.pojo;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Reports {
    private String reporterName;
    private String uid;
    private String reporterImage;
    private String emergency;
    private double latitude;
    private double longitude;

    @ServerTimestamp
    private Date time;

    private FirebaseAuth auth;

    public Reports() {
    }

    public Reports(String emergency, double latitude, double longitude) {
        this.emergency = emergency;
        this.latitude = latitude;
        this.longitude = longitude;

        auth = FirebaseAuth.getInstance();
        reporterName = auth.getCurrentUser().getDisplayName();
        reporterImage = auth.getCurrentUser().getPhotoUrl().toString();
        uid = auth.getCurrentUser().getUid();
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
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

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReporterImage() {
        return reporterImage;
    }

    public void setReporterImage(String reporterImage) {
        this.reporterImage = reporterImage;
    }
}
