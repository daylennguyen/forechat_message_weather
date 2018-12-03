package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import thedankdevs.tcss450.uw.edu.tddevschat.R;

public class ThemeUtils {

    public static final String THEME_CLASSIC = "theme_classic";
    public static final String THEME_MINT = "theme_mint";

    private static String sTheme = THEME_CLASSIC;

    private ThemeUtils() {

    }


    public static void onActivityCreateTheme(Activity activity) {

        String prefKey = activity.getString(R.string.current_theme);
        SharedPreferences sharedPref = activity.getSharedPreferences(prefKey, Context.MODE_PRIVATE);
        sTheme = sharedPref.getString(prefKey, THEME_CLASSIC);
        Log.d("Theme Utils", "sTheme = " + sTheme);
        switch(sTheme) {
            case THEME_MINT:
                activity.setTheme(R.style.AppTheme_Mint);
                break;
            default:
                activity.setTheme(R.style.AppTheme);
        }

        Log.d("Theme Utils", "created theme");
    }
}
