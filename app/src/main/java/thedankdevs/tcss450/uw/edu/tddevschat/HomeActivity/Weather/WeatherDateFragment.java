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

import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.WaitFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.dummy.WeatherData;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class WeatherDateFragment extends Fragment {
    private static final String TAG = "WEATHER";
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private String mState, mCity;

    private List<WeatherData.WeatherDate> currentWeatherData;
    private RecyclerView myRV;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public WeatherDateFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static WeatherDateFragment newInstance(int columnCount) {
        WeatherDateFragment fragment = new WeatherDateFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getCurrentWeatherData();

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void PreWeatherRequest() {
        mListener.onWaitFragmentInteractionShow();
    }

    private void PostWeatherRequest(final String result) {
        try {
            //This is the result from the web service
            currentWeatherData = new ArrayList<>();
            JSONObject res = new JSONObject(result);
            JSONArray data = res.getJSONArray("data");

            for (int i = 0; i < 10; i++) {
                JSONObject currentDay = (JSONObject) data.get(i);
                String date = currentDay.getString("datetime");
                double avg = currentDay.getDouble("temp");
                double min = currentDay.getDouble("min_temp");
                double max = currentDay.getDouble("max_temp");
                String desc = currentDay.getJSONObject("weather").getString("description");
                WeatherData.WeatherDate Day = new WeatherData.WeatherDate(i, i + " Day(s) away: " + date, min, max, avg, desc);
                if (i == 0) {
                    Day = new WeatherData.WeatherDate(i, "Today: " + date, min, max, avg, desc);
                }
                currentWeatherData.add(Day);
                Log.w(TAG, "\n[ i = " + i + " ]\n\tDate: " + date + "\n\tAvg:" + avg + "\n\tMin:" + min + "\n\tMax" + max + "\n\tdesc:" + desc);
            }
            myRV.setAdapter(new MyWeatherDateRecyclerViewAdapter(currentWeatherData, mListener));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mListener.onWaitFragmentInteractionHide();
    }


    private void getCurrentWeatherData(String StateAbbreviation, String City) {
        mState = StateAbbreviation;
        mCity = City;
        String mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.base_url))
                .appendPath(getString(R.string.ep_weather))
                .build()
                .toString();
        JSONObject request = new JSONObject();
        try {
            request.put("city", City.toUpperCase());
            request.put("state", StateAbbreviation.toUpperCase());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.w("HERE? ", mSendUrl);
        new SendPostAsyncTask.Builder(mSendUrl, request)
                .onPreExecute(this::PreWeatherRequest)
                .onPostExecute(this::PostWeatherRequest)
                .onCancelled(error -> Log.e(TAG, error))
                .build()
                .execute();

    }


    public void getCurrentWeatherData() {
        getCurrentWeatherData("WA", "TACOMA");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_weatherdate_list, container, false);
        myRV = view.findViewById(R.id.weatherlist);

        // Set the adapter
        if (myRV != null) {
            Context context = myRV.getContext();
            if (mColumnCount <= 1) {
                myRV.setLayoutManager(new LinearLayoutManager(context));
            } else {
                myRV.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            if (currentWeatherData != null)
                myRV.setAdapter(new MyWeatherDateRecyclerViewAdapter(currentWeatherData, mListener));
            ((TextView) view.findViewById(R.id.city_head_textview)).setText(mCity);
            ((TextView) view.findViewById(R.id.state_head_textview)).setText(mState);


        }

        return view;
    }


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
        void onWeatherListItemFragmentInteraction(WeatherData.WeatherDate item);
    }
}
