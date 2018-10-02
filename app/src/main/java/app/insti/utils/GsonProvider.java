package app.insti.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Shivam Sharma on 13-08-2018.
 */

public class GsonProvider {
    private static final Gson gsonInput = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").create();

    private static final Gson gsonOutput = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();

    public static Gson getGsonInput() {
        return gsonInput;
    }

    public static Gson getGsonOutput() {
        return gsonOutput;
    }
}