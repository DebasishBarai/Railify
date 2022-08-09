package com.arcticwolflabs.railify.base.netapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HTTPRequester  {
    private final String USER_AGENT = "Mozilla/5.0";


    private final TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }
        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }
    };

    // Install the all-trusting trust manager
//    private SSLContext sc = null;

   /* {
        try {
            sc = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
*/    public HTTPRequester()  {
        /*try {
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);*/
    }

    public String requestGETString(String _url) {
        String data = null;
        HttpURLConnection conn = null;
        try {
            Log.d("HTTPR", "Requesting URL GET.");
            URL url = new URL(_url);
            Log.d("HTTPR", "URL: "+_url);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "text/html");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestMethod("GET");
            // conn.setRequestProperty("Authorization", "someAuthString");
            conn.connect();
            int rc = conn.getResponseCode();
            Log.d("RCVAL", "RC: "+rc);
            if (rc==200) {
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                data = total.toString();
                Log.d("HTTPR", "Got HTTP GET data.");
            } else {
                Log.d("HTTPR", "Some HTTP GET error happened with code "+rc+".");
                return null;
            }

        } catch (Exception e) {
            Log.d("HTTPR", "Exceptioned: "+e.getLocalizedMessage());
            data = null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return data;
    }
    public Bitmap requestGETBitmap(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String requestGETFile(String _url) {
        String data = null;
        HttpURLConnection conn = null;
        try {
            Log.d("HTTPR", "Requesting URL GET.");
            URL url = new URL(_url);
            Log.d("HTTPR", "URL: "+_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "text/html");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestMethod("GET");
            // conn.setRequestProperty("Authorization", "someAuthString");
            int rc = conn.getResponseCode();
            if (rc==200) {
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                data = total.toString();
                Log.d("HTTPR", "Got HTTP GET data.");
            } else {
                Log.d("HTTPR", "Some HTTP GET error happened with code "+rc+".");
                return null;
            }

        } catch (Exception e) {
            Log.d("HTTPR", "Exceptioned: "+e.getLocalizedMessage());
            data = null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return data;
    }

    public String requestPOSTString(String _url, String postdata) {
        String data = null;
        HttpURLConnection conn = null;
        try {
            Log.d("HTTPR", "Requesting URL POST.");
            URL url = new URL(_url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "text/html");
            conn.setRequestProperty("User-Agent", USER_AGENT);
            conn.setRequestMethod("POST");
            // conn.setRequestProperty("Authorization", "someAuthString");
            int rc = conn.getResponseCode();
            if (rc==200) {
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n');
                }
                data = total.toString();
                Log.d("HTTPR", "Got HTTP POST data.");
            } else {
                Log.d("HTTPR", "Some HTTP POST error happened with code "+rc+".");
                return null;
            }

        } catch (Exception e) {
            Log.d("HTTPR", e.getLocalizedMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return data;
    }

}
