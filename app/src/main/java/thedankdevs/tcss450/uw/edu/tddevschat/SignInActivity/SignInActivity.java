package thedankdevs.tcss450.uw.edu.tddevschat.SignInActivity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.MainActivity;
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
        implements LoginFragment.OnFragmentInteractionListener, RegisterFragment.OnFragmentInteractionListener{

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
     * From {@link RegisterFragment}
     *
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
        openMain(new Credentials.Builder("", "").build()); //TODO get the real credentials
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
