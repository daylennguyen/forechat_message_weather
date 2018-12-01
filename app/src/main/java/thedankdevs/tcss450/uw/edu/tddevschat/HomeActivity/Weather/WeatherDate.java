package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Weather;

public class WeatherDate {
    public final int    id;
    public final String icon;
    final        String mDateTxtView;
    final        double mLoTxtView;
    final        double mHiTxtView;
    final        double mAvgTxtView;
    final        String mConditionTxtView;
    String mMetric;

    WeatherDate( int id, String metric, String icon, String mDateTxtView, double mLoTxtView, double mHiTxtView, double mAvgTxtView, String mConditionTxtView ) {
        this.id = id;
        mMetric = metric;
        this.icon = icon;
        this.mDateTxtView = mDateTxtView;
        this.mLoTxtView = mLoTxtView;
        this.mHiTxtView = mHiTxtView;
        this.mAvgTxtView = mAvgTxtView;
        this.mConditionTxtView = mConditionTxtView;

    }
}

