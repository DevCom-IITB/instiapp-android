package app.insti.fragment;


import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.Fragment;

import app.insti.ActivityBuffer;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment {

    /* Member Variables. */
    private ActivityBuffer mActivityBuffer;

    public BaseFragment() {
        // Implement the Parent.
        super();
        // Allocate the ActivityBuffer.
        this.mActivityBuffer = new ActivityBuffer();
    }

    @Override
    public final void onAttach(final Context pContext) {
        // Handle as usual.
        super.onAttach(pContext);
        // Is the Context an Activity?
        if (pContext instanceof Activity) {
            // Cast Accordingly.
            final Activity lActivity = (Activity) pContext;
            // Inform the ActivityBuffer.
            this.getActivityBuffer().onContextGained(lActivity);
        }
    }

    @Deprecated
    @Override
    public final void onAttach(final Activity pActivity) {
        // Handle as usual.
        super.onAttach(pActivity);
        // Inform the ActivityBuffer.
        this.getActivityBuffer().onContextGained(pActivity);
    }

    @Override
    public final void onDetach() {
        // Handle as usual.
        super.onDetach();
        // Inform the ActivityBuffer.
        this.getActivityBuffer().onContextLost();
    }

    /* Getters. */
    public final ActivityBuffer getActivityBuffer() {
        return this.mActivityBuffer;
    }

}