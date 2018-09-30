package app.insti.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.insti.R;
import app.insti.api.model.Venter;
import app.insti.fragment.ComplaintDetailsFragment;
import app.insti.utils.GsonProvider;

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
        private Button buttonComments;
        private Button buttonVotes;
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
        }

        public void bindHolder(int position) {
            this.pos = position;
            Log.i(TAG, "@@@@@@@@@@@@json = " + GsonProvider.getGsonOutput().toJson(complaintList.get(pos)));
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                textViewLocation.setText(complaint.getLocationDescription());

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                textViewUserName.setText(complaint.getComplaintCreatedBy().getUserName());

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                textViewStatus.setText(complaint.getStatus().toUpperCase());
                if (complaint.getStatus().equalsIgnoreCase("Reported")) {
                    textViewStatus.setBackgroundColor(Color.parseColor("#FF0000"));
                    textViewStatus.setTextColor(context.getResources().getColor(R.color.primaryTextColor));
                } else if (complaint.getStatus().equalsIgnoreCase("In Progress")) {
                    textViewStatus.setBackgroundColor(context.getResources().getColor(R.color.colorSecondary));
                    textViewStatus.setTextColor(context.getResources().getColor(R.color.secondaryTextColor));
                } else if (complaint.getStatus().equalsIgnoreCase("Resolved")) {
                    textViewStatus.setBackgroundColor(Color.parseColor("#00FF00"));
                    textViewStatus.setTextColor(context.getResources().getColor(R.color.secondaryTextColor));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                textViewReportDate.setText(complaint.getComplaintReportDate().toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                buttonComments.setText("COMMENTS(" + complaint.getComment().size() + ")");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                buttonVotes.setText("UP VOTES(" + complaint.getUsersUpVoted().size() + ")");
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
        ComplaintsViewHolder complaintsViewHolder = new ComplaintsViewHolder(view);
        return complaintsViewHolder;
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
