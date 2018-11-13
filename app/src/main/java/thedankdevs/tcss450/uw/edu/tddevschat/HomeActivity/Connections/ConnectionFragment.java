package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import thedankdevs.tcss450.uw.edu.tddevschat.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConnectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * @author Michelle Brown
 */
public class ConnectionFragment extends Fragment {

    private String mEmail;
    private String mUsername;
    private String mFirstName;
    private String mLastName;
    private int mChatID;

    private View mChatButton;

    private OnFragmentInteractionListener mListener;

    public ConnectionFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEmail = getArguments().getString(getString(R.string.key_connection_email));
            mUsername = getArguments().getString(getString(R.string.key_connection_username));
            mFirstName = getArguments().getString(getString(R.string.key_connection_first));
            mLastName = getArguments().getString(getString(R.string.key_connection_last));
            mChatID = getArguments().getInt(getString(R.string.key_connection_chatID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connection, container, false);
        mChatButton = v.findViewById(R.id.btn_connection_openchat);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getArguments() != null) { //the arguments will have been retrieved already
            TextView tv = getActivity().findViewById(R.id.tv_connection_username);
            tv.setText(mUsername);
            tv = getActivity().findViewById(R.id.tv_connection_firstname);
            tv.setText(mFirstName);
            tv = getActivity().findViewById(R.id.tv_connection_lastname);
            tv.setText(mLastName);
        }
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

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onOpenChatInteraction(mChatID, mEmail);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        void onOpenChatInteraction(int chatID, String email);
    }
}
