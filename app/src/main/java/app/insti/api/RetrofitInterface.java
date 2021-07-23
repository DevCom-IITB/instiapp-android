package app.insti.api;

import com.google.gson.JsonObject;

import java.util.List;

import app.insti.api.model.Event;
import app.insti.api.model.HostelMessMenu;
import app.insti.api.model.NewsArticle;
import app.insti.api.model.Notification;
import app.insti.api.model.PlacementBlogPost;
import app.insti.api.model.SearchDataPost;
import app.insti.api.model.TrainingBlogPost;
import app.insti.api.model.User;
import app.insti.api.model.Venter;
import app.insti.api.model.Venue;
import app.insti.api.request.CommentCreateRequest;
import app.insti.api.request.ComplaintCreateRequest;
import app.insti.api.request.EventCreateRequest;
import app.insti.api.request.ImageUploadRequest;
import app.insti.api.request.QuestionCreateRequest;
import app.insti.api.request.UserFCMPatchRequest;
import app.insti.api.request.UserShowContactPatchRequest;
import app.insti.api.response.ComplaintCreateResponse;
import app.insti.api.response.EventCreateResponse;
import app.insti.api.response.ExploreResponse;
import app.insti.api.response.ImageUploadResponse;
import app.insti.api.response.LoginResponse;
import app.insti.api.response.NewsFeedResponse;
import app.insti.api.response.QuestionCreateResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {
    @GET("../andro.json")
    Call<JsonObject> getLatestVersion();

    @GET("login")
    Call<LoginResponse> login(@Query("code") String AUTH_CODE, @Query("redir") String redirectURI, @Query("fcm_id") String fcmID);

    @GET("login")
    Call<LoginResponse> login(@Query("code") String AUTH_CODE, @Query("redir") String redirectURI);

    @GET("pass-login")
    Call<LoginResponse> passwordLogin(@Query("username") String username, @Query("password") String password);

    @GET("pass-login")
    Call<LoginResponse> passwordLogin(@Query("username") String username, @Query("password") String password, @Query("fcm_id") String fcmId);

    @POST("events")
    Call<EventCreateResponse> createEvent(@Header("Cookie") String sessionId, @Body EventCreateRequest eventCreateRequest);

    @GET("events/{uuid}")
    Call<Event> getEvent(@Header("Cookie") String sessionId, @Path("uuid") String uuid);

    @GET("events")
    Call<NewsFeedResponse> getNewsFeed(@Header("Cookie") String sessionId);

    @GET("events")
    Call<NewsFeedResponse> getEventsBetweenDates(@Header("Cookie") String sessionId, @Query("start") String start, @Query("end") String end);

    @GET("locations")
    Call<List<Venue>> getAllVenues();

    @GET("users/{uuid}")
    Call<User> getUser(@Header("Cookie") String sessionId, @Path("uuid") String uuid);

    @GET("bodies/{uuid}")
    Call<app.insti.api.model.Body> getBody(@Header("Cookie") String sessionId, @Path("uuid") String uuid);

    @GET("bodies")
    Call<List<app.insti.api.model.Body>> getAllBodies(@Header("Cookie") String sessionId);

    @GET("bodies/{bodyID}/follow")
    Call<Void> updateBodyFollowing(@Header("Cookie") String sessionID, @Path("bodyID") String eventID, @Query("action") int action);

    @POST("upload")
    Call<ImageUploadResponse> uploadImage(@Header("Cookie") String sessionID, @Body ImageUploadRequest imageUploadRequest);

    @GET("user-me")
    Call<User> getUserMe(@Header("Cookie") String sessionID);

    @PATCH("user-me")
    Call<User> patchUserMe(@Header("Cookie") String sessionID, @Body UserFCMPatchRequest userFCMPatchRequest);

    @PATCH("user-me")
    Call<User> patchUserMe(@Header("Cookie") String sessionID, @Body UserShowContactPatchRequest userShowContactPatchRequest);

    @GET("user-me/ues/{eventID}")
    Call<Void> updateUserEventStatus(@Header("Cookie") String sessionID, @Path("eventID") String eventID, @Query("status") int status);

    @GET("placement-blog")
    Call<List<PlacementBlogPost>> getPlacementBlogFeed(@Header("Cookie") String sessionID, @Query("from") int from, @Query("num") int num, @Query("query") String query);

    @GET("training-blog")
    Call<List<TrainingBlogPost>> getTrainingBlogFeed(@Header("Cookie") String sessionID, @Query("from") int from, @Query("num") int num, @Query("query") String query);

    @GET("mess")
    Call<List<HostelMessMenu>> getInstituteMessMenu(@Header("Cookie") String sessionID);

    @GET("news")
    Call<List<NewsArticle>> getNews(@Header("Cookie") String sessionID, @Query("from") int from, @Query("num") int num, @Query("query") String query);

    @GET("notifications")
    Call<List<Notification>> getNotifications(@Header("Cookie") String sessionID);

    @GET("notifications/read/{notificationID}")
    Call<Void> markNotificationRead(@Header("Cookie") String sessionID, @Path("notificationID") String notificationID);

    @GET("notifications/read/{notificationID}?delete=1")
    Call<Void> markNotificationDeleted(@Header("Cookie") String sessionID, @Path("notificationID") String notificationID);

    @GET("logout")
    Call<Void> logout(@Header("Cookie") String sessionID);

    @GET("search")
    Call<ExploreResponse> search(@Header("Cookie") String sessionID, @Query("query") String query);

    @GET("venter/complaints")
    Call<List<Venter.Complaint>> getAllComplaints(@Header("Cookie") String sessionId, @Query("from") int from, @Query("num") int num);

    @GET("venter/complaints?filter=me")
    Call<List<Venter.Complaint>> getUserComplaints(@Header("Cookie") String sessionId);

    @GET("venter/complaints/{complaintId}")
    Call<Venter.Complaint> getComplaint(@Header("Cookie") String sessionId, @Path("complaintId") String complaintId);

    @GET("venter/complaints/{complaintId}/upvote")
    Call<Venter.Complaint> upVote(@Header("Cookie") String sessionId, @Path("complaintId") String complaintId, @Query("action") int count);

    @GET("venter/complaints/{complaintId}/subscribe")
    Call<Venter.Complaint> subscribetoComplaint(@Header("Cookie") String sessionId, @Path("complaintId") String complaintId, @Query("action") int count);

    @POST("venter/complaints")
    Call<ComplaintCreateResponse> postComplaint(@Header("Cookie") String sessionId, @Body ComplaintCreateRequest complaintCreateRequest);

    @POST("venter/complaints/{complaintId}/comments")
    Call<Venter.Comment> postComment(@Header("Cookie") String sessionId, @Path("complaintId") String commentId, @Body CommentCreateRequest commentCreateRequest);

    @PUT("venter/comments/{commentId}")
    Call<Venter.Comment> updateComment(@Header("Cookie") String sessionId, @Path("commentId") String commentId, @Body CommentCreateRequest commentCreateRequest);

    @DELETE("venter/comments/{commentId}")
    Call<String> deleteComment(@Header("Cookie") String sessionId, @Path("commentId") String commentId);

    @GET("venter/tags")
    Call<List<Venter.TagUri>> getTags(@Header("Cookie") String sessionID);

    @POST("query")
    Call<QuestionCreateResponse> postQuestion(@Header("Cookie") String sessionId, @Body QuestionCreateRequest questionCreateRequest);

    @GET("query")
    Call<List<SearchDataPost>> getSearchFeed(@Header("Cookie") String sessionID, @Query("from") int from, @Query("num") int num, @Query("query") String query);
}
