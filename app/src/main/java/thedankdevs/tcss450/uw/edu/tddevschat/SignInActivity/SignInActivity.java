package thedankdevs.tcss450.uw.edu.tddevschat.SignInActivity;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
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

    @Override
    public void onNotVerified(Credentials credentials) {
        VerifyFragment verificationFragment = new VerifyFragment();
        Bundle args = new Bundle();
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
                .commit();
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
//        FragmentManager fm = getSupportFragmentManager();
//        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
//            fm.popBackStack();
//        }
        //TODO: look up how to clear an activity
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //End this Activity and remove it from the Activity back stack.
        finish();
    }

    @Override
    public void onRegisterSuccess(Credentials c) {
        VerifyFragment verificationFragment = new VerifyFragment();
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_signin_container, verificationFragment)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
        //openMain(c);
    }

    @Override
    public void onVerificationSuccess() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //End this Activity and remove it from the Activity back stack.
        finish();
    }
}
