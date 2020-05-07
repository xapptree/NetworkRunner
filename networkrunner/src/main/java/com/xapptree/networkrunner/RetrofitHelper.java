package com.xapptree.networkrunner;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

class RetrofitHelper {

    private static Retrofit retrofit;

    static Retrofit getClient() {
        if (retrofit != null) {
            return retrofit;
        }

        final OkHttpClient okHttpClient = NetworkRunnerProvider.getInstance().getOkHttpClient();

        retrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(NetworkRunnerProvider.getInstance().getConfiguration().getBaseUrl())
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

}
