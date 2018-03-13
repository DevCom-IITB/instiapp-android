package in.ac.iitb.gymkhana.iitbapp.api;

import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.NotificationsRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.NotificationsResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitInterface {
    @POST("login/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/createEvent/")
    Call<EventCreateResponse> eventCreate(@Body EventCreateRequest eventCreateRequest);

    @GET("users/{uuid}/followed_bodies_events")
    Call<NewsFeedResponse> getNewsFeed(@Path("uuid") String uuid);

    @POST("getNotifications/")
    Call<NotificationsResponse> getNotifications(@Body NotificationsRequest notificationsRequest);
}
