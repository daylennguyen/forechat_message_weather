package thedankdevs.tcss450.uw.edu.tddevschat.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class WeatherData {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<WeatherDate> ITEMS = new ArrayList<WeatherDate>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, WeatherDate> ITEM_MAP = new HashMap<String, WeatherDate>();

    private static final int COUNT = 5;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(WeatherDate item) {
        ITEMS.add(item);
        ITEM_MAP.put(Integer.toString(item.id), item);
    }

    private static WeatherDate createDummyItem(int position) {
        return new WeatherDate(position, "5-5-1997", 69, 69, 69, "69");
    }


    /**
     * A dummy item representing a piece of content.
     */
    public static class WeatherDate {
        public final int id;

        public final String mDateTxtView;
        public final double mLoTxtView;
        public final double mHiTxtView;
        public final double mAvgTxtView;
        public final String mConditionTxtView;

        public WeatherDate(int id, String mDateTxtView, double mLoTxtView, double mHiTxtView, double mAvgTxtView, String mConditionTxtView) {
            this.id = id;
            this.mDateTxtView = mDateTxtView;
            this.mLoTxtView = mLoTxtView;
            this.mHiTxtView = mHiTxtView;
            this.mAvgTxtView = mAvgTxtView;
            this.mConditionTxtView = mConditionTxtView;

        }

//        @Override
//        public String toString() {
//            return content;
//        }
    }
}
