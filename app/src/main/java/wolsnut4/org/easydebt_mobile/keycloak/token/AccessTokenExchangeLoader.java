package wolsnut4.org.easydebt_mobile.keycloak.token;

import android.content.AsyncTaskLoader;
import android.content.Context;

import wolsnut4.org.easydebt_mobile.keycloak.KeyCloak;
import wolsnut4.org.easydebt_mobile.keycloak.KeyCloakAccount;
import wolsnut4.org.easydebt_mobile.keycloak.util.TokenExchangeUtils;

/**
 * Created by Alpha on 2/17/2018.
 */

public class AccessTokenExchangeLoader extends AsyncTaskLoader<KeyCloakAccount> {


    private final KeyCloak kc;
    private final String accessToken;
    private KeyCloakAccount account;

    public AccessTokenExchangeLoader(Context context, String accessToken) {
        super(context);
        this.kc = new KeyCloak(context);
        this.accessToken = accessToken;
    }

    @Override
    public KeyCloakAccount loadInBackground() {
        return TokenExchangeUtils.exchangeForAccessCode(accessToken, kc);
    }

}