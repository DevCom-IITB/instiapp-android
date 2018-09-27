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
import app.insti.api.ComplaintAPI;
import app.insti.api.ServiceGenerator;
import app.insti.data.Venter;
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
    private EditText editTextComment;
    private ImageButton imageButtonSend;

    private List<Venter.Comment> commentList = new ArrayList<>();

    public CommentRecyclerViewAdapter(Context context, String sessionId, String userId) {
        this.context = context;
        this.sessionId = sessionId;
        this.userId = userId;
        inflater = LayoutInflater.from(context);
    }

//    public void setUpdateCommentElements(ImageButton imageButtonSend, EditText editTextComment) {
//        this.editTextComment = editTextComment;
//        this.imageButtonSend = imageButtonSend;
//
//    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private CircleImageView circleImageView;
        private TextView textViewName;
        private TextView textViewCommentTime;
        private TextView textViewComment;

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
                Log.i(TAG, "@@@@@@@@@@@@@@@@@ PROFILE URL: " + profileUrl);
                if (profileUrl != null)
                    Picasso.get().load(profileUrl).into(circleImageView);
                else
                    Picasso.get().load(R.drawable.baseline_account_circle_black_48).into(circleImageView);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                textViewName.setText(comment.getUser().getUserName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                textViewCommentTime.setText(comment.getTime().toString());
            } catch (Exception e) {
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
                    final ComplaintAPI complaintAPI = ServiceGenerator.createService(ComplaintAPI.class);
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
                                    complaintAPI.deleteComment("sessionid=" + sessionId, comment.getId()).enqueue(new Callback<String>() {
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
                                            Log.i(TAG, "@@@@@@@@@ failure in deleting: " + t.toString());

                                        }
                                    });
                                    break;
//                                case R.id.edit_comment_option:
//                                    Log.i(TAG, "@@@@@@@@@@@@@@ Current User Id: "+ userId);
//                                    Log.i(TAG, "@@@@@@@@@@@@@@ Comment User Id: "+ comment.getUser().getUserID());
//                                    Log.i(TAG, "@@@@@@@@@@@@@@ !(comment.getUser().getUserID().equals(userId)): "+ !(comment.getUser().getUserID().equals(userId)));
//                                    if (!(comment.getUser().getUserID().equals(userId))) {
//                                        Toast.makeText(activity, "You can't edit this comment", Toast.LENGTH_SHORT).show();
//                                    } else {
//                                        editTextComment.setText(textViewComment.getText().toString());
//                                        imageButtonSend.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View v) {
//                                                CommentCreateRequest commentCreateRequest = new CommentCreateRequest(editTextComment.getText().toString());
//                                                complaintAPI.updateComment("sessionid=" + sessionId, comment.getId(), commentCreateRequest).enqueue(new Callback<Venter.Comment>() {
//                                                    @Override
//                                                    public void onResponse(Call<Venter.Comment> call, Response<Venter.Comment> response) {
//                                                        if (response.isSuccessful()) {
//                                                            Venter.Comment commentResponse = response.body();
//                                                            textViewComment.setText(commentResponse.getText());
//                                                            Toast.makeText(activity, "Comment Updated", Toast.LENGTH_SHORT).show();
//                                                            editTextComment.setText(null);
//                                                        } else {
//                                                            Toast.makeText(activity, "You can't edit this comment", Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onFailure(Call<Venter.Comment> call, Throwable t) {
//                                                        Log.i(TAG, "@@@@@@@@@@@@@@@ failure in editing: " + t.toString());
//                                                    }
//                                                });
//                                            }
//                                        });
//                                    }
//                                    break;
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
