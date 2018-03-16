package in.ac.iitb.gymkhana.iitbapp.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceGenerator {
    //TODO: Change BASE_URL once the server is hosted
    private static final String BASE_URL = "https://temp-iitb.radialapps.com/api/";
    private static OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
    private static Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create());
    private static Retrofit retrofit;

    public static <S> S createService(Class<S> serviceClass) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        retrofit = retrofitBuilder.client(clientBuilder.addInterceptor(httpLoggingInterceptor).build()).build();
        return retrofit.create(serviceClass);
    }
}
