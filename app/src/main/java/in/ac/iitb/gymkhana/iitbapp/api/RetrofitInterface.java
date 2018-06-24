package in.ac.iitb.gymkhana.iitbapp.api;

import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.ImageUploadRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.ImageUploadResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedResponse;
import in.ac.iitb.gymkhana.iitbapp.data.HostelMessMenu;
import in.ac.iitb.gymkhana.iitbapp.data.NewsArticle;
import in.ac.iitb.gymkhana.iitbapp.data.PlacementBlogPost;
import in.ac.iitb.gymkhana.iitbapp.data.TrainingBlogPost;
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

    @GET("bodies/{uuid}")
    Call<in.ac.iitb.gymkhana.iitbapp.data.Body> getBody(@Header("Cookie") String sessionId, @Path("uuid") String uuid);

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

//    @POST("getNotifications/")
//    Call<NotificationsResponse> getNotifications(@Body NotificationsRequest notificationsRequest);
}
