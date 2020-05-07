package com.xapptree.networkrunner;

public interface NetworkRunnerCallback {
    void onResponse(NRResponse response, int requestCode);

    void onFailure(int requestCode, Throwable t);
}
