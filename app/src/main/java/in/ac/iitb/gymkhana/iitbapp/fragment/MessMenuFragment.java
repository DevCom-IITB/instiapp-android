package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.ActivityBuffer;
import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.adapter.MessMenuAdapter;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.data.AppDatabase;
import in.ac.iitb.gymkhana.iitbapp.data.HostelMessMenu;
import in.ac.iitb.gymkhana.iitbapp.data.MessMenu;
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
                    displayMenu("Tansa");
                else
                    displayMenu(Integer.toString(i + 1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (hostel.equals("Tansa"))
            hostelSpinner.setSelection(16);
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
        Toast.makeText(getContext(), "Could not retrieve your hostel menu. Please contact your mess secretary/councillor.", Toast.LENGTH_LONG).show();
        return null;
    }

    private void displayMessMenu(HostelMessMenu hostelMessMenu) {
        final MessMenuAdapter messMenuAdapter = new MessMenuAdapter(hostelMessMenu.getMessMenus());
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
            displayMessMenu(hostelMessMenu);
        }
    }
}
