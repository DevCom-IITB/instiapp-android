package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;

import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.MainActivity;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.data.AppDatabase;
import in.ac.iitb.gymkhana.iitbapp.data.Body;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BodyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BodyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    private AppDatabase appDatabase;
    String TAG = "BodyFragment";

    // TODO: Rename and change types of parameters
    private Body min_body;
    private SwipeRefreshLayout bodySwipeRefreshLayout;


    public BodyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param arg_body Body for details
     * @return A new instance of fragment BodyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BodyFragment newInstance(Body arg_body) {
        BodyFragment fragment = new BodyFragment();
        Bundle args = new Bundle();
        args.putString(Constants.BODY_JSON, new Gson().toJson(arg_body));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            min_body = new Gson().fromJson(getArguments().getString(Constants.BODY_JSON), Body.class);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        /* Initialize */
        appDatabase = AppDatabase.getAppDatabase(getContext());

        Body[] inLocalDb = appDatabase.dbDao().getBody(min_body.getBodyID());
        if (inLocalDb.length > 0) {
            displayBody(inLocalDb[0]);
        } else {
            updateBody();
        }
        bodySwipeRefreshLayout=getActivity().findViewById(R.id.body_swipe_refresh_layout);
        bodySwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateBody();
            }
        });
    }

    private void updateBody() {
        RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
        retrofitInterface.getBody(((MainActivity)getActivity()).getSessionIDHeader(), min_body.getBodyID()).enqueue(new Callback<Body>() {
            @Override
            public void onResponse(Call<Body> call, Response<Body> response) {
                if (response.isSuccessful()) {
                    Body body = response.body();

                    appDatabase.dbDao().insertBody(body);

                    displayBody(body);
                    bodySwipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<Body> call, Throwable t) {
                bodySwipeRefreshLayout.setRefreshing(false);
                // Network Error
            }
        });
    }

    private void displayBody(Body body) {
        TextView bodyName = (TextView) getView().findViewById(R.id.body_name);
        TextView bodyDescription = (TextView) getView().findViewById(R.id.body_description);

        bodyName.setText(body.getBodyName());
        bodyDescription.setText(body.getBodyDescription());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_body, container, false);
    }

}
