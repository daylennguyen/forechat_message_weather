package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConnectionFragment.OnConnectionFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * @author Michelle Brown
 */
public class ConnectionFragment extends Fragment implements View.OnClickListener {

    /**
     * the button that lets you request a connection or open a new chat with an existing one*/
    Button mChatButton;
    /*
     * Connection information*/
    private String                                  mTheirEmail;
    private String                                  mTheirUsername;
    private String                                  mTheirFirstName;
    private String                                  mTheirLastName;
    private int                                     mOurChatID;
    private boolean                                 mIsMine = false;
    /**
     * The current user's credentials*/
    private Credentials                             mCredentials;

    private OnConnectionFragmentInteractionListener mListener;

    /**
     * required empty constructor
     */
    public ConnectionFragment() {
    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        if ( context instanceof OnConnectionFragmentInteractionListener ) {
            mListener = ( OnConnectionFragmentInteractionListener ) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnFragmentInteractionListener" );
        }
    }

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        if ( getArguments() != null ) {
            Connection theConnection = ( Connection ) getArguments().getSerializable( getString( R.string.key_connection_connection ) );
            mCredentials = ( Credentials ) getArguments().getSerializable( getString( R.string.key_credential ) );
            if ( theConnection != null ) {
                mTheirEmail = theConnection.getEmail();
            }
            assert theConnection != null;
            mTheirUsername = theConnection.getUsername();
            mTheirFirstName = theConnection.getFirstName();
            mTheirLastName = theConnection.getLastName();
            mOurChatID = theConnection.getChatID();
            mIsMine = theConnection.getIsMine();
        }
    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        View v = inflater.inflate( R.layout.fragment_connection, container, false );
        mChatButton = v.findViewById( R.id.btn_connection_openchat );
        if ( mOurChatID > 0 ) {
            mChatButton.setText( R.string.connection_chatinitialized );
        } else if ( !mIsMine ) {
            mChatButton.setText( R.string.connection_requestconnection );
        } else if ( mOurChatID < 0 ) {
            mChatButton.setText( R.string.connection_chatuninitialized );
        }
        mChatButton.setOnClickListener( this );
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if ( getArguments() != null ) { //the arguments will have been retrieved already
            TextView tv = Objects.requireNonNull( getActivity() ).findViewById( R.id.tv_connection_username );
            tv.setText( mTheirUsername );
            Log.d( "MICHELLE", "is the connection mine? " + mIsMine );
            if ( mIsMine ) {
                tv = getActivity().findViewById( R.id.tv_connection_firstname );
                tv.setText( mTheirFirstName );
                tv = getActivity().findViewById( R.id.tv_connection_lastname );
                tv.setText( mTheirLastName );
            } else {
                tv = getActivity().findViewById( R.id.tv_connection_firstname );
                tv.setText( "" );
                tv = getActivity().findViewById( R.id.tv_connection_lastname );
                tv.setText( "" );
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull( getActivity() ).setTitle( "Connections" );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * The onClidkListener for the Connection Fragment.
     * When the button is clicked, a connection request will be sent
     * or a chat will be opened
     *
     * @param v the view that was clicked
     */
    @Override
    public void onClick( View v ) {
        if ( mListener != null ) {
            switch ( v.getId() ) {
                case R.id.btn_connection_openchat:
                    if ( !mIsMine ) {
                        requestConnection();
                    } else {
                        mListener.onOpenChatInteraction( mOurChatID, mTheirUsername );
                    }
                    break;
            }
        }
    }

    /**
     * Call endpoint to request a connection with the other user
     */
    private void requestConnection() {
        //loadingFragment = frag;
        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put( "myMemberID", mCredentials.getMemberID() );
            requestJson.put( "myUsername", mCredentials.getUsername() );
            requestJson.put( "theirUsername", mTheirUsername );
            Log.d( "REQUESTING CONNECTION", requestJson.toString() );
        } catch ( JSONException e ) {
            Log.wtf( "JSON", "Error creating JSON: " + e.getMessage() );
        }
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( getString( R.string.base_url ) )
                .appendPath( getString( ( R.string.ep_connections ) ) )
                .appendPath( getString( R.string.ep_requestConnection ) )
                .build();
        Log.w( "URL for requesting a connection:", uri.toString() );
        new SendPostAsyncTask.Builder( uri.toString(), requestJson )
                .onPostExecute( this::handleRequestOnPostExecute )
                .onCancelled( error -> Log.e( "ERROR MICHELLE", error ) )
                .build().execute();
    }


    /**
     * The method that gets called once we get a response from our web service
     *
     * @param result the String representation of the result
     *               that we got back from our web service
     */
    private void handleRequestOnPostExecute( String result ) {
        try {
            Log.w( "REQUEST CONNECTION POST RESULT", result );
            //This is the result from the web service
            JSONObject res = new JSONObject( result );

            String toastMsg;
            if ( res.has( "success" ) && res.getBoolean( "success" ) ) {
                toastMsg = getString( R.string.connection_requestsent );
            } else {
                JSONObject row = res.getJSONObject("row");
                if ( row.has( "verified" ) ) {
                    mChatButton.setText(R.string.connection_pendingrequest);
                    toastMsg = getString(R.string.connection_requestfailedpending);
                } else {
                    toastMsg = getString(R.string.connection_requestfailed);
                }
            }
            Toast toast = Toast.makeText( Objects.requireNonNull( getActivity() ).getApplicationContext(),
                    toastMsg, Toast.LENGTH_SHORT );
            toast.show();
            mChatButton.setEnabled( false );
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnConnectionFragmentInteractionListener {
        void onOpenChatInteraction( int chatID, String username );
    }
}
