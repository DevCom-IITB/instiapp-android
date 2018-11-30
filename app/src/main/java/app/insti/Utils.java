package app.insti;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import app.insti.activity.MainActivity;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.Body;
import app.insti.api.model.Event;
import app.insti.api.model.User;
import app.insti.fragment.BodyFragment;
import app.insti.fragment.EventFragment;
import app.insti.fragment.UserFragment;

public final class Utils {
    public static UpdatableList<Event> eventCache = new UpdatableList<>();
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

    public static void openBodyFragment(Body body, FragmentActivity fragmentActivity) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BODY_JSON, new Gson().toJson(body));
        BodyFragment bodyFragment = new BodyFragment();
        bodyFragment.setArguments(bundle);
        updateFragment(bodyFragment, fragmentActivity);
    }

    public static void openEventFragment(Event event, FragmentActivity fragmentActivity) {
        String eventJson = new Gson().toJson(event);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EVENT_JSON, eventJson);
        EventFragment eventFragment = new EventFragment();
        eventFragment.setArguments(bundle);
        updateFragment(eventFragment, fragmentActivity);
    }

    public static void openUserFragment(User user, FragmentActivity fragmentActivity) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER_ID, user.getUserID());
        UserFragment userFragment = new UserFragment();
        userFragment.setArguments(bundle);
        updateFragment(userFragment, fragmentActivity);
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
}
