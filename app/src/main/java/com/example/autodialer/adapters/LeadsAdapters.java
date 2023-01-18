package com.example.autodialer.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autodialer.api.Constant_client;
import com.example.autodialer.R;
import com.example.autodialer.api.ApiInterface;
import com.example.autodialer.api.Constants;
import com.example.autodialer.fragments.LeadsFragment;
import com.example.autodialer.models.Datum;
import com.example.autodialer.models.Recording;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeadsAdapters extends RecyclerView.Adapter<LeadsAdapters.ViewHolder> {

    private Context context;
    public List<Datum> datumList;
    final Calendar myCalendar= Calendar.getInstance();
    final Calendar c = Calendar.getInstance();
    int mHour = c.get(Calendar.HOUR_OF_DAY);
    int mMinute = c.get(Calendar.MINUTE);
    String email, Feedback, Schedule_res="No", Datee,Timee,del_id, w_feed, lead_id, campaign_id, company_name;



    public LeadsAdapters(Context context, List<Datum> datumList) {
        this.context = context;
        this.datumList = datumList;
    }

    @NonNull
    @Override
    public LeadsAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leads, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadsAdapters.ViewHolder holder, int position) {

        Datum datum = datumList.get(position);;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("AutoDialer", Activity.MODE_PRIVATE);
        String userId = pref.getString("id","");
        String admin_id = pref.getString("admin_id","");

        ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);

        Constant_client.id = datum.getId().toString();
        Constant_client.name = datum.getName();
        Constant_client.city = datum.getCity();
        Constant_client.country = datum.getCompany();
        Constant_client.phone = datum.getPhone();

        holder.Whatsappbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + holder.PhoneText.getText().toString());

                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(sendIntent);
                Constants.cal1 = Calendar.getInstance().getTimeInMillis();
                View popupView = inflater.inflate(R.layout.feedback_popup, null);

                // create the popup window
                int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                Button submit = (Button) popupView.findViewById(R.id.submit_feedback);
                EditText w_feedback = (EditText) popupView.findViewById(R.id.w_feedback);
                CheckBox schedule = (CheckBox) popupView.findViewById(R.id.schedule);
                Button dd = (Button) popupView.findViewById(R.id.datetime);
                Button tt = (Button) popupView.findViewById(R.id.timee);
                ImageView w_btn = (ImageView) popupView.findViewById(R.id.whatsapp_btn1);
                w_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + holder.PhoneText.getText().toString());
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(sendIntent);
                    }
                });
                DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH,month);
                        myCalendar.set(Calendar.DAY_OF_MONTH,day);
                        String myFormat="MM-dd-yy";
                        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
                        dd.setText(dateFormat.format(myCalendar.getTime()));
                        Datee= dd.getText().toString();
                    }
                };
                TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinute = c.get(Calendar.MINUTE);
                        if(hourOfDay>0 && hourOfDay<10) {
                            if(minute>=0 && minute<10) {
                                tt.setText("0" + hourOfDay + ":0" + minute);
                            }
                            else{
                                tt.setText("0"+hourOfDay + ":" + minute);
                            }
                        }
                        else if(minute>=0 && minute<10){
                            tt.setText("0" + hourOfDay + ":0" + minute);
                        }
                        else{
                            tt.setText(hourOfDay + ":" + minute);
                        }
                        Timee=tt.getText().toString();
                    }
                };
                tt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TimePickerDialog(context,time,c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
                    }
                });
                dd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DatePickerDialog(context,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
                RadioGroup feedback = (RadioGroup) popupView.findViewById(R.id.feedback);

                schedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            Schedule_res = "Yes";
                        }

                        else {
                            Schedule_res = "No";
                        }
                    }
                });
                feedback.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    }
                });
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (progress!=null)
                            progress.show();

                        int selectedId = feedback.getCheckedRadioButtonId();
                        if (selectedId == -1) {
                            // Toast.makeText("No answer has been selected",Toast.LENGTH_SHORT).show();
                        } else {

                            RadioButton radioButton = (RadioButton) feedback.findViewById(selectedId);
                            Feedback = radioButton.getText().toString();

                        }
                        del_id = datum.getId().toString();
                        campaign_id = datum.getCampaign_id().toString();
                        lead_id = datum.getLead_id().toString();
                        company_name = datum.getCompany();

                        String date_time;
                        if(Schedule_res.equals("Yes")){
                            if (Datee==null)
                            {
                                Toast.makeText(context, "Please select date", Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                                return;
                            }
                            else if (Timee==null)
                            {
                                Toast.makeText(context, "Please select time", Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                                return;
                            }
                            date_time = getConverteddate(Datee)+" "+getConvertedTime(Timee);
                        }else{
                            date_time= "00:00";
                        }

                        w_feed = w_feedback.getText().toString();
                        if (w_feed.isEmpty())
                            w_feed = "NA";

                        ApiInterface.getApiRequestInterface().saveRecording(del_id,userId,admin_id,Feedback,w_feed
                                        ,Schedule_res,"",Constant_client.phone,
                                        Constant_client.name,Constant_client.city,date_time,
                                        "00:00",company_name,campaign_id,lead_id)
                                .enqueue(new Callback<Recording>() {
                                    @Override
                                    public void onResponse(Call<Recording> call, Response<Recording> response) {
                                        if (response.isSuccessful()) {
                                            ApiInterface.getApiRequestInterface().deleteLead(del_id)
                                                    .enqueue(new Callback<String>() {
                                                        @Override
                                                        public void onResponse(Call<String> call, Response<String> response) {
                                                            if (response.isSuccessful())
                                                            {
                                                                LeadsFragment.LoadDataAfterDelete(context);
                                                                popupWindow.dismiss();
                                                                Toast.makeText(context, "Submitted Successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                            if (progress!=null)
                                                                progress.dismiss();
                                                        }

                                                        @Override
                                                        public void onFailure(Call<String> call, Throwable t) {
                                                            if (progress!=null)
                                                                progress.dismiss();
                                                            Toast.makeText( context, "Error : "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                        else {
                                            if (progress!=null)
                                                progress.dismiss();
                                            try {
                                                JSONObject jObjError = new JSONObject(response.errorBody().string());
                                                Toast.makeText(context, ""+jObjError.getString("message"), Toast.LENGTH_LONG).show();
                                            } catch (Exception e) {
                                                System.out.println("Error :"+e.getLocalizedMessage());
                                                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Recording> call, Throwable t) {
                                        if (progress!=null)
                                            progress.dismiss();
                                        Toast.makeText( context, "Error : "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });

            }
        });

        holder.DialBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + holder.PhoneText.getText()));
                context.startActivity(callIntent);
                Constants.cal1 = Calendar.getInstance().getTimeInMillis();


                // Constants.time= Time.valueOf(Constants.currentTime);
                View popupView = inflater.inflate(R.layout.feedback_popup, null);

                // create the popup window
                int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                Button submit = (Button) popupView.findViewById(R.id.submit_feedback);
                EditText w_feedback = (EditText) popupView.findViewById(R.id.w_feedback);
                CheckBox schedule = (CheckBox) popupView.findViewById(R.id.schedule);
                Button dd = (Button) popupView.findViewById(R.id.datetime);
                Button tt = (Button) popupView.findViewById(R.id.timee);
                ImageView w_btn = (ImageView) popupView.findViewById(R.id.whatsapp_btn1);
                w_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + holder.PhoneText.getText().toString());

                        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);

                        context.startActivity(sendIntent);
                    }
                });
                DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH,month);
                        myCalendar.set(Calendar.DAY_OF_MONTH,day);
                        String myFormat="MM-dd-yy";
                        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
                        dd.setText(dateFormat.format(myCalendar.getTime()));
                        Datee= dd.getText().toString();
                    }
                };
                TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = c.get(Calendar.HOUR_OF_DAY);
                        mMinute = c.get(Calendar.MINUTE);
                        if(hourOfDay>=0 && hourOfDay<10) {
                            if(minute>=0 && minute<10) {
                                tt.setText("0" + hourOfDay + ":0" + minute);
                            }
                            else{
                                tt.setText("0"+hourOfDay + ":" + minute);
                            }
                        }
                        else if(minute>=0 && minute<10){
                            tt.setText(hourOfDay + ":0" + minute);
                        }
                        else{
                            tt.setText(hourOfDay + ":" + minute);
                        }
                        Timee=tt.getText().toString();
                    }
                };
                tt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new TimePickerDialog(context,time,c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),true).show();
                    }
                });
                dd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new DatePickerDialog(context,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
                RadioGroup feedback = (RadioGroup) popupView.findViewById(R.id.feedback);
                schedule.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked){
                            Schedule_res = "Yes";
                        }
                        else {
                            Schedule_res = "No";
                        }
                    }
                });
                feedback.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton radioButton = (RadioButton) group.findViewById(checkedId);
                    }
                });
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (progress!=null)
                            progress.show();

                        int selectedId = feedback.getCheckedRadioButtonId();
                        if (selectedId == -1) {
                            // Toast.makeText("No answer has been selected",Toast.LENGTH_SHORT).show();
                        } else {

                            RadioButton radioButton = (RadioButton) feedback.findViewById(selectedId);
                            Feedback = radioButton.getText().toString();

                        }
                        del_id = datum.getId().toString();
                        campaign_id = datum.getCampaign_id().toString();
                        lead_id = datum.getLead_id().toString();
                        company_name = datum.getCompany();

                        String date_time;
                        if(Schedule_res.equals("Yes")){
                            if (Datee==null)
                            {
                                Toast.makeText(context, "Please select date", Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                                return;
                            }
                            else if (Timee==null)
                            {
                                Toast.makeText(context, "Please select time", Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                                return;
                            }
                            date_time = getConverteddate(Datee)+" "+getConvertedTime(Timee);
                        }else{
                            date_time= "00:00";
                        }

                        w_feed = w_feedback.getText().toString();
                        if (w_feed.isEmpty())
                            w_feed = "NA";

                        ApiInterface.getApiRequestInterface().saveRecording(del_id,userId,admin_id,Feedback,
                                w_feed,Schedule_res,"",Constant_client.phone,
                                Constant_client.name,Constant_client.city,date_time,
                                        "00:00",company_name,campaign_id,lead_id)
                                .enqueue(new Callback<Recording>() {
                            @Override
                            public void onResponse(Call<Recording> call, Response<Recording> response) {
                                if (response.isSuccessful()) {
                                    ApiInterface.getApiRequestInterface().deleteLead(del_id)
                                            .enqueue(new Callback<String>() {
                                                @Override
                                                public void onResponse(Call<String> call, Response<String> response) {
                                                    if (response.isSuccessful())
                                                    {
                                                        LeadsFragment.LoadDataAfterDelete(context);
                                                        popupWindow.dismiss();
                                                        Toast.makeText(context, "Submitted Successfully", Toast.LENGTH_SHORT).show();
                                                    }
                                                    if (progress!=null)
                                                        progress.dismiss();
                                                }

                                                @Override
                                                public void onFailure(Call<String> call, Throwable t) {
                                                    if (progress!=null)
                                                        progress.dismiss();
                                                    Toast.makeText( context, "Error : "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                                else {
                                    if (progress!=null)
                                        progress.dismiss();
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        Toast.makeText(context, ""+jObjError.getString("message"), Toast.LENGTH_LONG).show();
                                    } catch (Exception e) {
                                        System.out.println("Error :"+e.getLocalizedMessage());
                                        Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Recording> call, Throwable t) {
                                if (progress!=null)
                                    progress.dismiss();
                                Toast.makeText( context, "Error : "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        });

        holder.NameText.setText(datum.getName());
        holder.CompanyText.setText(datum.getCompany());
        holder.CityText.setText(datum.getCity());
        holder.PhoneText.setText(datum.getPhone());

    }

    private String getConvertedTime(String time)
    {
        Date time2 = null;
        SimpleDateFormat formatter1=new SimpleDateFormat("HH:mm");
        try {
            time2=formatter1.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String strTime = dateFormat.format(time2);
        return strTime;
    }

    private String getConverteddate(String date)
    {
        Date date2 = null;
        SimpleDateFormat formatter1=new SimpleDateFormat("MM-dd-yy");
        try {
            date2=formatter1.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String strDate = dateFormat.format(date2);
        return strDate;
    }

    @Override
    public int getItemCount() {
        return datumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView NameText, CompanyText, CityText, PhoneText;
        private ImageButton DialBtn;
        private ImageView Whatsappbtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            NameText = itemView.findViewById(R.id.item_lead_full_name);
            CompanyText = itemView.findViewById(R.id.item_lead_company);
            CityText = itemView.findViewById(R.id.item_lead_city);
            PhoneText = itemView.findViewById(R.id.item_lead_phone);
            DialBtn = itemView.findViewById(R.id.call_btn);
            Whatsappbtn = itemView.findViewById(R.id.whatsapp_btn);
        }
    }
}
