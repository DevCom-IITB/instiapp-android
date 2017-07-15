package in.ac.iitb.gymkhana.iitbapp.api;

import in.ac.iitb.gymkhana.iitbapp.api.model.LoginRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.NotificationsRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.NotificationsResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("login/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("getNewsFeed/")
    Call<NewsFeedResponse> getNewsFeed(@Body NewsFeedRequest newsFeedRequest);

    @POST("getNotifications/")
    Call<NotificationsResponse> getNotifications(@Body NotificationsRequest notificationsRequest);
}
