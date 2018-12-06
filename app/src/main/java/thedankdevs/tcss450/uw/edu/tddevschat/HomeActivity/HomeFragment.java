package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.SettingsNode;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

import java.text.MessageFormat;
import java.util.Objects;

import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.SettingsFragment.DETERMINANT_PREF;
import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.SettingsFragment.METRIC_PREF;
import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.LocationNode.*;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private String       mState;
    private String       mCity;
    private HomeActivity mHome;
    private int          current_LocationDeterminant;
    private String       current_WeatherMetric;

    /*Textview to which we will display the weather data*/
    private TextView high, low, city_state;
    private SharedPreferences myLocationPref, myMetricPref, myDeterminPref;
    private TextView description;
    private TextView date;

    public HomeFragment() {
    }

    /* *********LIFE CYCLE METHODS***************************************************************/

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_home, container, false );
        /*Retrieve the UI for weather homepage*/
        retrieveWeatherTextViewsFromActivity( view );

        /*Retrieve and display the data to the ui*/
        Coordinates_getCurrentWeatherData();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    /* *********   HELPER METHODS    ***************************************************************/

    /**
     * Retrieves the activity and shared preferences relating to the data needed to display
     */
    private void getSharedPrefAndValue() {
        mHome = ( HomeActivity ) getActivity();
        myMetricPref = Objects.requireNonNull( getContext() ).getSharedPreferences( METRIC_PREF, Context.MODE_PRIVATE );
        myDeterminPref = getContext().getSharedPreferences( DETERMINANT_PREF, Context.MODE_PRIVATE );
        myLocationPref = Objects.requireNonNull( getContext() ).getSharedPreferences( SettingsNode.LOCATIONPREF, 0 );
        current_WeatherMetric = myMetricPref.getString( METRIC_PREF, "C" );
        current_LocationDeterminant = myLocationPref.getInt( DETERMINANT_PREF, SettingsNode.GPS_DATA );
        Log.d( "DAYY", current_WeatherMetric + current_LocationDeterminant );
    }

    /**
     * Retrieves the user interface relating to the weather for today inside of home fragmnet
     */
    private void retrieveWeatherTextViewsFromActivity( View v ) {
        //retrieve each textview for displaying the weather data
        high = Objects.requireNonNull( v ).findViewById( R.id.high_temp_home );
        low = Objects.requireNonNull( v ).findViewById( R.id.low_temp_home );
        date = Objects.requireNonNull( v ).findViewById( R.id.weather_dateview );
        description = Objects.requireNonNull( v ).findViewById( R.id.forecast_home );
        city_state = Objects.requireNonNull( v ).findViewById( R.id.city_state_home );
        current_LocationDeterminant = Objects.requireNonNull( getContext() ).getSharedPreferences( DETERMINANT_PREF, Context.MODE_PRIVATE ).getInt( DETERMINANT_PREF, SettingsNode.GPS_DATA );
        current_WeatherMetric = getContext().getSharedPreferences( METRIC_PREF, Context.MODE_PRIVATE ).getString( METRIC_PREF, SettingsNode.FAHRENHEIT );
    }

    /**
     * Helper method to Generate a URI string dependant on location determinant
     */
    private String constructRequestLocation() {
        SharedPreferences myDeterPref = Objects.requireNonNull( getContext() ).getSharedPreferences( DETERMINANT_PREF, Context.MODE_PRIVATE );
        Uri.Builder uri_builder = new Uri.Builder()
                .scheme( "https" )
                .appendPath( getString( R.string.base_url ) )
                .appendPath( getString( R.string.ep_weather ) );
        switch ( myDeterPref.getInt( DETERMINANT_PREF, SettingsNode.GPS_DATA ) ) {
            case SettingsNode.CITY_STATE:
                uri_builder.appendPath( getString( R.string.ep_weather_citystate ) );
                break;
            case SettingsNode.GPS_DATA:
                uri_builder.appendPath( getString( R.string.ep_weather_bycoordinate ) );
                break;
            case SettingsNode.SELECT_FROM_MAP:
                uri_builder.appendPath( getString( R.string.ep_weather_bycoordinate ) );
                break;
            case SettingsNode.POSTAL_CODE:
                uri_builder.appendPath( getString( R.string.ep_weather_postalcode ) );
                break;
        }
        return uri_builder.build().toString();
    }


    /*returns a json object, dependent on the location determinant*/
    private JSONObject constructRequestJSON() {
        JSONObject request = new JSONObject();
        myDeterminPref = Objects.requireNonNull( getContext() ).getSharedPreferences( DETERMINANT_PREF, Context.MODE_PRIVATE );
        myMetricPref = Objects.requireNonNull( getContext() ).getSharedPreferences( METRIC_PREF, 0 );
        try {
            switch ( myDeterminPref.getInt( DETERMINANT_PREF, SettingsNode.GPS_DATA ) ) {
                case SettingsNode.CITY_STATE:
                    mCity = myLocationPref.getString( CITY_KEY, "TACOMA" );
                    mState = myLocationPref.getString( STATE_KEY, "WA" );
                    Log.w( "DAYLEN LOCATION BASED ON PREFERENCE", mCity + mState );
                    request.put( getString( R.string.weather_lcase_city ), mCity );
                    request.put( getString( R.string.weather_lcase_state ), mState );
                    break;
                case SettingsNode.GPS_DATA:
                    double mLat = mHome.getCurrentLat();
                    double mLon = mHome.getCurrentLon();
                    Log.w( "DAYLEN LOCATION BASED ON PREFERENCE", mLat + " mlatlon " + mLon );

                    request.put( getString( R.string.weather_lon_json ), mLon );
                    request.put( getString( R.string.weather_lat_json ), mLat );
                    break;
                case SettingsNode.SELECT_FROM_MAP:
                    mLat = myLocationPref.getFloat( MAP_LAT_KEY, 0 );
                    mLon = myLocationPref.getFloat( MAP_LON_KEY, 0 );
                    Log.w( "DAYLEN LOCATION BASED ON PREFERENCE", mLat + " mlatlon " + mLon );
                    request.put( getString( R.string.weather_lon_json ), mLon );
                    request.put( getString( R.string.weather_lat_json ), mLat );
                    break;
                case SettingsNode.POSTAL_CODE:
                    String mZip = myLocationPref.getString( ZIP_KEY, "98422" );
                    request.put( getString( R.string.weather_json_postal ), mZip );
                    Log.w( "DAYLEN LOCATION BASED ON PREFERENCE", mZip + " MZIP " );
                    break;
            }
            /*DEFAULT UNIT IS CELSIUS*/
            if ( !Objects.requireNonNull( myMetricPref.getString( METRIC_PREF, "C" ) ).equals( SettingsNode.CELSIUS ) ) {
                switch ( Objects.requireNonNull( myMetricPref.getString( METRIC_PREF, "C" ) ) ) {
                    case SettingsNode.FAHRENHEIT:
                        request.put( "units", "I" );
                        break;
                    case SettingsNode.KELVIN:
                        request.put( "units", "S" );
                        break;
                }
            }
        } catch ( Exception e ) {
            Log.e( "WEATHER", String.valueOf( e ) );
            try {
                request.put( "units", "I" );
                request.put( "lon", -122.465973 );
                request.put( "lat", 47.258728 );
            } catch ( JSONException e1 ) {
                e1.printStackTrace();
            }

        }
        return request;
    }

    /**
     * Create an Async task to retrieve the weather data
     * NOTE-To be modified to cache data
     */
    public void Coordinates_getCurrentWeatherData() {
        getSharedPrefAndValue();
        JSONObject request  = constructRequestJSON();
        String     mSendUrl = constructRequestLocation();
        new SendPostAsyncTask.Builder( mSendUrl, request )
                .onPreExecute( () -> {
                } )
                .onPostExecute( this::PostWeatherRequest )
                .onCancelled( error -> Log.e( "daylen weather", error ) )
                .build()
                .execute();
    }

    /**
     * AFTER the post request, parse the response from the server and extract the weather data
     */
    private void PostWeatherRequest( String result ) {
        try {
            Log.w( "dayday", result );
            // This is the result from the web service
            JSONObject res = new JSONObject( result );
            Log.i( "Daylen", String.valueOf( res ) );

            // retrieve the city and state of the current user from the
            // latitude and longitude
            mState = res.getString( "state_code" );
            mCity = res.getString( "city_name" );
            Log.i( "DAYLEN_WEATH_DATE_FRAG", mCity + mState );

            // response will contain a json array containing 16 day forecast
            JSONArray data = res.getJSONArray( "data" );

            // data will be in the form of a json object
            JSONObject currentDay = ( JSONObject ) data.get( 0 );

            // extract json values from response
            date.setText( currentDay.getString( "datetime" ) );

            /*retrieve weather data*/
            double     min       = currentDay.getDouble( "min_temp" );
            double     max       = currentDay.getDouble( "max_temp" );
            JSONObject weather   = currentDay.getJSONObject( "weather" );
            String     weathDesc = weather.getString( "description" );


            //apply the weather data to the user interface
            description.setText( weathDesc );
            city_state.setText( String.format( "in %s, %s, The forcast calls for ", mCity, mState ) );
            low.setText( MessageFormat.format( "{0}°{1}", String.valueOf( min ), current_WeatherMetric ) );
            high.setText( MessageFormat.format( "{0}°{1}", String.valueOf( max ), current_WeatherMetric ) );
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
    }

}
