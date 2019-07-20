package app.insti.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import app.insti.Constants;
import app.insti.R;
import app.insti.adapter.GenericAdapter;
import app.insti.adapter.RoleAdapter;
import app.insti.api.model.Role;
import app.insti.interfaces.CardInterface;

/**
 * A simple {@link Fragment} subclass..
 * Use the {@link GenericRecyclerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenericRecyclerViewFragment extends Fragment implements TransitionTargetFragment, TransitionTargetChild {
    public static final String TAG = "GenericRecyclerViewFragment";
    public Fragment parentFragment = null;

    private List<CardInterface> cardInterfaces;

    public GenericRecyclerViewFragment() {
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
    public static GenericRecyclerViewFragment newInstance(List<CardInterface> cardInterfaces) {
        GenericRecyclerViewFragment fragment = new GenericRecyclerViewFragment();
        Bundle args = new Bundle();

        /* Serialize the objects */
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(cardInterfaces);
            so.flush();
            args.putByteArray(Constants.ROLE_LIST_JSON, bo.toByteArray());
        } catch (Exception e) {
            Log.wtf("profile", e);
        }

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            try {
                byte b[] = getArguments().getByteArray(Constants.ROLE_LIST_JSON);
                ByteArrayInputStream bi = new ByteArrayInputStream(b);
                ObjectInputStream si = new ObjectInputStream(bi);
                cardInterfaces = (List<CardInterface>) si.readObject();
            } catch (Exception e) {
                Log.wtf("profile", e);
                cardInterfaces = new ArrayList<>();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.role_recycler_view);
        GenericAdapter genericAdapter = new GenericAdapter(cardInterfaces, this);
        genericAdapter.uid = "GRVFrag";
        recyclerView.setAdapter(genericAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_role_recycler_view, container, false);
    }


}