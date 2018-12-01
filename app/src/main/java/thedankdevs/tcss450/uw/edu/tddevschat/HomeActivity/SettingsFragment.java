package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity;


import android.Manifest;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    /* Bundle Keys */
    public static final  String METRIC_PREF        = "WEATHER_METRIC";
    public static final  String DETERMINANT_PREF   = "LOCATION_DETERMINANT";
    private static final double AMERICA_CENTER_LON = 39.8283;
    private static final double AMERICA_CENTER_LAT = 98.5795;
    EditText mStateView, mCityView, mZipView;
    RadioGroup         mWeatherMetricRGroup;
    HomeActivity       home;
    MapView            mMap;
    boolean            gpsIsActive;
    MapIsReadyCallback mCallback;

    public SettingsFragment() {/*Required empty public constructor*/
        home = ( HomeActivity ) getActivity();
    }

    @Override
    public void onCreate( @Nullable Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        home = ( HomeActivity ) getActivity();
    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_settings, container, false );
        /*Spinner is the Dropdown for how we will be retrieving the location.
                  - GPS Data
                  - Select Location on Map
                  - Postal Code
                  - City State              */
        Spinner locate_spinner = view.findViewById( R.id.locate_spinner );
        locate_spinner.setOnItemSelectedListener( new SettingsNode.LocationDeterminantDropdownListener( view ) );

        /* Weather degree metric; which radio button is checked? */
        mWeatherMetricRGroup = view.findViewById( R.id.radiogroup_weather_metric );
        mWeatherMetricRGroup.setOnCheckedChangeListener( SettingsNode::onRadioButtonSelection );
        view.findViewById( R.id.map_container ).setVisibility( View.GONE );
        /*Retrieve the ui to receive input from the user & assign onClick action*/
        mCityView = view.findViewById( R.id.city_txtvjew );
        mStateView = view.findViewById( R.id.state_txtentry );
        mZipView = view.findViewById( R.id.zipcode_txtvjew );


        /*Check if permissions were granted yet*/
        if ( ActivityCompat.checkSelfPermission( home, Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( home, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            gpsIsActive = false;
            /*if not, then ask for them again*/
            ActivityCompat.requestPermissions( home, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_LOCATIONS );

            if ( ActivityCompat.checkSelfPermission( home, Manifest.permission.ACCESS_FINE_LOCATION )
                    != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission( home, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                /*if they still deny gps, then tell them gps functionality will be deactivated.*/
                home.stopGPS();
                gpsIsActive = false;
                Toast.makeText( home, "Permissions were denied, closing.", Toast.LENGTH_SHORT ).show();
                home.finishAndRemoveTask();
            } else {
                //The user has already allowed the use of Locations. Get the current location.
                home.startGPS();
                gpsIsActive = true;
            }
        }


        Button mapLocationButton = view.findViewById( R.id.select_location_button );
        mCallback = new MapIsReadyCallback( home );


        view.findViewById( R.id.citystate_apply_button ).setOnClickListener( this::cityStateActionListener );
        view.findViewById( R.id.zip_apply_button ).setOnClickListener( this::zipActionListener );

        int determin_pref = view.getContext().getSharedPreferences( DETERMINANT_PREF, 0 ).getInt( DETERMINANT_PREF, 1 );


        switch ( determin_pref ) {
            case SettingsNode.GPS_DATA:
                if ( gpsIsActive ) {
                    locate_spinner.setSelection( SettingsNode.GPS_DATA );
                } else {
                    locate_spinner.setSelection( SettingsNode.POSTAL_CODE );
                }

                break;
            case SettingsNode.SELECT_FROM_MAP:
                locate_spinner.setSelection( SettingsNode.SELECT_FROM_MAP );
                break;
            case SettingsNode.POSTAL_CODE:
                locate_spinner.setSelection( SettingsNode.POSTAL_CODE );
                break;
            case SettingsNode.CITY_STATE:
                locate_spinner.setSelection( SettingsNode.CITY_STATE );
                break;
            default:
                locate_spinner.setSelection( SettingsNode.CITY_STATE );
                break;
        }

        mMap = view.findViewById( R.id.fragment_map );
        mapLocationButton.setOnClickListener( mCallback );

        mMap.onCreate( savedInstanceState );

        /*Retrieve preferences*/
        setMetricViewFromSavedPreference();
        mMap.getMapAsync( mCallback );
//        mMap.setOnClickListener( mCallback );
        mapLocationButton.setOnClickListener( mCallback );

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mMap.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMap.onResume();
    }

    @Override
    public void onSaveInstanceState( @NonNull Bundle outState ) {
        super.onSaveInstanceState( outState );
        mMap.onSaveInstanceState( outState );
    }

    @Override
    public void onPause() {
        super.onPause();
        mMap.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMap.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMap.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Get from the SharedPreferences
        SharedPreferences metSet = Objects.requireNonNull( this.getContext() ).getSharedPreferences( METRIC_PREF, 0 );
        SharedPreferences locSet = Objects.requireNonNull( this.getContext() ).getSharedPreferences( DETERMINANT_PREF, 0 );

        String METRICPREF       = metSet.getString( METRIC_PREF, "C" );
        int    DeterminantState = locSet.getInt( DETERMINANT_PREF, 1 );
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
        mMap.onDestroy();
    }

    private void zipActionListener( View v ) {
        SharedPreferences        sp = Objects.requireNonNull( this.getContext() ).getSharedPreferences( SettingsNode.LOCATIONPREF, 0 );
        SharedPreferences.Editor e  = sp.edit();

        /*  */
        home.stopGPS();
        String inputZip = mZipView.getText().toString();
        /*Todo: Add check for validity from database*/
        if ( !inputZip.isEmpty() ) {
            if ( inputZip.length() > 4 ) {
                try {
                    Integer.parseInt( inputZip );
                    e.putString( ZIP_KEY, mZipView.getText().toString() );
                    Toast.makeText( home, "Zip Code Applied", Toast.LENGTH_SHORT ).show();

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

    private void cityStateActionListener( View v ) {
        /*Retrieve shared preferences for editing*/
        SharedPreferences        sp = Objects.requireNonNull( this.getContext() ).getSharedPreferences( SettingsNode.LOCATIONPREF, 0 );
        SharedPreferences.Editor e  = sp.edit();
        // TODO : Add city state check and tables within database
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
            Toast.makeText( home, "City and State Applied", Toast.LENGTH_SHORT ).show();
            Objects.requireNonNull( home ).stopGPS();
        } else {
            /* TODO:  */
            if ( city.isEmpty() ) mCityView.setError( "Enter a City" );
            if ( state.isEmpty() ) mStateView.setError( "Enter a State" );
        }

    }

    public String getSavedMetricPreference() {
        SharedPreferences settings = Objects.requireNonNull( this.getContext() ).getSharedPreferences( METRIC_PREF, 0 );
        return settings.getString( METRIC_PREF, "C" );
    }

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

    private class MapIsReadyCallback implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener {
        GoogleMap     googleMap;
        MarkerOptions currentMarker;
        CircleOptions mCircle;
        HomeActivity  mMaster;
        LatLng        cLocate;

        MapIsReadyCallback( HomeActivity master ) {
            mMaster = master;
        }

        @Override
        public void onMapReady( GoogleMap googleMap ) {
            this.googleMap = googleMap;

            try {
                LatLng CurrentLocation = new LatLng( mMaster.getCurrentLat(), mMaster.getCurrentLon() );
                mCircle = new CircleOptions()
                        .center( CurrentLocation )
                        .radius( 10000 )
                        .strokeColor( ContextCompat.getColor( Objects.requireNonNull( getContext() ), R.color.colorLightPurple ) )
                        .fillColor( ContextCompat.getColor( Objects.requireNonNull( getContext() ), R.color.transparentcolorLightPurple ) );
                currentMarker = new MarkerOptions().position( CurrentLocation ).title( "Current Location" );
                googleMap.addMarker( currentMarker );
                googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( CurrentLocation, 5.0f ) );
                googleMap.setOnMapClickListener( this );
                googleMap.addCircle( mCircle );

            } catch ( Exception e ) {
                Toast.makeText( mMaster, "Error Retrieving your current location! " +
                        "Check your Settings and Notifications", Toast.LENGTH_SHORT ).show();
                LatLng CurrentLocation = new LatLng( AMERICA_CENTER_LAT, AMERICA_CENTER_LON );
                currentMarker = new MarkerOptions().position( CurrentLocation ).title( "Anerica" );
                googleMap.addMarker( currentMarker );
                googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( CurrentLocation, 1.0f ) );
                googleMap.setOnMapClickListener( this );
//                googleMap.addCircle( mCircle );

            }
            googleMap.setOnMapClickListener( this::onMapClick );
        }

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

        @Override
        public void onClick( View v ) {
            SharedPreferences sp = Objects.requireNonNull( getActivity() )
                    .getSharedPreferences( SettingsNode.LOCATIONPREF, 0 );
            SharedPreferences.Editor e = sp.edit();
            float                    curLat;
            float                    curLon;
            if ( cLocate != null ) {
                curLat = ( float ) cLocate.latitude;
                curLon = ( float ) cLocate.longitude;
                e.putFloat( MAP_LAT_KEY, curLat );
                e.putFloat( MAP_LON_KEY, curLon );
                Toast.makeText( mMaster, "Latitude: "
                                .concat( String.valueOf( curLat ) )
                                .concat( " and\nLongitude: " )
                                .concat( String.valueOf( curLon ) )
                                .concat( " have been applied" ),
                        Toast.LENGTH_SHORT )
                        .show();
            } else {
                /*Toast.makeText( mMaster, "Error setting the location, check your GPS settings and permissions", Toast.LENGTH_SHORT ).show();*/
                Toast.makeText( mMaster, "Error setting the location; tap a location to drop a pin", Toast.LENGTH_SHORT ).show();
            }

            e.apply();
        }
    }
}
