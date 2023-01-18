package com.example.autodialer.api;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

public class Constants {
    public static String id="";
    public static String name="";
    public static String email="";
    public static String phoneNo="";
    public static String address="";
    public static String adminID="";
    public static int pending=0;
    public static String done="0";
    public static boolean refresh=false;
    public static ArrayList<String> Schedule_notification = new ArrayList<>();
    public static SwipeRefreshLayout Swipe;
    public static String Call_duration="";
    public static long cal1;
    public static Context home_context;
    public static Activity activity;
    public static View leadsview;
    //public static ArrayList<Tasks> NotificationList = new ArrayList<>();

    public static String BASE_URL = "https://autodialer.cc/api/";
}
