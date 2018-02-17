package wolsnut4.org.easydebt_mobile.keycloak.util;

import android.accounts.NetworkErrorException;
import android.util.Base64;
import android.util.Log;

import org.jboss.aerogear.android.pipe.http.HeaderAndBody;
import org.jboss.aerogear.android.pipe.http.HttpException;
import org.jboss.aerogear.android.pipe.http.HttpRestProvider;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import wolsnut4.org.easydebt_mobile.keycloak.KeyCloak;
import wolsnut4.org.easydebt_mobile.keycloak.KeyCloakAccount;

/**
 * Created by Alpha on 2/17/2018.
 */

public final class TokenExchangeUtils {

    private static final String TAG = TokenExchangeUtils.class.getSimpleName();

    private TokenExchangeUtils() {
    }

    public static KeyCloakAccount exchangeForAccessCode(String accessToken, KeyCloak kc) {

        final Map<String, String> data = new HashMap<String, String>();
        data.put("code", accessToken);
        data.put("client_id", kc.getClientId());
        data.put("redirect_uri", kc.getRedirectUri());

        data.put("grant_type", "authorization_code");
        if (kc.getClientSecret() != null) {
            data.put("client_secret", kc.getClientSecret());
        }

        try {
            URL accessTokenEndpoint = new URL(kc.getBaseURL() + "/protocol/openid-connect/token");

            if (kc.getClientSecret() == null) {
                accessTokenEndpoint = new URL(kc.getBaseURL() + "/protocol/openid-connect/token&client_id=" + IOUtils.encodeURIComponent(kc.getClientId()));
            }

            String bodyString = getBody(data);

            HttpRestProvider provider = getHttpProvider(kc, accessTokenEndpoint);

            HeaderAndBody result = provider.post(bodyString);

            JSONObject accessResponse = handleResult(result);
            KeyCloakAccount account = new KeyCloakAccount();
            account.extractTokenProperties(accessResponse);

            return account;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public static KeyCloakAccount refreshToken(KeyCloakAccount account, KeyCloak kc) throws NetworkErrorException {
        final Map<String, String> data = new HashMap<String, String>();
        data.put("refresh_token", account.getRefreshToken());
        data.put("grant_type", "refresh_token");


        try {
            URL refreshTokenEndpoint = new URL(kc.getBaseURL() + "/protocol/openid-connect/token/refresh");

            if (kc.getClientSecret() == null) {
                refreshTokenEndpoint = new URL(kc.getBaseURL() + "/protocol/openid-connect/token/refresh&client_id=" + IOUtils.encodeURIComponent(kc.getClientId()));
            }

            String bodyString = getBody(data);

            HttpRestProvider provider = getHttpProvider(kc, refreshTokenEndpoint);

            HeaderAndBody result = provider.post(bodyString);

            JSONObject accessResponse = handleResult(result);
            account.extractTokenProperties(accessResponse);

            return account;
        } catch (HttpException e) {
            throw e;
        } catch (Exception e) {
            throw new NetworkErrorException(e);
        }
    }

    private static JSONObject handleResult(HeaderAndBody result) {
        byte[] bodyData = result.getBody();
        String body = new String(bodyData);
        try {
            return new JSONObject(body);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static String getBody(Map<String, String> data) {
        final StringBuilder bodyBuilder = new StringBuilder();
        final String formTemplate = "%s=%s";

        String amp = "";
        for (Map.Entry<String, String> entry : data.entrySet()) {
            bodyBuilder.append(amp);
            try {
                bodyBuilder.append(String.format(formTemplate, entry.getKey(), URLEncoder.encode(entry.getValue(), "UTF-8")));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
            amp = "&";
        }

        return  bodyBuilder.toString();

    }

    private static HttpRestProvider  getHttpProvider(KeyCloak kc, URL url) {
        HttpRestProvider  provider  = new HttpRestProvider(url);

        provider.setDefaultHeader("Content-Type", "application/x-www-form-urlencoded");

        if (kc.getClientSecret() != null) {
            try {
                provider.setDefaultHeader("Authorization", "Basic " + Base64.encodeToString((kc.getClientId() + ":" + kc.getClientSecret()).getBytes("UTF-8"), Base64.DEFAULT | Base64.NO_WRAP));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
        return provider;
    }

}
