package app.insti.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import androidx.appcompat.widget.Toolbar;

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
    private SharedPreferences.Editor editor;
    private SwitchPreferenceCompat showContactPref;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Get preferences and editor
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();

        // Show contact number
        showContactPref = (SwitchPreferenceCompat) findPreference("show_contact");
        showContactPref.setOnPreferenceChangeListener((preference, option) -> {
            toggleShowContact((SwitchPreferenceCompat) preference, option);
            return false;
        });
        showContactPref.setEnabled(false);

        // Dark Theme
        SwitchPreferenceCompat darkThemePref = (SwitchPreferenceCompat) findPreference("dark_theme");
        darkThemePref.setOnPreferenceChangeListener((preference, option) -> {
            toggleDarkTheme((boolean) option);
            return true;
        });
        darkThemePref.setChecked(sharedPref.getBoolean(Constants.DARK_THEME, false));

        // Add to Calendar
        ListPreference calendarPref = (ListPreference) findPreference("add_to_calendar");
        calendarPref.setOnPreferenceChangeListener((preference, option) -> {
            toggleCalendarDialog((String) option);
            return true;
        });
        calendarPref.setValue(sharedPref.getString(Constants.CALENDAR_DIALOG, Constants.CALENDAR_DIALOG_ALWAYS_ASK));

        // Update Profile
        Preference profilePref = findPreference("profile");
        profilePref.setOnPreferenceClickListener(preference -> {
            openWebURL("https://gymkhana.iitb.ac.in/sso/user");
            return false;
        });

        // Feedback
        Preference feedbackPref = findPreference("feedback");
        feedbackPref.setOnPreferenceClickListener(preference -> {
            openWebURL("https://insti.app/feedback");
            return false;
        });

        // About
        Preference aboutPref = findPreference("about");
        aboutPref.setOnPreferenceClickListener(preference -> {
            openAbout();
            return false;
        });

        // Logout
        Preference logoutPref = findPreference("logout");
        logoutPref.setOnPreferenceClickListener(preference -> {
            logout();
            return false;
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

    private void toggleShowContact(final SwitchPreferenceCompat showContactPref, Object option) {
        final boolean isChecked = (boolean) option;
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

    private void toggleDarkTheme(boolean option) {
        editor.putBoolean(Constants.DARK_THEME, option);
        editor.commit();
        Utils.changeTheme(this, option);
    }

    private void toggleCalendarDialog(String option) {
        // Using strings.xml values for populating ListPreference. `option` comes from strings.xml
        // Using Constants for updating SharedPrefs. `choice` comes from Constants.
        String choice = Constants.CALENDAR_DIALOG_ALWAYS_ASK;

        if (option.equals(getString(R.string.calendar_yes))) choice = Constants.CALENDAR_DIALOG_YES;
        else if (option.equals(getString(R.string.calendar_no))) choice = Constants.CALENDAR_DIALOG_NO;

        editor.putString(Constants.CALENDAR_DIALOG, choice);
        editor.commit();
    }

    private void openAbout() {
        Utils.updateFragment(new AboutFragment(), getActivity());
    }

    private void logout() {
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
