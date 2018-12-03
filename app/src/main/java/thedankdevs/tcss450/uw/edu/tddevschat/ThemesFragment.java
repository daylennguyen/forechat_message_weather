package thedankdevs.tcss450.uw.edu.tddevschat;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.ThemeUtils;

import java.util.Objects;


/**

 */
public class ThemesFragment extends Fragment {

    private final String TAG = getClass().getSimpleName();
    private SharedPreferences mSharedPref;
    private OnFragmentInteractionListener mListener;

    public ThemesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        mSharedPref = Objects.requireNonNull( getActivity() ).getSharedPreferences( getString( R.string.current_theme ), Context.MODE_PRIVATE );

    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        View   v          = inflater.inflate( R.layout.fragment_themes, container, false );
        Switch switchView = v.findViewById( R.id.switch_theme );
        switchView.setOnClickListener( view -> onSwitch( switchView ) );
        String theme = mSharedPref.getString( getString( R.string.current_theme ), ThemeUtils.THEME_CLASSIC );
        assert theme != null;
        switch ( theme ) {
            case ThemeUtils.THEME_MINT:
                switchView.setChecked( true );
            default:

        }
        return v;
    }

    public void onSwitch( View v ) {
        Log.d( TAG, "I hit the switch" );
        Switch switchView = ( Switch ) v;
        String theme      = ThemeUtils.THEME_CLASSIC;
        if ( switchView.isChecked() ) {
            theme = ThemeUtils.THEME_MINT;
        }

        mSharedPref.edit().putString(getString(R.string.current_theme), theme).apply();
        mListener.onChangeTheme(theme);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onChangeTheme(String theme);

    }
}



