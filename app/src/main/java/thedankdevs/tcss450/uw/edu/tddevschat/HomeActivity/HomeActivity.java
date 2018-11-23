package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.IOException;
import java.util.Objects;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.ChatsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.content.Chat;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionListFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.ChatNode;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.ConnectionsNode;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.LocationNode;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Weather.WeatherDate;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Weather.WeatherDateFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.SettingsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.WaitFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;

import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.LocationNode.LATITUDE_KEY;
import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.LocationNode.LONGITUDE_KEY;

/**
 *
 *
 */
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ConnectionListFragment.OnListFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        WeatherDateFragment.OnListFragmentInteractionListener,
        ChatsFragment.OnChatsListFragmentInteractionListener,
        ConnectionFragment.OnConnectionFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener {


    /**
     * Current user information
     **/
    private Credentials mCredential;
    /** */
    private LocationNode mLocationNode;
    /** */
    private ConnectionsNode mConnectionsNode;

    private ChatNode mChatNode;

    @Override
    protected void onResume() {
        super.onResume();
        mLocationNode.startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationNode.stopLocationUpdates();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Main Page");
        /**/

        Log.d("DAYLEN", "initializing Cred");
        mCredential = (Credentials) getIntent().getSerializableExtra(getString(R.string.key_credential));
        Log.d("DAYLEN", "Cred initialized");

        /*Connections*/
        Log.d("DAYLEN", "initializing connect");

        mConnectionsNode = new ConnectionsNode(this, mCredential);
        Log.d("DAYLEN", "connect initialized");

        /*Location*/
        Log.d("DAYLEN", "initializing location");
        mLocationNode = new LocationNode(this);
        Log.d("DAYLEN", "location initialized");

        /*Chat*/
        Log.d("DAYLEN", "initializing chat");
        mChatNode = new ChatNode(this, mCredential);
        Log.d("DAYLEN", "chat initialized");

        /*insert option items into the tool bar*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        View hView = navigationView.getHeaderView(0);

        navigationView.setNavigationItemSelectedListener(this);

        TextView nav_user = hView.findViewById(R.id.tv_drawerheader_username);

        nav_user.setText(mCredential.getUsername()); //Set the header username.

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem search = menu.findItem(R.id.action_search_contacts);
        search.setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Bundle args = new Bundle();
        Fragment fragment = new HomeFragment();

        /*depending on the ID of the nav_item, route them to the appropriate fragment*/
        boolean loadingFromDifferentMethods = false;

        switch (item.getItemId()) {

            case R.id.nav_home:
                setTitle("Main Page");
                fragment = new HomeFragment();
                break;

            case R.id.nav_connections:
                setTitle("Connections");
                mConnectionsNode.loadConnections();
                break;
            case R.id.nav_weather:
                setTitle("Weather");
                if ((mLocationNode.getmCurrentLocation() != null)
                        && (mLocationNode.getmCurrentLocation() != null)) {
                    args.putDouble(LATITUDE_KEY, mLocationNode.getmCurrentLocation().getLatitude());
                    args.putDouble(LONGITUDE_KEY, mLocationNode.getmCurrentLocation().getLongitude());
                }
                fragment = new WeatherDateFragment();
                fragment.setArguments(args);
                break;

            case R.id.nav_chat:
                setTitle("Chat");
                mChatNode.loadAllChats();
                onWaitFragmentInteractionShow();
                loadingFromDifferentMethods = true;
                break;

            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;

            default:

        }
        if (!loadingFromDifferentMethods) {
            /*Send the args to the fragment before displaying*/
            fragment.setArguments(args);
            /*display the fragment*/
            loadFragment(fragment);
        }
        /*after we display the fragment, close the drawer*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onWaitFragmentInteractionShow() {
        /*displays the wait fragment to the user, meaning that something is loading*/
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_home_container, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        /*remove the wait fragment that is displayed; meaning that something is done loading*/
        getSupportFragmentManager()
                .beginTransaction()
                .remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("WAIT")))
                .commit();
    }

    /*Signs the user out of the current account*/
    private void logout() {

        new DeleteTokenAsyncTask(this).execute();
    }


    /*Helper method to load an instance of the given fragment into the current activity*/
    public void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_home_container, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationNode.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onWeatherListItemFragmentInteraction(WeatherDate item) {

    }

    @Override
    public void onListFragmentInteraction(Connection item) {
        mChatNode.onListFragmentInteraction(item);
    }

    @Override
    public void onOpenChatInteraction(int chatID, String email, String username) {
        mChatNode.onOpenChatInteraction(chatID, email, username);
    }

    @Override
    public void onChatsListFragmentInteraction(Chat item) {
        mChatNode.onChatsListFragmentInteraction(item);
    }

    @Override
    public void onCreateNewChatButtonInteraction() {

    }


    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {

        private HomeActivity mMaster;

        public DeleteTokenAsyncTask(HomeActivity master) {
            mMaster = master;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMaster.onWaitFragmentInteractionShow();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //since we are already doing stuff in the background, go ahead
            //and remove the credentials from shared prefs here.
            SharedPreferences prefs = mMaster.getSharedPreferences(mMaster.getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
            prefs.edit().remove(mMaster.getString(R.string.keys_prefs_password)).apply();
            prefs.edit().remove(mMaster.getString(R.string.keys_prefs_email)).apply();
            try {
                //this call must be done asynchronously.
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch (IOException e) {

                Log.e("FCM", "Delete error!");
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //close the app
            mMaster.finishAndRemoveTask();
        }
    }


}