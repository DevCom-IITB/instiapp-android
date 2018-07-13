package app.insti.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import app.insti.ItemClickListener;
import app.insti.R;
import app.insti.data.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> userList;
    private ItemClickListener itemClickListener;
    private Context context;

    public UserAdapter(List<User> userList, ItemClickListener itemClickListener) {
        this.userList = userList;
        this.itemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.role_card, parent, false);
        final ViewHolder postViewHolder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClick(view, postViewHolder.getAdapterPosition());
            }
        });

        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.userName.setText(user.getUserName());
        holder.role.setText(user.getCurrentRole());
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName;
        public TextView role;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            userName = (TextView) itemView.findViewById(R.id.role_card_body);
            role = (TextView) itemView.findViewById(R.id.role_card_role);
            image = (ImageView) itemView.findViewById(R.id.role_card_avatar);
        }
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
