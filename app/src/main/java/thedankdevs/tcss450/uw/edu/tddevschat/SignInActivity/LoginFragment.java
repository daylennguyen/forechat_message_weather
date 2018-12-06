package thedankdevs.tcss450.uw.edu.tddevschat.SignInActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import org.json.JSONException;
import org.json.JSONObject;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.WaitFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

import java.util.Objects;


/**
 * <p>This class handles the user input in LoginFragment and validates each field. If all fields are valid
 * and the server response is ok, then OnFragmentInteractionListener is attached
 * for onLoginSuccess().</p>
 *
 * <p>This class also handles the user event in the case that the Register Button is clicked.
 * In this case, the OnFragmentInteractionListener is attached to onRegister()</p>
 * <p>
 * <p>
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 *
 * @author Michelle Brown, Bryan Santos, Emmett Kang
 * @version 1 December 2018
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    /**
     * The tag used for Logcat messages for this class
     */
    private static final String TAG = LoginFragment.class.getSimpleName();

    /**
     * Helper object used to make a JSON object of the user's email and password
     */
    private Credentials mCredentials;

    /**
     * OnFragmentInteractionListener for this Fragment
     */
    private OnFragmentInteractionListener mListener;

    /**
     * Inflated view of fragment_login.xml
     */
    private View mView;

    /**
     * The EditText field for email
     */
    private EditText mEmailField;
    private String   mEmail;
    private String   mUsername;
    private String   mFirstname;
    private String   mLastname;
    /**
     * The EditText field for password
     */
    private EditText mPasswordField;
    private String   mPassword;

    /**
     * The retrieved id of the member
     */
    private int mMemberID;

    /**
     * The unique firebase token
     */
    private String mFirebaseToken;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Attaches mListener to appropriate OnFragmentInteraction methods depending
     * on the button clicked.
     * <p>
     * If login button was clicked, it validates the user input and sends a request to the server.
     * If successful, OnFragmentInteraction is attached to mListener.
     *
     * @param viewClicked view object that was clicked
     */

    @Override
    public void onClick( View viewClicked ) {
        mEmailField = Objects.requireNonNull( getActivity() ).findViewById( R.id.et_login_email );
        mPasswordField = getActivity().findViewById( R.id.et_login_password );
        switch ( viewClicked.getId() ) {
            case R.id.btn_login_register:
                mListener.onRegisterClicked();
                break;
            case R.id.btn_login_login:
                mEmail = mEmailField.getText().toString();
                mPassword = mPasswordField.getText().toString();
                //getMemberID();//CHANGED
                getFirebaseToken( mEmail, mPassword ); //THIS IS NOW DONE AFTER getMemberID
                //to guarantee that we get the memberID before we continue.
              /*  if (!isLoginValid(email, password)) {
                    break;
                }
                buildLoginServerCredentials(email, password);*/
                // mListener is attached in handleLoginOnPost();
                break;
            default:
                Log.e( TAG, "Error when button is clicked in Login Fragment" );
        }
    }

    /**
     * Gets the id of the current user by their supplied email using an Asynchronous task.
     */
    private void getMemberID() {
        JSONObject memberInfo = new JSONObject();
        Log.d( "VALUE OF EMAIL", mEmail );
        try {
            memberInfo.put( "email", mEmail );
            Log.d( "JSON BODY", memberInfo.toString() );
        } catch ( JSONException e ) {
            Log.wtf( "JSON", "Error creating JSON: " + e.getMessage() );
        }
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( getString( R.string.base_url ) )
                .appendPath( getString( R.string.ep_member ) )
                .appendPath( getString( R.string.ep_getID ) )
                .build();
        Log.w( "URL for getting memberID of user:", uri.toString() );

        new SendPostAsyncTask.Builder( uri.toString(), memberInfo )
                .onPreExecute( this::handleIDOnPre )
                .onPostExecute( this::handleIDOnPostExecute )
                .onCancelled( error -> Log.e( "ERROR MICHELLE", error ) )
                .build().execute();
    }

    private void handleIDOnPre() {
        mListener.onWaitFragmentInteractionShow();
        Log.d( "GETTING MEMBERID", "pre" );
    }

    /**
     * Handles what happens after a Post request is made to get the memberID
     *
     * @param result
     */
    private void handleIDOnPostExecute( String result ) {

        Log.d( "Debug Bryan", "Handle Id on post execute" );


        try {
            JSONObject resultsJSON  = new JSONObject( result );
            JSONObject jsonMemberID = resultsJSON.getJSONObject( "memberID" );
            mMemberID = jsonMemberID.getInt( "memberid" );
            mUsername = jsonMemberID.getString( "username" );
            mFirstname = jsonMemberID.getString( "firstname" );
            mLastname = jsonMemberID.getString( "lastname" );

            mCredentials.setMemberID( mMemberID );
            mCredentials.setUsername( mUsername );
            mCredentials.setFirstname( mFirstname );
            mCredentials.setLastname( mLastname );

            Log.d( "VALUE OF MEMBERID", String.valueOf( mMemberID ) );
            Log.d( getClass().getSimpleName(), "Value of JSON Post: " + resultsJSON );
            mListener.onLoginSuccess( mCredentials );
            mListener.onWaitFragmentInteractionHide();
            // getFirebaseToken();
        } catch ( JSONException e ) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e( "Could not retrieve memberID", e.getMessage() );
            //TODO: how do i make sure that we can still get the memberID, or just quit?
        }

    }

    private void doLogin(/*String email, String password*/ ) {
        Boolean valid = isLoginValid( mEmail, mPassword );
        if ( valid ) {
            buildLoginServerCredentials( mEmail, mPassword );
        } else {
            Log.w( "Login Valid", "It ain't valid fam" );
        }

    }

    /**
     * Validates the strings for email and password. If invalid, an error is set
     * to each corresponding EditText fields.
     *
     * @param email    User input for email
     * @param password User input for password
     * @return validity of the email and password
     */
    private boolean isLoginValid( String email, String password ) {

        EditText[] fields  = { mEmailField, mPasswordField };
        String[]   strings = { email, password };

        boolean valid = true;

        for ( int i = 0; i < fields.length; i++ ) {
            if ( strings[ i ].isEmpty() ) {
                fields[ i ].setError( "This field cannot be empty" );
                valid = false;
            }
        }

        if ( !isEmailValid( email ) ) {
            mEmailField.setError( "Email must contain a single '@' symbol" );
            valid = false;
        }

        return valid;
    }

    /**
     * Checks if the email contains a single '@' symbol.
     *
     * @param email email to be validated
     * @return true if email only has a single '@' symbol
     */
    private boolean isEmailValid( String email ) {
        int count = 0; // character '@' count;
        for ( int i = 0; i < email.length(); i++ ) {
            if ( email.charAt( i ) == '@' ) {
                count++;
            }
        }

        return count == 1;
    }

    /**
     * Builds the URI path to the server.
     * Also builds the Credentials object with the email and password given by the user
     * to be used to create the JSON object to be sent in a POST request to the server.
     *
     * @param email    User input for email
     * @param password User input for password
     */

    private void buildLoginServerCredentials( String email, String password ) {

        //build the web service URL
        //getMemberID();
        //Log.d( "MEMBERID", String.valueOf( mMemberID ) );
        mCredentials = new Credentials.Builder( email, password )
                .build();
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( getString( R.string.base_url ) )
                .appendPath( getString( R.string.ep_login ) )
                .appendPath( getString( R.string.ep_with_token ) )
                .build();

        //build the JSONObject
        JSONObject msg = mCredentials.asJSONObject();
        Log.d( "Debug Bryan", msg.toString() );

        try {
            msg.put( "token", mFirebaseToken );
        } catch ( JSONException e ) {
            e.printStackTrace();
        }


        //instantiate and execute the AsyncTask
        new SendPostAsyncTask.Builder( uri.toString(), msg )
                .onPreExecute( this::handleLoginOnPre )
                .onPostExecute( this::handleLoginOnPost )
                .onCancelled( this::handleErrorsInTask )
                .build()
                .execute();


    }

    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        if ( context instanceof OnFragmentInteractionListener ) {
            mListener = ( OnFragmentInteractionListener ) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnFragmentInteractionListener" );
        }
    }

    /**
     * Inflates fragment_login.xml, and attaches Register and Login button with
     * onClick Listeners.
     *
     * @param inflater           LayoutInflater
     * @param container          ViewGroup
     * @param savedInstanceState Bundle
     * @return inflated view of fragment_login.xml
     */
    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        mView = inflater.inflate( R.layout.fragment_login, container, false );

        Button b = mView.findViewById( R.id.btn_login_login );
        b.setOnClickListener( this );
        b = mView.findViewById( R.id.btn_login_register );
        b.setOnClickListener( this );
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                Objects.requireNonNull( getActivity() ).getSharedPreferences(
                        getString( R.string.keys_shared_prefs ),
                        Context.MODE_PRIVATE );
        //retrieve the stored credentials from SharedPrefs
        if ( prefs.contains( getString( R.string.keys_prefs_email ) ) &&
                prefs.contains( getString( R.string.keys_prefs_password ) ) ) {
            Log.d( "DEBUG Bryan", "I have stored preferences" );
            mEmail = prefs.getString( getString( R.string.keys_prefs_email ), "" );
            mPassword = prefs.getString( getString( R.string.keys_prefs_password ), "" );
            Log.d( "DEBUG Bryan", "mEmail: " + mEmail + " mPassword: " + mPassword );

            //Load the two login EditTexts with the credentials found in SharedPrefs
            EditText emailEdit = getActivity().findViewById( R.id.et_login_email );
            emailEdit.setText( mEmail );
            EditText passwordEdit = getActivity().findViewById( R.id.et_login_password );
            passwordEdit.setText( mPassword );
            //getMemberID(); //CHANGED
            getFirebaseToken( mEmail, mPassword ); //THIS IS NOW DONE AFTER getMemberID
            //to guarantee that we get the memberID before we continue.
            //buildLoginServerCredentials(email, password);

        }

        Log.d( "DEBUG Bryan", "I dont have stored preferences; exiting onStart" );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Get firebase token and insert to the database, then do login operations.
     * @param email user's email
     * @param password user's pasword
     * @author Charles Bryan, Emmett Kang
     */
    private void getFirebaseToken( final String email, final String password ) {
        //add this app on this device to listen for the topic all
        FirebaseMessaging.getInstance().subscribeToTopic( "all" );
        //the call to getInstanceId happens asynchronously. task is an onCompleteListener
        //similar to a promise in JS.
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener( task -> {
                    if ( !task.isSuccessful() ) {
                        Log.w( "FCM: ", "getInstanceId failed", task.getException() );
                        mListener.onWaitFragmentInteractionHide();
                        return;
                    }
                    // Get new Instance ID token
                    mFirebaseToken = Objects.requireNonNull( task.getResult() ).getToken();
                    Log.d( "FCM: ", mFirebaseToken );
                    //the helper method that initiates login service
                    doLogin();
                } );
        //no code here. wait for the Task to complete.
    }

    /**
     * Handle the setup of the UI before the HTTP call to the webservice.
     */
    private void handleLoginOnPre() {
        mListener.onWaitFragmentInteractionShow();
    }

    /**
     * Handle onPostExecute of the AsynceTask. The result from our webservice is
     * a JSON formatted String. Parse it for success or failure.
     *
     * @param result the JSON formatted String response from the web service
     */
    private void handleLoginOnPost( String result ) {
        try {
            Log.d( "JSON result login", result );
            JSONObject resultsJSON = new JSONObject( result );
            boolean    success     = resultsJSON.getBoolean( "success" );

            if ( success ) {

                saveCredentials( mCredentials );
                //Login was successful. Inform the Activity so it can do its thing.
                getMemberID();
            } else {
                String verified = resultsJSON.getString( "message" );
                if ( verified.equals( "NV" ) ) { //If User's account was not verified, send another one.
                    resendVerificationCode();
                } else {
                    //Login was unsuccessful. Don’t switch fragments and inform the user
                    ( ( TextView ) Objects.requireNonNull( getView() ).findViewById( R.id.et_login_email ) )
                            .setError( "Login Unsuccessful" );
                }
            }
        } catch ( JSONException e ) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e( "JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage() );
            mListener.onWaitFragmentInteractionHide();
            ( ( TextView ) Objects.requireNonNull( getView() ).findViewById( R.id.et_login_email ) )
                    .setError( "Login Unsuccessful" );
        } catch ( Exception e ) {
            Log.d( "Debug Bryan", "Not sure what this runtime exception is: " + e );
            e.printStackTrace();
        }
    }

    /**
     * Handle errors that may occur during the AsyncTask.
     *
     * @param result the error message provide from the AsyncTask
     */
    private void handleErrorsInTask( String result ) {
        Log.e( "ASYNCT_TASK_ERROR", result );
    }


    private void saveCredentials( final Credentials credentials ) {
        SharedPreferences prefs =
                Objects.requireNonNull( getActivity() ).getSharedPreferences(
                        getString( R.string.keys_shared_prefs ),
                        Context.MODE_PRIVATE );
        //Store the credentials in SharedPrefs
        prefs.edit().putString( getString( R.string.keys_prefs_email ), credentials.getEmail() ).apply();
        prefs.edit().putString( getString( R.string.keys_prefs_password ), credentials.getPassword() ).apply();
    }

    /**
     * This method  sends a verification email to the user's email while changing the verification code on
     * the database for re-verification since the user might have not verified during registration.
     *
     * @Author Emmett Kang
     * @Version 11 November 2018
     */
    private void resendVerificationCode() {
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( getString( R.string.base_url ) )
                .appendPath( getString( R.string.ep_resend_vericode ) )
                .build();
        //build the JSONObject
        JSONObject msg = mCredentials.asJSONObject(); //Create a JSONObject for credential.

        //instantiate and execute the AsyncTask for sending new verification code.
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
     * Check the result if email was sent.
     *
     * @param result is from the server side of the if sending email was a success.
     * @Author Emmett Kang
     * @Version 11 November 2018
     */
    private void handleSendVCOnPost( String result ) {
        try {
            Log.d( "JSON result after sending veri code", result );
            JSONObject resultsJSON = new JSONObject( result );
            boolean    success     = resultsJSON.getBoolean( "success" );
            mListener.onWaitFragmentInteractionHide();
            if ( success ) {

                //After sending the email, send the user to verification fragment.
                mListener.onNotVerified( mCredentials );
            } else {
                ( ( TextView ) Objects.requireNonNull( getView() ).findViewById( R.id.et_login_email ) )
                        .setError( "Sending email unsuccessful" );
            }
        } catch ( JSONException e ) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e( "JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage() );
            mListener.onWaitFragmentInteractionHide();
            ( ( TextView ) Objects.requireNonNull( getView() ).findViewById( R.id.et_login_email ) )
                    .setError( "Verification couldn't be sent" );
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener extends WaitFragment.OnFragmentInteractionListener {
        void onLoginSuccess( Credentials credentials );

        void onNotVerified( Credentials credentials );

        void onRegisterClicked();
    }
}
