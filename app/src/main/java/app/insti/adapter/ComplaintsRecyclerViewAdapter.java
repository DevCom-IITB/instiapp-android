package app.insti.adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import app.insti.R;
import app.insti.Utils;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Venter;
import app.insti.fragment.ComplaintDetailsFragment;
import app.insti.utils.DateTimeUtil;
import app.insti.utils.GsonProvider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Shivam Sharma on 15-08-2018.
 */

public class ComplaintsRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private Activity context;
    private String sessionID;
    private String userID;
    private static final String TAG = ComplaintsRecyclerViewAdapter.class.getSimpleName();
    List<Venter.Complaint> complaintList = new ArrayList<>();

    public class ComplaintsViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView textViewDescription;
        private ImageButton buttonComments;
        private ImageButton buttonVotes;
        private TextView textViewComments;
        private TextView textViewVotes;
        private TextView textViewLocation;
        private TextView textViewUserName;
        private TextView textViewReportDate;
        private TextView textViewStatus;

        private int pos;
        private int voteCount = 0;

        public ComplaintsViewHolder(View currentView) {
            super(currentView);
            cardView = currentView.findViewById(R.id.cardView);
            textViewUserName = currentView.findViewById(R.id.textViewUserName);
            textViewStatus = currentView.findViewById(R.id.textViewStatus);
            textViewReportDate = currentView.findViewById(R.id.textViewReportDate);
            textViewLocation = currentView.findViewById(R.id.textViewLocation);
            textViewDescription = currentView.findViewById(R.id.textViewDescription);
            buttonComments = currentView.findViewById(R.id.buttonComments);
            buttonVotes = currentView.findViewById(R.id.buttonVotes);
            textViewComments = currentView.findViewById(R.id.text_comments);
            textViewVotes = currentView.findViewById(R.id.text_votes);
        }

        public void bindHolder(final int position) {
            this.pos = position;
            Log.i(TAG, "json = " + GsonProvider.getGsonOutput().toJson(complaintList.get(pos)));
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", complaintList.get(pos).getComplaintID());
                    bundle.putString("sessionId", sessionID);
                    bundle.putString("userId", userID);
                    ComplaintDetailsFragment complaintDetailsFragment = new ComplaintDetailsFragment();
                    complaintDetailsFragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) context;
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_for_fragment, complaintDetailsFragment).addToBackStack(TAG).commit();
                }
            });

            Venter.Complaint complaint = complaintList.get(position);
            try {
                textViewDescription.setText(complaint.getDescription());
                textViewLocation.setText(complaint.getLocationDescription());
                textViewUserName.setText(complaint.getComplaintCreatedBy().getUserName());
                textViewStatus.setText(complaint.getStatus().toUpperCase());
                if (complaint.getStatus().equalsIgnoreCase("Reported")) {
                    textViewStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorRed)));
                    textViewStatus.setTextColor(context.getResources().getColor(R.color.primaryTextColor));
                } else if (complaint.getStatus().equalsIgnoreCase("In Progress")) {
                    textViewStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorSecondary)));
                    textViewStatus.setTextColor(context.getResources().getColor(R.color.secondaryTextColor));
                } else if (complaint.getStatus().equalsIgnoreCase("Resolved")) {
                    textViewStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorGreen)));
                    textViewStatus.setTextColor(context.getResources().getColor(R.color.secondaryTextColor));
                }
                String time = DateTimeUtil.getDate(complaint.getComplaintReportDate());
                Log.i(TAG, "time: " + time);
                textViewReportDate.setText(time);
                textViewComments.setText(String.valueOf(complaint.getComment().size()));
                textViewVotes.setText(String.valueOf(complaint.getUsersUpVoted().size()));
                buttonComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", complaintList.get(pos).getComplaintID());
                        bundle.putString("sessionId", sessionID);
                        bundle.putString("userId", userID);
                        ComplaintDetailsFragment complaintDetailsFragment = new ComplaintDetailsFragment();
                        complaintDetailsFragment.setArguments(bundle);
                        AppCompatActivity activity = (AppCompatActivity) context;
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_for_fragment, complaintDetailsFragment).addToBackStack(TAG).commit();
                    }
                });
                buttonVotes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (voteCount == 0) {
                            RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
                            retrofitInterface.upVote("sessionid=" + sessionID, complaintList.get(pos).getComplaintID()).enqueue(new Callback<Venter.Complaint>() {
                                @Override
                                public void onResponse(Call<Venter.Complaint> call, Response<Venter.Complaint> response) {
                                    if (response.isSuccessful()) {
                                        Venter.Complaint complaint = response.body();
                                        if (complaint != null) {
                                            textViewVotes.setText(String.valueOf(complaint.getUsersUpVoted().size()));
                                        }
                                        Toast.makeText(context, "You have Up Voted this complaint", Toast.LENGTH_SHORT).show();
                                        voteCount++;
                                    }
                                }

                                @Override
                                public void onFailure(Call<Venter.Complaint> call, Throwable t) {
                                    Log.i(TAG, "failure in up vote: " + t.toString());
                                }
                            });
                        } else {
                            Toast.makeText(context, "You have already UpVoted this complaint", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ComplaintsRecyclerViewAdapter(Activity ctx, String sessionID, String userID) {
        this.context = ctx;
        this.sessionID = sessionID;
        this.userID = userID;
        inflater = LayoutInflater.from(ctx);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(R.layout.complaint_card, parent, false);
        return new ComplaintsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof ComplaintsViewHolder) {
            ((ComplaintsViewHolder) viewHolder).bindHolder(position);
        }
    }

    @Override
    public int getItemCount() {
        return complaintList.size();
    }

    public void setcomplaintList(List<Venter.Complaint> list) {
        this.complaintList = list;
    }
}