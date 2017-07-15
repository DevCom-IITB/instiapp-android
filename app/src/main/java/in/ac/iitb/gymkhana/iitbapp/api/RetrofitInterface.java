package in.ac.iitb.gymkhana.iitbapp.api;

import in.ac.iitb.gymkhana.iitbapp.api.model.LoginRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.NewsFeedResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface RetrofitInterface {
    @POST("login/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("getNewsFeed/")
    Call<NewsFeedResponse> getNewsFeed(@Body NewsFeedRequest newsFeedRequest);
}
