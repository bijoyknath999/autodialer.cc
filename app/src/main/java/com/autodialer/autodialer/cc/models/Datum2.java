package com.autodialer.autodialer.cc.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum2 {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("admin_id")
    @Expose
    private String adminId;
    @SerializedName("feedback")
    @Expose
    private String feedback;
    @SerializedName("w_feedback")
    @Expose
    private String wFeedback;
    @SerializedName("recording")
    @Expose
    private String recording;
    @SerializedName("scheduled")
    @Expose
    private String scheduled;
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("call_duration")
    @Expose
    private String callDuration;
    @SerializedName("client_pno")
    @Expose
    private String clientPno;
    @SerializedName("client_name")
    @Expose
    private String clientName;
    @SerializedName("client_city")
    @Expose
    private String clientCity;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("campaign_id")
    @Expose
    private String campaign_id;
    @SerializedName("lead_id")
    @Expose
    private String lead_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getwFeedback() {
        return wFeedback;
    }

    public void setwFeedback(String wFeedback) {
        this.wFeedback = wFeedback;
    }

    public String getRecording() {
        return recording;
    }

    public void setRecording(String recording) {
        this.recording = recording;
    }

    public String getScheduled() {
        return scheduled;
    }

    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getClientPno() {
        return clientPno;
    }

    public void setClientPno(String clientPno) {
        this.clientPno = clientPno;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientCity() {
        return clientCity;
    }

    public void setClientCity(String clientCity) {
        this.clientCity = clientCity;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCampaign_id() {
        return campaign_id;
    }

    public void setCampaign_id(String campaign_id) {
        this.campaign_id = campaign_id;
    }

    public String getLead_id() {
        return lead_id;
    }

    public void setLead_id(String lead_id) {
        this.lead_id = lead_id;
    }
}
