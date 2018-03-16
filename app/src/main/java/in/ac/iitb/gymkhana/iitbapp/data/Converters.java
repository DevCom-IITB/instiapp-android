package in.ac.iitb.gymkhana.iitbapp.data;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mrunz on 14/3/18.
 */

public class Converters {
    @TypeConverter
    public static List<Event> eventsfromString(String value) {
        Type listType = new TypeToken<List<Event>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringfromEvents(List<Event> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static List<User> usersfromString(String value) {
        Type listType = new TypeToken<List<User>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringfromUsers(List<User> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
    @TypeConverter
    public static List<Venue> venuesfromString(String value) {
        Type listType = new TypeToken<List<Venue>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringfromVenues(List<Venue> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
    @TypeConverter
    public static List<Body> bodiesfromString(String value) {
        Type listType = new TypeToken<List<Body>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringfromBodies(List<Body> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}