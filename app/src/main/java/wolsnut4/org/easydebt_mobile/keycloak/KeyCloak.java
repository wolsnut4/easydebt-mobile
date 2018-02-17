package wolsnut4.org.easydebt_mobile.keycloak;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

import wolsnut4.org.easydebt_mobile.keycloak.util.IOUtils;

/**
 * Created by Alpha on 2/17/2018.
 */

public class KeyCloak {

    public static final String ACCOUNT_KEY = "org.keycloak.KeyCloakAccount";
    public static final String ACCOUNT_TYPE = "org.keycloak.Account";
    public static final String ACCOUNT_AUTHTOKEN_TYPE = "org.keycloak.Account.authToken";
    private final KeyCloakConfig config;
    private final Context context;

    public KeyCloak(Context context) {
        this.config = KeyCloakConfig.getInstance(context);
        this.context = context.getApplicationContext();
    }

    public String createLoginUrl() {
        String state = UUID.randomUUID().toString();
        String redirectUri = getRedirectUri();

        saveState(state);

        String url = config.realmUrl
                + "/protocol/openid-connect/auth"
                + "?client_id=" + IOUtils.encodeURIComponent(config.clientId)
                + "&redirect_uri=" + IOUtils.encodeURIComponent(redirectUri)
                + "&state=" + IOUtils.encodeURIComponent(state)
                + "&response_type=code";


        return url;
    }

    private void saveState(String state) {
        SharedPreferences prefs = context.getSharedPreferences(getClass().getSimpleName(), Context.MODE_PRIVATE);
        prefs.edit().putString("state", state).commit();
    }

    public String getClientId() {
        return config.clientId;
    }

    public String getRedirectUri() {
        return "urn:ietf:wg:oauth:2.0:oob";
    }

    public String getClientSecret() {
        return config.clientSecret;
    }

    public String getBaseURL() {
        return config.realmUrl;
    }
}
