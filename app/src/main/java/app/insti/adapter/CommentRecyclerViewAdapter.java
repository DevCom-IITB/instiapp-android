package app.insti.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.activity.MainActivity;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Venter;
import app.insti.utils.DateTimeUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Shivam Sharma on 23-09-2018.
 */

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = CommentRecyclerViewAdapter.class.getSimpleName();

    private Context context;
    private LayoutInflater inflater;
    private String sessionId, userId;
    private Activity activity;

    private List<Venter.Comment> commentList = new ArrayList<>();

    public CommentRecyclerViewAdapter(Activity activity, Context context, String sessionId, String userId) {
        this.context = context;
        this.sessionId = sessionId;
        this.userId = userId;
        inflater = LayoutInflater.from(context);
        this.activity = activity;
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private CircleImageView circleImageView;
        private TextView textViewName;
        private TextView textViewCommentTime;
        private TextView textViewComment;
        final RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();

        public CommentsViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewComment);
            textViewName = itemView.findViewById(R.id.textViewUserComment);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewCommentTime = itemView.findViewById(R.id.textViewTime);
            circleImageView = itemView.findViewById(R.id.circleImageViewUserImage);
        }

        public void bindHolder(final int position) {

            final Venter.Comment comment = commentList.get(position);
            try {
                String profileUrl = comment.getUser().getUserProfilePictureUrl();
                Log.i(TAG, "PROFILE URL: " + profileUrl);
                if (profileUrl != null)
                    Picasso.get().load(profileUrl).into(circleImageView);
                else
                    Picasso.get().load(R.drawable.baseline_account_circle_black_36).into(circleImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                textViewName.setText(comment.getUser().getUserName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                String time = DateTimeUtil.getDate(comment.getTime().toString());
                Log.i(TAG, "time: " + time);
                Log.i(TAG, "inside try");
                textViewCommentTime.setText(time);

            } catch (Exception e) {
                Log.i(TAG, "Inside catch");
                e.printStackTrace();
            }
            try {
                textViewComment.setText(comment.getText());
            } catch (Exception e) {
                e.printStackTrace();
            }

            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    PopupMenu popupMenu = new PopupMenu(context, cardView);
                    if (!(comment.getUser().getUserID().equals(userId))) {
                        popupMenu.inflate(R.menu.comments_options_secondary_menu);
                    } else {
                        popupMenu.inflate(R.menu.comment_options_primary_menu);
                    }
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.copy_comment_option:
                                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText("Text Copied", textViewComment.getText().toString());
                                    clipboardManager.setPrimaryClip(clipData);
                                    Toast.makeText(context, "Comment Copied", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.delete_comment_option:
                                    retrofitInterface.deleteComment("sessionid=" + sessionId, comment.getId()).enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            if (response.isSuccessful()) {
                                                Toast.makeText(context, "Comment Deleted", Toast.LENGTH_SHORT).show();
                                                commentList.remove(position);
                                                notifyDataSetChanged();
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, commentList.size() - position);
                                            } else {
                                                Toast.makeText(context, "You can't delete this comment", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {
                                            Log.i(TAG, " failure in deleting: " + t.toString());
                                        }
                                    });
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                    return true;
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.comments_card, parent, false);

        CommentsViewHolder commentsViewHolder = new CommentsViewHolder(view);
        return commentsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentsViewHolder) {
            ((CommentsViewHolder) holder).bindHolder(position);
        }
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void setCommentList(List<Venter.Comment> commentList) {
        this.commentList = commentList;
    }
}