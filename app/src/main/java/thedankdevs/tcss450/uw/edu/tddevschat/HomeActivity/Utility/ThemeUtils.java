package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;

public class ThemeUtils implements MenuItem.OnMenuItemClickListener {

    public static final String THEME_CLASSIC = "theme_classic";
    public static final String THEME_MINT = "theme_mint";

    private static String sTheme = THEME_CLASSIC;

    private HomeActivity mMaster;
    private Credentials mCredentials;

    public ThemeUtils(HomeActivity activity, Credentials credentials) {
        mMaster = activity;
        mCredentials = credentials;
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



    @Override
    public boolean onMenuItemClick(MenuItem item) {

        Switch switchView = (Switch) item.getActionView();

        String theme      = ThemeUtils.THEME_CLASSIC;
        if ( switchView.isChecked() ) {
            theme = ThemeUtils.THEME_MINT;
        }
        SharedPreferences sharedPref = mMaster.getSharedPreferences(
                mMaster.getString( R.string.current_theme ),
                Context.MODE_PRIVATE );

        sharedPref.edit().putString(mMaster.getString(R.string.current_theme), theme).apply();
        Intent intent = new Intent( mMaster, HomeActivity.class );
        intent.putExtra( mMaster.getString( R.string.key_credential ), mCredentials );
        intent.putExtra( mMaster.getString( R.string.reload_themes ), true );
        mMaster.startActivity( intent );
        mMaster.finish();

        return false;
    }
}
