package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.SettingsNode;
import thedankdevs.tcss450.uw.edu.tddevschat.R;

import java.util.Objects;

import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.LocationNode.*;
import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.SettingsNode.Weather_Preference;


/**
 * The settings fragment in which users may choose their preferred weather settings
 *
 * @author /Daylen Nguyen
 * @version 12/6/2018
 */
public class SettingsFragment extends Fragment {

    /* Bundle Keys */
    public static final  String METRIC_PREF        = "WEATHER_METRIC";
    public static final  String DETERMINANT_PREF   = "LOCATION_DETERMINANT";
    /*Static values of the center of USA*/
    private static final double AMERICA_CENTER_LON = 39.8283;
    private static final double AMERICA_CENTER_LAT = 98.5795;
    /*UI Elements to display weather data*/
    EditText mStateView, mCityView, mZipView;
    Spinner            locate_spinner;
    RadioGroup         mWeatherMetricRGroup;
    MapView            mMap;
    MapIsReadyCallback mCallback;
    boolean            gpsIsActive;
    HomeActivity       home;

    /**
     * default constructor
     */
    public SettingsFragment() {/*Required empty public constructor*/}

    /**
     * retrieves a pointer to the homeactivity
     *
     * @param savedInstanceState state to be saved
     */
    @Override
    public void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        home = ( HomeActivity ) getActivity();
    }

    /**
     * initializes many ui element's onclick and creates pointers to those elements
     *
     * @param inflater           used to create the view
     * @param container          used to create the view
     * @param savedInstanceState bundle where we will recover state
     * @return the view
     */
    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_settings, container, false );
        /*
            Spinner is the Dropdown for how we will be retrieving the location.
                      - GPS Data
                      - Select Location on Map
                      - Postal Code
                      - City State
        */
        /*Retrieve the ui to receive input from the user & assign onClick action*/
        locate_spinner = view.findViewById( R.id.locate_spinner );
        view.findViewById( R.id.citystate_apply_button ).setOnClickListener( this::cityStateActionListener );
        view.findViewById( R.id.zip_apply_button ).setOnClickListener( this::zipActionListener );
        view.findViewById( R.id.map_container ).setVisibility( View.GONE );
        Button mapLocationButton = view.findViewById( R.id.select_location_button );
        mMap = view.findViewById( R.id.fragment_map );
        /*Retrieve preferences*/
        mWeatherMetricRGroup =
                view.findViewById( R.id.radiogroup_weather_metric );
        mCityView = view.findViewById( R.id.city_txtvjew );
        mStateView = view.findViewById( R.id.state_txtentry );
        mZipView = view.findViewById( R.id.zipcode_txtvjew );

        /*Check if permissions were granted yet*/
        if ( ActivityCompat.checkSelfPermission( home, Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( home, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            /*if they still deny gps, then tell them gps functionality will be deactivated.*/
            home.stopGPS();
            gpsIsActive = false;
            Toast.makeText( home, "Location Permission has not been accepted; " +
                    "please change them within the app settings.", Toast.LENGTH_LONG ).show();
        } else {
            //The user has already allowed the use of Locations. Get the current location.
            home.startGPS();
            gpsIsActive = true;
        }
        mCallback = new MapIsReadyCallback( home );
        /* Weather degree metric; which radio button is checked? */
        mWeatherMetricRGroup.setOnCheckedChangeListener( SettingsNode::onRadioButtonSelection );
        locate_spinner.setOnItemSelectedListener( new SettingsNode.LocationDeterminantDropdownListener( view ) );
        mapLocationButton.setOnClickListener( mCallback );
        mapLocationButton.setOnClickListener( mCallback );

        setMetricViewFromSavedPreference();

        mMap.getMapAsync( mCallback );
        mMap.onCreate( savedInstanceState );
        return view;
    }

    /**
     * Retrieves the shared preferences, the location from those preferences and also
     * initializes the spinner with the user's previously selected item
     */
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences determin_sp   = Objects.requireNonNull( getContext() ).getSharedPreferences( Weather_Preference, Context.MODE_PRIVATE );
        int               determin_pref = determin_sp.getInt( DETERMINANT_PREF, -1 );
        SharedPreferences sp = Objects.requireNonNull( getActivity() )
                .getSharedPreferences( Weather_Preference, 0 );
        Log.w( "DAYLENTEST", String.valueOf( determin_pref ) );
        switch ( determin_pref ) {
            case SettingsNode.GPS_DATA:
                if ( gpsIsActive ) {
                    locate_spinner.setSelection( SettingsNode.GPS_DATA );
                } else {
                    Toast.makeText( this.getActivity(), "Error Retrieving GPS Data", Toast.LENGTH_LONG ).show();
                }
                break;
            case SettingsNode.SELECT_FROM_MAP:
                locate_spinner.setSelection( SettingsNode.SELECT_FROM_MAP );
                break;
            case SettingsNode.POSTAL_CODE:
                mZipView.setText( sp.getString( ZIP_KEY, "Ex. 98422" ), TextView.BufferType.EDITABLE );
                locate_spinner.setSelection( SettingsNode.POSTAL_CODE );
                break;
            case SettingsNode.CITY_STATE:
                //if they've already made a prefered city+state, set it in the field as the hint
                locate_spinner.setSelection( SettingsNode.CITY_STATE );
                mStateView.setText( sp.getString( STATE_KEY, "Ex. Tacoma" ), TextView.BufferType.EDITABLE );
                mCityView.setText( sp.getString( CITY_KEY, "Ex. WA" ), TextView.BufferType.EDITABLE );
                break;
            default:
                Log.d( "DAYLENTEST", String.valueOf( determin_pref ) );
                locate_spinner.setSelection( SettingsNode.CITY_STATE );
                break;
        }
        mMap.onStart();
    }

    /**
     * calls the maps on resume method
     */
    @Override
    public void onResume() {
        super.onResume();
        mMap.onResume();
    }

    /**
     * calls the mMaps onSaveInstanceState method
     *
     * @param outState state being saved
     */
    @Override
    public void onSaveInstanceState( @NonNull Bundle outState ) {
        super.onSaveInstanceState( outState );
        mMap.onSaveInstanceState( outState );
    }

    /**
     * calls the map's onPause method
     */
    @Override
    public void onPause() {
        super.onPause();
        mMap.onPause();
    }

    /**
     * calls the contained map's onStop method.
     */
    @Override
    public void onStop() {
        super.onStop();
        // Get from the SharedPreferences
        SharedPreferences.Editor determin_pref    = Objects.requireNonNull( getActivity() ).getSharedPreferences( Weather_Preference, Context.MODE_PRIVATE ).edit();
        int                      DeterminantState = locate_spinner.getSelectedItemPosition();
        determin_pref.putInt( DETERMINANT_PREF, DeterminantState );
        determin_pref.apply();
        /*if the gps setting is not active, disable the gps*/
        switch ( DeterminantState ) {
            case SettingsNode.SELECT_FROM_MAP:
                Objects.requireNonNull( home ).stopGPS();
                break;
            case SettingsNode.GPS_DATA:
                Objects.requireNonNull( home ).startGPS();
                break;
            case SettingsNode.CITY_STATE:
                Objects.requireNonNull( home ).stopGPS();
                break;
            case SettingsNode.POSTAL_CODE:
                Objects.requireNonNull( home ).stopGPS();
                break;
        }
        mMap.onStop();
    }

    /**
     * calls the contained map's onLowMemory method.
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMap.onLowMemory();
    }

    /**
     * calls the contained map's onDestroy method.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        mMap.onDestroy();
    }

    /**
     * sets the shared preference corresponding to the user's input
     *
     * @param v the view on which we are setting the action listener for
     */
    private void zipActionListener( View v ) {
        SharedPreferences        sp = Objects.requireNonNull( this.getContext() ).getSharedPreferences( Weather_Preference, 0 );
        SharedPreferences.Editor e  = sp.edit();
        home.stopGPS();
        String inputZip = mZipView.getText().toString();
        if ( !inputZip.isEmpty() ) {
            if ( inputZip.length() > 4 ) {
                try {
                    Integer.parseInt( inputZip );
                    e.putString( ZIP_KEY, mZipView.getText().toString() );
                    Toast.makeText( home, "Zip Code Applied\n" + inputZip, Toast.LENGTH_SHORT ).show();

                } catch ( Exception except ) {
                    Log.e( "LOCATION INPUT INVALID", "[Settings Fragment]" + String.valueOf( except ) );
                    mZipView.setError( "Invalid Postal Code!" );
                }
            } else {
                mZipView.setError( "Zip code must be 5 or more digits" );
            }
        } else {
            mZipView.setError( "Enter a Zip Code" );
        }
        e.apply();
    }

    /**
     * sets the shared preference corresponding to the user's input
     *
     * @param v the view on which we are setting the action listener for
     */
    private void cityStateActionListener( View v ) {
        /*Retrieve shared preferences for editing*/
        SharedPreferences        sp = Objects.requireNonNull( this.getContext() ).getSharedPreferences( Weather_Preference, 0 );
        SharedPreferences.Editor e  = sp.edit();
        /*Retrieve the city and state text from the views*/
        String state = mStateView.getText().toString();
        String city  = mCityView.getText().toString();

        /*Check for empty input*/
        if ( !city.isEmpty() && !state.isEmpty() ) {
            /*Insert City and State into the shared preferences*/
            e.putString( CITY_KEY, city );
            e.putString( STATE_KEY, state );
            e.apply();

            /*Turn off the GPS*/
            Toast.makeText( getActivity(), state + "," + city + " applied to Location", Toast.LENGTH_LONG ).show();
            Objects.requireNonNull( home ).stopGPS();
        } else {
            if ( city.isEmpty() ) mCityView.setError( "Enter a City" );
            if ( state.isEmpty() ) mStateView.setError( "Enter a State" );
        }

    }

    /**
     * @return returns the saved metric preference in the form a string
     */
    public String getSavedMetricPreference() {
        SharedPreferences settings = Objects.requireNonNull( this.getContext() ).getSharedPreferences( Weather_Preference, 0 );
        return settings.getString( METRIC_PREF, "C" );
    }

    /**
     * Sets the metric view depending on the user's previous selection
     */
    public void setMetricViewFromSavedPreference() {
        switch ( getSavedMetricPreference() ) {
            case SettingsNode.KELVIN:
                Log.d( "DAYLEN", "KELVIN" );
                mWeatherMetricRGroup.check( R.id.radio_kelvin );
                break;
            case SettingsNode.CELSIUS:
                Log.d( "DAYLEN", "CELSIUS" );
                mWeatherMetricRGroup.check( R.id.radio_celsius );
                break;
            case SettingsNode.FAHRENHEIT:
                Log.d( "DAYLEN", "FEHREN" );
                mWeatherMetricRGroup.check( R.id.radio_fahren );
                break;
            default:
                mWeatherMetricRGroup.check( R.id.radio_kelvin );
        }
    }

    /**
     * The inner callback class to which initializes the node and allows the user to select a location from a google map
     *
     * @author Daylen Nguyen
     */
    private class MapIsReadyCallback implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener {
        /*Fields corresponding to the map*/
        GoogleMap     googleMap;
        MarkerOptions currentMarker;
        CircleOptions mCircle;
        HomeActivity  mMaster;
        LatLng        cLocate;

        /**
         * THE MASTER OF THE MAP
         *
         * @param master usually home activity
         */
        MapIsReadyCallback( HomeActivity master ) {
            mMaster = master;
        }

        /**
         * CALLED WHEN THE MAP IS READY; INITS THE MAP ASYNC
         *
         * @param googleMap THE MAP BEING INIT
         */
        @Override
        public void onMapReady( GoogleMap googleMap ) {
            this.googleMap = googleMap;
            SharedPreferences sp = Objects.requireNonNull( getActivity() )
                    .getSharedPreferences( Weather_Preference, 0 );
            String lat = sp.getString( MAP_LAT_KEY, "47.24515" ); // val 0 if none
            String lon = sp.getString( MAP_LON_KEY, "-122.437456" );
            LatLng CurrentLocation;

            try {
                /*Attempt to retrieve shared preferences*/
                assert lat != null;
                assert lon != null;
                if ( !lat.equals( "47.24515" ) && !lon.equals( "-122.437456" ) ) {
                    /*If the user had previously set the location preference, display a circle and pin correspondingly*/
                    CurrentLocation = new LatLng( Float.valueOf( lat), Float.valueOf( lon ));
                    mCircle = new CircleOptions()
                            .center( CurrentLocation )
                            .radius( 30000 )
                            .strokeColor( ContextCompat.getColor( Objects.requireNonNull( getContext() ), R.color.colorLightPurple ) )
                            .fillColor( ContextCompat.getColor( Objects.requireNonNull( getContext() ), R.color.transparentcolorLightPurple ) );
                    currentMarker = new MarkerOptions().position( CurrentLocation ).title( "Current Location" );
                } else {
                    /*No shared preferences? then get their gps coordinates and display that instead*/
                    CurrentLocation = new LatLng( mMaster.getCurrentLat(), mMaster.getCurrentLon() );
                    mCircle = new CircleOptions()
                            .center( CurrentLocation )
                            .radius( 30000 )
                            .strokeColor( ContextCompat.getColor( Objects.requireNonNull( getContext() ), R.color.colorLightPurple ) )
                            .fillColor( ContextCompat.getColor( Objects.requireNonNull( getContext() ), R.color.transparentcolorLightPurple ) );
                    currentMarker = new MarkerOptions().position( CurrentLocation ).title( "Current Location" );
                }
                googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( CurrentLocation, 5.0f ) );
                googleMap.addCircle( mCircle );
            } catch ( Exception e ) {
                /*If we are unable to find the users current location nor the stored location then set the view
                 to an over view of America*/
                CurrentLocation = new LatLng( AMERICA_CENTER_LAT, AMERICA_CENTER_LON );
                Toast.makeText( mMaster, "Error Retrieving your current location! " +
                        "\nCheck your Settings and Notifications", Toast.LENGTH_LONG ).show();
                currentMarker = new MarkerOptions().position( CurrentLocation ).title( "Anerica" );
                googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( CurrentLocation, 1.0f ) );
            } finally {
                googleMap.setOnMapClickListener( this );
                googleMap.addMarker( currentMarker );
            }
            googleMap.setOnMapClickListener( this );
        }

        /**
         * WHEN THE MAP IS PRESSED SET A PIN ON IT AND A CIRCLE AROUND IT
         *
         * @param latLng THE LOCATION WHERE THE USER PRESSED
         */
        @Override
        public void onMapClick( LatLng latLng ) {
            googleMap.clear();
            Log.d( "LAT/LONG", latLng.toString() );
            googleMap.addCircle( new CircleOptions()
                    .center( latLng )
                    .radius( 10000 )
                    .strokeColor( ContextCompat.getColor( Objects.requireNonNull( getContext() ), R.color.colorLightPurple ) )
                    .fillColor( ContextCompat.getColor( Objects.requireNonNull( getContext() ), R.color.transparentcolorLightPurple ) ) );

            googleMap.addMarker( new MarkerOptions().position( latLng )
                    .icon( BitmapDescriptorFactory.defaultMarker( BitmapDescriptorFactory.HUE_VIOLET ) ) );
            googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( latLng, 10.0f ) );
            cLocate = latLng;
        }

        /**
         * when setting the map location; if the user hits the button, show a toast and save it
         *
         * @param v the button which is being pressed
         */
        @Override
        public void onClick( View v ) {
            SharedPreferences sp = Objects.requireNonNull( getActivity() )
                    .getSharedPreferences( Weather_Preference, 0 );
            SharedPreferences.Editor e = sp.edit();
            float                    curLat;
            float                    curLon;
            if ( cLocate != null ) {
                curLat = ( float ) cLocate.latitude;
                curLon = ( float ) cLocate.longitude;
                e.putString( MAP_LAT_KEY, String.valueOf( curLat ) );
                e.putString( MAP_LON_KEY, String.valueOf( curLon ) );
                Toast.makeText( mMaster, "Latitude: "
                                .concat( String.valueOf( curLat ) )
                                .concat( " and Longitude: " )
                                .concat( String.valueOf( curLon ) )
                                .concat( " have been applied" ),
                        Toast.LENGTH_SHORT )
                        .show();
            } else {
                /*Toast.makeText( mMaster, "Error setting the location, check your GPS settings and permissions", Toast.LENGTH_SHORT ).show();*/
                Toast.makeText( mMaster, "Error setting the location; tap a location to drop a pin", Toast.LENGTH_LONG ).show();
            }
            e.apply();
        }
    }
}
