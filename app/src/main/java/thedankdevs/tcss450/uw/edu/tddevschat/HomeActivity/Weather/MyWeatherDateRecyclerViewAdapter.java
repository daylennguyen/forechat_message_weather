package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Weather;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Weather.WeatherDateFragment.OnListFragmentInteractionListener;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.dummy.WeatherData.WeatherDate;

/**
 * {@link RecyclerView.Adapter} that can display a {@link WeatherDate} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyWeatherDateRecyclerViewAdapter extends RecyclerView.Adapter<MyWeatherDateRecyclerViewAdapter.ViewHolder> {

    private final List<WeatherDate> mValues;
    private final OnListFragmentInteractionListener mListener;

    MyWeatherDateRecyclerViewAdapter(List<WeatherDate> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_weatherdate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.mDateTxtView.setText(mValues.get(position).mDateTxtView);
        holder.mLoTxtView.setText(String.format("Lo: %s", Double.toString(mValues.get(position).mLoTxtView)));
        holder.mHiTxtView.setText(String.format("Hi: %s", Double.toString(mValues.get(position).mHiTxtView)));
        holder.mAvgTxtView.setText(String.format("Avg:%s", Double.toString(mValues.get(position).mAvgTxtView)));
        holder.mConditionTxtView.setText(mValues.get(position).mConditionTxtView);
        /*TODO set the img for the weather*/
        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                mListener.onWeatherListItemFragmentInteraction(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mDateTxtView;
        final TextView mLoTxtView;
        final TextView mHiTxtView;
        final TextView mAvgTxtView;
        final TextView mConditionTxtView;
        final ImageView mImg;
        WeatherDate mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDateTxtView = view.findViewById(R.id.tv_weather_date);
            mLoTxtView = view.findViewById(R.id.tv_weather_low);
            mConditionTxtView = view.findViewById(R.id.tv_weather_condition);
            mHiTxtView = view.findViewById(R.id.tv_weather_hi);
            mAvgTxtView = view.findViewById(R.id.tv_weather_avg);
            mImg = view.findViewById(R.id.weather_img);
        }

//        @Override
//        public String toString() {
//            return super.toString() + " '" + mContentView.getText() + "'";
//        }
    }
}
