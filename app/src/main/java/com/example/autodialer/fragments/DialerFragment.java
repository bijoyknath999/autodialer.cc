package com.example.autodialer.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.autodialer.R;
import com.example.autodialer.api.ApiInterface;
import com.example.autodialer.api.Constants;
import com.example.autodialer.models.Completed;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialerFragment extends Fragment {
    View view;
    Button done, pending;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dialer, container, false);
        pending = (Button) view.findViewById(R.id.PendingBtn);
        done = (Button) view.findViewById(R.id.DoneBtn);
        sp = getActivity().getSharedPreferences("AutoDialer", Context.MODE_PRIVATE);
        editor = sp.edit();
        Constants.id = sp.getString("id", "");
        Constants.done = sp.getString("done", "0");
        Constants.pending = Integer.parseInt(sp.getString("pending", "0"));

        done.setText("Done\n" + "0");
        pending.setText("Pending\n" + "0");
        getData();
        getpendingData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void getData(){
        ApiInterface.getApiRequestInterface().getCompleted()
                .enqueue(new Callback<List<Completed>>() {
                    @Override
                    public void onResponse(Call<List<Completed>> call, retrofit2.Response<List<Completed>> response) {
                        if (response.isSuccessful())
                        {
                            List<Completed> completedList = response.body();
                            if (completedList.size()>0)
                            {
                                for (Completed completed : completedList)
                                {
                                    if (completed.getUserId().equals(Constants.id)) {
                                        editor.putString("done", completed.getTotal().toString());
                                        editor.commit();
                                        Constants.done = completed.getTotal().toString();
                                        done.setText("Done\n" + completed.getTotal());
                                    }
                                }
                            }
                            else
                            {
                                editor.putString("done", "0");
                                editor.commit();
                                Constants.done = "0";
                                done.setText("Done\n" + "0");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Completed>> call, Throwable t) {
                        Toast.makeText(context, "Error : "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getpendingData(){
        ApiInterface.getApiRequestInterface().getTotalLeads(Integer.parseInt(Constants.id))
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful())
                        {
                            editor.putString("pending", response.body().toString());
                            editor.commit();
                            Constants.pending = Integer.parseInt(response.body());
                            pending.setText("Pending\n" + response.body().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });
    }

}
