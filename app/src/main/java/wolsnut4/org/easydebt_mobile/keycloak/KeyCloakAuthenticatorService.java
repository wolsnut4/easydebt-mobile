package wolsnut4.org.easydebt_mobile.keycloak;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Alpha on 2/17/2018.
 */

public class KeyCloakAuthenticatorService extends Service {

    private KeyCloakAccountAuthenticator authenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        authenticator = new KeyCloakAccountAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
