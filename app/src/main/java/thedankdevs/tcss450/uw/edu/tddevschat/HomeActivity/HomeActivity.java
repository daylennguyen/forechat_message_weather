package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.WaitFragment;

/**
 *
 *
 */
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        WeatherFragment.OnFragmentInteractionListener,
        ConnectionsFragment.OnListFragmentInteractionListener,
        ConnectionFragment.OnConnectionFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorLightPurple));


/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/

/*
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        TextView usernameDisplay = findViewById(R.id.tv_drawerheader_username);
        usernameDisplay.setText(prefs.getString(getString(R.string.keys_prefs_email), ""));
*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            HomeFragment homeFragment = new HomeFragment();
            Bundle args = new Bundle();
            homeFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_home_container, homeFragment)
                    .addToBackStack(null);
            // Commit the transaction
            transaction.commit();
        } else if  (id == R.id.nav_connections) {
            /*Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.base_url))
                    .appendPath(getString(R.string.ep_connections))
                    .appendPath(getString(R.string.ep_getConnections)) //TODO: get correct endpoints
                    .build();
            new GetAsyncTask.Builder(uri.toString())
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleConnectionsGetOnPostExecute)
                    .build().execute();
                    */
            //Send dummy data
            ArrayList<Connection> connections = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                connections.add(new Connection.Builder("email"+ i +"@fake.com", "DankDev")
                        .addFirstName("John")
                        .addLastName("Doe")
                        .addChatID(1)
                        .build());
            }
            //open fragment
            Bundle args = new Bundle();
            args.putSerializable(ConnectionsFragment.ARG_CONNECTIONS_LIST, connections);
            Fragment frag = new ConnectionsFragment();
            frag.setArguments(args);
            loadFragment(frag);
        } else if (id == R.id.nav_weather) {
            WeatherFragment weatherFragment = new WeatherFragment();
            Bundle args = new Bundle();
            weatherFragment.setArguments(args);
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_home_container, weatherFragment)
                    .addToBackStack(null);
            // Commit the transaction
            transaction.commit();
        } else if (id == R.id.nav_chat) {
            ChatFragment chatFragment = new ChatFragment();
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_home_container, chatFragment)
                    .addToBackStack(null);
            transaction.commit();
        }


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    private void handleConnectionsGetOnPostExecute(String result) {
        //parse JSON
        try {
            JSONObject root = new JSONObject(result);
            if (root.has("response")) {
                JSONObject response = root.getJSONObject("response");
                if (response.has("data")) {
                    JSONArray data = response.getJSONArray("data");
                    ArrayList<Connection> connections = new ArrayList<>();
                    for(int i = 0; i < data.length(); i++) {
                        JSONObject jsonConnection = data.getJSONObject(i);
                        connections.add(new Connection.Builder(jsonConnection.getString("email"),
                                jsonConnection.getString("username"))
                                .addFirstName(jsonConnection.getString("firstName"))
                                .addLastName(jsonConnection.getString("lastName"))
                                .build());
                    }
                    Bundle args = new Bundle();
                    args.putSerializable(ConnectionsFragment.ARG_CONNECTIONS_LIST, connections);
                    Fragment frag = new ConnectionsFragment();
                    frag.setArguments(args);
                    onWaitFragmentInteractionHide();
                    loadFragment(frag);
                } else {
                    Log.e("ERROR!", "No data array");
                    //notify user
                    onWaitFragmentInteractionHide();
                }
            } else {
                Log.e("ERROR!", "No response");
                //notify user
                onWaitFragmentInteractionHide();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR!", e.getMessage());
            //notify user
            onWaitFragmentInteractionHide();
        }
    }




    @Override
    public void onWaitFragmentInteractionShow() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frame_home_container, new WaitFragment(), "WAIT")
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onWaitFragmentInteractionHide() {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag("WAIT"))
                .commit();
    }




    private void logout() {
        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        //remove the saved credentials from StoredPrefs
        prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
        prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
        //close the app
        finishAndRemoveTask();
        //or close this activity and bring back the Login
        // Intent i = new Intent(this, MainActivity.class);
        // startActivity(i);
        // End this Activity and remove it from the Activity back stack.
        // finish();
    }



    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_home_container, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }




    /**
     * Does something when something was clicked in {@link HomeFragment}
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO do something
    }

    /**
     * Opens a Connection fragment for the corresponding connection
     * that was clicked on in {@link ConnectionsFragment}
     *
     * @param item
     */
    @Override
    public void onListFragmentInteraction(Connection item) {
        ConnectionFragment connectionFragment = new ConnectionFragment();
        Bundle args = new Bundle();
        args.putSerializable(getString(R.string.key_connection_email), item.getEmail());
        args.putSerializable(getString(R.string.key_connection_username), item.getUsername());
        args.putSerializable(getString(R.string.key_connection_first), item.getFirstName());
        args.putSerializable(getString(R.string.key_connection_last), item.getLastName());
        args.putSerializable(getString(R.string.key_connection_chatID), item.getChatID());
        connectionFragment.setArguments(args);
        loadFragment(connectionFragment);
    }

    /**
     * Does something when something was clicked in {@link ConnectionFragment}
     * @param chatID
     */
    @Override
    public void onOpenChatInteraction(int chatID, String email) {
        ChatFragment chatFragment = new ChatFragment();
        loadFragment(chatFragment);
        //Where is this coming from??
    }
}
