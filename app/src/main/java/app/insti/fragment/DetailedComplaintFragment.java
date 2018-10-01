package app.insti.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cunoraz.tagview.TagView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.insti.R;
import app.insti.activity.MainActivity;
import app.insti.adapter.CommentRecyclerViewAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.request.CommentCreateRequest;
import app.insti.api.model.User;
import app.insti.api.model.Venter;
import app.insti.utils.DateTimeUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailedComplaintFragment extends Fragment {

    private final String TAG = DetailedComplaintFragment.class.getSimpleName();
    private LayoutInflater inflater;
    private Venter.Complaint detailedComplaint;
    private MapView mMapView;
    private GoogleMap googleMap;

    private TextView textViewUserName;
    private TextView textViewReportDate;
    private TextView textViewLocation;
    private TextView textViewDescription;
    private TextView textViewCommentLabel;
    private TextView textViewVoteUpLabel;
    private TextView textViewStatus;
    private LinearLayout layoutVotes;
    private TagView tagView;
    private EditText editTextComment;
    private ImageButton imageButtonSend;
    private CircleImageView circleImageViewCommentUserImage;
    private RecyclerView recyclerViewComments;

    private static String sId, cId, uId;
    private CommentRecyclerViewAdapter commentListAdapter;
    private List<Venter.Comment> commentList;


    public static DetailedComplaintFragment getInstance(String sessionid, String complaintid, String userid) {
        sId = sessionid;
        cId = complaintid;
        uId = userid;
        return new DetailedComplaintFragment();
    }

    private void initialiseViews(View view) {
        textViewUserName = view.findViewById(R.id.textViewUserName);
        Log.i(TAG, "@@@@@@@@@@ textViewUser: "+textViewUserName);
        textViewReportDate = view.findViewById(R.id.textViewReportDate);
        textViewLocation = view.findViewById(R.id.textViewLocation);
        textViewDescription = view.findViewById(R.id.textViewDescription);
        textViewStatus = view.findViewById(R.id.textViewStatus);
        textViewCommentLabel = view.findViewById(R.id.comment_label);
        textViewVoteUpLabel = view.findViewById(R.id.up_vote_label);
        layoutVotes = view.findViewById(R.id.layoutVotes);
        tagView = view.findViewById(R.id.tag_group);
        recyclerViewComments = view.findViewById(R.id.recyclerViewComments);
        editTextComment = view.findViewById(R.id.edit_comment);
        imageButtonSend = view.findViewById(R.id.send_comment);
        circleImageViewCommentUserImage = view.findViewById(R.id.comment_user_image);
        Picasso.get().load(R.drawable.baseline_account_circle_black_48).into(circleImageViewCommentUserImage);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detailed_complaint, container, false);
        this.inflater = inflater;
        commentList = new ArrayList<>();
        initialiseViews(view);

        commentListAdapter = new CommentRecyclerViewAdapter(getActivity(),getContext(), sId, uId);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewComments.setLayoutManager(linearLayoutManager);
        recyclerViewComments.setHasFixedSize(true);
        recyclerViewComments.setAdapter(commentListAdapter);

//        commentListAdapter.setUpdateCommentElements(imageButtonSend, editTextComment);

        mMapView = view.findViewById(R.id.google_map);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(editTextComment.getText().toString().trim().isEmpty())) {
                    postComment();
                } else {
                    Toast.makeText(getContext(), "Please enter comment text", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void postComment() {
        final CommentCreateRequest commentCreateRequest = new CommentCreateRequest(editTextComment.getText().toString());
        RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
        retrofitInterface.postComment("sessionid=" + sId, cId, commentCreateRequest).enqueue(new Callback<Venter.Comment>() {
            @Override
            public void onResponse(Call<Venter.Comment> call, Response<Venter.Comment> response) {
                if (response.isSuccessful()) {
                    Venter.Comment comment = response.body();
                    addNewComment(comment);
                    editTextComment.setText(null);
                    Toast.makeText(getActivity(), "Comment Added", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Venter.Comment> call, Throwable t) {
                Log.i(TAG, "@@@@@@@@@@@@@@@@@ failure in posting comments: " + t.toString());
            }
        });
    }

    private void addNewComment(Venter.Comment newComment) {
        commentList.add(newComment);
        commentListAdapter.setCommentList(commentList);
        commentListAdapter.notifyItemInserted(commentList.indexOf(newComment));
        commentListAdapter.notifyItemRangeChanged(0,commentListAdapter.getItemCount());
        recyclerViewComments.post(new Runnable() {
            @Override
            public void run() {
                MainActivity.hideKeyboard(getActivity());
            }
        });
    }

    public void setDetailedComplaint(Venter.Complaint detailedComplaint) {
        this.detailedComplaint = detailedComplaint;
        populateViews();
    }

    private void populateViews() {
        try {
            textViewUserName.setText(detailedComplaint.getComplaintCreatedBy().getUserName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String time = DateTimeUtil.getDate(detailedComplaint.getComplaintReportDate().toString());
            Log.i(TAG, "@@@@@@@@@@@@ time: " + time);
            Log.i(TAG, "@@@@@@@@@@@@inside try");
            textViewReportDate.setText(time);

        } catch (Exception e) {
            Log.i(TAG, "@@@@@@@@@@@@@@@@@Inside catch");
            e.printStackTrace();
        }
        try {
            textViewLocation.setText(detailedComplaint.getLocationDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            textViewDescription.setText(detailedComplaint.getDescription());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            textViewStatus.setText(detailedComplaint.getStatus().toUpperCase());
            if (detailedComplaint.getStatus().equalsIgnoreCase("Reported")) {
                textViewStatus.setBackgroundColor(Color.parseColor("#FF0000"));
                textViewStatus.setTextColor(getResources().getColor(R.color.primaryTextColor));
            } else if (detailedComplaint.getStatus().equalsIgnoreCase("In Progress")) {
                textViewStatus.setBackgroundColor(getResources().getColor(R.color.colorSecondary));
                textViewStatus.setTextColor(getResources().getColor(R.color.secondaryTextColor));
            } else if (detailedComplaint.getStatus().equalsIgnoreCase("Resolved")) {
                textViewStatus.setBackgroundColor(Color.parseColor("#00FF00"));
                textViewStatus.setTextColor(getResources().getColor(R.color.secondaryTextColor));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            textViewCommentLabel.setText("COMMENTS");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            textViewVoteUpLabel.setText("UP VOTES");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            addVotesToView(detailedComplaint);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            addCommentsToView(detailedComplaint);
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> tags = new ArrayList<>();
        for (Venter.TagUri tagUri : detailedComplaint.getTags()) {
            tags.add(tagUri.getTagUri());
        }

        String[] tagArray = tags.toArray(new String[tags.size()]);
        tagView.addTags(tagArray);

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mGoogleMap) {
                googleMap = mGoogleMap;

                // For dropping a marker at a point on the Map
                LatLng loc = new LatLng(detailedComplaint.getLatitude(), detailedComplaint.getLongitude());
                if (loc != null) {
                    googleMap.addMarker(new MarkerOptions().position(loc).title(detailedComplaint.getLatitude().toString() + " , " + detailedComplaint.getLongitude().toString()).snippet(detailedComplaint.getLocationDescription()));
                    // For zooming automatically to the location of the marker
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(16).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
    }

    private void addCommentsToView(Venter.Complaint detailedComplaint) {
        for (Venter.Comment comment : detailedComplaint.getComment())
            commentList.add(comment);
        commentListAdapter.setCommentList(commentList);
        commentListAdapter.notifyDataSetChanged();
    }

    public void addVotesToView(Venter.Complaint detailedComplaint) {

        layoutVotes.removeAllViews();

        for (User users : detailedComplaint.getUsersUpVoted()) {
            View voteView = inflater.inflate(R.layout.vote_up_card, null);
            CircleImageView circleImageView = voteView.findViewById(R.id.circleImageViewUserUpVoteImage);
            String profileUrl = users.getUserProfilePictureUrl();
            if (profileUrl != null)
                Picasso.get().load(profileUrl).into(circleImageView);
            else
                Picasso.get().load(R.drawable.baseline_account_circle_black_48).into(circleImageView);
            TextView textViewName = voteView.findViewById(R.id.textViewUserUpVoteName);
            textViewName.setText(users.getUserName());

            layoutVotes.addView(voteView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}
