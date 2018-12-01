package thedankdevs.tcss450.uw.edu.tddevschat.SignInActivity;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.WaitFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

/**
 * This fragment is responsible for verifying user's email address. Once "VERIFY" button
 * gets clicked, it will activate the webserver part of verification method and verify the
 * status of the user. This class also provides options for resending verification code to
 * the same user if they accidentally deleted it.
 *
 * @version 11 November 2018
 * @Author Emmett Kang
 */
public class VerifyFragment extends Fragment implements View.OnClickListener {
    private Credentials mCredentials;

    /**
     * Interaction listener to interact with the activity.
     */
    private OnVerifyFragmentInteractionListener mListener;

    public VerifyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick( View v ) {
        if ( mListener != null ) {
            switch ( v.getId() ) {
                case R.id.btn_verify_verify:
                    attemptVerify(); //Try to verify
                    break;
                case R.id.btn_verify_resend:
                    resendVerificationCode();
                    break;

            }
        }

    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        if ( context instanceof RegisterFragment.OnRegisterFragmentInteractionListener ) {
            mListener = ( VerifyFragment.OnVerifyFragmentInteractionListener ) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnFragmentInteractionListener" );
        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        Bundle b = getArguments();
        mCredentials = ( Credentials ) b.getSerializable( getString( R.string.key_credential ) );
        // Inflate the layout for this fragment
        View   v            = inflater.inflate( R.layout.fragment_verify, container, false );
        Button verifyButton = v.findViewById( R.id.btn_verify_verify );
        verifyButton.setOnClickListener( this ); //Set click listener for the button.

        Button resendButton = v.findViewById( R.id.btn_verify_resend );
        resendButton.setOnClickListener( this ); //Set click listener for the button.
        return v;
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask( String result ) {
        Log.e( "ASYNCT_TASK_ERROR", result );
    }

    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleVerifyOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleVerifyOnPost( String result ) {
        try {

            Log.d( "JSON result verify: ", result );
            JSONObject resultsJSON = new JSONObject( result );
            boolean    success     = resultsJSON.getBoolean( "success" );

            mListener.onWaitFragmentInteractionHide();
            if ( success ) {
                //verification was successful. Inform the Activity so it can do its thing.
                mListener.onVerificationSuccess( mCredentials );
            } else {
                //verification was unsuccessful. Don’t switch fragments and inform the user
                ( ( TextView ) getView().findViewById( R.id.et_verify_code ) )
                        .setError( "Verification Unsuccessful" );
            }

        } catch ( JSONException e ) {
            //It appears that the web service didn’t return a JSON formatted String
            //or it didn’t have what we expected in it.
            Log.e( "JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage() );

            mListener.onWaitFragmentInteractionHide();
            ( ( TextView ) getView().findViewById( R.id.et_verify_code ) )
                    .setError( "Verification Unsuccessful" );
        }
    }

    /**
     * This is a helper method to attempt verification. This will get the verification code
     * and activate the async task to verify if there are any user with the same verification code
     * to be verified.
     */
    private void attemptVerify() {
        EditText verificationCode_field = getActivity().findViewById( R.id.et_verify_code );
        boolean  hasError               = false; //Indicator for any of the errors in the EditTexts.
        if ( verificationCode_field.getText().toString().isEmpty() ) {
            hasError = true;
            verificationCode_field.setError( "Verification code is empty!" );
        }
        if ( !hasError ) {
            JSONObject msg = new JSONObject();
            try {
                msg.put( "vericode", verificationCode_field.getText().toString() );
            } catch ( JSONException e ) {
                Log.wtf( "JSON", "Error creating JSON: " + e.getMessage() );
            }
            //build the web service URL
            Uri uri = new Uri.Builder()
                    .scheme( "https" )
                    .appendPath( getString( R.string.base_url ) )
                    .appendPath( getString( ( R.string.ep_verify ) ) )
                    .build();

            Log.w( "URL for Verify", uri.toString() );

            new SendPostAsyncTask.Builder( uri.toString(), msg )
                    .onPreExecute( this::handleVerifyOnPre )
                    .onPostExecute( this::handleVerifyOnPost )
                    .onCancelled( this::handleErrorsInTask )
                    .build().execute();
        }
    }

    /**
     * When invoked, this method will send a new verification code to the user and
     * Change the current verification code that is assigned to them to match the new one.
     */
    private void resendVerificationCode() {
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( getString( R.string.base_url ) )
                .appendPath( getString( R.string.ep_resend_vericode ) )
                .build();
        //build the JSONObject
        JSONObject msg = mCredentials.asJSONObject();

        //instantiate and execute the AsyncTask
        new SendPostAsyncTask.Builder( uri.toString(), msg )
                .onPreExecute( this::handleSendVCOnPre )
                .onPostExecute( this::handleSendVCOnPost )
                .onCancelled( this::handleErrorsInTask )
                .build()
                .execute();
    }

    private void handleSendVCOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * This asnyc task does not do anything other than returning if there was
     * a success in sending the email containg the verificatoin code.
     *
     * @param result is a whether email was sent.
     */
    private void handleSendVCOnPost( String result ) {
        try {
            Log.d( "JSON result after sending veri code", result );
            JSONObject resultsJSON = new JSONObject( result );
            boolean    success     = resultsJSON.getBoolean( "success" );
            mListener.onWaitFragmentInteractionHide();
            if ( success ) {
            } else {
                ( ( TextView ) getView().findViewById( R.id.et_login_email ) )
                        .setError( "uh.. Unsuccessful" );
            }
        } catch ( JSONException e ) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e( "JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage() );
            mListener.onWaitFragmentInteractionHide();
            ( ( TextView ) getView().findViewById( R.id.et_login_email ) )
                    .setError( "Verification couldn't be sent" );
        }
    }

    public interface OnVerifyFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener {
        void onVerificationSuccess( Credentials c );
    }


}
