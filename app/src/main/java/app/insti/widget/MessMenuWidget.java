package app.insti.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.List;

import app.insti.R;
import app.insti.SessionManager;
import app.insti.Utils;
import app.insti.activity.MainActivity;
import app.insti.api.RetrofitInterface;
import app.insti.api.ServiceGenerator;
import app.insti.api.model.HostelMessMenu;
import app.insti.api.model.MessMenu;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Implementation of App Widget functionality.
 */
public class MessMenuWidget extends AppWidgetProvider {

    // TODO Remove duplicate code from MessMenuFragment
    private RemoteViews views;
    private List<HostelMessMenu> instituteMessMenu;

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                         int appWidgetId) {

        // Construct the RemoteViews object
        views = new RemoteViews(context.getPackageName(), R.layout.mess_menu_widget);
        SessionManager sessionManager = new SessionManager(context);
        if (sessionManager.isLoggedIn()) {
            ServiceGenerator serviceGenerator = new ServiceGenerator(context);
            RetrofitInterface retrofitInterface = serviceGenerator.getRetrofitInterface();
            retrofitInterface.getInstituteMessMenu(sessionManager.getSessionID()).enqueue(new Callback<List<HostelMessMenu>>() {
                @Override
                public void onResponse(Call<List<HostelMessMenu>> call, Response<List<HostelMessMenu>> response) {
                    if (response.isSuccessful()) {
                        instituteMessMenu = response.body();
                        displayMenu(sessionManager.getCurrentUser().getHostel());

                        // Instruct the widget manager to update the widget
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    }
                }

                @Override
                public void onFailure(Call<List<HostelMessMenu>> call, Throwable t) {
                    // Network error
                }
            });
        } else {
            views.setTextViewText(R.id.meal_text_view, "Login to see your mess menu");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    private void displayMenu(String hostel) {
        HostelMessMenu hostelMessMenu = findMessMenu(instituteMessMenu, hostel);
        if (hostelMessMenu != null)
            displayMessMenu(hostelMessMenu);
    }

    private void displayMessMenu(HostelMessMenu hostelMessMenu) {
        MessMenu todaysMenu = hostelMessMenu.getSortedMessMenus().get(0);

        views.setTextViewText(R.id.day_text_view, Utils.generateDayString(todaysMenu.getDay()));

        Calendar calendar = Calendar.getInstance();
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // TODO: Consider moving to a separate Meal class
        String mealType;
        String mealTime;
        String menu;
        if (hourOfDay < 10 || hourOfDay >= 22) {
            // breakfast
            mealType = "Breakfast";
            menu = todaysMenu.getBreakfast();
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                mealTime = "8am to 10am";
            } else {
                mealTime = "7:30am to 9:30am";
            }
        } else if (hourOfDay < 14) {
            // lunch
            mealType = "Lunch";
            menu = todaysMenu.getLunch();
            mealTime = "12noon to 2pm";
        } else if (hourOfDay < 18) {
            // snacks
            mealType = "Snacks";
            menu = todaysMenu.getSnacks();
            mealTime = "4:30pm to 6:15pm";
        } else {
            // dinner
            mealType = "Dinner";
            menu = todaysMenu.getDinner();
            mealTime = "8pm to 10pm";
        }

        views.setTextViewText(R.id.meal_name_text_view, mealType);
        views.setTextViewText(R.id.meal_time_text_view, mealTime);
        views.setTextViewText(R.id.meal_text_view, menu);
    }

    private HostelMessMenu findMessMenu(List<HostelMessMenu> hostelMessMenus, String hostel) {
        for (HostelMessMenu hostelMessMenu : hostelMessMenus) {
            if (hostelMessMenu.getShortName().equals(hostel)) {
                return hostelMessMenu;
            }
        }
        return null;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://insti.app/mess/"));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        views.setOnClickPendingIntent(R.id.mess_menu_widget, pendingIntent);
    }
}

