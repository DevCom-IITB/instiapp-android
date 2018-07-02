package app.insti.api;

import java.util.List;

import app.insti.api.model.EventCreateRequest;
import app.insti.api.model.EventCreateResponse;
import app.insti.api.model.ImageUploadRequest;
import app.insti.api.model.ImageUploadResponse;
import app.insti.api.model.LoginResponse;
import app.insti.api.model.NewsFeedResponse;
import app.insti.data.HostelMessMenu;
import app.insti.data.NewsArticle;
import app.insti.data.PlacementBlogPost;
import app.insti.data.TrainingBlogPost;
import app.insti.data.User;
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

    @GET("pass-login")
    Call<LoginResponse> passwordLogin(@Query("username") String username, @Query("password") String password);

    @POST("events")
    Call<EventCreateResponse> createEvent(@Header("Cookie") String sessionId, @Body EventCreateRequest eventCreateRequest);

    @GET("events")
    Call<NewsFeedResponse> getNewsFeed(@Header("Cookie") String sessionId);

    @GET("events")
    Call<NewsFeedResponse> getEventsBetweenDates(@Header("Cookie") String sessionId, @Query("start") String start, @Query("end") String end);

    @GET("users/{uuid}")
    Call<User> getUser(@Header("Cookie") String sessionId, @Path("uuid") String uuid);

    @GET("bodies/{uuid}")
    Call<app.insti.data.Body> getBody(@Header("Cookie") String sessionId, @Path("uuid") String uuid);

    @GET("bodies/{bodyID}/follow")
    Call<Void> updateBodyFollowing(@Header("Cookie") String sessionID, @Path("bodyID") String eventID, @Query("action") int action);

    @POST("upload")
    Call<ImageUploadResponse> uploadImage(@Header("Cookie") String sessionID, @Body ImageUploadRequest imageUploadRequest);

    @GET("user-me/ues/{eventID}")
    Call<Void> updateUserEventStatus(@Header("Cookie") String sessionID, @Path("eventID") String eventID, @Query("status") int status);

    @GET("placement-blog")
    Call<List<PlacementBlogPost>> getPlacementBlogFeed(@Header("Cookie") String sessionID);

    @GET("training-blog")
    Call<List<TrainingBlogPost>> getTrainingBlogFeed(@Header("Cookie") String sessionID);

    @GET("mess")
    Call<List<HostelMessMenu>> getInstituteMessMenu(@Header("Cookie") String sessionID);

    @GET("news")
    Call<List<NewsArticle>> getNews(@Header("Cookie") String sessionID);

    @GET("logout")
    Call<Void> logout(@Header("Cookie") String sessionID);

//    @POST("getNotifications/")
//    Call<NotificationsResponse> getNotifications(@Body NotificationsRequest notificationsRequest);
}
