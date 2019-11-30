package app.insti.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import app.insti.Constants;
import app.insti.R;
import app.insti.Utils;
import app.insti.adapter.MessMenuAdapter;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.HostelMessMenu;
import app.insti.api.model.MessMenu;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessMenuFragment extends BaseFragment {

    private List<HostelMessMenu> instituteMessMenu = null;
    private MessMenuAdapter messMenuAdapter;
    private SwipeRefreshLayout messMenuSwipeRefreshLayout;
    private String hostel;
    private Boolean initialized = false;
    List<String> hostels = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

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
        Utils.setSelectedMenuItem(getActivity(), R.id.nav_mess_menu);
        setupAdapter();
        hostel = (String) getArguments().get(Constants.USER_HOSTEL);

        messMenuSwipeRefreshLayout = getActivity().findViewById(R.id.mess_menu_swipe_refresh_layout);
        messMenuSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateMessMenu(hostel);
            }
        });

        updateMessMenu(null);
    }

    private void displayMenu(final String hostel) {
        if (instituteMessMenu != null) {
            HostelMessMenu hostelMessMenu = findMessMenu(instituteMessMenu, hostel);
            if(hostelMessMenu != null)
                displayMessMenu(hostelMessMenu);
        } else {
            updateMessMenu(hostel);
        }
    }

    private void initialize() {
        initialized = true;

        Spinner hostelSpinner = getActivity().findViewById(R.id.hostel_spinner);
        for (HostelMessMenu hmm : instituteMessMenu) {
            hostels.add(hmm.getName());
        }

        spinnerAdapter = new ArrayAdapter(getContext(), R.layout.hostel_spinner_item, hostels.toArray());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        hostelSpinner.setAdapter(spinnerAdapter);
        hostelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                hostel = instituteMessMenu.get(i).getShortName();
                displayMenu(hostel);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        for (int i = 0; i < instituteMessMenu.size(); i++) {
            if (instituteMessMenu.get(i).getShortName().equals(hostel)) {
                hostelSpinner.setSelection(i);
            }
        }
    }

    private void updateMessMenu(final String hostel) {
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        retrofitInterface.getInstituteMessMenu(Utils.getSessionIDHeader()).enqueue(new Callback<List<HostelMessMenu>>() {
            @Override
            public void onResponse(Call<List<HostelMessMenu>> call, Response<List<HostelMessMenu>> response) {
                if (response.isSuccessful()) {
                    if (getActivity() == null || getView() == null || response.body() == null) return;

                    instituteMessMenu = response.body();
                    if (!initialized) {
                        initialize();
                    } else {
                        displayMenu(hostel);
                    }
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

        final List<MessMenu> sortedMenus = hostelMessMenu.getSortedMessMenus();

        /* Display */
        notifyChange(sortedMenus);
        getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);
    }

    private void setupAdapter() {
        if (messMenuAdapter == null) {
            messMenuAdapter = new MessMenuAdapter(new ArrayList<MessMenu>());
        }
        RecyclerView messMenuRecyclerView = getActivity().findViewById(R.id.mess_menu_recycler_view);
        messMenuRecyclerView.setAdapter(messMenuAdapter);
        messMenuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void notifyChange(final List<MessMenu> menus) {
        messMenuAdapter.setMenu(menus);
        messMenuAdapter.notifyDataSetChanged();
    }
}
