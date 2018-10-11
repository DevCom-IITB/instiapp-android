package app.insti.adapter;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import app.insti.R;
import app.insti.Utils;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.User;
import app.insti.api.model.Venter;
import app.insti.fragment.ComplaintFragment;
import app.insti.utils.DateTimeUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Shivam Sharma on 15-08-2018.
 */

public class ComplaintsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private Activity context;
    private String sessionID;
    private String userID;
    private String userProfileUrl;
    private static final String TAG = ComplaintsAdapter.class.getSimpleName();
    private List<Venter.Complaint> complaintList = new ArrayList<>();

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
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", complaintList.get(pos).getComplaintID());
                    bundle.putString("sessionId", sessionID);
                    bundle.putString("userId", userID);
                    bundle.putString("userProfileUrl", userProfileUrl);
                    ComplaintFragment complaintFragment = new ComplaintFragment();
                    complaintFragment.setArguments(bundle);
                    AppCompatActivity activity = (AppCompatActivity) context;
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_for_fragment, complaintFragment).addToBackStack(TAG).commit();
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
                        bundle.putString("userProfileUrl", userProfileUrl);
                        ComplaintFragment complaintFragment = new ComplaintFragment();
                        complaintFragment.setArguments(bundle);
                        AppCompatActivity activity = (AppCompatActivity) context;
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_for_fragment, complaintFragment).addToBackStack(TAG).commit();
                    }
                });
                buttonVotes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (complaintList.get(position).getVoteCount() == 0) {
                            RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
                            retrofitInterface.upVote("sessionid=" + sessionID, complaintList.get(pos).getComplaintID(), 1).enqueue(new Callback<Venter.Complaint>() {
                                @Override
                                public void onResponse(Call<Venter.Complaint> call, Response<Venter.Complaint> response) {
                                    if (response.isSuccessful()) {
                                        Venter.Complaint complaint = response.body();
                                        if (complaint != null) {
                                            textViewVotes.setText(String.valueOf(complaint.getUsersUpVoted().size()));
                                        }
                                        complaintList.get(position).setVoteCount(1);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Venter.Complaint> call, Throwable t) {
                                    Log.i(TAG, "failure in up vote: " + t.toString());
                                }
                            });
                        } else if (complaintList.get(position).getVoteCount() == 1) {
                            RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
                            retrofitInterface.upVote("sessionid=" + sessionID, complaintList.get(pos).getComplaintID(), 0).enqueue(new Callback<Venter.Complaint>() {
                                @Override
                                public void onResponse(Call<Venter.Complaint> call, Response<Venter.Complaint> response) {
                                    if (response.isSuccessful()) {
                                        Venter.Complaint complaint = response.body();
                                        if (complaint != null) {
                                            textViewVotes.setText(String.valueOf(complaint.getUsersUpVoted().size()));
                                        }
                                        complaintList.get(position).setVoteCount(0);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Venter.Complaint> call, Throwable t) {
                                    Log.i(TAG, "failure in up vote: " + t.toString());
                                }
                            });
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ComplaintsAdapter(Activity ctx, String sessionID, String userID, String userProfileUrl) {
        this.context = ctx;
        this.sessionID = sessionID;
        this.userID = userID;
        this.userProfileUrl = userProfileUrl;
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
        List<User> userList = complaintList.get(position).getUsersUpVoted();
        for (User user : userList) {
            if (user.getUserID().equals(userID))
               complaintList.get(position).setVoteCount(1);
            else
                complaintList.get(position).setVoteCount(0);
        }
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