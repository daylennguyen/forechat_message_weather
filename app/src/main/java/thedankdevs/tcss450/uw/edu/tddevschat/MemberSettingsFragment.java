package thedankdevs.tcss450.uw.edu.tddevschat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.MemberSettingsNode;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


/**
 * This Fragment handles the actions when the user attempts
 * to change their personal information. It also creates a
 * new Credentials object with the updated information if
 * change information transaction was successful. This new
 * Credentials object is then stored in SharedPreferences.
 *
 * @author Bryan Santos
 * @version 12/05/2018
 */
public class MemberSettingsFragment extends Fragment {

    /** Key constants that matches the keys needed to make post request to the endpoint. */
    private static final String[]                      KEYS                =
            { "firstname", "lastname", "username", "email", "password" };

    /** Text to display when button can be changed. */
    private static final String                        CHANGE_BTN_TEXT     = "Change";

    /** Text to display of button when reverting back the EditText Field's information to default value. */
    private static final String                        UNDO_BTN_TEXT       = "Undo";

    /** Tag for Logcat. */
    private final        String                        TAG                 = getClass().getSimpleName();

    /** Current credentials of user (Unchanged). */
    private              Credentials                   mCredentials;

    private              OnFragmentInteractionListener mListener;

    /** Storage of default Credentials and a way to link key constants needed
     * to make post request to endpoint with its appropriate EditTextFields
     * and Credential Object's information.*/
    private              Map<Integer, UpdateValue>     mCredentialsMap;

    /** Storage of the values updated, making of KEYS and its counterpart data. */
    private              Map<String, String>           mUpdateMap;

    /** Layout that contains all EditTextFields and Buttons. */
    private              GridLayout                    mGridLayout;

    /**
     * A running counter of the Change/Undo Buttons pressed. Used for optimization
     * in an attempt to remove the need to loop through the child elements of mGridLayout
     * to check the state of the EditTextFields to check whether or not to enable the Apply Button.
     */
    private              int                           mEnabledChangeButtons;

    /** View being inflated by this class. */
    private View mView;

    private              Button                        mApplyButton;
    private              EditText                      mPassword_et;
    private              EditText                      mConfirmPassword_et;
    private              TextView                      mConfirmPassword_tv;



