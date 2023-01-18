package com.example.autodialer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autodialer.api.ApiInterface;
import com.example.autodialer.api.Constants;
import com.example.autodialer.models.Login;
import com.example.autodialer.models.Welcome;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity implements PermissionUtil.PermissionsCallBack{
    Button login_btn;
    EditText email, password;
    TextView mLink;
    private static final int REQUEST_PHONE_CALL = 1;
    private static final int REQUEST_NOTIFICATIONS = 2;
    private ProgressDialog progress;

    private static final String TAG = "MainActivity";


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sp = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        boolean b = sp.getBoolean("key_name", false);
        if (b) {
            Constants.id = sp.getString("id", "");
            Constants.name = sp.getString("name", "");
            Constants.email = sp.getString("email", "");
            Constants.phoneNo = sp.getString("phone", "");
            Constants.address = sp.getString("address", "");
            Constants.adminID = sp.getString("admin_id", "");
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLink = (TextView) findViewById(R.id.link);
        if (mLink != null) {
            mLink.setMovementMethod(LinkMovementMethod.getInstance());
        }
        login_btn = (Button) findViewById(R.id.login_button);
        email = (EditText) findViewById(R.id.userName);
        password = (EditText) findViewById(R.id.userPassword);

        requestPermissions();

        progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                String emailstr = email.getText().toString();
                String passwordstr = password.getText().toString();
                if (emailstr.isEmpty())
                    email.setError("Email is empty");
                else if (passwordstr.isEmpty())
                    password.setError("Password is empty");
                else {
                    progress.show();
                    checkLogin(emailstr, passwordstr);
                }
            }
        });

    }

    private void checkLogin(String emailstr, String passwordstr) {
        ApiInterface.getApiRequestInterface().sendLogin(emailstr, passwordstr)
                .enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, retrofit2.Response<Login> response) {
                        if (response.isSuccessful()) {
                            Login login = response.body();
                            Welcome welcome = login.getWelcome();
                            Constants.id = welcome.getId().toString();
                            Constants.name = welcome.getName();
                            Constants.email = welcome.getEmail();
                            Constants.phoneNo = welcome.getPhone();
                            Constants.address = welcome.getAddress();
                            Constants.adminID = welcome.getAdminId();

                            ApiInterface.getApiRequestInterface().saveStatus(Constants.id)
                                    .enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                                            if (response.isSuccessful()) {
                                                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", Activity.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = pref.edit();
                                                editor.putBoolean("key_name", true);
                                                editor.putString("id", Constants.id);
                                                editor.putString("name", Constants.name);
                                                editor.putString("email", Constants.email);
                                                editor.putString("phone", Constants.phoneNo);
                                                editor.putString("address", Constants.address);
                                                editor.putString("admin_id", Constants.adminID);// Storing boolean - true/false
                                                editor.commit();
                                                progress.dismiss();
                                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                                                startActivity(intent);
                                                finishAffinity();
                                            } else {
                                                progress.dismiss();
                                                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            progress.dismiss();
                                            Toast.makeText(MainActivity.this, "Error : " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            progress.dismiss();
                            try {
                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                Toast.makeText(MainActivity.this, ""+jObjError.getString("message"), Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                System.out.println("Error :"+e.getLocalizedMessage());
                                Toast.makeText(MainActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Login> call, Throwable t) {
                        progress.dismiss();
                        Toast.makeText(MainActivity.this, "Error : " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionUtil.checkAndRequestPermissions(this,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.ACCESS_NOTIFICATION_POLICY)) {
                Log.i(TAG, "Permissions are granted. Good to go!");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, grantResults, this);
    }

    @Override
    public void permissionsGranted() {
        Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void permissionsDenied() {
        Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_SHORT).show();
    }
}

