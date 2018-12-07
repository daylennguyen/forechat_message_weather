package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.google.android.gms.location.*;

import java.io.Serializable;
import java.util.Objects;

import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.SettingsFragment.DETERMINANT_PREF;
import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.SettingsFragment.METRIC_PREF;
import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.SettingsNode.Weather_Preference;

/**
 * The Location Node; contains the primary functionality pertaining to retrieving Location Data
 * from the application database/server. Later passed to the Weather fragment.
 *
 * @author Daylen Nguyen
 * @version 12/6/2018
 */
public class LocationNode implements Serializable {
    /**
     * Key to which is used to retrieve the ZIPCODE through the location preferences
     */
    public static final String ZIP_KEY     = "ZIP";
    /**
     * Key to which is used to retrieve the CITY through the location preferences
     */
    public static final String CITY_KEY    = "CITY";
    /**
     * Key to which is used to retrieve the STATE through the location preferences
     */
    public static final String STATE_KEY   = "STATE";
    /**
     * Key to which is used to retrieve the map Longitude through the location preferences
     */
    public static final String MAP_LON_KEY = "MLON";

    /**
     * Key to which is used to retrieve the MAP latitude through the location preferences
     */
    public static final String MAP_LAT_KEY = "MLAT";

    /**
     * Key to which is used to retrieve the Longitude through the location preferences
     */
    public static final  String LONGITUDE_KEY                           = "LONGITUDE";
    /**
     * Key to which is used to retrieve the latitude through the location preferences
     */
    public static final  String LATITUDE_KEY                            = "LATITUDE";
    /**
     * The permission code to which is handled by: onPermissionsResults
     */
    private static final int    MY_PERMISSIONS_LOCATIONS                = 8414;
    /*Location Services*/
    /**
     * The interval to which the location will be update (ms)
     */
    private static final long   UPDATE_INTERVAL_IN_MILLISECONDS         = 10000;
    /**
     * The upper-bound on the interval to which the location will be updated
     */
    private static final long   FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    /**
     * The master node to which this is attached
     */
    private              AppCompatActivity           myNodeMaster;
    /**
     * used to request a quality of service for location updates from the FusedLocationProviderApi
     */
    private              LocationRequest             mLocationRequest;
    /**
     * The current location of the user, as given by the fused location provider
     */
    private              Location                    mCurrentLocation;
    /**
     * of type, FusedLocationProviderClient which acts
     * as the main entry point for interacting with the fused location provider
     */
    private              FusedLocationProviderClient mFusedLocationClient;
    /**
     * The callback method which is used on post location retrieval
     */
    private              LocationCallback            mLocationCallback;
    private              String                      GPSISON;

