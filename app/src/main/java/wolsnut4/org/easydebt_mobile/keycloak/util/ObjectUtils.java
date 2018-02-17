package wolsnut4.org.easydebt_mobile.keycloak.util;

/**
 * Created by Alpha on 2/17/2018.
 */

public class ObjectUtils {
    public static <T>T getOrDefault(T mayBeNull, T defaultIfNull) {
        if (mayBeNull != null) {
            return mayBeNull;
        } else {
            return defaultIfNull;
        }
    }
}