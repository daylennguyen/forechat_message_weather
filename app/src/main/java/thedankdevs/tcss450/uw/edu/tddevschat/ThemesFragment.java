package thedankdevs.tcss450.uw.edu.tddevschat;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.ThemeUtils;


/**

 */
public class ThemesFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private SharedPreferences mSharedPref;

    public ThemesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPref =  getActivity().getSharedPreferences(getString(R.string.current_theme),Context.MODE_PRIVATE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_themes, container, false);
        Switch switchView = v.findViewById(R.id.switch_theme);
        switchView.setOnClickListener(view -> onSwitch(switchView));
        String theme = mSharedPref.getString(getString(R.string.current_theme), ThemeUtils.THEME_CLASSIC);
        switch (theme) {
            case ThemeUtils.THEME_MINT:
                switchView.setChecked(true);
            default:

        }
        return v;
    }

    public void onSwitch(View v) {
        Log.d(TAG, "I hit the switch");
        Switch switchView = (Switch) v;
        String theme = ThemeUtils.THEME_CLASSIC;
        if (switchView.isChecked()) {
            theme = ThemeUtils.THEME_MINT;
        }

        mSharedPref.edit().putString(getString(R.string.current_theme), theme).apply();
        ThemeUtils.changeTheme(getActivity(), theme);
    }
}



