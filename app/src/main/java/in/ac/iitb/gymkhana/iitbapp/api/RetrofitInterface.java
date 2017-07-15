package in.ac.iitb.gymkhana.iitbapp.api;

import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.EventCreateResponse;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginRequest;
import in.ac.iitb.gymkhana.iitbapp.api.model.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {
    @POST("/login/")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST("/createEvent/")
    Call<EventCreateResponse> eventCreate(@Body EventCreateRequest eventCreateRequest);
}
