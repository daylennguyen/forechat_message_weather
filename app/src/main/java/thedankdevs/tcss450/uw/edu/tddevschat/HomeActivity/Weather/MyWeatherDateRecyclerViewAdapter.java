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
import thedankdevs.tcss450.uw.edu.tddevschat.dummy.DummyContent.DummyItem;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyWeatherDateRecyclerViewAdapter extends RecyclerView.Adapter<MyWeatherDateRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    MyWeatherDateRecyclerViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener) {
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
//        holder.mIdView.setText(mValues.get(position).id);
//        holder.mContentView.setText(mValues.get(position).content);
//        holder.mAvgTxtView.setText("avg");
        holder.mDateTxtView.setText(R.string.date_sampl_weath);
        holder.mLoTxtView.setText(R.string.lo_sampl_weath);
        holder.mHiTxtView.setText(R.string.hi_sampl_weath);
        holder.mAvgTxtView.setText(R.string.avg_sampl_weath);
        holder.mConditionTxtView.setText(R.string.condi_sampl_weath);
//        holder.mImg.set
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
        DummyItem mItem;

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
