package com.autodialer.autodialer.cc.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import androidx.recyclerview.widget.RecyclerView;

import com.autodialer.autodialer.cc.R;
import com.autodialer.autodialer.cc.Tools;
import com.autodialer.autodialer.cc.api.ApiInterface;
import com.autodialer.autodialer.cc.api.Constant_client;
import com.autodialer.autodialer.cc.api.Constants;
import com.autodialer.autodialer.cc.fragments.LeadsFragment;
import com.autodialer.autodialer.cc.fragments.NoAnswerFragment;
import com.autodialer.autodialer.cc.models.Datum2;
import com.autodialer.autodialer.cc.models.Recording;

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

public class NoAnswerAdapters extends RecyclerView.Adapter<NoAnswerAdapters.ViewHolder> {

    private final List<Datum2> datum2List;
    private final Context context;
    String del_id;
    String email, Feedback, Schedule_res="No";
    EditText w_feedback;
    CheckBox schedule;
    View v;
    LeadsFragment TF=new LeadsFragment();
    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
    Button dd,tt;
    String Datee,Timee, campaign_id, lead_id,company_name, w_feed, callduration;
    final Calendar myCalendar= Calendar.getInstance();
    final Calendar c = Calendar.getInstance();
    int mHour = c.get(Calendar.HOUR_OF_DAY);
    int mMinute = c.get(Calendar.MINUTE);
    Tools tools = new Tools();

    public NoAnswerAdapters(List<Datum2> datum2List, Context context) {
        this.datum2List = datum2List;
        this.context = context;
    }

    @NonNull
    @Override
    public NoAnswerAdapters.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoAnswerAdapters.ViewHolder holder, int position) {
        Datum2 datum2 = datum2List.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        SharedPreferences pref = context.getApplicationContext().getSharedPreferences("AutoDialer", Activity.MODE_PRIVATE);
        String userId = pref.getString("id","");
        String admin_id = pref.getString("admin_id","");

        ProgressDialog progress = new ProgressDialog(context);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false);

        Constant_client.id = datum2.getId().toString();
        Constant_client.name = datum2.getClientName();
        Constant_client.city = datum2.getClientCity();
        Constant_client.country = "";
        Constant_client.phone = datum2.getClientPno();

        holder.whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + holder.Phone.getText().toString());
                Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(sendIntent);
                Constants.cal1 = Calendar.getInstance().getTimeInMillis();
                View popupView = inflater.inflate(R.layout.feedback_popup, null);

                int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                Button submit = (Button) popupView.findViewById(R.id.submit_feedback);
                w_feedback = (EditText) popupView.findViewById(R.id.w_feedback);
                schedule = (CheckBox) popupView.findViewById(R.id.schedule);
                dd = (Button) popupView.findViewById(R.id.datetime);
                tt = (Button) popupView.findViewById(R.id.timee);
                ImageView w_btn = (ImageView) popupView.findViewById(R.id.whatsapp_btn1);
                w_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + holder.Phone.getText().toString());
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
                        del_id = datum2.getId().toString();
                        campaign_id = datum2.getCampaign_id();
                        lead_id = datum2.getLead_id();
                        company_name = datum2.getCompany();

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

                        ApiInterface.getApiRequestInterface().updateRecording(Integer.parseInt(del_id),userId,admin_id,Feedback,
                                w_feed,Schedule_res,"",datum2.getClientPno(),
                                        datum2.getClientName(),datum2.getClientCity(),date_time,
                                        "00:00:00",company_name,campaign_id,lead_id)
                                .enqueue(new Callback<Recording>() {
                                    @Override
                                    public void onResponse(Call<Recording> call, Response<Recording> response) {
                                        if (response.isSuccessful())
                                        {
                                            NoAnswerFragment.LoadDataAfterDelete(context);
                                            if (progress!=null)
                                                progress.dismiss();
                                            popupWindow.dismiss();
                                            Toast.makeText(context, "Submitted Successfully", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(context, "Error : "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        });
        holder.Dialbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + holder.Phone.getText()));
                context.startActivity(callIntent);
                Constants.cal1 = Calendar.getInstance().getTimeInMillis();
                tools.startTimer(context);

                View popupView = inflater.inflate(R.layout.feedback_popup, null);

                int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
                int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                Button submit = (Button) popupView.findViewById(R.id.submit_feedback);
                w_feedback = (EditText) popupView.findViewById(R.id.w_feedback);
                schedule = (CheckBox) popupView.findViewById(R.id.schedule);
                dd = (Button) popupView.findViewById(R.id.datetime);
                tt = (Button) popupView.findViewById(R.id.timee);
                ImageView w_btn = (ImageView) popupView.findViewById(R.id.whatsapp_btn1);
                w_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + holder.Phone.getText().toString());
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
                        del_id = datum2.getId().toString();
                        campaign_id = datum2.getCampaign_id();
                        lead_id = datum2.getLead_id();
                        company_name = datum2.getCompany();


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

                        callduration = pref.getString("callduration","00:00:00");

                        ApiInterface.getApiRequestInterface().updateRecording(Integer.parseInt(del_id),userId,admin_id,Feedback,
                                        w_feed,Schedule_res,"",datum2.getClientPno(),
                                        datum2.getClientName(),datum2.getClientCity(),date_time,
                                        callduration,company_name,campaign_id,lead_id)
                                .enqueue(new Callback<Recording>() {
                                    @Override
                                    public void onResponse(Call<Recording> call, Response<Recording> response) {
                                        if (response.isSuccessful())
                                        {
                                            SharedPreferences.Editor editor = pref.edit();
                                            editor.putString("callduration","00:00:00");
                                            editor.apply();
                                            tools.stoptimer();
                                            NoAnswerFragment.LoadDataAfterDelete(context);
                                            if (progress!=null)
                                                progress.dismiss();
                                            popupWindow.dismiss();
                                            Toast.makeText(context, "Submitted Successfully", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(context, "Error : "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
            }
        });


        holder.Name.setText(datum2.getClientName());
        holder.City.setText(datum2.getClientCity());
        holder.Phone.setText(datum2.getClientPno());
        holder.CompanyText.setText(datum2.getCompany());
        holder.TimeDateText.setText("Schedule Time : "+ datum2.getDateTime());

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
        return datum2List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView whatsapp;
        private final ImageButton Dialbtn;
        private final TextView Name;
        private final TextView City;
        private final TextView Phone;
        private final TextView TimeDateText;
        private final TextView CompanyText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.item_schedule_first_name);
            City = itemView.findViewById(R.id.item_schedule_city);
            Phone = itemView.findViewById(R.id.item_schedule_phone);
            TimeDateText = itemView.findViewById(R.id.item_schedule_datetime);
            CompanyText = itemView.findViewById(R.id.item_schedule_company);
            Dialbtn = itemView.findViewById(R.id.call_btn);
            whatsapp = itemView.findViewById(R.id.whatsapp_btn);
        }
    }
}
