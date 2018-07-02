package app.insti.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
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
import app.insti.data.Body;
import app.insti.data.Role;


public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.ViewHolder> {

    private List<Role> roleList;
    private ItemClickListener itemClickListener;
    private Context context;

    public RoleAdapter(List<Role> roleList, ItemClickListener itemClickListener) {
        this.roleList = roleList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
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

        Role role = roleList.get(position);
        Body roleBody = role.getRoleBodyDetails();
        holder.bodyName.setText(roleBody.getBodyName());
        holder.role.setText(role.getRoleName());
        Picasso.with(context).load(roleBody.getBodyImageURL()).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return roleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView bodyName;
        public TextView role;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            bodyName = (TextView) itemView.findViewById(R.id.role_card_body);
            role = (TextView) itemView.findViewById(R.id.role_card_role);
            image = (ImageView) itemView.findViewById(R.id.role_card_avatar);
        }


    }
}
