package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import thedankdevs.tcss450.uw.edu.tddevschat.R;

/**
 * Utility class for setting/updating the theme of the given Activity.
 *
 * @author Bryan Santos
 * @version 12/05/2018
 */
public class ThemeUtils {

    public static final String THEME_CLASSIC = "theme_classic";
    public static final String THEME_MINT    = "theme_mint";

    /**
     * Field used to check which theme to switch to. THEME_CLASSIC is the default value.
     */
    private static String sTheme = THEME_CLASSIC;

    private ThemeUtils() {
        // empty constructor
    }

    /**
     * Set the theme on the given activity based on the
     * currently values in SharedPreferences.
     *
     * @param activity Activity that will have its theme set
     */
    public static void onActivityCreateTheme( Activity activity ) {

        String            prefKey    = activity.getString( R.string.current_theme );
        SharedPreferences sharedPref = activity.getSharedPreferences( prefKey, Context.MODE_PRIVATE );
        sTheme = sharedPref.getString( prefKey, THEME_CLASSIC );
        Log.d( "Theme Utils", "sTheme = " + sTheme );
        switch ( sTheme ) {
            case THEME_MINT:
                activity.setTheme( R.style.AppTheme_Mint );
                break;
            default:
                activity.setTheme( R.style.AppTheme );
        }

        Log.d( "Theme Utils", "created theme" );
    }
}
