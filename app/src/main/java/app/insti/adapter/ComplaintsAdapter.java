package app.insti.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private LinearLayout linearLayoutSuggestions;
    private LinearLayout linearLayoutLocationDetails;
    private TextView textViewDescription;
    private TextView textViewSuggestions;
    private TextView textViewlocationDetails;
    private TextView textViewLocation;
    private TextView textViewUserName;
    private TextView textViewReportDate;
    private TextView textViewStatus;

    public class ComplaintsViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private ImageButton buttonComments;
        private ImageButton buttonVotes;
        private ImageButton notificationson;
        private ImageButton notificationsoff;
        private TextView textViewVotes;
        private TextView textViewComments;
        private int pos;

        public ComplaintsViewHolder(View currentView) {
            super(currentView);
            cardView = currentView.findViewById(R.id.cardView);
            textViewUserName = currentView.findViewById(R.id.textViewUserName);
            textViewStatus = currentView.findViewById(R.id.textViewStatus);
            textViewReportDate = currentView.findViewById(R.id.textViewReportDate);
            textViewLocation = currentView.findViewById(R.id.textViewLocation);
            linearLayoutSuggestions = currentView.findViewById(R.id.linearLayoutSuggestions);
            linearLayoutLocationDetails = currentView.findViewById(R.id.linearLayoutLocationDetails);
            textViewDescription = currentView.findViewById(R.id.textViewDescription);
            textViewSuggestions = currentView.findViewById(R.id.textViewSuggestions);
            textViewlocationDetails = currentView.findViewById(R.id.textViewLocationDetails);
            buttonComments = currentView.findViewById(R.id.buttonComments);
            buttonVotes = currentView.findViewById(R.id.buttonVotes);
            notificationsoff = currentView.findViewById(R.id.buttonnotificationsoff);
            notificationson = currentView.findViewById(R.id.buttonnotificationson);
            textViewComments = currentView.findViewById(R.id.text_comments);
            textViewVotes = currentView.findViewById(R.id.text_votes);
        }

        private void bindHolder(final int position) {
            this.pos = position;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Venter.Complaint detailedComplaint = complaintList.get(pos);
                    getComplaint(detailedComplaint);
                }
            });

            try {
                populateViews(pos, notificationson, notificationsoff, textViewVotes, textViewComments);
                buttonComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Venter.Complaint detailedComplaint = complaintList.get(pos);
                        getComplaint(detailedComplaint);

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
                                    Log.i(TAG, "failure in subscribe: " + t.toString());
                                }
                            });
                        }
                    }
                });
                notificationsoff.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Venter.Complaint detailedComplaint = complaintList.get(pos);
                        subscribeToComplaint(detailedComplaint, notificationsoff, notificationson);
                    }
                });
                notificationson.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Venter.Complaint detailedComplaint = complaintList.get(pos);
                        subscribeToComplaint(detailedComplaint, notificationsoff, notificationson);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void populateViews(int pos, ImageButton notificationson, ImageButton notificationsoff, TextView textViewVotes, TextView textViewComments) {
        try {
            textViewLocation.setText(complaintList.get(pos).getLocationDescription());
            textViewUserName.setText(complaintList.get(pos).getComplaintCreatedBy().getUserName());
            textViewStatus.setText(complaintList.get(pos).getStatus().toUpperCase());
            if (complaintList.get(pos).getStatus().equalsIgnoreCase("Reported")) {
                textViewStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorRed)));
                textViewStatus.setTextColor(context.getResources().getColor(R.color.primaryTextColor));
            } else if (complaintList.get(pos).getStatus().equalsIgnoreCase("In Progress")) {
                textViewStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorSecondary)));
                textViewStatus.setTextColor(context.getResources().getColor(R.color.secondaryTextColor));
            } else if (complaintList.get(pos).getStatus().equalsIgnoreCase("Resolved")) {
                textViewStatus.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorGreen)));
                textViewStatus.setTextColor(context.getResources().getColor(R.color.secondaryTextColor));
            }
            String time = DateTimeUtil.getDate(complaintList.get(pos).getComplaintReportDate());
            textViewReportDate.setText(time);
            textViewDescription.setText(complaintList.get(pos).getDescription());
            if (complaintList.get(pos).getComplaintsubscribed() == 1){
                notificationson.setVisibility(View.VISIBLE);
                notificationsoff.setVisibility(View.GONE);
            }else if (complaintList.get(pos).getComplaintsubscribed() == 0){
                notificationson.setVisibility(View.GONE);
                notificationsoff.setVisibility(View.VISIBLE);
            }
            textViewVotes.setText(String.valueOf(complaintList.get(pos).getUsersUpVoted().size()));
            textViewComments.setText(String.valueOf(complaintList.get(pos).getComment().size()));
            if (!(complaintList.get(pos).getComplaintSuggestions().equals(""))){
                linearLayoutSuggestions.setVisibility(View.VISIBLE);
                textViewSuggestions.setText(complaintList.get(pos).getComplaintSuggestions());
            }
            if (!(complaintList.get(pos).getComplaintLocationDetails().equals(""))){
                linearLayoutLocationDetails.setVisibility(View.VISIBLE);
                textViewlocationDetails.setText(complaintList.get(pos).getComplaintLocationDetails());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getComplaint(Venter.Complaint detailedComplaint) {
        Bundle bundle = new Bundle();
        bundle.putString("id", detailedComplaint.getComplaintID());
        bundle.putString("sessionId", sessionID);
        bundle.putString("userId", userID);
        bundle.putString("userProfileUrl", userProfileUrl);
        ComplaintFragment complaintFragment = new ComplaintFragment();
        complaintFragment.setArguments(bundle);
        AppCompatActivity activity = (AppCompatActivity) context;
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_for_fragment, complaintFragment).addToBackStack(TAG).commit();
    }

    private void subscribeToComplaint(final Venter.Complaint detailedComplaint, final ImageButton notificationsoff, final ImageButton notificationson) {
        final RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        if (detailedComplaint.getComplaintsubscribed() == 1) {
            AlertDialog.Builder unsubscribe = new AlertDialog.Builder(context);
            unsubscribe.setMessage("Are you sure you want to unsubscribe to this complaint?");
            unsubscribe.setCancelable(true);

            unsubscribe.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            retrofitInterface.subscribetoComplaint("sessionid=" + sessionID, detailedComplaint.getComplaintID(), 0).enqueue(new Callback<Venter.Complaint>() {
                                @Override
                                public void onResponse(Call<Venter.Complaint> call, Response<Venter.Complaint> response) {
                                    if (response.isSuccessful()) {
                                        notificationson.setVisibility(View.GONE);
                                        notificationsoff.setVisibility(View.VISIBLE);
                                        detailedComplaint.setComplaintsubscribed(0);
                                        Toast.makeText(context, "You have been unsubscribed from this complaint!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Venter.Complaint> call, Throwable t) {
                                    Log.i(TAG, "failure in subscribe: " + t.toString());
                                }
                            });
                        }
                    });

            unsubscribe.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = unsubscribe.create();
            alert11.show();
        } else if (detailedComplaint.getComplaintsubscribed() == 0){
            retrofitInterface.subscribetoComplaint("sessionid=" + sessionID, detailedComplaint.getComplaintID(), 1).enqueue(new Callback<Venter.Complaint>() {
                @Override
                public void onResponse(Call<Venter.Complaint> call, Response<Venter.Complaint> response) {
                    if (response.isSuccessful()) {
                        notificationson.setVisibility(View.VISIBLE);
                        notificationsoff.setVisibility(View.GONE);
                        detailedComplaint.setComplaintsubscribed(1);
                        Toast.makeText(context, "You have been subscribed to this complaint!",
                                Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Venter.Complaint> call, Throwable t) {
                    Log.i(TAG, "failure in subscribe: " + t.toString());
                }
            });
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

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setcomplaintList(List<Venter.Complaint> list) {
        this.complaintList = list;
    }
}