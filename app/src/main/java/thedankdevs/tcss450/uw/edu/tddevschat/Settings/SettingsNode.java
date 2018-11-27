package thedankdevs.tcss450.uw.edu.tddevschat.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.R;

import static thedankdevs.tcss450.uw.edu.tddevschat.SettingsFragment.DETERMINANT_PREF;
import static thedankdevs.tcss450.uw.edu.tddevschat.SettingsFragment.METRIC_PREF;

public class SettingsNode {

    /*Weather Temp. Metric*/
    public static final String KELVIN = "K";
    public static final String CELSIUS = "C";
    public static final String FAHRENHEIT = "F";
    /*Location Determinant*/
    private static final int GPS_DATA = 0;
    private static final int SELECT_FROM_MAP = 1;
    private static final int POSTAL_CODE = 2;
    private static final int CITY_STATE = 3;
    private HomeActivity mMaster;
    private int current_LocationDeterminant;
    private String current_WeatherMetric;


    public SettingsNode(HomeActivity master) {
        mMaster = master;
        current_LocationDeterminant = getAndUpdateLocationDeterminantPref();
        current_WeatherMetric = getAndUpdateMetricPreferences();
    }

    /**
     * The Static method, of which is called when the weather metric setting is changed
     */
    public static void onRadioButtonSelection(RadioGroup rg, int state) {
        SharedPreferences settings = rg.getContext().getSharedPreferences(METRIC_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        switch (state) {
            case R.id.radio_kelvin:
                Log.d("DAYLEN", "KELVIN");
                editor.putString(METRIC_PREF, KELVIN);
                break;
            case R.id.radio_celsius:
                Log.d("DAYLEN", "CELSIUS");
                editor.putString(METRIC_PREF, CELSIUS);
                break;
            case R.id.radio_fahren:
                Log.d("DAYLEN", "FEHREN");
                editor.putString(METRIC_PREF, FAHRENHEIT);
                break;
            default:
                editor.putString(METRIC_PREF, CELSIUS);
        }
        editor.apply();
    }

    public String getAndUpdateMetricPreferences() {
        SharedPreferences settings = mMaster.getApplicationContext().getSharedPreferences(METRIC_PREF, Context.MODE_PRIVATE);
        current_WeatherMetric = settings.getString(METRIC_PREF, "C");
        return current_WeatherMetric;
    }

    public int getAndUpdateLocationDeterminantPref() {
        SharedPreferences settings = mMaster.getApplicationContext().getSharedPreferences(DETERMINANT_PREF, Context.MODE_PRIVATE);
        current_LocationDeterminant = settings.getInt(DETERMINANT_PREF, GPS_DATA);
        return current_LocationDeterminant;
    }

    public static class LocationDeterminantDropdownListener implements AdapterView.OnItemSelectedListener {
        View view;

        public LocationDeterminantDropdownListener(View masterView) {
            view = masterView;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View aview, int position, long id) {
            SharedPreferences settings = view.getContext().getSharedPreferences(DETERMINANT_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            view.findViewById(R.id.select_location_button).setVisibility(View.GONE);
            view.findViewById(R.id.setting_section_zip).setVisibility(View.GONE);
            view.findViewById(R.id.setting_section_citystate).setVisibility(View.GONE);
            switch (position) {
                case GPS_DATA:
                    /*Store GPS DATA IN PREFERENCE*/
//                    Log.d("DAYLEN", "GPS_DATA");
                    editor.putInt(DETERMINANT_PREF, GPS_DATA);
                    break;
                case SELECT_FROM_MAP:
//                    Log.d("DAYLEN", "SELECT_FROM_MAP");
                    view.findViewById(R.id.select_location_button).setVisibility(View.VISIBLE);
                    editor.putInt(DETERMINANT_PREF, SELECT_FROM_MAP);
                    break;
                case POSTAL_CODE:
//                    Log.d("DAYLEN", "POSTAL_CODE");
                    view.findViewById(R.id.setting_section_zip).setVisibility(View.VISIBLE);
                    editor.putInt(DETERMINANT_PREF, POSTAL_CODE);
                    break;
                case CITY_STATE:
//                    Log.d("DAYLEN", "CITY_STATE");
                    view.findViewById(R.id.setting_section_citystate).setVisibility(View.VISIBLE);
                    editor.putInt(DETERMINANT_PREF, CITY_STATE);
                    break;
                default:
                    break;
            }

            editor.apply();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
