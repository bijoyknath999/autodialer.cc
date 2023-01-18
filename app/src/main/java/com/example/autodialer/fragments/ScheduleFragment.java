package com.example.autodialer.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autodialer.R;
import com.example.autodialer.adapters.ScheduleAdapters;
import com.example.autodialer.api.ApiInterface;
import com.example.autodialer.api.Constants;
import com.example.autodialer.models.Datum2;
import com.example.autodialer.models.Recording;
import com.example.autodialer.models.Schedule;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ScheduleFragment extends Fragment {
    String id, name,city, country, phone;
    private View view;
    public static SwipeRefreshLayout swipeRefreshLayout;
    public static ProgressDialog progress;
    public static RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    public static SpinKitView spinKitView;
    public static boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount, page =1;
    public static List<Datum2> datum2List;
    public static ScheduleAdapters scheduleAdapters;
    private Context context;
    public static TextView ScheduleError;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_schedule, container, false);

        recyclerView = view.findViewById(R.id.schedule_recylerview);
        swipeRefreshLayout = view.findViewById(R.id.schedule_swipe);
        spinKitView = view.findViewById(R.id.schedule_spin);
        ScheduleError = view.findViewById(R.id.schedule_error);

        datum2List = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        scheduleAdapters = new ScheduleAdapters(datum2List,context);
        recyclerView.setAdapter(scheduleAdapters);

        SharedPreferences pref = context.getSharedPreferences("AutoDialer", Activity.MODE_PRIVATE);
        Constants.adminID = pref.getString("admin_id","");

        getData(1,context);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0) //check for scroll down
                {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisiblesItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            spinKitView.setVisibility(View.VISIBLE);
                            loading = false;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    page = page+1;
                                    getData(page,context);
                                }
                            },2000);
                        }
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progress.show();
                LoadDataAfterDelete(context);
            }
        });


        progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        return view;
    }

    public static void getData(int page, Context context)
    {

        ApiInterface.getApiRequestInterface().getScheduledList(Constants.adminID,page)
                .enqueue(new Callback<Schedule>() {
                    @Override
                    public void onResponse(Call<Schedule> call, retrofit2.Response<Schedule> response) {
                        if (response.isSuccessful())
                        {
                            if (progress!=null)
                                progress.dismiss();

                            spinKitView.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            loading = true;

                            Schedule schedule = response.body();
                            List<Datum2> datum2List1 = schedule.getData();
                            for (Datum2 datum2 : datum2List1)
                            {
                                SharedPreferences pref = context.getApplicationContext().getSharedPreferences("AutoDialer", Activity.MODE_PRIVATE);
                                String userId = pref.getString("id","");
                                if (userId.equals(datum2.getUserId()))
                                    datum2List.add(datum2);
                            }

                            if (datum2List.size()>0) {
                                ScheduleError.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            else {
                                ScheduleError.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }

                            scheduleAdapters.notifyDataSetChanged();                        }
                        if (progress!=null)
                            progress.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Schedule> call, Throwable t) {
                        ScheduleError.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        if (progress!=null)
                            progress.dismiss();
                        Toast.makeText(context, "Error : "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void LoadDataAfterDelete(Context context)
    {
        if (datum2List.size()>0)
            datum2List.clear();
        getData(1,context);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (progress != null) { progress.dismiss(); progress = null; }
    }
    @Override
    public void onResume() {
        super.onResume();
        long cal2 = Calendar.getInstance().getTimeInMillis();
        long diff = cal2 - Constants.cal1;
        long diffSeconds = (diff / 1000)-3;
        long diffMinutes = diff / (60 * 1000);
        if(diffSeconds>=60){
            Constants.Call_duration=diffMinutes+":00 Min";
            System.out.println("Call Duration in Minutes: "+Constants.Call_duration);
        }
        else if(diffSeconds<60 && diffSeconds>0) {
            Constants.Call_duration="00:"+diffSeconds+" Sec";
            System.out.println("Call Duration in Sec: " + Constants.Call_duration);
        }

    }
}