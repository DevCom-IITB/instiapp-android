package app.insti.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.insti.R;
import app.insti.fragment.RecyclerViewFragment;
import app.insti.interfaces.Clickable;
import app.insti.interfaces.ItemClickListener;
import app.insti.interfaces.Readable;
import app.insti.interfaces.Writable;

public abstract class RecyclerViewAdapter<T extends Clickable> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Readable<T>, Writable<T> {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<T> posts;
    protected ItemClickListener itemClickListener;

    public RecyclerViewAdapter(List<T> posts, ItemClickListener itemClickListener) {
        this.posts = posts;
        this.itemClickListener = itemClickListener;
        this.setHasStableIds(true);
    }

    @Override
    public List<T> getPosts() {
        return posts;
    }

    @Override
    public void setPosts(List<T> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        if (viewType == VIEW_ITEM) {
            return getViewHolder(parent, context);
        } else {
            LayoutInflater inflater = LayoutInflater.from(context);
            View loadView = inflater.inflate(R.layout.blog_load_item, parent, false);
            return new ProgressViewHolder(loadView);
        }
    }

    abstract RecyclerView.ViewHolder getViewHolder(@NonNull ViewGroup parent, Context context);

    @Override
    public int getItemViewType(int position) {
        return posts.size() > position ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public long getItemId(int position) {
        if (position < posts.size()) {
            return posts.get(position).getId().hashCode();
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return RecyclerViewFragment.showLoader ? (posts.size() + 1) : posts.size();
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.blog_load_item);
        }
    }
}
