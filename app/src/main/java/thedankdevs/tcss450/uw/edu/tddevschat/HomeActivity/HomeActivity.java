package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
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

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Weather.WeatherDateFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.SettingsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.WaitFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.dummy.WeatherData;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

/**
 *
 *
 */
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        WeatherDateFragment.OnListFragmentInteractionListener,
//        WeatherFragment.OnFragmentInteractionListener,
        ConnectionsFragment.OnListFragmentInteractionListener,
        ConnectionFragment.OnConnectionFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener {

    private Credentials mCredential;
    private int mChatID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mCredential = (Credentials) getIntent().getSerializableExtra(getString(R.string.key_credential));

        if (savedInstanceState == null) {
            if (findViewById(R.id.frame_home_container) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_home_container, new HomeFragment())
                        .commit();
            }
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // [ Snippet 1 ] now on bottom
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        Bundle args = new Bundle();
        Fragment fragment = new HomeFragment();

        switch (item.getItemId()) {
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_connections:
                ArrayList<Connection> connections = new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    connections.add(new Connection.Builder("email@fake.com", "DankDev")
                            .addFirstName("John")
                            .addLastName("Doe")
                            .addChatID(1)
                            .build());
                }
                args.putSerializable(ConnectionsFragment.ARG_CONNECTIONS_LIST, connections);
                fragment = new ConnectionsFragment();
                fragment.setArguments(args);
                break;
            case R.id.nav_weather:
                fragment = new WeatherDateFragment();
                break;
            case R.id.nav_chat:
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            default:

        }
        fragment.setArguments(args);
        loadFragment(fragment);
        /* Snippet 2 removed and placed at end. */
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
                    for (int i = 0; i < data.length(); i++) {
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
                .remove(Objects.requireNonNull(getSupportFragmentManager().findFragmentByTag("WAIT")))
                .commit();
    }


    private void logout() {
        new DeleteTokenAsyncTask().execute();
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
     *
     * @param uri uniform resource identifier
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO do something
    }

    /**
     * Opens a Connection fragment for the corresponding connection
     * that was clicked on in {@link ConnectionsFragment}
     *
     * @param item the connection selected
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
     *
     * @param chatID
     */
    @Override
    public void onOpenChatInteraction(int chatID, String email) {
        //TODO: show wait fragment and connect to endpoints-------------------------------------------
        chatID = 25;
        if (chatID == -1) {
            createNewChat();
        } else {
            mChatID = chatID;
            loadOldChats();
            //loadNewChat();
        }
        /*Snippet 3 placed on end*/
    }

    private void loadOldChats() {
        JSONObject chatterInfo = new JSONObject();
        try {
            chatterInfo.put("email", mCredential.getEmail());
            chatterInfo.put("chatID", mChatID);
        } catch (JSONException e) {
            Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
        }

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.base_url))
                .appendPath(getString((R.string.ep_messaging)))
                .appendPath(getString(R.string.ep_getAllMessages))
                .build();

        Log.w("URL for getting all chat:", uri.toString());
        new SendPostAsyncTask.Builder(uri.toString(), chatterInfo)
                .onPostExecute(this::handleOldChatPost)
                .onCancelled(error -> Log.e("ERROR EMMETT", error))
                .build().execute();

    }

    private void handleOldChatPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            JSONArray temp = resultsJSON.getJSONArray("messages");
            Log.w("here?", temp.toString());

            Bundle bundle = new Bundle();

            bundle.putString(getString(R.string.key_json_array), temp.toString());
            loadNewChat(bundle);

        } catch (JSONException e) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    private void createNewChat() {
        JSONObject chatName = new JSONObject();
        try {
            chatName.put("name", "messaging room");
        } catch (JSONException e) {
            Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
        }
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.base_url))
                .appendPath(getString((R.string.ep_messaging)))
                .appendPath(getString(R.string.ep_messaging_new))
                .build();

        Log.w("URL for create new chat:", uri.toString());
        new SendPostAsyncTask.Builder(uri.toString(), chatName)
                .onPostExecute(this::handleNewChatPost)
                .onCancelled(error -> Log.e("ERROR EMMETT", error))
                .build().execute();
    }

    private void handleNewChatPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            JSONArray temp = resultsJSON.getJSONArray("newChatID");
            JSONObject tempContent = temp.getJSONObject(0);
            mChatID = tempContent.getInt("chatid");
            Log.w("CHATID", String.valueOf(mChatID));
            addChatters(mChatID, "mcb35@uw.edu");
            addChatters(mChatID, mCredential.getEmail());


            Bundle bundle = new Bundle();
            loadNewChat(bundle);

        } catch (JSONException e) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    private void addChatters(int chatID, String email) {
        JSONObject chatterInfo = new JSONObject();
        Log.w("Adding", email);
        Log.w("Adding", String.valueOf(chatID));
        try {
            chatterInfo.put("chatID", chatID);
            chatterInfo.put("email", email);
        } catch (JSONException e) {
            Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
        }
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.base_url))
                .appendPath(getString((R.string.ep_messaging)))
                .appendPath(getString(R.string.ep_messaging_add))
                .build();
        Log.w("URL for create new chat:", uri.toString());

        new SendPostAsyncTask.Builder(uri.toString(), chatterInfo)
                .onPostExecute(this::handleAddChattersPost)
                .onCancelled(error -> Log.e("ERROR EMMETT", error))
                .build().execute();
    }

    private void handleAddChattersPost(String result) {
        try {
            Log.w("JSON result adding peeps", result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean didIt = resultsJSON.getBoolean("success");
            if (didIt) {
                Log.w("Adding", "YAY WE DID IT");
            } else {
                Log.w("Adding", "DARN IT, IT DIDN'T WORK");
            }

        } catch (JSONException e) {

        }
    }

    private void loadNewChat(Bundle bundle) {
        ChatFragment chatFragment = new ChatFragment();
        bundle.putSerializable(getString(R.string.key_connection_chatID), mChatID);
        //   bundle.putSerializable(getString(R.string.key_connection_email), email);
        bundle.putSerializable(getString(R.string.key_credential), mCredential);
        chatFragment.setArguments(bundle);
        loadFragment(chatFragment);
    }

    @Override
    public void onWeatherListItemFragmentInteraction(WeatherData.WeatherDate item) {

    }

    // Deleting the InstanceId (Firebase token) must be done asynchronously. Good thing
    // we have something that allows us to do that.
    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onWaitFragmentInteractionShow();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //since we are already doing stuff in the background, go ahead
            //and remove the credentials from shared prefs here.
            SharedPreferences prefs = getSharedPreferences(getString(R.string.keys_shared_prefs), Context.MODE_PRIVATE);
            prefs.edit().remove(getString(R.string.keys_prefs_password)).apply();
            prefs.edit().remove(getString(R.string.keys_prefs_email)).apply();
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
            finishAndRemoveTask();
        }
    }

}
/*
        [Snippet 1]****************************************************
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        SharedPreferences prefs =
                getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        TextView usernameDisplay = findViewById(R.id.tv_drawerheader_username);
        usernameDisplay.setText(prefs.getString(getString(R.string.keys_prefs_email), ""));

*/


