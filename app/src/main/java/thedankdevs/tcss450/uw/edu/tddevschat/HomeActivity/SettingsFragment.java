package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.SettingsNode;
import thedankdevs.tcss450.uw.edu.tddevschat.R;

import java.util.Objects;

import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.LocationNode.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    /* Bundle Keys */
    public static final String METRIC_PREF = "WEATHER_METRIC";
    public static final String DETERMINANT_PREF = "LOCATION_DETERMINANT";
    EditText mStateView, mCityView, mZipView;
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
            x - Postal Code
            x - City State
    */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        home = (HomeActivity) getActivity();
        if (home != null) {
            home.startGPS();
        }

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        /*The dropdown for choosing the location determinant*/
        Spinner locate_spinner = view.findViewById(R.id.locate_spinner);
        locate_spinner.setOnItemSelectedListener(new SettingsNode.LocationDeterminantDropdownListener(view));

        /* Weather degree metric; which radio button is checked? */
        mWeatherMetricRGroup = view.findViewById(R.id.radiogroup_weather_metric);
        mWeatherMetricRGroup.setOnCheckedChangeListener(SettingsNode::onRadioButtonSelection);

        /*Retrieve the ui to receive input from the user & assign onClick action*/
        mCityView = view.findViewById(R.id.city_head_textview);
        mStateView = view.findViewById(R.id.state_txtentry);
        mStateView = view.findViewById(R.id.state_txtentry);
        mZipView = view.findViewById(R.id.zipcode_txtvjew);
        view.findViewById(R.id.citystate_apply_button).setOnClickListener(this::cityStateActionListener);
        view.findViewById(R.id.zip_apply_button).setOnClickListener(this::zipActionListener);

        /*TO-DO: Add apply button for zip code*/
        /*Invisible sections*/
        setMetricViewFromSavedPreference();
        return view;
    }

    private void zipActionListener(View v) {
        SharedPreferences sp = Objects.requireNonNull(this.getContext()).getSharedPreferences(SettingsNode.LOCATIONPREF, 0);
        SharedPreferences.Editor e = sp.edit();

        home.stopGPS();
        String inputZip = mZipView.getText().toString();
        /*Todo: Add check for validity from database*/
        if (!inputZip.isEmpty()) {
            try {
                Integer.parseInt(inputZip);
                e.putString(ZIP_KEY, mZipView.getText().toString());
            } catch (Exception except) {
                Log.e("LOCATION INPUT INVALID", "[Settings Fragment]" + String.valueOf(except));
                mZipView.setError("Invalid Postal Code!");
            }
        } else {
            mZipView.setError("Enter a Zip Code");
        }
        e.apply();
    }

    private void cityStateActionListener(View v) {
        /*Retrieve shared preferences for editing*/
        SharedPreferences sp = Objects.requireNonNull(this.getContext()).getSharedPreferences(SettingsNode.LOCATIONPREF, 0);
        SharedPreferences.Editor e = sp.edit();
        // ToDo: Add city state check and tables within database
        /*Retrieve the city and state text from the views*/
        String city = mStateView.getText().toString();
        String state = mCityView.getText().toString();

        /*Check for empty input*/
        if (!city.isEmpty() && !state.isEmpty()) {
            /*Insert City and State into the shared preferences*/
            e.putString(CITY_KEY, city);
            e.putString(STATE_KEY, state);
            e.apply();
            /*Turn off the GPS*/
            Objects.requireNonNull(home).stopGPS();
        } else {
            /*TODO:*/
            if (city.isEmpty()) mCityView.setError("Enter a City");
            if (state.isEmpty()) mStateView.setError("Enter a State");
        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        // Get from the SharedPreferences
        SharedPreferences metSet = Objects.requireNonNull(this.getContext()).getSharedPreferences(METRIC_PREF, 0);
        SharedPreferences locSet = Objects.requireNonNull(this.getContext()).getSharedPreferences(DETERMINANT_PREF, 0);
        String METRICPREF = metSet.getString(METRIC_PREF, "C");
        int DeterminantState = locSet.getInt(DETERMINANT_PREF, 1);
        switch (DeterminantState) {
            case SettingsNode.SELECT_FROM_MAP:
                Objects.requireNonNull(home).stopGPS();

                break;
            case SettingsNode.GPS_DATA:
                Objects.requireNonNull(home).startGPS();
                break;
            case SettingsNode.CITY_STATE:

                break;
            case SettingsNode.POSTAL_CODE:

                break;

        }

//        if (DeterminantState == SettingsNode.GPS_ISON) {
//            Objects.requireNonNull(h).startGPS();
//        } else {
//            Objects.requireNonNull(h).stopGPS();
//        }
//        Log.e("DAYLEN", "gpeSTATE=" + String.valueOf(DeterminantState));
//        Log.e("DAYLEN", String.valueOf());

        /*update the shared preference fields within settings node*/

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
