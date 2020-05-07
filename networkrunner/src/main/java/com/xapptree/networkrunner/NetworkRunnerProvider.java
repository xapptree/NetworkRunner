package com.xapptree.networkrunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

final class NetworkRunnerProvider {
    private X509TrustManager trustManager;
    private SSLSocketFactory sslSocketFactory;
    private static NetworkRunnerProvider mInstance;
    private NetworkRunnerConfig configuration;

    static NetworkRunnerProvider getInstance() {
        if (mInstance == null) {
            mInstance = new NetworkRunnerProvider();
        }
        return mInstance;
    }

    void setConfiguration(NetworkRunnerConfig configuration) {
        this.configuration = configuration;
    }

    NetworkRunnerConfig getConfiguration() {
        return configuration;
    }


    /*OkHTTP Client*/
    OkHttpClient getOkHttpClient() {

        OkHttpClient client;
        if (configuration.IsHttps()) {
            if (configuration.IsBasicAuth()) {
                String authToken = Credentials.basic(configuration.getUserName(), configuration.getPassword());
                AuthenticationInterceptor interceptor = new AuthenticationInterceptor(authToken);

                client = new OkHttpClient.Builder()
                        .connectTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                        .writeTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                        .readTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                        .addInterceptor(interceptor)
                        .build();

            } else if (configuration.HavePins()) {
                sslSocketFactory = getPinnedSSLSocketFactory();
                client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslSocketFactory, trustManager)
                        .connectTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                        .writeTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                        .readTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                        .hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(final String hostname, final SSLSession session) {
                                return true;
                            }
                        }).build();

            } else if (configuration.HaveSSLStream()) {
                customTrust();
                client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslSocketFactory, trustManager)
                        .connectTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                        .writeTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                        .readTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                        .hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(final String hostname, final SSLSession session) {
                                return true;
                            }
                        }).build();
            } else {
                client = new OkHttpClient.Builder()
                        .connectTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                        .writeTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                        .readTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                        .build();
            }
        } else {
            client = new OkHttpClient.Builder()
                    .connectTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                    .writeTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                    .readTimeout(configuration.getTimeOut(), TimeUnit.MILLISECONDS)
                    .build();
        }

        return client;
    }

    /*Custom Trust*/
    private void customTrust() {
        try {
            InputStream lInputStream = configuration.getSslStream();
            //InputStream lInputStream = context.getResources().getAssets().open("sepatqa.crt");
            CertificateFactory lCF = CertificateFactory.getInstance("X.509");
            Certificate lCertificate = lCF.generateCertificate(lInputStream);

            String lDefaultType = KeyStore.getDefaultType();
            KeyStore lKeystore = KeyStore.getInstance(lDefaultType);
            lKeystore.load(null, null);
            lKeystore.setCertificateEntry("ca", lCertificate);

            String lTrustString = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory lTrustManager = TrustManagerFactory.getInstance(lTrustString);
            lTrustManager.init(lKeystore);

            TrustManager[] trustManagers = lTrustManager.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            trustManager = (X509TrustManager) trustManagers[0];

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{trustManager}, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public RequestBody prepareDescription(String description) {

        return RequestBody.create(MultipartBody.FORM, description);
    }

    public MultipartBody.Part prepareMultiPartBody(String partName, String filepath) {

        File file = new File(filepath);

        //create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), file);
        //RequestBody requestFile = RequestBody.create(MediaType.parse(Objects.requireNonNull(context.getContentResolver().getType(Uri.fromFile(file)))), file);

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public MultipartBody.Part prepareMultiPartBody(String partName, File file) {

        //create RequestBody instance from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("*/*"), file);

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    /*Certificate Pinning*/
    private SSLSocketFactory getPinnedSSLSocketFactory() {
        Set<String> PINS = new HashSet<>(configuration.getPins());
        TrustManager[] tm = {new PublicKeyManager(PINS)};
        SSLSocketFactory pinnedSSLSocketFactory = null;
        try {
            trustManager = (PublicKeyManager) tm[0];
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tm, null);
            pinnedSSLSocketFactory = context.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return pinnedSSLSocketFactory;
    }
}
