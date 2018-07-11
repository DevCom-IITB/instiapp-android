package app.insti.fragment;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import app.insti.ActivityBuffer;
import app.insti.Constants;
import app.insti.R;
import app.insti.adapter.MessMenuAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.data.AppDatabase;
import app.insti.data.HostelMessMenu;
import app.insti.data.MessMenu;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessMenuFragment extends BaseFragment {

    private RecyclerView messMenuRecyclerView;
    private SwipeRefreshLayout messMenuSwipeRefreshLayout;
    private AppDatabase appDatabase;
    private Spinner hostelSpinner;


    public MessMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mess_menu, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Mess Menu");

        final String hostel = (String) getArguments().get(Constants.USER_HOSTEL);
        displayMenu(hostel);

        messMenuSwipeRefreshLayout = getActivity().findViewById(R.id.mess_menu_swipe_refresh_layout);
        messMenuSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateMessMenu(hostel);
            }
        });

        hostelSpinner = getActivity().findViewById(R.id.hostel_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.hostels_array, R.layout.hostel_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hostelSpinner.setAdapter(adapter);
        hostelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 16)
                    displayMenu("tansa");
                else if (i == 17)
                    displayMenu("qip");
                else
                    displayMenu(Integer.toString(i + 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (hostel.equals("tansa"))
            hostelSpinner.setSelection(16);
        else if (hostel.equals("qip"))
            hostelSpinner.setSelection(17);
        else
            hostelSpinner.setSelection(Integer.parseInt(hostel) - 1);
    }

    private void displayMenu(final String hostel) {
        appDatabase = AppDatabase.getAppDatabase(getContext());
        new showMessMenuFromDB().execute(hostel);

        updateMessMenu(hostel);
    }

    private void updateMessMenu(final String hostel) {
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getInstituteMessMenu("sessionid=" + getArguments().getString(Constants.SESSION_ID)).enqueue(new Callback<List<HostelMessMenu>>() {
            @Override
            public void onResponse(Call<List<HostelMessMenu>> call, Response<List<HostelMessMenu>> response) {
                if (response.isSuccessful()) {
                    List<HostelMessMenu> instituteMessMenu = response.body();
                    HostelMessMenu hostelMessMenu = findMessMenu(instituteMessMenu, hostel);
                    displayMessMenu(hostelMessMenu);

                    new updateDatabase().execute(instituteMessMenu);
                }
                //Server Error
                messMenuSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<HostelMessMenu>> call, Throwable t) {
                //Network Error
                messMenuSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private HostelMessMenu findMessMenu(List<HostelMessMenu> hostelMessMenus, String hostel) {
        for (HostelMessMenu hostelMessMenu : hostelMessMenus) {
            if (hostelMessMenu.getShortName().equals(hostel)) {
                return hostelMessMenu;
            }
        }
        return null;
    }

    private void displayMessMenu(HostelMessMenu hostelMessMenu) {
        /* Skip if we're already destroyed */
        if (getActivity() == null || getView() == null) return;

        List<MessMenu> messMenus = hostelMessMenu.getMessMenus();

        /* Sort by day starting today
         * This could have been done in a much simpler way with Java 8 :(
         * Don't try to fix this */
        final List<MessMenu> sortedMenus = new ArrayList();
        final Calendar calendar = Calendar.getInstance(Locale.UK);
        int today = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (today == -1) { today = 6; }

        for (int i = 0; i < 7; i++) {
            final int day = (today + i) % 7 + 1;
            for(MessMenu menu : messMenus) {
                if(menu.getDay() == day) {
                    sortedMenus.add(menu);
                }
            }
        }

        final MessMenuAdapter messMenuAdapter = new MessMenuAdapter(sortedMenus);
        getActivityBuffer().safely(new ActivityBuffer.IRunnable() {
            @Override
            public void run(Activity pActivity) {
                try {
                    messMenuRecyclerView = getActivity().findViewById(R.id.mess_menu_recycler_view);
                    messMenuRecyclerView.setAdapter(messMenuAdapter);
                    messMenuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    private class updateDatabase extends AsyncTask<List<HostelMessMenu>, Void, Integer> {
        @Override
        protected Integer doInBackground(List<HostelMessMenu>... menus) {
            appDatabase.dbDao().deleteHostelMessMenus();
            appDatabase.dbDao().insertHostelMessMenus(menus[0]);
            return 1;
        }
    }

    public class showMessMenuFromDB extends AsyncTask<String, Void, HostelMessMenu> {

        @Override
        protected HostelMessMenu doInBackground(String... strings) {
            return findMessMenu(appDatabase.dbDao().getAllHostelMessMenus(), strings[0]);
        }

        @Override
        protected void onPostExecute(HostelMessMenu hostelMessMenu) {
            if (hostelMessMenu != null)
                displayMessMenu(hostelMessMenu);
        }
    }
}
