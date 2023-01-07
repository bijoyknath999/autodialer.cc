package com.example.autodialer.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Completed {
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("user_id")
    @Expose
    private String userId;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
