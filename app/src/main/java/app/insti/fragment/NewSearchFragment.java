package app.insti.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.vision.CameraSource;

import app.insti.Constants;
import app.insti.R;
import app.insti.Utils;
import app.insti.api.RetrofitInterface;
import app.insti.api.request.ComplaintCreateRequest;
import app.insti.api.request.QuestionCreateRequest;
import app.insti.api.response.ComplaintCreateResponse;
import app.insti.api.response.QuestionCreateResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewSearchFragment extends Fragment {

    public ValueCallback<Uri[]> uploadMessage;
    public static final String TAG = NewSearchFragment.class.getSimpleName();
    private ProgressDialog progressDialog;
    private boolean disableProgress = false;
    private final String host = "www.insti.app";
    private EditText editQ;
    public Button buttonSubmit;
    private String userId;

    public NewSearchFragment() {
        // Required empty public constructor
    }



    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        container.removeAllViews();
        View view = inflater.inflate(R.layout.fragment_create_search_q, container, false);

        /* Show progress dialog */
//        progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("Loading");
//        progressDialog.setCancelable(false);
//        progressDialog.show();

        editQ = view.findViewById(R.id.editQ);
        buttonSubmit = view.findViewById(R.id.buttonSubmitSearch);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitQuestion();
            }
        });
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Request for new question");
        }


        return view;
    }

    public void submitQuestion(){
        final String question = editQ.getText().toString();
        if(question.isEmpty()){
            new AlertDialog.Builder(getContext())
                    .setTitle("Question Needed")
                    .setMessage("You have not specified question.")
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(), "Submission aborted", Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                    })
                    .create()
                    .show();
        } else {
            QuestionCreateRequest questionCreateRequest = new QuestionCreateRequest(question);
            RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
            retrofitInterface.postQuestion("sessionid=" + getArguments().getString(Constants.SESSION_ID), questionCreateRequest).enqueue(new Callback<QuestionCreateResponse>() {
                @Override
                public void onResponse(Call<QuestionCreateResponse> call, Response<QuestionCreateResponse> response) {
                    Toast.makeText(getContext(), "Complaint successfully posted", Toast.LENGTH_LONG).show();
                    Bundle bundle = getArguments();
                    bundle.putString(Constants.USER_ID, userId);
                    SearchFragment searchFragment = new SearchFragment();
                    searchFragment.setArguments(bundle);
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.framelayout_for_fragment, searchFragment, searchFragment.getTag());
                    transaction.addToBackStack(searchFragment.getTag());
                    manager.popBackStackImmediate("Search Fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    transaction.commit();
                }

                @Override
                public void onFailure(Call<QuestionCreateResponse> call, Throwable t) {
                    Log.i(TAG, "failure in addComplaint: " + t.toString());
                    Toast.makeText(getContext(), "Complaint Creation Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
