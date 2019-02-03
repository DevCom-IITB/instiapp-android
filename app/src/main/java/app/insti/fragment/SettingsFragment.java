package app.insti.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.support.v7.widget.Toolbar;

import app.insti.Constants;
import app.insti.R;
import app.insti.SessionManager;
import app.insti.Utils;
import app.insti.activity.LoginActivity;
import app.insti.api.EmptyCallback;
import app.insti.api.RetrofitInterface;
import app.insti.api.model.User;
import app.insti.api.request.UserShowContactPatchRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends PreferenceFragmentCompat {
    private SwitchPreferenceCompat showContactPref;
    private SwitchPreferenceCompat darkThemePref;
    private Preference profilePref;
    private Preference feedbackPref;
    private Preference aboutPref;
    private Preference logoutPref;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Get preferences and editor
        sharedPref = getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        // Show contact number
        showContactPref = (SwitchPreferenceCompat) findPreference("show_contact");
        showContactPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                toggleShowContact((SwitchPreferenceCompat) preference, o);
                return false;
            }
        });
        showContactPref.setEnabled(false);

        // Dark Theme
        darkThemePref = (SwitchPreferenceCompat) findPreference("dark_theme");
        darkThemePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                toggleDarkTheme((SwitchPreferenceCompat) preference, o);
                return true;
            }
        });
        darkThemePref.setChecked(sharedPref.getBoolean(Constants.DARK_THEME, false));

        // Update Profile
        profilePref = findPreference("profile");
        profilePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openWebURL("https://gymkhana.iitb.ac.in/sso/user");
                return false;
            }
        });

        // Feedback
        feedbackPref = findPreference("feedback");
        feedbackPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openWebURL("https://insti.app/feedback");
                return false;
            }
        });

        // About
        aboutPref = findPreference("about");
        aboutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                openAbout();
                return false;
            }
        });

        // Logout
        logoutPref = findPreference("logout");
        logoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                logout();
                return false;
            }
        });

        // Disable buttons if not logged in
        final SessionManager sessionManager = new SessionManager(getContext());
        if (!sessionManager.isLoggedIn()) {
            showContactPref.setVisible(false);
            logoutPref.setVisible(false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set toolbar title
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        Utils.setSelectedMenuItem(getActivity(), R.id.nav_settings);

        if (Utils.currentUserCache == null) {
            // Get the user id
            Bundle bundle = getArguments();
            String userID = bundle.getString(Constants.USER_ID);

            // Fill in the user
            RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
            retrofitInterface.getUser(Utils.getSessionIDHeader(), userID).enqueue(new EmptyCallback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        if (getActivity() == null || getView() == null) return;
                        User user = response.body();
                        showContactPref.setChecked(user.getShowContactNumber());
                        showContactPref.setEnabled(true);
                        Utils.currentUserCache = user;
                    }
                }
            });
        } else {
            showContactPref.setChecked(Utils.currentUserCache.getShowContactNumber());
            showContactPref.setEnabled(true);
        }
    }

    public void toggleShowContact(final SwitchPreferenceCompat showContactPref, Object o) {
        final boolean isChecked = (boolean) o;
        showContactPref.setEnabled(false);
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        retrofitInterface.patchUserMe(Utils.getSessionIDHeader(), new UserShowContactPatchRequest(isChecked)).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(getActivity() == null || getView() == null) return;
                if (response.isSuccessful()) {
                    showContactPref.setChecked(response.body().getShowContactNumber());
                    showContactPref.setEnabled(true);
                    Utils.currentUserCache = response.body();
                } else {
                    showContactPref.setChecked(!isChecked);
                    showContactPref.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                if(getActivity() == null || getView() == null) return;
                showContactPref.setChecked(!isChecked);
                showContactPref.setEnabled(true);
            }
        });
    }

    public void toggleDarkTheme(final SwitchPreferenceCompat showContactPref, Object o) {
        editor.putBoolean(Constants.DARK_THEME, (boolean) o);
        editor.commit();
        Utils.changeTheme(this, (boolean) o);
    }

    public void openAbout() {
        Utils.updateFragment(new AboutFragment(), getActivity());
    }

    public void logout() {
        final SessionManager sessionManager = new SessionManager(getContext());
        RetrofitInterface retrofitInterface = Utils.getRetrofitInterface();
        retrofitInterface.logout(Utils.getSessionIDHeader()).enqueue(new EmptyCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    sessionManager.logout();
                    Utils.clearCookies(getActivity());
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }

    private void openWebURL(String URL) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(URL));
        startActivity(browse);
    }
}
