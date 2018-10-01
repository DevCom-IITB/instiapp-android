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
import app.insti.api.model.Body;
import app.insti.api.model.Role;
import app.insti.interfaces.ItemClickListener;


public class RoleAdapter extends RecyclerView.Adapter<RoleAdapter.ViewHolder> {

    private List<Role> roleList;
    private Context context;
    private Fragment fragment;

    public RoleAdapter(List<Role> roleList, Fragment mFragment) {
        this.roleList = roleList;
        this.fragment = mFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.feed_card, parent, false);
        final ViewHolder postViewHolder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.openBodyFragment(roleList.get(postViewHolder.getAdapterPosition()).getRoleBodyDetails(), fragment.getActivity());
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
        Picasso.get().load(
                Utils.resizeImageUrl(roleBody.getBodyImageURL())
        ).into(holder.image);

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
            bodyName = (TextView) itemView.findViewById(R.id.object_title);
            role = (TextView) itemView.findViewById(R.id.object_subtitle);
            image = (ImageView) itemView.findViewById(R.id.object_picture);
        }


    }
}
