package thedankdevs.tcss450.uw.edu.tddevschat;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.util.AndroidUtilsLight;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MemberSettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MemberSettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberSettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM = "credentials";
    private final String TAG = getClass().getSimpleName();
    private static final String[] labels =
            {"First Name", "Last Name", "Username", "Email", "Password", "Confirm Password"};


    private static final int MARGIN_10 = 10;
    private static final int MARGIN_5 = 5;
    private Credentials mCredentials;
    private OnFragmentInteractionListener mListener;
    private List<String> mCredentialsList;

    public MemberSettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MemberSettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemberSettingsFragment newInstance(Credentials credentials) {
        MemberSettingsFragment fragment = new MemberSettingsFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM, credentials);;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCredentialsList = new ArrayList<>();
        Serializable serial = getArguments().getSerializable(ARG_PARAM);
        if (getArguments() != null && serial instanceof Credentials) {
            mCredentials = (Credentials) serial;
            mCredentialsList.add(mCredentials.getFirstName());
            mCredentialsList.add(mCredentials.getLastName());
            mCredentialsList.add(mCredentials.getUsername());
            mCredentialsList.add(mCredentials.getEmail());

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_member_settings, container, false);
        Context context = getContext();




        return v;
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

        void changeMemberInfo(Map<String, String> info);
    }
}
