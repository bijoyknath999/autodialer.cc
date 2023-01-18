package com.example.autodialer.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autodialer.MainActivity;
import com.example.autodialer.R;
import com.example.autodialer.api.ApiInterface;
import com.example.autodialer.api.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {
    private View view;
    private ProgressDialog progress;
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = context.getSharedPreferences("AutoDialer", Context.MODE_PRIVATE);

        Constants.id = sharedPreferences.getString("id", "");
        Constants.name = sharedPreferences.getString("name", "");
        Constants.email = sharedPreferences.getString("email", "");
        Constants.phoneNo = sharedPreferences.getString("phone", "");
        Constants.address = sharedPreferences.getString("address", "");
        editor = sharedPreferences.edit();

        TextView name =(TextView) view.findViewById(R.id.person_name);
        TextView country =(TextView) view.findViewById(R.id.person_country);
        TextView email =(TextView) view.findViewById(R.id.person_email);
        TextView phone =(TextView) view.findViewById(R.id.person_phone);
        Button logout =(Button) view.findViewById(R.id.logout);
        name.setText("Name: "+Constants.name);
        country.setText("Address: "+Constants.address);
        email.setText("Email: "+Constants.email);
        phone.setText("Phone: "+Constants.phoneNo);

        progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.show();
                logout();
            }
        });
        return view;
    }

    private void logout()
    {
        ApiInterface.getApiRequestInterface().saveStatusInActive(Constants.id)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful())
                        {
                            editor.putBoolean("key_name",false);
                            editor.apply();
                            progress.dismiss();
                            Intent intent = new Intent(getContext(), MainActivity.class);
                            startActivity(intent);
                        }
                        progress.dismiss();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        progress.dismiss();
                        Toast.makeText(context, "Error : "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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