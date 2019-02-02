package app.insti;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.transition.Fade;
import android.support.transition.Slide;
import android.support.transition.Transition;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import app.insti.activity.MainActivity;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Body;
import app.insti.api.model.Event;
import app.insti.api.model.Notification;
import app.insti.api.model.User;
import app.insti.fragment.BodyFragment;
import app.insti.fragment.EventFragment;
import app.insti.fragment.TransitionTargetChild;
import app.insti.fragment.TransitionTargetFragment;
import app.insti.fragment.UserFragment;

public final class Utils {
    public static UpdatableList<Event> eventCache = new UpdatableList<>();
    public static UpdatableList<Notification> notificationCache = null;
    public static UpdatableList<Body> bodyCache = new UpdatableList<>();

    private static String sessionId;
    private static RetrofitInterface retrofitInterface;
    public static Gson gson;
    public static boolean darkTheme = false;

    public static final void loadImageWithPlaceholder(final ImageView imageView, final String url) {
        Picasso.get()
                .load(resizeImageUrl(url))
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Picasso.get()
                                .load(url)
                                .placeholder(imageView.getDrawable())
                                .into(imageView);
                    }

                    @Override
                    public void onError(Exception ex) {
                        // Do nothing
                    }
                });
    }

    public static final String resizeImageUrl(String url) {
        return resizeImageUrl(url, 200);
    }

    public static final String resizeImageUrl(String url, Integer dim) {
        if (url == null) {
            return url;
        }
        return url.replace("api.insti.app/static/", "img.insti.app/static/" + dim.toString() + "/");
    }

    /**
     * Update the open fragment
     */
    public static final void updateFragment(Fragment fragment, FragmentActivity fragmentActivity) {
        FragmentTransaction ft = fragmentActivity.getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        ft.replace(R.id.framelayout_for_fragment, fragment, fragment.getTag());
        ft.addToBackStack(fragment.getTag());
        ft.commit();
    }

    public static void updateSharedElementFragment(final Fragment fragment, final Fragment currentFragment, Map<View, String> sharedElements) {
        FragmentTransaction ft = currentFragment.getActivity().getSupportFragmentManager().beginTransaction();

        Transition transition = new DetailsTransition();

        /* Set up transitions */
        fragment.setSharedElementEnterTransition(transition);
        fragment.setEnterTransition(new Slide());
        currentFragment.setExitTransition(new Fade());
        fragment.setSharedElementReturnTransition(transition);

        /* Set transition for parent in case it is a child */
        if (currentFragment instanceof TransitionTargetChild) {
            ((TransitionTargetChild) currentFragment).getParent().setExitTransition(new Fade());
        }

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                if (fragment instanceof TransitionTargetFragment) {
                    ((TransitionTargetFragment) fragment).transitionEnd();
                }

                if (currentFragment instanceof TransitionTargetFragment) {
                    ((TransitionTargetFragment) currentFragment).transitionEnd();
                }
            }

            @Override
            public void onTransitionCancel(Transition transition) {}

            @Override
            public void onTransitionPause(Transition transition) {}

            @Override
            public void onTransitionResume(Transition transition) {}
        });

        /* Add all shared elements */
        for (Map.Entry<View, String> entry : sharedElements.entrySet()) {
            ft.addSharedElement(entry.getKey(), entry.getValue());
        }

        /* Update the fragment */
        ft.replace(R.id.framelayout_for_fragment, fragment, fragment.getTag())
                .addToBackStack(fragment.getTag())
                .commit();
    }

    public static BodyFragment getBodyFragment(Body body, boolean sharedElements) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BODY_JSON, new Gson().toJson(body));
        bundle.putBoolean(Constants.NO_SHARED_ELEM, !sharedElements);
        BodyFragment bodyFragment = new BodyFragment();
        bodyFragment.setArguments(bundle);
        return bodyFragment;
    }

    public static void openBodyFragment(Body body, FragmentActivity fragmentActivity) {
        updateFragment(getBodyFragment(body, false), fragmentActivity);
    }

    public static void openBodyFragment(Body body, Fragment currentFragment, View sharedAvatar) {
        Map<View, String> sharedElements = new HashMap<>();
        sharedElements.put(sharedAvatar, "sharedAvatar");
        updateSharedElementFragment(
                getBodyFragment(body, true), currentFragment, sharedElements
        );
    }

    public static EventFragment getEventFragment(Event event, boolean sharedElements) {
        String eventJson = new Gson().toJson(event);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EVENT_JSON, eventJson);
        bundle.putBoolean(Constants.NO_SHARED_ELEM, !sharedElements);
        EventFragment eventFragment = new EventFragment();
        eventFragment.setArguments(bundle);
        return eventFragment;
    }

    public static void openEventFragment(Event event, FragmentActivity fragmentActivity) {
        updateFragment(getEventFragment(event, false), fragmentActivity);
    }

    public static void openEventFragment(Event event, Fragment currentFragment, View sharedAvatar) {
        Map<View, String> sharedElements = new HashMap<>();
        sharedElements.put(sharedAvatar, "sharedAvatar");
        updateSharedElementFragment(
                getEventFragment(event, true), currentFragment, sharedElements
        );
    }

    public static void openUserFragment(User user, FragmentActivity fragmentActivity) {
        openUserFragment(user.getUserID(), fragmentActivity);
    }

    public static void openUserFragment(User user, Fragment currentFragment, View sharedAvatar) {
        Map<View, String> sharedElements = new HashMap<>();
        sharedElements.put(sharedAvatar, "sharedAvatar");
        updateSharedElementFragment(
                UserFragment.newInstance(user), currentFragment, sharedElements
        );
    }

    public static void openUserFragment(String userId, FragmentActivity fragmentActivity) {
        updateFragment(UserFragment.newInstance(userId), fragmentActivity);
    }

    public static void setSessionId(String sessionId1) {
        sessionId = sessionId1;
    }

    public static String getSessionIDHeader() {
        return "sessionid=" + sessionId;
    }

    public static RetrofitInterface getRetrofitInterface() {
        return retrofitInterface;
    }

    public static void setRetrofitInterface(RetrofitInterface retrofitInterface) {
        Utils.retrofitInterface = retrofitInterface;
    }

    public static void openWebURL(Context context, String URL) {
        if (URL != null && !URL.isEmpty()) {
            Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
            context.startActivity(browse);
        }
    }

    public static void makeGson() {
        Utils.gson = new Gson();
    }

    public static void changeTheme(FragmentActivity context) {
        if (darkTheme) {
            context.setTheme(R.style.AppTheme);
        } else {
            Toast.makeText(context, "You have unlocked super max pro mode", Toast.LENGTH_SHORT).show();
            context.setTheme(R.style.AppThemeDark);
        }
        darkTheme = !darkTheme;
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        context.finish();
    }

    public static void setSelectedMenuItem(Activity activity, int id) {
        if (activity != null) {
            NavigationView navigationView = activity.findViewById(R.id.nav_view);
            if (navigationView != null) {
                navigationView.setCheckedItem(id);
            }
        }
    }

    @RequiresApi(21)
    public static void clearCookies(Context context) {
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
    }
}
