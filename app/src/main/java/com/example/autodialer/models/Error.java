package com.example.autodialer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Error {
    @SerializedName("user_id")
    @Expose
    private List<String> userId = null;
    @SerializedName("admin_id")
    @Expose
    private List<String> adminId = null;
    @SerializedName("feedback")
    @Expose
    private List<String> feedback = null;
    @SerializedName("w_feedback")
    @Expose
    private List<String> wFeedback = null;
    @SerializedName("recording")
    @Expose
    private List<String> recording = null;
    @SerializedName("scheduled")
    @Expose
    private List<String> scheduled = null;
    @SerializedName("date_time")
    @Expose
    private List<String> dateTime = null;
    @SerializedName("call_duration")
    @Expose
    private List<String> callDuration = null;
    @SerializedName("client_pno")
    @Expose
    private List<String> clientPno = null;
    @SerializedName("client_name")
    @Expose
    private List<String> clientName = null;
    @SerializedName("client_city")
    @Expose
    private List<String> clientCity = null;

    public List<String> getUserId() {
        return userId;
    }

    public void setUserId(List<String> userId) {
        this.userId = userId;
    }

    public List<String> getAdminId() {
        return adminId;
    }

    public void setAdminId(List<String> adminId) {
        this.adminId = adminId;
    }

    public List<String> getFeedback() {
        return feedback;
    }

    public void setFeedback(List<String> feedback) {
        this.feedback = feedback;
    }

    public List<String> getwFeedback() {
        return wFeedback;
    }

    public void setwFeedback(List<String> wFeedback) {
        this.wFeedback = wFeedback;
    }

    public List<String> getRecording() {
        return recording;
    }

    public void setRecording(List<String> recording) {
        this.recording = recording;
    }

    public List<String> getScheduled() {
        return scheduled;
    }

    public void setScheduled(List<String> scheduled) {
        this.scheduled = scheduled;
    }

    public List<String> getDateTime() {
        return dateTime;
    }

    public void setDateTime(List<String> dateTime) {
        this.dateTime = dateTime;
    }

    public List<String> getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(List<String> callDuration) {
        this.callDuration = callDuration;
    }

    public List<String> getClientPno() {
        return clientPno;
    }

    public void setClientPno(List<String> clientPno) {
        this.clientPno = clientPno;
    }

    public List<String> getClientName() {
        return clientName;
    }

    public void setClientName(List<String> clientName) {
        this.clientName = clientName;
    }

    public List<String> getClientCity() {
        return clientCity;
    }

    public void setClientCity(List<String> clientCity) {
        this.clientCity = clientCity;
    }
}
