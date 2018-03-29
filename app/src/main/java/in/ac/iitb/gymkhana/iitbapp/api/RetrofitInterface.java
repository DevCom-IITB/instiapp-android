package in.ac.iitb.gymkhana.iitbapp.api;

import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.ImageUploadRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.ImageUploadResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedResponse;
import in.ac.iitb.gymkhana.iitbapp.data.User;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {
    @GET("login")
    Call<LoginResponse> login(@Query("code") String AUTH_CODE, @Query("redir") String redirectURI, @Query("fcm_id") String fcmID);

    @POST("events")
    Call<EventCreateResponse> createEvent(@Header("Cookie") String sessionId, @Body EventCreateRequest eventCreateRequest);

    @GET("events")
    Call<NewsFeedResponse> getNewsFeed(@Header("Cookie") String sessionId);

    @GET("users/{uuid}")
    Call<User> getUser(@Header("Cookie") String sessionId, @Path("uuid") String uuid);

    @POST("upload")
    Call<ImageUploadResponse> uploadImage(@Header("Cookie") String sessionID, @Body ImageUploadRequest imageUploadRequest);

    @GET("user-me/ues/{eventID}")
    Call<Void> updateUserEventStatus(@Header("Cookie") String sessionID, @Path("eventID") String eventID, @Query("status") int status);

//    @POST("getNotifications/")
//    Call<NotificationsResponse> getNotifications(@Body NotificationsRequest notificationsRequest);
}
