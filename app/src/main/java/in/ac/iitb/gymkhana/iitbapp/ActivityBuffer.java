package in.ac.iitb.gymkhana.iitbapp;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/** A class which maintains a list of transactions to occur when Context becomes available. */
public final class ActivityBuffer {

    /** A class which defines operations to execute once there's an available Context. */
    public interface IRunnable {
        /** Executes when there's an available Context. Ideally, will it operate immediately. */
        void run(final Activity pActivity);
    }

    /* Member Variables. */
    private       Activity        mActivity;
    private final List<IRunnable> mRunnables;

    /** Constructor. */
    public ActivityBuffer() {
        // Initialize Member Variables.
        this.mActivity  = null;
        this.mRunnables = new ArrayList<IRunnable>();
    }

    /** Executes the Runnable if there's an available Context. Otherwise, defers execution until it becomes available. */
    public final void safely(final IRunnable pRunnable) {
        // Synchronize along the current instance.
        synchronized(this) {
            // Do we have a context available?
            if(this.isContextAvailable()) {
                // Fetch the Activity.
                final Activity lActivity = this.getActivity();
                // Execute the Runnable along the Activity.
                lActivity.runOnUiThread(new Runnable() { @Override public final void run() { pRunnable.run(lActivity); } });
            }
            else {
                // Buffer the Runnable so that it's ready to receive a valid reference.
                this.getRunnables().add(pRunnable);
            }
        }
    }

    /** Called to inform the ActivityBuffer that there's an available Activity reference. */
    public final void onContextGained(final Activity pActivity) {
        // Synchronize along ourself.
        synchronized(this) {
            // Update the Activity reference.
            this.setActivity(pActivity);
            // Are there any Runnables awaiting execution?
            if(!this.getRunnables().isEmpty()) {
                // Iterate the Runnables.
                for(final IRunnable lRunnable : this.getRunnables()) {
                    // Execute the Runnable on the UI Thread.
                    pActivity.runOnUiThread(new Runnable() { @Override public final void run() {
                        // Execute the Runnable.
                        lRunnable.run(pActivity);
                    } });
                }
                // Empty the Runnables.
                this.getRunnables().clear();
            }
        }
    }

    /** Called to inform the ActivityBuffer that the Context has been lost. */
    public final void onContextLost() {
        // Synchronize along ourself.
        synchronized(this) {
            // Remove the Context reference.
            this.setActivity(null);
        }
    }

    /** Defines whether there's a safe Context available for the ActivityBuffer. */
    public final boolean isContextAvailable() {
        // Synchronize upon ourself.
        synchronized(this) {
            // Return the state of the Activity reference.
            return (this.getActivity() != null);
        }
    }

    /* Getters and Setters. */
    private final void setActivity(final Activity pActivity) {
        this.mActivity = pActivity;
    }

    private final Activity getActivity() {
        return this.mActivity;
    }

    private final List<IRunnable> getRunnables() {
        return this.mRunnables;
    }

}