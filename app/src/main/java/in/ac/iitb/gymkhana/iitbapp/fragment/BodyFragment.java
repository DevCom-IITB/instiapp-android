package in.ac.iitb.gymkhana.iitbapp.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import in.ac.iitb.gymkhana.iitbapp.Constants;
import in.ac.iitb.gymkhana.iitbapp.ItemClickListener;
import in.ac.iitb.gymkhana.iitbapp.MainActivity;
import in.ac.iitb.gymkhana.iitbapp.R;
import in.ac.iitb.gymkhana.iitbapp.ShareURLMaker;
import in.ac.iitb.gymkhana.iitbapp.adapter.BodyAdapter;
import in.ac.iitb.gymkhana.iitbapp.adapter.EventAdapter;
import in.ac.iitb.gymkhana.iitbapp.api.RetrofitInterface;
import in.ac.iitb.gymkhana.iitbapp.api.ServiceGenerator;
import in.ac.iitb.gymkhana.iitbapp.data.AppDatabase;
import in.ac.iitb.gymkhana.iitbapp.data.Body;
import in.ac.iitb.gymkhana.iitbapp.data.Event;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.noties.markwon.Markwon;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BodyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BodyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_BODY = "body";

    private AppDatabase appDatabase;
    String TAG = "BodyFragment";

    // TODO: Rename and change types of parameters
    private Body min_body;


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
        args.putString(ARG_BODY, new Gson().toJson(arg_body));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            min_body = new Gson().fromJson(getArguments().getString(ARG_BODY), Body.class);
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
                }
            }

            @Override
            public void onFailure(Call<Body> call, Throwable t) {
                // Network Error
            }
        });
    }

    private void displayBody(final Body body) {
        TextView bodyName = (TextView) getView().findViewById(R.id.body_name);
        TextView bodyDescription = (TextView) getView().findViewById(R.id.body_description);
        ImageView eventPicture = (ImageView) getActivity().findViewById(R.id.body_picture);
        ImageButton webBodyButton = getActivity().findViewById(R.id.web_body_button);
        ImageButton shareBodyButton = getActivity().findViewById(R.id.share_body_button);
        final Button followButton = getActivity().findViewById(R.id.follow_button);

        /* Set body information */
        bodyName.setText(body.getBodyName());
        Markwon.setMarkdown(bodyDescription, body.getBodyDescription());
        Picasso.with(getContext()).load(body.getBodyImageURL()).into(eventPicture);

        /* Check if user is already following
         * Initialize follow button */
        followButton.setBackgroundColor(getResources().getColor(body.getBodyUserFollows() ? R.color.colorAccent : R.color.colorWhite));

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RetrofitInterface retrofitInterface = ServiceGenerator.createService(RetrofitInterface.class);
                retrofitInterface.updateBodyFollowing(((MainActivity) getActivity()).getSessionIDHeader(), body.getBodyID(), body.getBodyUserFollows() ? 0:1).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            body.setBodyUserFollows(!body.getBodyUserFollows());
                            new updateDbBody().execute(body);
                            followButton.setBackgroundColor(getResources().getColor(body.getBodyUserFollows() ? R.color.colorAccent : R.color.colorWhite));
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Network Error", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        /* Initialize web button */
        if (body.getBodyWebsiteURL() != null && !body.getBodyWebsiteURL().isEmpty())
        {
            webBodyButton.setVisibility(View.VISIBLE);
            webBodyButton.setOnClickListener(new View.OnClickListener() {
                String bodywebURL = body.getBodyWebsiteURL();
                @Override
                public void onClick(View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(bodywebURL));
                    startActivity(browserIntent);
                }
            });
        }

        /* Initialize share button */
        shareBodyButton.setOnClickListener(new View.OnClickListener() {
            String shareUrl = ShareURLMaker.getBodyURL(body);
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, shareUrl);
                startActivity(Intent.createChooser(i, "Share URL"));
            }
        });

        /* Initialize events */
        final List<Event> eventList = body.getBodyEvents();
        RecyclerView eventRecyclerView = (RecyclerView) getActivity().findViewById(R.id.event_card_recycler_view);
        EventAdapter eventAdapter = new EventAdapter(eventList, new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Event event = eventList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString(Constants.EVENT_JSON, new Gson().toJson(event));
                EventFragment eventFragment = new EventFragment();
                eventFragment.setArguments(bundle);
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.framelayout_for_fragment, eventFragment, eventFragment.getTag());
                ft.addToBackStack(eventFragment.getTag());
                ft.commit();
            }
        });
        eventRecyclerView.setAdapter(eventAdapter);
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private class updateDbBody extends AsyncTask<Body, Void, Integer> {
        @Override
        protected Integer doInBackground(Body... body) {
            appDatabase.dbDao().updateBody(body[0]);
            return 1;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_body, container, false);
    }

}
