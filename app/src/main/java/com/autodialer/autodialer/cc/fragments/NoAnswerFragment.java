package com.autodialer.autodialer.cc.fragments;

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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.autodialer.autodialer.cc.R;
import com.autodialer.autodialer.cc.adapters.NoAnswerAdapters;
import com.autodialer.autodialer.cc.adapters.ScheduleAdapters;
import com.autodialer.autodialer.cc.api.ApiInterface;
import com.autodialer.autodialer.cc.api.Constants;
import com.autodialer.autodialer.cc.models.Datum2;
import com.autodialer.autodialer.cc.models.Recording;
import com.autodialer.autodialer.cc.models.Schedule;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoAnswerFragment extends Fragment {

    private View view;
    public static SwipeRefreshLayout swipeRefreshLayout;
    public static ProgressDialog progress;
    public static RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    public static SpinKitView spinKitView;
    public static boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount, page =1;
    public static List<Datum2> datum2List;
    public static NoAnswerAdapters noAnswerAdapters;
    private Context context;
    public static TextView noAnswerError;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_no_answer, container, false);
        recyclerView = view.findViewById(R.id.no_answer_recylerview);
        swipeRefreshLayout = view.findViewById(R.id.no_answer_swipe);
        spinKitView = view.findViewById(R.id.no_answer_spin);
        noAnswerError = view.findViewById(R.id.no_answer_error);
        searchView = view.findViewById(R.id.no_answer_searchView);

        datum2List = new ArrayList<>();

        linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);

        noAnswerAdapters = new NoAnswerAdapters(datum2List,context);
        recyclerView.setAdapter(noAnswerAdapters);

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (progress!=null)
                    progress.show();

                SharedPreferences pref = context.getApplicationContext().getSharedPreferences("AutoDialer", Activity.MODE_PRIVATE);
                String userId = pref.getString("id","");
                String admin_id = pref.getString("admin_id","");
                ApiInterface.getApiRequestInterface().searchnoanser(userId,admin_id,query)
                        .enqueue(new Callback<List<Datum2>>() {
                            @Override
                            public void onResponse(Call<List<Datum2>> call, Response<List<Datum2>> response) {
                                if (response.isSuccessful())
                                {
                                    if (progress!=null)
                                        progress.dismiss();

                                    if (datum2List.size()>0)
                                        datum2List.clear();

                                    spinKitView.setVisibility(View.GONE);
                                    swipeRefreshLayout.setRefreshing(false);
                                    loading = true;

                                    datum2List.addAll(response.body());

                                    if (datum2List.size()>0) {
                                        noAnswerError.setVisibility(View.GONE);
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                    else {
                                        noAnswerError.setVisibility(View.VISIBLE);
                                        recyclerView.setVisibility(View.GONE);
                                    }

                                    noAnswerAdapters.notifyDataSetChanged();
                                }
                                if (progress!=null)
                                    progress.dismiss();
                            }

                            @Override
                            public void onFailure(Call<List<Datum2>> call, Throwable t) {
                                noAnswerError.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                if (progress!=null)
                                    progress.dismiss();
                                Toast.makeText(context, "Error : "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();                            }
                        });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //    adapter.getFilter().filter(newText);
                return false;
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

        ApiInterface.getApiRequestInterface().getNoAnswerList(Constants.adminID,page)
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
                                noAnswerError.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            else {
                                noAnswerError.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }

                            noAnswerAdapters.notifyDataSetChanged();
                        }
                        if (progress!=null)
                            progress.dismiss();
                    }

                    @Override
                    public void onFailure(Call<Schedule> call, Throwable t) {
                        noAnswerError.setVisibility(View.GONE);
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
}