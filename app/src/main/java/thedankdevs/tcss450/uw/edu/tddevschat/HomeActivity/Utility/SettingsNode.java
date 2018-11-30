package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.R;

import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.SettingsFragment.DETERMINANT_PREF;
import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.SettingsFragment.METRIC_PREF;

public class SettingsNode {

    /******************[CONSTANTS]******************/
    /*Weather Temp. Metric*/
    public static final String KELVIN     = "K";
    public static final String CELSIUS    = "C";
    public static final String FAHRENHEIT = "F";

    /*Location Determinant*/
    public static final int    GPS_DATA        = 0;
    public static final int    SELECT_FROM_MAP = 1;
    public static final int    POSTAL_CODE     = 2;
    public static final int    CITY_STATE      = 3;
    public static final String LOCATIONPREF    = "GPSSK";
    public static final int    GPS_ISON        = 1;
    public static final int    GPS_ISOFF       = 0;


    private HomeActivity master;

    /************************************************/

    public SettingsNode( HomeActivity master ) {
        this.master = master;
    }

    /**
     * The Static method, of which is called when the weather metric setting is changed
     */
    public static void onRadioButtonSelection( RadioGroup rg, int state ) {
        SharedPreferences        settings = rg.getContext().getSharedPreferences( METRIC_PREF, Context.MODE_PRIVATE );
        SharedPreferences.Editor editor   = settings.edit();
        switch ( state ) {
            case R.id.radio_kelvin:
                Log.d( "DAYLEN", "KELVIN" );
                editor.putString( METRIC_PREF, KELVIN );
                break;
            case R.id.radio_celsius:
                Log.d( "DAYLEN", "CELSIUS" );
                editor.putString( METRIC_PREF, CELSIUS );
                break;
            case R.id.radio_fahren:
                Log.d( "DAYLEN", "FEHREN" );
                editor.putString( METRIC_PREF, FAHRENHEIT );
                break;
            default:
                editor.putString( METRIC_PREF, CELSIUS );
        }
        editor.apply();
    }

    public static class LocationDeterminantDropdownListener implements AdapterView.OnItemSelectedListener {
        View view;

        public LocationDeterminantDropdownListener( View masterView ) {
            view = masterView;
        }

        @Override
        public void onItemSelected( AdapterView<?> parent, View aview, int position, long id ) {
            SharedPreferences        settings     = view.getContext().getSharedPreferences( DETERMINANT_PREF, Context.MODE_PRIVATE );
            SharedPreferences.Editor editor       = settings.edit();
            Button                   locateButton = view.findViewById( R.id.select_location_button );
            View                     zippy        = view.findViewById( R.id.setting_section_zip );
            View                     cs           = view.findViewById( R.id.setting_section_citystate );
            FrameLayout              map          = view.findViewById( R.id.mapandbutt_holder );
            view.findViewById( R.id.map_container ).setVisibility( View.GONE );

            locateButton.setVisibility( View.GONE );
            cs.setVisibility( View.GONE );
            map.setVisibility( View.GONE );
            zippy.setVisibility( View.GONE );
            /*Store GPS DATA IN PREFERENCE*/
            switch ( position ) {
                case GPS_DATA:
                    editor.putInt( DETERMINANT_PREF, GPS_DATA );
                    break;
                case SELECT_FROM_MAP:
                    view.findViewById( R.id.map_container ).setVisibility( View.VISIBLE );
                    map.setVisibility( View.VISIBLE );
                    view.findViewById( R.id.select_location_button ).setVisibility( View.VISIBLE );
                    editor.putInt( DETERMINANT_PREF, SELECT_FROM_MAP );
                    break;
                case POSTAL_CODE:
                    zippy.setVisibility( View.VISIBLE );
                    editor.putInt( DETERMINANT_PREF, POSTAL_CODE );
                    break;
                case CITY_STATE:
                    cs.setVisibility( View.VISIBLE );
                    editor.putInt( DETERMINANT_PREF, CITY_STATE );
                    break;
                default:
                    break;
            }
            editor.apply();
        }

        @Override
        public void onNothingSelected( AdapterView<?> parent ) {
        }
    }
}
