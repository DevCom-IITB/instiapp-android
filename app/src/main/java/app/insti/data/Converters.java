package app.insti.data;

import android.arch.persistence.room.TypeConverter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.List;

public class Converters {
    @TypeConverter
    public static List<Event> eventsfromString(String value) {
        Type listType = new TypeToken<List<Event>>() {
        }.getType();
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
        Type listType = new TypeToken<List<User>>() {
        }.getType();
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
        Type listType = new TypeToken<List<Venue>>() {
        }.getType();
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
        Type listType = new TypeToken<List<Body>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringfromBodies(List<Body> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static List<Role> rolesfromString(String value) {
        Type listType = new TypeToken<List<Role>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringfromRoles(List<Role> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static Timestamp timestampfromString(String value) {
        try {
            return new Gson().fromJson(value, Timestamp.class);
        } catch (JsonSyntaxException e) {
            Log.d("Converters", "timestampfromString: " + value);
            e.printStackTrace();
            return null;
        }
    }

    @TypeConverter
    public static String stringfromTimestamp(Timestamp timestamp) {
        Gson gson = new Gson();
        String json = gson.toJson(timestamp);
        return json;
    }

    @TypeConverter
    public static Body bodyfromString(String value) {
        Type listType = new TypeToken<Body>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringfromBody(Body body) {
        Gson gson = new Gson();
        String json = gson.toJson(body);
        return json;
    }

    @TypeConverter
    public static Object objectFromString(String value) {
        Type listType = new TypeToken<Object>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringFromObject(Object object) {
        Gson gson = new Gson();
        String json = gson.toJson(object);
        return json;
    }

    @TypeConverter
    public static List<String> stringsfromString(String value) {
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringfromStrings(List<String> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }

    @TypeConverter
    public static List<MessMenu> messMenusfromString(String value) {
        Type listType = new TypeToken<List<MessMenu>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String stringfromMessMenus(List<MessMenu> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}