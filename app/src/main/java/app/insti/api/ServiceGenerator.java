package app.insti.api;

import android.content.Context;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_PRAGMA = "Pragma";
    private static final String BASE_URL = "https://api.insti.app/api/";

    private Context context;

    private Interceptor provideCacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());

            CacheControl cacheControl;

            if (isConnected()) {
                cacheControl = new CacheControl.Builder()
                        .maxAge(0, TimeUnit.SECONDS)
                        .build();
            } else {
                cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();
            }

            return response.newBuilder()
                    .removeHeader(HEADER_PRAGMA)
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build();
        }
    };

    private Interceptor provideOfflineCacheInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            if (!isConnected()) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();

                request = request.newBuilder()
                        .removeHeader(HEADER_PRAGMA)
                        .removeHeader(HEADER_CACHE_CONTROL)
                        .cacheControl(cacheControl)
                        .build();
            }

            return chain.proceed(request);
        }
    };

    private Cache provideCache() {
        Cache cache = null;

        try {
            cache = new Cache(new File(context.getCacheDir(), "api-cache"),
                    50 * 1024 * 1024); // 50 MB
        } catch (Exception e) {
            Log.e("cache", "Could not create Cache!");
        }

        return cache;
    }

    public boolean isConnected() {
        Log.wtf("cache", "MEOW");
        try {
            android.net.ConnectivityManager e = (android.net.ConnectivityManager) context.getSystemService(
                    Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = e.getActiveNetworkInfo();
            Log.wtf("cache", Boolean.toString(activeNetwork != null && activeNetwork.isConnectedOrConnecting()));
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        } catch (Exception e) {
            Log.w("cache", e.toString());
        }

        return false;
    }

    private static OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

    private static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());

    private Retrofit retrofit;
    public RetrofitInterface retrofitInterface;
    public ServiceGenerator(Context mContext) {
        context = mContext;
        retrofit = retrofitBuilder.client(
                clientBuilder
                        .addInterceptor(provideOfflineCacheInterceptor)
                        .addNetworkInterceptor(provideCacheInterceptor)
                        .cache(provideCache())
                        .build()
        ).build();
        retrofitInterface = retrofit.create(RetrofitInterface.class);
    }

    public RetrofitInterface getRetrofitInterface() {
        return retrofitInterface;
    }
}