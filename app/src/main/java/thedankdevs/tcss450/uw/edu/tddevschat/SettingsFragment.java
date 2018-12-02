package thedankdevs.tcss450.uw.edu.tddevschat;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.util.Objects;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.Settings.SettingsNode;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    /* Bundle Keys */
    public static final String METRIC_PREF = "WEATHER_METRIC";
    public static final String DETERMINANT_PREF = "LOCATION_DETERMINANT";

    Button mApplyButton;
    RadioGroup mWeatherMetricRGroup;
    HomeActivity home;

    public SettingsFragment() {/*Required empty public constructor*/
        home = (HomeActivity) getActivity();
    }

    /*
        Dropdown for how we will be retrieving the location.
            - GPS Data
            - Select Location on Map
            - Postal Code
            - City State
    */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*Inflate the layout for this fragment*/
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mWeatherMetricRGroup = view.findViewById(R.id.radiogroup_weather_metric);
        Spinner locate_spinner = view.findViewById(R.id.locate_spinner);
        locate_spinner.setOnItemSelectedListener(new SettingsNode.LocationDeterminantDropdownListener(view));
        /* Weather degree metric; which radio button is checked? */
        mWeatherMetricRGroup.setOnCheckedChangeListener(SettingsNode::onRadioButtonSelection);
        /*Invisible sections*/
        setMetricViewFromSavedPreference();
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Get from the SharedPreferences
        SharedPreferences settings = Objects.requireNonNull(this.getContext()).getSharedPreferences(METRIC_PREF, 0);
        String METRICPREF = settings.getString(METRIC_PREF, "0");
        Log.e("DAYLEN", String.valueOf(METRICPREF));
        /*update the shared preference fields within settings node*/
        if (home != null && home.mSettingsNode != null) {
            home.mSettingsNode.getAndUpdateLocationDeterminantPref();
            home.mSettingsNode.getAndUpdateMetricPreferences();
        }
    }

    public String getSavedMetricPreference() {
        SharedPreferences settings = Objects.requireNonNull(this.getContext()).getSharedPreferences(METRIC_PREF, 0);
        return settings.getString(METRIC_PREF, "C");
    }

    public void setMetricViewFromSavedPreference() {
        switch (getSavedMetricPreference()) {
            case SettingsNode.KELVIN:
                Log.d("DAYLEN", "KELVIN");
                mWeatherMetricRGroup.check(R.id.radio_kelvin);
                break;
            case SettingsNode.CELSIUS:
                Log.d("DAYLEN", "CELSIUS");
                mWeatherMetricRGroup.check(R.id.radio_celsius);
                break;
            case SettingsNode.FAHRENHEIT:
                Log.d("DAYLEN", "FEHREN");
                mWeatherMetricRGroup.check(R.id.radio_fahren);
                break;
            default:
                mWeatherMetricRGroup.check(R.id.radio_kelvin);
        }
    }
}
