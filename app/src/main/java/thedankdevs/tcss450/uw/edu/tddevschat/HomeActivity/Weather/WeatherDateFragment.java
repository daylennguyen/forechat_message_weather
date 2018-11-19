package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Weather;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.WaitFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class WeatherDateFragment extends Fragment {
    /*Debug tag*/
    static final String TAG = "WEATHER";
    /*tag for # columns within the list*/
    private static final String ARG_COLUMN_COUNT = "column-count";
    /*value for # columns within the list*/
    private int mColumnCount = 1;

    /*Response from server will push content to list*/
    private List<WeatherDate> currentWeatherData;

    /*
        Weather Variables; data output is dependant on valid variables
        NOTE: State is the state abbreviation.
    */
    private String mState, mCity;
    private double mLon, mLat;
    /*TODO: implement listener for date selection*/
    private OnListFragmentInteractionListener mListener;

    /*The recycler view contained within this fragment*/
    private RecyclerView myRV;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WeatherDateFragment() {
    }

    /* When weather is opened on onCreate will make a post request to the server for current weather*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Send request for weather data

        if (getArguments() != null) {
            Bundle LocationData = getArguments();
            Coordinates_getCurrentWeatherData(LocationData.getDouble(HomeActivity.LATITUDE_KEY), LocationData.getDouble(HomeActivity.LONGITUDE_KEY));
            /*mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);*/
        }
    }

    /*Once the weather request has been fulfilled, we can remove the load/wait fragment*/
    private void PreWeatherRequest() {
        mListener.onWaitFragmentInteractionShow();
    }

    private void PostWeatherRequest(final String result) {
        try {
            // This is the result from the web service
            currentWeatherData = new ArrayList<>();
            JSONObject res = new JSONObject(result);

            // retrieve the city and state of the current user from the
            // latitude and longitude
            mState = res.getString("state_code");
            mCity = res.getString("city_name");
            // response will contain a json array containing 16 day forecast
            // each index, 1 day
            JSONArray data = res.getJSONArray("data");

            // pieces of the URI
            String icoextension = getResources().getString(R.string.ep_weather_icon_file_ext);
            String icolocation = getResources().getString(R.string.ep_weather_icon);

            for (int i = 0; i < 10; i++) {
                // data will be in the form of a json object
                JSONObject currentDay = (JSONObject) data.get(i);
                // extract json values from response
                String date = currentDay.getString("datetime");
                double avg = currentDay.getDouble("temp");
                double min = currentDay.getDouble("min_temp");
                double max = currentDay.getDouble("max_temp");
                JSONObject weather = currentDay.getJSONObject("weather");
                String desc = weather.getString("description");

                // retrieve the icon name for the forcast
                String iconAlias = weather.getString("icon");

                // join the pieces of the URI
                String iconURI = String.format("%s%s%s", icolocation, iconAlias, icoextension);

                // encapsulate the values for retrieval by the recycler view
                WeatherDate Day = new WeatherDate(i, iconURI, i + " Day(s) away: " + date, min, max, avg, desc);
                if (i == 0) {
                    Day = new WeatherDate(i, iconURI, "Today: " + date, min, max, avg, desc);
                }

                /*Add the current weather to the field [list]*/
                currentWeatherData.add(Day);
                Log.w(TAG, "\n[ i = " + i + " ]\n\tDate: " + date + "\n\tAvg:" + avg + "\n\tMin:" + min + "\n\tMax" + max + "\n\tdesc:" + desc);
            }
        } catch (JSONException e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        } finally {

            //display the location correlating to the data we are displaying
            ((TextView) getView().findViewById(R.id.city_head_textview)).setText(mCity);
            ((TextView) getView().findViewById(R.id.state_head_textview)).setText(mState);
            /*After retrieving the data, make it visible and remove the wait-fragment*/
            myRV.setAdapter(new MyWeatherDateRecyclerViewAdapter(currentWeatherData, mListener));
            mListener.onWaitFragmentInteractionHide();
        }

    }

    /*
        helper method to send a post request to the server, containing the state abbreviation
        and city (in JSON format)


    */
    private void Coordinates_getCurrentWeatherData(final double Latitude, final double Longitude) {
        mLon = Longitude;
        mLat = Latitude;
        String mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.base_url))
                .appendPath(getString(R.string.ep_weather))
                .appendPath(getString(R.string.ep_weather_bycoordinate))
                .build()
                .toString();
        JSONObject request = new JSONObject();
        try {
            request.put("lon", mLon);
            request.put("lat", mLat);
        } catch (JSONException e) {
            Log.d(TAG, e.toString());
            e.printStackTrace();
        }
        Log.d(TAG, mSendUrl);
        new SendPostAsyncTask.Builder(mSendUrl, request)
                .onPreExecute(this::PreWeatherRequest)
                .onPostExecute(this::PostWeatherRequest)
                .onCancelled(error -> Log.e(TAG, error))
                .build()
                .execute();

    }

//    /*overload, if no args are provided then default to uwt location*/
//    private void Coordinates_getCurrentWeatherData() {
//        this.Coordinates_getCurrentWeatherData(47.2446, 122.4376);
//    }

    /*Executed after on create, we set the adapter if the current weather data has already been retrieved*/
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weatherdate_list, container, false);
        myRV = view.findViewById(R.id.weatherlist); //retrieve the list contained within the fragment
        // Set the adapter and layout manager
        if (myRV != null) {
            Context context = myRV.getContext();
            if (mColumnCount <= 1) {
                myRV.setLayoutManager(new LinearLayoutManager(context));
            } else {
                myRV.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            if (currentWeatherData != null)
                myRV.setAdapter(new MyWeatherDateRecyclerViewAdapter(currentWeatherData, mListener));
        }

        return view;
    }


    /*
     * Ensure that the context extends our listener
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener {
        void onWeatherListItemFragmentInteraction(WeatherDate item);
    }
}
