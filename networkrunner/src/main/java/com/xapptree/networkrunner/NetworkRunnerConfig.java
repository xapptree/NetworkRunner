package com.xapptree.networkrunner;

import android.text.TextUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NetworkRunnerConfig {
    private String baseUrl;
    private int timeout;
    private boolean isHttps;
    private InputStream sslCertStream;
    private List<String> pinsList;
    private String user_name;
    private String password;
    private boolean basicAuth;
    private boolean havePins;
    private boolean haveSsl;

    private NetworkRunnerConfig(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.timeout = builder.timeout;
        this.isHttps = builder.isHttps;
        this.sslCertStream = builder.sslCertStream;
        this.pinsList = builder.pins;
        this.user_name = builder.userName;
        this.password = builder.password;
        this.basicAuth = builder.basicAuth;
        this.havePins = builder.havePins;
        this.haveSsl = builder.haveSsl;

        NetworkRunnerProvider.getInstance().setConfiguration(this);
    }

    String getBaseUrl() {
        return baseUrl;
    }

    int getTimeOut() {
        return timeout;
    }

    boolean IsHttps() {
        return isHttps;
    }

    InputStream getSslStream() {
        return sslCertStream;
    }

    List<String> getPins() {
        return pinsList;
    }

    String getUserName() {
        return user_name;
    }

    String getPassword() {
        return password;
    }

    boolean IsBasicAuth() {
        return basicAuth;
    }

    boolean HavePins() {
        return havePins;
    }

    boolean HaveSSLStream() {
        return haveSsl;
    }

    public static class Builder {
        private String baseUrl;
        private int timeout = 60000;
        private boolean isHttps = false;
        private InputStream sslCertStream;
        private List<String> pins = new ArrayList<>();
        private String userName;
        private String password;
        private boolean basicAuth = false;
        private boolean haveSsl = false;
        private boolean havePins = false;

        public Builder() {

        }

        public Builder setBaseUrl(String base_url) {
            this.baseUrl = base_url;
            return this;
        }

        public Builder setTimeOut(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setHttps(boolean is_https) {
            this.isHttps = is_https;
            return this;
        }

        public Builder setSslStream(InputStream ssl_cert_stream) {
            if (ssl_cert_stream == null) {
                throw new IllegalArgumentException("Missing InputStream, Certificate stream required for Trust Cert.");
            }
            this.sslCertStream = ssl_cert_stream;
            this.haveSsl = true;
            return this;
        }

        public Builder setPins(List<String> pins) {
            if (!(pins.size() > 0)) {
                throw new IllegalArgumentException("Missing keys, Certificate PINS are required for ssl pinning.");
            }
            this.pins = pins;
            this.havePins = true;
            return this;
        }

        public Builder setUserName(String user_name) {
            this.userName = user_name;
            return this;
        }

        public Builder setPassword(String password) {
            this.password = password;
            return this;
        }

        public Builder setBasicAuth(boolean basic_auth) {
            this.basicAuth = basic_auth;
            return this;
        }

        public NetworkRunnerConfig build() {
            if (TextUtils.isEmpty(baseUrl)) {
                throw new IllegalArgumentException("Missing base url, URL is required and it is mandatory for further process.");
            }
            if (basicAuth) {
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
                    throw new IllegalArgumentException("Missing values, UserName and Password are required in case you are using basic authentication.");
                }
            }
            return new NetworkRunnerConfig(this);
        }

    }
}
