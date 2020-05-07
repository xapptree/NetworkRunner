package com.xapptree.networkrunner;


import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Set;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;


final class PublicKeyManager implements X509TrustManager {

   private static final String TAG = PublicKeyManager.class.getSimpleName();

   private Set<String> publicKeyHashes;

   PublicKeyManager(Set<String> keyHashes) {
       this.publicKeyHashes = keyHashes;

   }

   @Override
   public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

   }

   @Override
   public void checkServerTrusted(X509Certificate[] x509Certificates, String authType) throws CertificateException {
       if (x509Certificates == null) {
           throw new IllegalArgumentException("checkServerTrusted: X509Certificate array is null");
       }
       if (!(x509Certificates.length > 0)) {
           throw new IllegalArgumentException("checkServerTrusted: X509Certificate is empty");
       }

       // Perform customary SSL/TLS checks
       TrustManagerFactory tmf;
       try {
           tmf = TrustManagerFactory.getInstance("X509");
           tmf.init((KeyStore) null);

           for (TrustManager trustManager : tmf.getTrustManagers()) {
               ((X509TrustManager) trustManager).checkServerTrusted(x509Certificates, authType);
           }

       } catch (Exception e) {
           throw new CertificateException(e.toString());
       }

       final boolean isValidCert = validatePinning(x509Certificates);
       if (!isValidCert) {
           throw new CertificateException("Not trusted: expected public key is different from Server public key!!");
       }
   }

   @Override
   public X509Certificate[] getAcceptedIssuers() {
       return new X509Certificate[0];
   }

   private boolean validatePinning(X509Certificate[] certs) {
       try {
           MessageDigest md = MessageDigest.getInstance("SHA-256");
           for (X509Certificate x509Certificate : certs) {
               byte[] key = x509Certificate.getPublicKey().getEncoded();
               md.update(key, 0, key.length);
               byte[] hashBytes = md.digest();
               StringBuffer hexHash = new StringBuffer();
               for (int i = 0; i < hashBytes.length; i++) {
                   int k = 0xFF & hashBytes[i];
                   String tmp = (k < 16) ? "0" : "";
                   tmp += Integer.toHexString(0xFF & hashBytes[i]);
                   hexHash.append(tmp);
               }
               if (publicKeyHashes.contains(hexHash.toString())) {
                   return true;
               }
           }

       } catch (Exception e) {
           e.printStackTrace();
           return false;
       }

       return false;
   }

}
