package wolsnut4.org.easydebt_mobile;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;

import wolsnut4.org.easydebt_mobile.keycloak.KeyCloak;
import wolsnut4.org.easydebt_mobile.keycloak.KeyCloakAccount;
import wolsnut4.org.easydebt_mobile.keycloak.token.AccessTokenExchangeLoader;
import wolsnut4.org.easydebt_mobile.keycloak.util.IOUtils;

/**
 * Created by Alpha on 2/17/2018.
 */

public class LoginActivity extends AccountAuthenticatorActivity implements LoaderManager.LoaderCallbacks<KeyCloakAccount> {

    private KeyCloak kc;
    private static final String ACCESS_TOKEN_KEY = "accessToken";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        kc = new KeyCloak(this);
        Account[] accounts = AccountManager.get(this).getAccountsByType(KeyCloak.ACCOUNT_TYPE);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new LoginActivity.PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public Loader<KeyCloakAccount> onCreateLoader(int i, Bundle bundle) {
        return new AccessTokenExchangeLoader(this, bundle.getString(ACCESS_TOKEN_KEY));
    }

    @Override
    public void onLoadFinished(Loader<KeyCloakAccount> keyCloakAccountLoader, KeyCloakAccount keyCloakAccount) {
        AccountAuthenticatorResponse response = getIntent().getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE);
        String keyCloakAccountJson = new Gson().toJson(keyCloakAccount);
        final Bundle accountBundle = new Bundle();
        accountBundle.putString(KeyCloak.ACCOUNT_KEY, keyCloakAccountJson);


        final AccountManager am = AccountManager.get(this);
        final Account androidAccount = new Account(keyCloakAccount.getPreferredUsername(), KeyCloak.ACCOUNT_TYPE);
        Account[] accounts = am.getAccountsByType(KeyCloak.ACCOUNT_TYPE);
        for (Account existingAccount : accounts) {
            if (existingAccount.name == androidAccount.name) {
                am.setUserData(androidAccount, KeyCloak.ACCOUNT_KEY, keyCloakAccountJson);
                if (response != null) {
                    response.onResult(accountBundle);
                }
                finish();
            }
        }

        am.removeAccount(androidAccount, new AccountManagerCallback<Boolean>() {
            @Override
            public void run(AccountManagerFuture<Boolean> accountManagerFuture) {
                boolean result = am.addAccountExplicitly(androidAccount, null, accountBundle);

            }
        }, null);


        if (response != null) {
            response.onResult(accountBundle);
        }
        finish();


    }

    @Override
    public void onLoaderReset(Loader<KeyCloakAccount> keyCloakAccountLoader) {

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private KeyCloak kc;
        private WebView webView;

        public PlaceholderFragment() {

        }

        @Override
        public void onAttach(Context activity) {
            super.onAttach(activity);
            if (kc == null) {
                kc = new KeyCloak(activity);
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.activity_login, container, false);
            webView = (WebView) rootView.findViewById(R.id.webview);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    if (url.contains("code=")) {
                        final String token = IOUtils.fetchToken(url);
                        Bundle data = new Bundle();
                        data.putString(ACCESS_TOKEN_KEY, token);
                        getLoaderManager().initLoader(1, data, (LoaderManager.LoaderCallbacks) (getActivity())).forceLoad();

                        return true;
                    }

                    return false;
                }


            });
            webView.loadUrl(kc.createLoginUrl());
            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
        }
    }
}