    public MemberSettingsFragment() {
        // Required empty public constructor
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        mCredentialsMap = new HashMap<>();
        if ( getArguments() != null ) {
            mCredentials = ( Credentials ) getArguments().getSerializable( getString( R.string.nav_membersettings ) );
            UpdateValue firstname = new UpdateValue( KEYS[ 0 ], mCredentials.getFirstName() );
            UpdateValue lastname  = new UpdateValue( KEYS[ 1 ], mCredentials.getLastName() );
            UpdateValue username  = new UpdateValue( KEYS[ 2 ], mCredentials.getUsername() );
            UpdateValue email     = new UpdateValue( KEYS[ 3 ], mCredentials.getEmail() );
            UpdateValue password  = new UpdateValue( KEYS[ 4 ], mCredentials.getPassword() );

            mCredentialsMap.put( R.id.et_member_settings_firstname, firstname );
            mCredentialsMap.put( R.id.et_member_settings_lastname, lastname );
            mCredentialsMap.put( R.id.et_member_settings_username, username );
            mCredentialsMap.put( R.id.et_member_settings_email, email );
            mCredentialsMap.put( R.id.et_member_settings_password, password );

            Log.d( TAG, "Finished initializing mCredentialsMap: " + mCredentialsMap );


        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState ) {

        // Inflate the layout for this fragment
        mView = inflater.inflate( R.layout.fragment_member_settings, container, false );

        mGridLayout = mView.findViewById( R.id.gl_member_settings );
        mPassword_et = mView.findViewById( R.id.et_member_settings_password );
        mConfirmPassword_et = mView.findViewById( R.id.et_member_settings_confirm_password );
        mConfirmPassword_tv = mView.findViewById( R.id.tv_member_settings_confirmpass );
        mApplyButton = mView.findViewById( R.id.btn_member_settings_apply );

        setDefault();

        return mView;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Helper method that sets default state of each
     * View object along with the appropriate
     * information from the mCredentials object; includes
     * setting onClickListeners, visibility state, and default texts.
     */
    private void setDefault() {

        mEnabledChangeButtons = 0;
        for ( int i = 0; i < mGridLayout.getChildCount(); i++ ) {
            View childView = mGridLayout.getChildAt( i );
            int  id        = childView.getId();
            if ( childView instanceof EditText ) {
                EditText et = ( EditText ) childView;
                if ( id == R.id.et_member_settings_confirm_password ) {
                    et.setVisibility( View.GONE );
                    mConfirmPassword_tv.setVisibility( View.GONE );
                    continue;
                }

                UpdateValue updateValue = mCredentialsMap.get( id );
                String      value       = null;
                if ( updateValue != null ) {
                    value = updateValue.getValue();
                }
                et.setText( value );
                et.setEnabled( false );

            } else if ( childView instanceof Button ) {
                Button btn = ( Button ) childView;
                if ( id == R.id.btn_member_settings_cancel ) {
                    btn.setOnClickListener( view -> onCancelButtonClick( childView ) );
                } else if ( id == R.id.btn_member_settings_apply ) {
                    btn.setOnClickListener( view -> onApplyButtonClick( childView ) );
                    btn.setEnabled( false );
                } else {
                    btn.setOnClickListener( view -> onChangeButtonClick( childView ) );
                    btn.setText( CHANGE_BTN_TEXT );
                    setButtonTags( btn );
                }

            }

        }
        mPassword_et.setTransformationMethod( PasswordTransformationMethod.getInstance() );
        mConfirmPassword_et.setTransformationMethod( PasswordTransformationMethod.getInstance() );
    }


    /**
     * Helper method that links a button to its EditTextField. Makes
     * use of the Tag attribute. Makes use of the actual EditTextField
     * object as the tag.
     * @param btn Button that the tag will be applied to
     */
    private void setButtonTags( Button btn ) {

        EditText et;
        int      id = btn.getId();
        switch ( id ) {
            case R.id.btn_member_settings_firstname:
                et = mView.findViewById( R.id.et_member_settings_firstname );
                break;
            case R.id.btn_member_settings_lastname:
                et = mView.findViewById( R.id.et_member_settings_lastname );
                break;
            case R.id.btn_member_settings_username:
                et = mView.findViewById( R.id.et_member_settings_username );
                break;
            case R.id.btn_member_settings_password:
                et = mView.findViewById( R.id.et_member_settings_password );
                break;
            default:
                Log.e( TAG, "Unknown button clicked" );
                throw new IllegalArgumentException();
        }

        btn.setTag( et );
    }

    /*******************************************************************************************
     *                         onClickListeners for Buttons
     *******************************************************************************************/
    public void onCancelButtonClick( View v ) {

        if ( mEnabledChangeButtons == 0 ) {
            return;
        } else if ( mEnabledChangeButtons < 0 ) {
            Log.d( TAG, "mEnabledChangeButtons < 0? = " + mEnabledChangeButtons );
        }

        Context             context = getContext();
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder( context, android.R.style.Theme_Material_Dialog_Alert );
        builder.setTitle( "Cancel Update" )
                .setMessage( "Are you sure you want to undo all your changes?" )
                .setPositiveButton( android.R.string.yes, ( dialog, which ) -> setDefault() )
                .setNegativeButton( android.R.string.no, ( dialog, which ) -> {
                    // do nothing
                } )
                .setIcon( R.drawable.ic_cancel_black_24dp )
                .show();

    }

    public void onChangeButtonClick( View v ) {

        Button   btnClicked = ( Button ) v;
        EditText et;
        if ( v.getTag() instanceof EditText ) {
            et = ( EditText ) v.getTag();
        } else {
            Log.e( TAG, "unknown tag" );
            throw new IllegalArgumentException();
        }
        int id = et.getId();
        if ( id != R.id.et_member_settings_password ) {
            flipTextFields( et, btnClicked );
        } else {
            flipPasswordFields( mPassword_et, mConfirmPassword_et, btnClicked );
        }

        // check apply button state
        if ( mEnabledChangeButtons > 0 && !mApplyButton.isEnabled() ) {
            mApplyButton.setEnabled( true );
        } else if ( mEnabledChangeButtons == 0 && mApplyButton.isEnabled() ) {
            mApplyButton.setEnabled( false );
        }


    }

    public void onApplyButtonClick( View v ) {

        if ( !areFieldsValid() ) {
            return;
        }

        // check if password fields are enabled and if they're matching
        if ( mPassword_et.isEnabled() ) {
            String pass         = mPassword_et.getText().toString();
            String confirm_pass = mConfirmPassword_et.getText().toString();
            if ( !arePasswordsValid( pass, confirm_pass ) ) {
                mPassword_et.setError( "Passwords do not match!" );
                return;
            }
        }

        Context             context = getContext();
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder( context, android.R.style.Theme_Material_Dialog_Alert );
        builder.setTitle( "Update Information" )
                .setMessage( "Are you sure you want to update the fields selected?" )
                .setPositiveButton( android.R.string.yes, ( dialog, which ) -> attachFragmentListener() )
                .setNegativeButton( android.R.string.no, ( dialog, which ) -> {
                    // do nothing
                } )
                .setIcon( R.drawable.ic_settings_thumb_up_black_24dp )
                .show();

    }

    /*****************************************************************************************/


    /******************************************************************************************
     *                                 Update View State
     ******************************************************************************************/

    /**
     * Flips the state of the EditTextFields (From enabled to disabled and vice versa)
     * depending on the button clicked. This method is used by all EditTextFields
     * except for the password fields.
     *
     * @param et EditTextField that is interacted with
     * @param btn Button that is interacted with.
     */
    private void flipTextFields( EditText et, Button btn ) {

        int key = et.getId();
        if ( !et.isEnabled() ) { // disabled to enabled
            et.setEnabled( true );
            btn.setText( UNDO_BTN_TEXT );
            mEnabledChangeButtons += 1;
        } else { // enabled to disabled
            et.setEnabled( false );
            et.setText( Objects.requireNonNull( mCredentialsMap.get( key ) ).getValue() );
            btn.setText( CHANGE_BTN_TEXT );
            mEnabledChangeButtons -= 1;
        }

        // clears previous error if there is one
        et.setError( null );
    }


    /**
     * Flips the EditTextFields (enable to disabled and vice versa). Only used
     * by the password fields.
     *
     * @param pass_et Password EditText
     * @param confirmpass_et Confirm Password EditText
     * @param btn Button interacted with
     */
    private void flipPasswordFields( EditText pass_et, EditText confirmpass_et, Button btn ) {
        int key = pass_et.getId();

        if ( !pass_et.isEnabled() ) { // disabled to enabled
            pass_et.setEnabled( true );
            confirmpass_et.setVisibility( View.VISIBLE );
            mConfirmPassword_tv.setVisibility( View.VISIBLE );
            btn.setText( UNDO_BTN_TEXT );
            pass_et.setTransformationMethod( HideReturnsTransformationMethod.getInstance() );
            mEnabledChangeButtons += 1;
        } else { // enabled to disabled
            pass_et.setEnabled( false );
            pass_et.setText( Objects.requireNonNull( mCredentialsMap.get( key ) ).getValue() );
            confirmpass_et.setVisibility( View.GONE );
            mConfirmPassword_tv.setVisibility( View.GONE );
            btn.setText( CHANGE_BTN_TEXT );
            pass_et.setTransformationMethod( PasswordTransformationMethod.getInstance() );
            mEnabledChangeButtons -= 1;
        }

        // clear error if there is one previously set
        pass_et.setError( null );

    }

    /********************************************************************************************/



    /*****************************************************************************************
     *                              Enabled Fields Validity Checker
     *****************************************************************************************/

    /**
     * First check to see if the all enabled fields matches
     * the minimum requirements: at least 3 characters and
     * new value not matching the old value.
     *
     * @return validity of the enabled EditTextFields
     */
    private boolean areFieldsValid() {

        boolean valid = true;

        for ( int i = 0; i < mGridLayout.getChildCount(); i++ ) {
            View childView = mGridLayout.getChildAt( i );

            if ( childView instanceof EditText ) {
                EditText et   = ( EditText ) childView;
                int      id   = et.getId();
                if ( et.isEnabled() && id != R.id.et_member_settings_confirm_password ) {
                    String newValue = et.getText().toString();
                    String oldValue = Objects.requireNonNull( mCredentialsMap.get( id ) ).getValue();
                    if ( newValue.length() < 3 ) {
                        et.setError( "Field must contain at least 3 characters" );
                        valid = false;
                    } else if ( oldValue.equalsIgnoreCase( newValue ) ) {
                        et.setError( "The new value cannot match the existing value" );
                        valid = false;
                    }
                }
            }
        }


        return valid;
    }

    /**
     * A check to see if the strings in the password field
     * matches the string in the confirm password field. Sets error
     * to password EditText if they don't match and returns false.
     *
     * @param pass String in Password EditText
     * @param confirm_pass String in Confirm Password EditText
     * @return validity of pass and confirm_pass matching
     */
    private boolean arePasswordsValid( String pass, String confirm_pass ) {
        boolean valid = true;

        // check if any of the password fields are less than 6 characters
        EditText[] et_arr = { mPassword_et, mConfirmPassword_et };
        for ( EditText et : et_arr ) {
            String et_string = et.getText().toString();
            if ( et_string.length() < 6 ) {
                et.setError( "Passwords must be at least 6 characters long" );
                valid = false;
            }
        }

        // check for equality (case sensitive)
        if ( valid && !pass.equals( confirm_pass ) ) {
            mConfirmPassword_et.setError( "Passwords must match" );
            valid = false;
        }

        return valid;
    }

    /*****************************************************************************************/


    /***************************************************************************************
     *                          Attaching OnFragmentInteractionListener
     ***************************************************************************************/


    /**
     * Stores the new information from the user into mUpdateMap.
     * and attaches the OnFragmentInteractionListener.
     */
    private void attachFragmentListener() {

        mUpdateMap = new HashMap<>();
        for (int i = 0; i < mGridLayout.getChildCount(); i++) {
            View childView = mGridLayout.getChildAt(i);
            if (childView instanceof EditText) {
                EditText et = (EditText) childView;
                int id = et.getId();
                if (et.isEnabled() && id != R.id.et_member_settings_confirm_password) {
                    UpdateValue obj = mCredentialsMap.get(id);
                    assert obj != null;
                    mUpdateMap.put(obj.getKey(), et.getText().toString());
                    Log.d(TAG, "Added object to mUpdateMap: " + obj.getKey() + "=" + et.getText().toString());

                }


            }
        }

        if (mUpdateMap.isEmpty()) {
            Log.d(TAG, "Why is my update map empty?");
        }

        MemberSettingsNode node = new MemberSettingsNode((HomeActivity) getActivity());
        node.onChangeMemberInfo(mUpdateMap, mCredentials.getMemberID());

    }

    /**************************************************************************************/


    /************************************************************************************
     *                  Successful and Unsuccessful Dialog Boxes
     ************************************************************************************/

    public void successfulUpdateDialog() {

        Context             context = getContext();
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder( context );
        builder.setTitle( "Succesful Update" )
                .setMessage( "Information has been successfully updated" )
                .setNeutralButton( "OK",
                        ( DialogInterface dialog, int which ) ->
                        {
                            dialog.dismiss();
                            Credentials newCredentials = updateCredentials();
                            mListener.onChangeMemberInfo( newCredentials );
                        } )
                .setIcon( R.drawable.ic_check_black_24dp )
                .show();
    }

    public void unSuccessfulUpdateDialog() {
        Context             context = getContext();
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder( context, android.R.style.Theme_Material_Dialog_Alert );

        builder.setTitle( "Unsuccesful Update" )
                .setMessage( "Information update failed" )
                .setNeutralButton( "OK",
                        ( DialogInterface dialog, int which ) -> dialog.dismiss() )
                .setIcon( R.drawable.ic_cancel_black_24dp )
                .show();

        setDefault();
    }

    /****************************************************************************************/


    /**
     * Helper method that creates a new Credential object based
     * on the data in mUpdateMap. Uses this object to update
     * the SharedPreferences.
     *
     * @return new Credential object with updated information of user
     */
    private Credentials updateCredentials() {


        String      email    = mCredentials.getEmail();
        String      password = mCredentials.getPassword();
        Set<String> keys     = mUpdateMap.keySet();

        Log.d( TAG, "Keys updated: " + keys );

        if ( mUpdateMap.containsKey( "email" ) ) {
            email = mUpdateMap.get( "email" );
            keys.remove( "email" );
        }

        if ( mUpdateMap.containsKey( "password" ) ) {
            password = mUpdateMap.get( "password" );
            keys.remove( "password" );
        }


        Credentials.Builder builder        = new Credentials.Builder( email, password );
        boolean             firstnameAdded = false;
        boolean             lastnameAdded  = false;
        boolean             usernameAdded  = false;
        for ( String key : keys ) {
            String value = mUpdateMap.get( key );
            switch ( key ) {
                case "firstname":
                    builder.addFirstName( value );
                    firstnameAdded = true;
                    break;
                case "lastname":
                    builder.addLastName( value );
                    lastnameAdded = true;
                    break;
                case "username":
                    builder.addUsername( value );
                    usernameAdded = true;
                    break;
                default:
                    Log.d( TAG, "The key should've been one of the 3. What is this?: "+ key );
            }
        }

        if ( !firstnameAdded ) {
            builder.addFirstName( mCredentials.getFirstName() );
        }

        if ( !lastnameAdded ) {
            builder.addLastName( mCredentials.getLastName() );
        }

        if ( !usernameAdded ) {
            builder.addUsername( mCredentials.getUsername() );
        }
        builder.addMemberID( mCredentials.getMemberID() );
        Credentials newCredentials = builder.build();

        HomeActivity      activity = ( HomeActivity ) getActivity();
        SharedPreferences prefs    = activity.getSharedPreferences( activity.getString( R.string.keys_shared_prefs ), Context.MODE_PRIVATE );
        assert(prefs != null);
        prefs.edit().putString( activity.getString( R.string.keys_prefs_password ), newCredentials.getPassword() ).apply();
        prefs.edit().putString( activity.getString( R.string.keys_prefs_email ), newCredentials.getEmail() ).apply();


        return newCredentials;
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

        void onChangeMemberInfo( Credentials credentials );
    }

    /**
     * Helper object that stores the KEYS needed for the endpoint
     * with its appropriate data (e.g. firstname, lastname, email, etc.).
     * This object will store the default credentials of user.
     */
    private class UpdateValue {

        private String mKey;
        private String mValue;

        UpdateValue( String key, String value ) {
            mKey = key;
            mValue = value;
            if ( key == null ) {
                mKey = "";
            }

            if ( value == null ) {
                mValue = "";
            }

        }

        String getKey() {
            return mKey;
        }

        public String getValue() {
            return mValue;
        }

        @NonNull
        @Override
        public String toString() {
            return String.format( "{%s=%s}", mKey, mValue );
        }


    }

}
