package in.ac.iitb.gymkhana.iitbapp.api;

import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.ImageUploadRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.ImageUploadResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.NotificationsRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.NotificationsResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {
    @GET("login")
    Call<LoginResponse> login(@Query("code") String AUTH_CODE, @Query("redir") String redirectURI, @Query("fcm_id") String fcmID);

    @POST("events")
    Call<EventCreateResponse> createEvent(@Body EventCreateRequest eventCreateRequest);

    @GET("users/{uuid}/followed_bodies_events")
    Call<NewsFeedResponse> getNewsFeed(@Path("uuid") String uuid);

    @POST("getNotifications/")
    Call<NotificationsResponse> getNotifications(@Body NotificationsRequest notificationsRequest);

    @POST("upload")
    Call<ImageUploadResponse> uploadImage(@Body ImageUploadRequest imageUploadRequest);
}
