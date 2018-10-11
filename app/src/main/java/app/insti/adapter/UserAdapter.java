package app.insti.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.api.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> userList;
    private Fragment fragment;

    public UserAdapter(List<User> userList, Fragment mFragment) {
        this.userList = userList;
        fragment = mFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.feed_card, parent, false);
        final ViewHolder postViewHolder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openUserFragment(userList.get(postViewHolder.getAdapterPosition()), fragment.getActivity());
            }
        });

        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getUserName());
        if (user.getCurrentRole() == null || user.getCurrentRole().equals("")) {
            holder.role.setText(user.getUserLDAPId());
        } else {
            holder.role.setText(user.getCurrentRole());
        }
        Picasso.get()
                .load(user.getUserProfilePictureUrl())
                .resize(150, 0)
                .placeholder(R.drawable.user_placeholder)
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView role;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.object_title);
            role = itemView.findViewById(R.id.object_subtitle);
            image = itemView.findViewById(R.id.object_picture);
        }
    }
}
