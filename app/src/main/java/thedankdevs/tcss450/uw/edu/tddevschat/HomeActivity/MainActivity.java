package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.SignInActivity.RegisterFragment;

public class MainActivity extends AppCompatActivity implements RegisterFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //To test register fragment, uncomment this section.
//        /*
        if(savedInstanceState == null) {
            if (findViewById(R.id.frame_main_container) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_main_container, new HomeFragment())
                        .commit();
            }
        }//*/
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
