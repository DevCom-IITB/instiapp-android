package app.insti.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import app.insti.Constants;
import app.insti.R;
import app.insti.adapter.RoleAdapter;
import app.insti.api.model.Role;

/**
 * A simple {@link Fragment} subclass..
 * Use the {@link RoleRecyclerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoleRecyclerViewFragment extends Fragment implements TransitionTargetFragment, TransitionTargetChild {
    public static final String TAG = "RoleRecyclerViewFragment";
    public Fragment parentFragment = null;

    private List<Role> roleList;

    public RoleRecyclerViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void transitionEnd() {
        if (parentFragment instanceof TransitionTargetFragment) {
            ((TransitionTargetFragment) parentFragment).transitionEnd();
        }
    }

    @Override
    public Fragment getParent() {
        return parentFragment;
    }

    // TODO: Rename and change types and number of parameters
    public static RoleRecyclerViewFragment newInstance(List<Role> roleList) {
        RoleRecyclerViewFragment fragment = new RoleRecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ROLE_LIST_JSON, new Gson().toJson(roleList));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            roleList = new Gson().fromJson(getArguments().getString(Constants.ROLE_LIST_JSON), new TypeToken<List<Role>>() {
            }.getType());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.role_recycler_view);
        RoleAdapter roleAdapter = new RoleAdapter(roleList, this);
        roleAdapter.uid = "RRVFrag";
        recyclerView.setAdapter(roleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_role_recycler_view, container, false);
    }


}