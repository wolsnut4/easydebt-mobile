package wolsnut4.org.easydebt_mobile.keycloak;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import wolsnut4.org.easydebt_mobile.R;
import wolsnut4.org.easydebt_mobile.keycloak.util.IOUtils;
import wolsnut4.org.easydebt_mobile.keycloak.util.ObjectUtils;

/**
 * Created by Alpha on 2/17/2018.
 */

public final class KeyCloakConfig {

    private static final String TAG = KeyCloakConfig.class.getSimpleName().toUpperCase();

    public final String realmUrl;
    public final String authServerUrl;
    public final String realm;
    public final String clientId;
    public final String clientSecret;

    private static KeyCloakConfig instace;

    private KeyCloakConfig(Context context) {
        InputStream fileStream = context.getResources().openRawResource(R.raw.keycloak);
        String configText = IOUtils.getString(fileStream);
        IOUtils.close(fileStream);

        try {
            JSONObject configFile = new JSONObject(configText);
            realm = configFile.getString("realm");
            authServerUrl = configFile.getString("auth-server-url");
            realmUrl = authServerUrl + "/realms/" + URLEncoder.encode(realm, "UTF-8");
            clientId = configFile.getString("resource");
            clientSecret = ObjectUtils.getOrDefault(configFile.optJSONObject("credentials"), new JSONObject()).optString("secret");
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("You don't support UTF-8", e);
        }
    }

    public static synchronized KeyCloakConfig getInstance(Context context) {
        if (instace == null) {
            instace = new KeyCloakConfig(context);
        }
        return  instace;
    }



}
