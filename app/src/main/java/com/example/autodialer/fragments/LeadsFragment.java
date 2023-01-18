package com.example.autodialer.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
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
import com.example.autodialer.adapters.LeadsAdapters;
import com.example.autodialer.api.ApiInterface;
import com.example.autodialer.api.Constants;
import com.example.autodialer.models.Datum;
import com.example.autodialer.models.Error;
import com.example.autodialer.models.Leads;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class LeadsFragment extends Fragment  {
    public static RecyclerView recyclerView;
    public static LeadsAdapters leadsAdapters;
    public static ProgressDialog progress ;
    public static List<Datum> datumList;
    public static SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager linearLayoutManager;
    private NestedScrollView nestedScrollView;
    public static SpinKitView spinKitView;
    public static boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount, page =1;
    private Context context;
    public static TextView ErrorText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leads, container, false);
        swipeRefreshLayout = view.findViewById(R.id.taskRefresh);
        spinKitView = view.findViewById(R.id.leads_spin);
        ErrorText = view.findViewById(R.id.leads_error);

        progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        datumList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.leads_recylerview);
        linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        leadsAdapters = new LeadsAdapters(context, datumList);
        recyclerView.setAdapter(leadsAdapters);

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
                                    loadData(page,context);
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
                if (progress!=null)
                    progress.show();
                LoadDataAfterDelete(context);
            }
        });

        loadData(1, context);
        return view;
    }

    public static void loadData(int page, Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("AutoDialer", Activity.MODE_PRIVATE);
        Constants.id = pref.getString("id","");
        ApiInterface.getApiRequestInterface().getLeads(Integer.parseInt(Constants.id), page)
                .enqueue(new Callback<Leads>() {
                    @Override
                    public void onResponse(Call<Leads> call, retrofit2.Response<Leads> response) {
                        if (response.isSuccessful())
                        {
                            if (progress!=null)
                                progress.dismiss();
                            spinKitView.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            loading = true;
                            Leads leads = response.body();
                            List<Datum> datumLists = leads.getData();
                            for (Datum datum : datumLists)
                            {
                                datumList.add(datum);
                            }

                            if (datumList.size()>0) {
                                ErrorText.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            else {
                                ErrorText.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                            leadsAdapters.notifyDataSetChanged();
                        }
                        if (progress!=null)
                            progress.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Leads> call, Throwable t) {
                        if (progress!=null)
                            progress.dismiss();
                        ErrorText.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        Toast.makeText(context, "Error : "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static void LoadDataAfterDelete(Context context)
    {
        if (datumList.size()>0)
            datumList.clear();

        loadData(1,context);
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