/*      [ Snippet 2 ]****************************************************
        if (id == R.id.nav_home) {
            HomeFragment homeFragment = new HomeFragment();
            Bundle args = new Bundle();
            homeFragment.setArguments(args);
            loadFragment(homeFragment);
        } else if  (id == R.id.nav_connections) {
            *//*Uri uri = new Uri.Builder()
                    .scheme("https")
                    .appendPath(getString(R.string.base_url))
                    .appendPath(getString(R.string.ep_connections))
                    .appendPath(getString(R.string.ep_getConnections)) //TODO: get correct endpoints
                    .build();
            new GetAsyncTask.Builder(uri.toString())
                    .onPreExecute(this::onWaitFragmentInteractionShow)
                    .onPostExecute(this::handleConnectionsGetOnPostExecute)
                    .build().execute();
                    *//*
            //Send dummy data
            ArrayList<Connection> connections = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                connections.add(new Connection.Builder("email@fake.com", "DankDev")
                        .addFirstName("John")
                        .addLastName("Doe")
                        //.addChatID(1)
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
            loadFragment(weatherFragment);
        } else if (id == R.id.nav_chat) {
            ChatFragment chatFragment = new ChatFragment();
            loadFragment(chatFragment);
        }*/





       /* [ Snippet 3 ]****************************************************
           ChatFragment chatFragment = new ChatFragment();
            chatFragment.setArguments(bundle);
            bundle.putSerializable(getString(R.string.key_connection_chatID), mChatID);
            bundle.putSerializable(getString(R.string.key_connection_email), email);
            bundle.putSerializable(getString(R.string.key_credential), mCredential);
            loadFragment(chatFragment);
            //Where is this coming from??

        */