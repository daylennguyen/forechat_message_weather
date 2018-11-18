package thedankdevs.tcss450.uw.edu.tddevschat.SignInActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.WaitFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;

/**
 * Activities that contain this fragment must implement the
 *
 * @author Michelle Brown
 * @version 1 November 2018
 */
public class SignInActivity extends AppCompatActivity
        implements LoginFragment.OnFragmentInteractionListener,
        RegisterFragment.OnRegisterFragmentInteractionListener,
        VerifyFragment.OnVerifyFragmentInteractionListener {

    private boolean mLoadFromChatNotification = false;
    private static final String TAG = SignInActivity.class.getSimpleName();
    private String mChatIDfromNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("type")) {
                Log.d(TAG, "type of message: " + getIntent().getExtras().getString("type"));
                Log.d(TAG, "chatID from notification: " + getIntent().getExtras().getString("chatID"));

                mLoadFromChatNotification = getIntent().getExtras().getString("type").equals("contact");
                mChatIDfromNotification = getIntent().getExtras().getString("chatID");
            } else {
                Log.d(TAG, "NO MESSAGE");
            }
        }
        if(savedInstanceState == null) {
            if (findViewById(R.id.frame_signin_container) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_signin_container, new LoginFragment())
                        .commit();
            }
        }
    }

    /**
     * From {@link LoginFragment}
     * gets called when a user
     *
     * @param credentials
     */
    @Override
    public void onLoginSuccess(Credentials credentials) {
        openMain(credentials);
    }


    /**
     * This method is called after verification email has sent again to the user.
     * Send the user to verification fragment, so they can enter their verficiation code.
     * @param credentials
     * @Author Emmett Kang
     * @Version 11 November 2018
     */
    @Override
    public void onNotVerified(Credentials credentials) {

        //Tell the user that they're not verified.
        Context context = getApplicationContext();
        CharSequence text = "Your account isn't verified yet =[";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();


        VerifyFragment verificationFragment = new VerifyFragment();
        Bundle args = new Bundle();
        //Package the credentials to the next fragment.
        args.putSerializable(getString(R.string.key_credential), credentials);
        verificationFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_signin_container, verificationFragment)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    /**
     * From {@link LoginFragment}
     */
    @Override
    public void onRegisterClicked() {
        RegisterFragment userRegistrationFragment = new RegisterFragment();
        Bundle args = new Bundle();
        userRegistrationFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_signin_container, userRegistrationFragment)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }


    /**
     * From {@link WaitFragment}
     */
    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_signin_container, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }

    /**
     * From {@link WaitFragment}
     */
    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }

    private void openMain(Credentials credentials) {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(getString(R.string.key_credential), credentials);
        intent.putExtra(getString(R.string.keys_intent_notification_msg), mLoadFromChatNotification);
        if (mChatIDfromNotification != null) {
            intent.putExtra(getString(R.string.keys_intent_notification_chatID), Integer.parseInt(mChatIDfromNotification));
        }
        startActivity(intent);
        //End this Activity and remove it from the Activity back stack.
        finish();
    }

    /**
     * This method is called when registration part is done, and leads the
     * user to verification section.
     * @param c Credentials to pass onto verification fragment.
     * @Author Emmett Kang
     * @Version 11 November 2018
     */
    @Override
    public void onRegisterSuccess(Credentials c) {

        //Warning to the user on limited time to verify.
        Context context = getApplicationContext();
        CharSequence text = "You have 1 hour to verify your account!";
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.key_credential), c); //Package the credential to next fragment.
        VerifyFragment verificationFragment = new VerifyFragment();
        verificationFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_signin_container, verificationFragment)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
        //openMain(c);
    }

    /**
     * When verification is done, send the user to the weather(landing) Activity.
     * @Author Emmett Kang
     * @Version 11 November 2018
     */
    @Override
    public void onVerificationSuccess(Credentials c) {
        openMain(c);
    }


}
