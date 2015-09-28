package karl.codes;

import org.apache.http.conn.ssl.SSLSocketFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by karl on 9/24/15.
 */
public class Java {
    static final TrustManager TRUST_ALL = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    public static void ignoreSSLIssues() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext ssl = SSLContext.getInstance("TLS");
        ssl.init(null, new TrustManager[]{TRUST_ALL},new SecureRandom());

        HttpsURLConnection.setDefaultHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        HttpsURLConnection.setDefaultSSLSocketFactory(ssl.getSocketFactory());
    }

    public static final List<String> signer(Map<String,String> properties) {
        return Arrays.asList(
                properties.get("consumerKey"),
                properties.get("consumerSecret"),
                properties.get("accessToken"),
                properties.get("secretToken"));
    }

    public static final List<String> pairs(Map<String,String> properties) {
        ArrayList<String> out = new ArrayList<>(properties.size() * 2);

        for(Map.Entry<String,String> e : properties.entrySet()) {
            out.add(e.getKey());
            out.add(e.getValue());
        }

        return out;
    }
}
