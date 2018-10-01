package app.insti.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import app.insti.Constants;
import app.insti.R;
import app.insti.api.model.Body;
import app.insti.fragment.BodyFragment;


public class BodyAdapter extends RecyclerView.Adapter<BodyAdapter.ViewHolder> {

    private List<Body> bodyList;
    private Context context;
    private Fragment fragment;

    public BodyAdapter(List<Body> bodyList, Fragment mFragment) {
        this.bodyList = bodyList;
        fragment = mFragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context)
                .inflate(R.layout.feed_card, parent, false);
        final ViewHolder postViewHolder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBody(bodyList.get(postViewHolder.getAdapterPosition()));
            }
        });

        return postViewHolder;
    }

    /**
     * Open body fragment for a body
     */
    private void openBody(Body body) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BODY_JSON, new Gson().toJson(body));
        BodyFragment bodyFragment = new BodyFragment();
        bodyFragment.setArguments(bundle);
        FragmentTransaction ft = fragment.getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        ft.replace(R.id.framelayout_for_fragment, bodyFragment, bodyFragment.getTag());
        ft.addToBackStack(bodyFragment.getTag());
        ft.commit();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Body body = bodyList.get(position);
        holder.name.setText(body.getBodyName());
        holder.description.setText(body.getBodyShortDescription());
        Picasso.get().load(
                Constants.resizeImageUrl(body.getBodyImageURL(), 200)
        ).into(holder.image);

    }

    @Override
    public int getItemCount() {
        return bodyList.size();
    }

    public void setBodyList(List<Body> bodyList) {
        this.bodyList = bodyList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView description;
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.object_title);
            description = (TextView) itemView.findViewById(R.id.object_subtitle);
            image = (ImageView) itemView.findViewById(R.id.object_picture);
        }
    }
}
