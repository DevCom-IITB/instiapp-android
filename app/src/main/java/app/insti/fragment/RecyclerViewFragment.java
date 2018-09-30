package app.insti.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import app.insti.ActivityBuffer;
import app.insti.api.RetrofitInterface;
import app.insti.interfaces.Browsable;
import app.insti.interfaces.ItemClickListener;
import app.insti.R;
import app.insti.interfaces.Readable;
import app.insti.interfaces.Writable;
import app.insti.activity.MainActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public abstract class RecyclerViewFragment<T extends Browsable, S extends RecyclerView.Adapter<RecyclerView.ViewHolder> & Readable<T> & Writable<T>> extends BaseFragment {
    public static boolean showLoader = true;
    protected RecyclerView recyclerView;
    protected Class<T> postType;
    protected Class<S> adapterType;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected String searchQuery;

    protected void updateData() {
        String sessionIDHeader = ((MainActivity) getActivity()).getSessionIDHeader();
        RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
        Call<List<T>> call = getCall(retrofitInterface, sessionIDHeader);
        call.enqueue(new Callback<List<T>>() {
            @Override
            public void onResponse(Call<List<T>> call, Response<List<T>> response) {
                if (response.isSuccessful()) {
                    List<T> posts = response.body();
                    displayData(posts);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<T>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    abstract Call<List<T>> getCall(RetrofitInterface retrofitInterface, String sessionIDHeader);

    private void displayData(final List<T> result) {
        /* Skip if we're already destroyed */
        if (getActivity() == null || getView() == null) return;

        try {
            final S s = adapterType.getDeclaredConstructor(List.class, ItemClickListener.class).newInstance(result, new ItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {
                    String link = result.get(position).getLink();
                    if (link != null && !link.isEmpty())
                        openWebURL(link);
                }
            });
            getActivityBuffer().safely(new ActivityBuffer.IRunnable() {
                @Override
                public void run(Activity pActivity) {
                    recyclerView.setAdapter(s);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        boolean loading = false;

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            if (dy > 0) {
                                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                if (((layoutManager.getChildCount() + layoutManager.findFirstVisibleItemPosition()) > (layoutManager.getItemCount() - 5)) && (!loading)) {
                                    loading = true;
                                    String sessionIDHeader = ((MainActivity) getActivity()).getSessionIDHeader();
                                    RetrofitInterface retrofitInterface = ((MainActivity) getActivity()).getRetrofitInterface();
                                    Call<List<T>> call = getCall(retrofitInterface, sessionIDHeader);
                                    call.enqueue(new Callback<List<T>>() {
                                        @Override
                                        public void onResponse(Call<List<T>> call, Response<List<T>> response) {
                                            if (response.isSuccessful()) {
                                                loading = false;
                                                List<T> posts = s.getPosts();
                                                posts.addAll(response.body());
                                                if (response.body().size() == 0) {
                                                    showLoader = false;
                                                }
                                                s.setPosts(posts);
                                                s.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<List<T>> call, Throwable t) {
                                            loading = false;
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            });
            getActivity().findViewById(R.id.loadingPanel).setVisibility(GONE);
        } catch (java.lang.InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void openWebURL(String URL) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        startActivity(browse);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_view_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView sv = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setActionView(sv);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    searchQuery = null;
                    updateData();
                    showLoader = true;
                    return true;
                } else if (newText.length() >= 3) {
                    performSearch(newText);
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearch(String query) {
        searchQuery = query;
        updateData();
        showLoader = false;
    }
}
