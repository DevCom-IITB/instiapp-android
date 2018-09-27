package app.insti.api;

import java.util.List;

import app.insti.api.model.CommentCreateRequest;
import app.insti.api.model.ComplaintCreateRequest;
import app.insti.api.model.ComplaintCreateResponse;
import app.insti.data.Venter;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by Shivam Sharma on 05-09-2018.
 */

public interface ComplaintAPI {

    @GET("complaints")
    Call<List<Venter.Complaint>> getAllComplaints(
            @Header("Cookie") String sessionId
    );

    @GET("complaints?filter=me")
    Call<List<Venter.Complaint>> getUserComplaints(
            @Header("Cookie") String sessionId
    );

    @GET("complaints/{complaintId}")
    Call<Venter.Complaint> getComplaint(
            @Header("Cookie") String sessionId,
            @Path("complaintId") String complaintId
    );

    @PUT("complaints/{complaintId}")
    Call<Venter.Complaint> upVote(
            @Header("Cookie") String sessionId,
            @Path("complaintId") String complaintId
    );

    @POST("complaints")
    Call<ComplaintCreateResponse> postComplaint(
            @Header("Cookie") String sessionId,
            @Body ComplaintCreateRequest complaintCreateRequest
    );

    @POST("complaints/{complaintId}/comments")
    Call<Venter.Comment> postComment(
            @Header("Cookie") String sessionId,
            @Path("complaintId") String commentId,
            @Body CommentCreateRequest commentCreateRequest
    );

    @PUT("comments/{commentId}")
    Call<Venter.Comment> updateComment(
            @Header("Cookie") String sessionId,
            @Path("commentId") String commentId,
            @Body CommentCreateRequest commentCreateRequest
    );

    @DELETE("comments/{commentId}")
    Call<String> deleteComment(
            @Header("Cookie") String sessionId,
            @Path("commentId") String commentId
    );
}