    /**
     * The default constructor to which initializes all location functionality for the application
     *
     * @param MasterActivity the activity to which it is attached {@AppCompatActivity}
     * @author Daylen Nguyen
     */
    public LocationNode( AppCompatActivity MasterActivity ) {
        setMyNodeMaster( MasterActivity );

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient( MasterActivity );

        if ( ActivityCompat.checkSelfPermission( MasterActivity, Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( MasterActivity, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( MasterActivity, new String[] { Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_LOCATIONS );
        } else {
            //The user has already allowed the use of Locations. Get the current location.
            requestLocation();
        }
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult( LocationResult locationResult ) {
                if ( locationResult == null ) {
                    Log.d( "LOCATION", "Error retrieving location" );
                }
                if ( locationResult != null ) {
                    for ( Location location : locationResult.getLocations() ) {
                        // Update UI with location data

                        SharedPreferences sp = myNodeMaster.getSharedPreferences( Weather_Preference, 0 );
                        sp.edit().putLong( LATITUDE_KEY, ( long ) location.getLatitude() ).putLong( LATITUDE_KEY, ( long ) location.getLongitude() ).apply();


                        Log.d( "LOCATION", "LATITUDE: " + String.valueOf( location.getLatitude() ) );
                        Log.d( "LOCATION", "LONG: " + String.valueOf( location.getLongitude() ) );

                    }
                }
            }
        };
        createLocationRequest();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     *
     * @author Daylen Nguyen
     */
    public void startLocationUpdates() {
        if ( ActivityCompat.checkSelfPermission( getMyNodeMaster(), Manifest.permission.ACCESS_FINE_LOCATION )
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( getMyNodeMaster(),
                Manifest.permission.ACCESS_COARSE_LOCATION )
                == PackageManager.PERMISSION_GRANTED ) {
            mFusedLocationClient.requestLocationUpdates( mLocationRequest, mLocationCallback, null );
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     *
     * @author Daylen Nguyen
     */
    public void stopLocationUpdates() {
        mFusedLocationClient.removeLocationUpdates( mLocationCallback );
    }

    /**
     * Create and configure a Location Request used when retrieving location updates
     *
     * @author Daylen Nguyen
     */
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval( UPDATE_INTERVAL_IN_MILLISECONDS );
        mLocationRequest.setFastestInterval( FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS );
        mLocationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );
    }

    /**
     * Sets the location of this object
     *
     * @param location the location to be set
     * @author Daylen Nguyen
     */
    private void setLocation( final Location location ) {
        mCurrentLocation = location;
    }

    /**
     * Method to request the location permissions from the user
     *
     * @author Daylen Nguyen
     */
    private void requestLocation() {
        if ( ActivityCompat.checkSelfPermission( getMyNodeMaster(), Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission( getMyNodeMaster(),
                Manifest.permission.ACCESS_COARSE_LOCATION )
                != PackageManager.PERMISSION_GRANTED ) {
            Log.d( "REQUEST LOCATION", "User did NOT allow permission to request location!" );
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener( getMyNodeMaster(), location -> {
                        // Got last known location. In some rare situations this can be null.
                        if ( location != null ) {
                            setLocation( location );
                            Log.d( "LOCATION", location.toString() );
                        }
                    } );
        }
    }

    /**
     * Code which is conditionally executed depending on which code is
     * executed and whether the permissions corresponding to the request code, were accepted.
     *
     * @param requestCode  corresponds to the permission request which was made
     * @param grantResults user's response to the permission request
     */
    public void onRequestPermissionsResult( int requestCode, int[] grantResults ) {
        SharedPreferences sp = Objects.requireNonNull( myNodeMaster )
                .getSharedPreferences( Weather_Preference, 0 );
        SharedPreferences.Editor e = sp.edit();

        switch ( requestCode ) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if ( grantResults.length > 0
                        && grantResults[ 0 ] == PackageManager.PERMISSION_GRANTED ) {
                    // permission was granted
                    requestLocation();
                    e.putBoolean( GPSISON, true );

                } else {
                    e.putInt( DETERMINANT_PREF, SettingsNode.CITY_STATE );
                    e.putString( METRIC_PREF, SettingsNode.FAHRENHEIT );
                    e.putBoolean( GPSISON, false );
                }
                e.apply();
            }
        }
    }

    /**
     * retrieves the master node referenced by this object
     *
     * @return the master node, usually of type {@HomeActivity}
     * @author Daylen Nguyen
     */
    private AppCompatActivity getMyNodeMaster() {
        return myNodeMaster;
    }

    /**
     * Sets the master node (usually of type HomeActivity) for this object
     *
     * @param homeactivity the Master Node
     * @author Daylen Nguyen
     */
    private void setMyNodeMaster( AppCompatActivity homeactivity ) {
        this.myNodeMaster = homeactivity;
    }

    /**
     * Retrieves the current location object
     *
     * @return the current location of this object
     * @author Daylen Nguyen
     */
    public Location getmCurrentLocation() {
        return mCurrentLocation;
    }

    /**
     * Sets the current location for this object
     *
     * @param location the location to be set
     * @author Daylen Nguyen
     */
    public void setmCurrentLocation( Location location ) {
        this.mCurrentLocation = location;
    }
}
