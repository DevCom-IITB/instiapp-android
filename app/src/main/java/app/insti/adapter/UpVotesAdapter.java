package app.insti.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import app.insti.R;
import app.insti.Utils;
import app.insti.api.model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class UpVotesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = CommentsAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private Fragment fragment;
    private List<User> userList = new ArrayList<>();

    public UpVotesAdapter(Fragment fragment, Context context) {
        inflater = LayoutInflater.from(context);
        this.fragment = fragment;
    }

    public class UpVotesViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private CircleImageView circleImageView;
        private TextView textViewName;

        UpVotesViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewUpVote);
            textViewName = itemView.findViewById(R.id.textViewUserUpVoteName);
            circleImageView = itemView.findViewById(R.id.circleImageViewUserUpVoteImage);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.openUserFragment(userList.get(getAdapterPosition()), fragment.getActivity());
                }
            });
        }

        private void bindHolder(final int position) {

            final User user = userList.get(position);
            try {
                String profileUrl = user.getUserProfilePictureUrl();
                Log.i(TAG, "PROFILE URL: " + profileUrl);
                Picasso.get().load(profileUrl).placeholder(R.drawable.user_placeholder).into(circleImageView);
                textViewName.setText(user.getUserName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.vote_up_card, viewGroup, false);
        return new UpVotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof UpVotesViewHolder) {
            ((UpVotesViewHolder) viewHolder).bindHolder(i);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUpVoteList(List<User> userList) {
        this.userList = userList;
    }
}