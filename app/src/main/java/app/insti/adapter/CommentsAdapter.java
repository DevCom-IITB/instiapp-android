package app.insti.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Venter;
import app.insti.utils.DateTimeUtil;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Shivam Sharma on 23-09-2018.
 */

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = CommentsAdapter.class.getSimpleName();

    private Context context;
    private LayoutInflater inflater;
    private String userId;
    private Fragment fragment;
    private TextView textViewCommentLabel;

    private List<Venter.Comment> commentList = new ArrayList<>();

    public CommentsAdapter(Context context, String userId, Fragment fragment) {
        this.context = context;
        this.userId = userId;
        inflater = LayoutInflater.from(context);
        this.fragment =fragment;
    }

    public class CommentsViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private CircleImageView circleImageView;
        private TextView textViewName;
        private TextView textViewCommentTime;
        private TextView textViewComment;
        private final RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();

        CommentsViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewComment);
            textViewName = itemView.findViewById(R.id.textViewUserComment);
            textViewComment = itemView.findViewById(R.id.textViewComment);
            textViewCommentTime = itemView.findViewById(R.id.textViewTime);
            circleImageView = itemView.findViewById(R.id.circleImageViewUserImage);
        }

        private void bindHolder(final int position) {

            final Venter.Comment comment = commentList.get(position);
            try {
                String profileUrl = comment.getUser().getUserProfilePictureUrl();
                Picasso.get().load(profileUrl).placeholder(R.drawable.user_placeholder).into(circleImageView);
                textViewName.setText(comment.getUser().getUserName());
                String time = DateTimeUtil.getDate(comment.getTime());
                textViewCommentTime.setText(time);
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
                                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText("Text Copied", textViewComment.getText().toString());
                                    if (clipboardManager != null) {
                                        clipboardManager.setPrimaryClip(clipData);
                                    }
                                    Toast.makeText(context, "Comment Copied", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.delete_comment_option:
                                    retrofitInterface.deleteComment(Utils.getSessionIDHeader(), comment.getId()).enqueue(new EmptyCallback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            if (response.isSuccessful()) {
                                                commentList.remove(position);
                                                notifyDataSetChanged();
                                                notifyItemRemoved(position);
                                                notifyItemRangeChanged(position, commentList.size() - position);
                                                textViewCommentLabel.setText("Comments (" + commentList.size() + ")");
                                            } else {
                                                Toast.makeText(context, "You can't delete this comment", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    break;

                                default:
                                    clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    clipData = ClipData.newPlainText("Text Copied", textViewComment.getText().toString());
                                    if (clipboardManager != null) {
                                        clipboardManager.setPrimaryClip(clipData);
                                    }
                                    Toast.makeText(context, "Comment Copied", Toast.LENGTH_SHORT).show();
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

        final CommentsViewHolder commentsViewHolder = new CommentsViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.openUserFragment(commentList.get(commentsViewHolder.getAdapterPosition()).getUser(), fragment.getActivity());
            }
        });
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

    public void setCommentList(List<Venter.Comment> commentList, TextView textViewCommentLabel) {
        this.commentList = commentList;
        this.textViewCommentLabel = textViewCommentLabel;
    }
}