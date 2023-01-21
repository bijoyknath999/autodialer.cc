package com.autodialer.autodialer.cc.api;

import com.autodialer.autodialer.cc.models.Completed;
import com.autodialer.autodialer.cc.models.Login;
import com.autodialer.autodialer.cc.models.Recording;
import com.autodialer.autodialer.cc.models.Schedule;
import com.autodialer.autodialer.cc.models.Leads;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiRequest {
    @GET("leads/{user_id}")
    Call<Leads> getLeads(@Path("user_id") int user_id,
                         @Query("page") int page);

    @FormUrlEncoded
    @POST("recording")
    Call<Recording> saveRecording(@Field("id") String id,
                                  @Field("user_id") String user_id,
                                  @Field("admin_id") String admin_id,
                                  @Field("feedback") String feedback,
                                  @Field("w_feedback") String w_feedback,
                                  @Field("scheduled") String scheduled,
                                  @Field("recording") String recording,
                                  @Field("client_pno") String client_pno,
                                  @Field("client_name") String client_name,
                                  @Field("client_city") String client_city,
                                  @Field("date_time") String date_time,
                                  @Field("call_duration") String call_duration,
                                  @Field("company") String company,
                                  @Field("campaign_id") String campaign_id,
                                  @Field("lead_id") String lead_id);

    @FormUrlEncoded
    @POST("login")
    Call<Login> sendLogin(@Field("email") String email,
                          @Field("password") String password);

    @GET("updateuser/{id}/Active")
    Call<String> saveStatus(@Path("id") String id);

    @GET("completed")
    Call<List<Completed>> getCompleted();

    @DELETE("delete/{id}")
    Call<String> deleteLead(@Path("id") String id);

    @GET("schedule/{id}")
    Call<Schedule> getScheduledList(@Path("id") String id,
                                    @Query("page") int page);

    @PUT("update")
    Call<Recording> updateRecording(@Query("id") int id,
                                    @Query("user_id") String user_id,
                                    @Query("admin_id") String admin_id,
                                    @Query("feedback") String feedback,
                                    @Query("w_feedback") String w_feedback,
                                    @Query("scheduled") String scheduled,
                                    @Query("recording") String recording,
                                    @Query("client_pno") String client_pno,
                                    @Query("client_name") String client_name,
                                    @Query("client_city") String client_city,
                                    @Query("date_time") String date_time,
                                    @Query("call_duration") String call_duration,
                                    @Query("company") String company,
                                    @Query("campaign_id") String campaign_id,
                                    @Query("lead_id") String lead_id);

    @GET("updateuser/{id}/Inactive")
    Call<String> saveStatusInActive(@Path("id") String id);

    @GET("gettotalleads/{user_id}")
    Call<String> getTotalLeads(@Path("user_id") int user_id);

    @FormUrlEncoded
    @POST("getschedulewithtime")
    Call<List<Recording>> getScheduleAll(@Field("user_id") String user_id,
                                         @Field("admin_id") String admin_id);

}
