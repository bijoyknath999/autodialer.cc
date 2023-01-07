package com.example.autodialer.api;

import com.example.autodialer.models.Completed;
import com.example.autodialer.models.Leads;
import com.example.autodialer.models.Login;
import com.example.autodialer.models.Recording;
import com.example.autodialer.models.Schedule;

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
    @GET("leads")
    Call<Leads> getLeads(@Query("page") int page);

    @FormUrlEncoded
    @POST("recording")
    Call<Recording> saveRecording(@Field("user_id") String user_id,
                                  @Field("admin_id") String admin_id,
                                  @Field("feedback") String feedback,
                                  @Field("w_feedback") String w_feedback,
                                  @Field("scheduled") String scheduled,
                                  @Field("recording") String recording,
                                  @Field("client_pno") String client_pno,
                                  @Field("client_name") String client_name,
                                  @Field("client_city") String client_city,
                                  @Field("date_time") String date_time,
                                  @Field("call_duration") String call_duration);

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

    @FormUrlEncoded
    @PUT("update")
    Call<Recording> updateRecording(@Field("id") String id,
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
                                    @Field("call_duration") String call_duration);

    @GET("updateuser/{id}/Inactive")
    Call<String> saveStatusInActive(@Path("id") String id);

}
