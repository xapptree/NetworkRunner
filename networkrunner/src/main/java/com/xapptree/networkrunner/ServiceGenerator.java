package com.xapptree.networkrunner;

public class ServiceGenerator {
    private volatile static ApiService apiInterface;

    public static ApiService getService() {
        if (apiInterface == null) {
            synchronized (ApiService.class) {
                if (apiInterface == null) {
                    apiInterface = RetrofitHelper.getClient().create(ApiService.class);
                }
            }
        }
        return apiInterface;
    }

}
