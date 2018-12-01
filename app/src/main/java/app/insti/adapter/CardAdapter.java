package app.insti.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import app.insti.R;
import app.insti.Utils;
import app.insti.interfaces.CardInterface;

public abstract class CardAdapter<T extends CardInterface> extends RecyclerView.Adapter<CardAdapter<T>.ViewHolder> {

    private List<T> tList;
    private Fragment mFragment;

    public void onClick(T t, FragmentActivity fragmentActivity) {};
    public void onClick(T t, Fragment fragment, View view) {}

    public String getBigImageUrl(T t) {
        return null;
    }

    public int getAvatarPlaceholder(T t) {
        return 0;
    }

    public CardAdapter(List<T> tList, Fragment fragment) {
        this.tList = tList;
        mFragment = fragment;
        this.setHasStableIds(true);
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.feed_card, viewGroup, false);

        final ViewHolder postViewHolder = new ViewHolder(postView);
        postView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CardAdapter.this.onClick(tList.get(postViewHolder.getAdapterPosition()), mFragment.getActivity());
                CardAdapter.this.onClick(tList.get(postViewHolder.getAdapterPosition()), mFragment, view);
            }
        });

        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        T t = tList.get(i);
        viewHolder.title.setText(t.getTitle());
        viewHolder.subtitle.setText(t.getSubtitle());

        // Set transition names
        viewHolder.avatar.setTransitionName(Integer.toString((int) t.getId()) + "_sharedAvatar");

        if (getBigImageUrl(t) != null) {
            // Show big image, hide avatar
            viewHolder.bigPicture.setVisibility(View.VISIBLE);
            viewHolder.bigPicture.setTransitionName(Integer.toString((int) t.getId()) + "_sharedBigPicture");
            viewHolder.avatar.setVisibility(View.GONE);

            // Load big image with low resolution as avatar
            Utils.loadImageWithPlaceholder(viewHolder.bigPicture, getBigImageUrl(t));
        } else {
            // Build basic request
            RequestCreator requestCreator;
            if (t.getAvatarUrl() != null)
                requestCreator = Picasso.get().load(Utils.resizeImageUrl(t.getAvatarUrl()));
            else if (getAvatarPlaceholder(t) != 0) {
                requestCreator = Picasso.get().load(getAvatarPlaceholder(t));
            } else {
                return;
            }

            // Check if we have a placeholder
            if (getAvatarPlaceholder(t) != 0) {
                requestCreator.placeholder(getAvatarPlaceholder(t));
            }

            // Load the image
            requestCreator.into(viewHolder.avatar);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView title;
        private TextView subtitle;
        private ImageView bigPicture;

        public ViewHolder(View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.object_picture);
            title = itemView.findViewById(R.id.object_title);
            subtitle = itemView.findViewById(R.id.object_subtitle);
            bigPicture = itemView.findViewById(R.id.big_object_picture);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 1;
        else return 2;
    }

    @Override
    public long getItemId(int position) {
        return tList.get(position).getId();
    }

    @Override
    public int getItemCount() {
        return tList.size();
    }

    public void setList(List<T> tList) {
        this.tList = tList;
    }
}
