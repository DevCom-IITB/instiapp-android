package app.insti.adapter;

import android.util.Log;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.insti.Constants;
import app.insti.R;
import app.insti.Utils;
import app.insti.api.model.Event;
import app.insti.api.model.PlacementBlogPost;
import app.insti.api.model.SearchDataPost;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import app.insti.fragment.RecyclerViewFragment;
import app.insti.interfaces.CardInterface;
import app.insti.interfaces.ItemClickListener;
import app.insti.interfaces.SearchDataInterface;
import app.insti.utils.BodyHeadCard;
import app.insti.utils.BodyHeadViewHolder;

public class SearchAdapter extends SearchCardAdapter<SearchDataPost> {


//    public void onClick(Event event, final Fragment fragment, View view) {
//        int picId = R.id.object_picture;
//
//        Utils.openEventFragment(event, fragment, view.findViewById(picId));
//    }

    public SearchAdapter(List<SearchDataPost> List) {
        super(List);
    }


    @Override
    protected RecyclerView.ViewHolder getViewHolder(@NonNull ViewGroup parent, Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View postView = inflater.inflate(R.layout.search_post_card, parent, false);

        final SearchAdapter.ViewHolder postViewHolder = new SearchAdapter.ViewHolder(postView);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder recyclerHolder, int position) {
        Log.d("tag-position", String.valueOf(position));
        if (recyclerHolder instanceof ViewHolder) {
            Log.d("tag-position", String.valueOf(position));
            SearchAdapter.ViewHolder holder = (SearchAdapter.ViewHolder) recyclerHolder;
            SearchDataPost post = getPosts().get(position);
            holder.postTitle.setText(post.getTitle());
            holder.postDescription.setText(post.getDescription());
        } else {
            ((RecyclerViewAdapter.ProgressViewHolder) recyclerHolder).progressBar.setIndeterminate(true);
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView postTitle;
        private TextView postDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            Log.d("View", itemView.findViewById(R.id.search_data_title).toString());
            postTitle = (TextView) itemView.findViewById(R.id.search_data_title);
            postDescription = itemView.findViewById(R.id.search_data_description);
            postDescription.setMovementMethod(LinkMovementMethod.getInstance());

        }
    }

}