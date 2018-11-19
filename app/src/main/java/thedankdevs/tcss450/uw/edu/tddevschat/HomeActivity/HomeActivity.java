package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.ChatsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.content.Chat;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionListFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Weather.WeatherDate;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Weather.WeatherDateFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.SettingsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.WaitFragment;
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
        ChatsFragment.OnChatsListFragmentInteractionListener,
        ConnectionListFragment.OnListFragmentInteractionListener,
        ConnectionFragment.OnConnectionFragmentInteractionListener,
        WaitFragment.OnFragmentInteractionListener {
    /**Current user information **/
    private Credentials mCredential;
    /**ChatId to be updated to load which chatID.**/
    private int mChatID = 0;

    /**Fields to be set when new chat is created**/
    private String theOtherReceiverEmail;
    private String theOtherReceiverUsername;
    private String defaultChatName;

    /*Location Services*/
    public static final String LONGITUDE_KEY = "LONGITUDE";
    public static final String LATITUDE_KEY = "LATITUDE";
    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int MY_PERMISSIONS_LOCATIONS = 8414;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null /* Looper */);
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.
        mFusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    /**
     * Create and configure a Location Request used when retrieving location updates
     */
    protected void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setTitle("Main Page");
        mCredential = (Credentials) getIntent().getSerializableExtra(getString(R.string.key_credential));

        //If notification received, then load all chats.
        if (getIntent().getBooleanExtra(getString(R.string.keys_intent_notification_msg), false)) {
            mChatID = getIntent().getExtras().getInt(getString(R.string.keys_intent_notification_chatID));
            loadAllMessages();
        } else { // load default fragment.
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_home_container, new HomeFragment())
                    .commit();
        }


        /*
        if (savedInstanceState == null) {
            if (findViewById(R.id.frame_home_container) != null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_home_container, new HomeFragment())
                        .commit();
            }
        }*/

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // [ Snippet 1 ] now on bottom
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        TextView nav_user = hView.findViewById(R.id.tv_drawerheader_username);
        nav_user.setText(mCredential.getUsername()); //Set the header username.
        navigationView.setNavigationItemSelectedListener(this);
        /*Location*/

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION
                            , Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_LOCATIONS);
        } else {
            //The user has already allowed the use of Locations. Get the current location.
            requestLocation();
        }
        /**/


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    Log.d("LOCATION", "LATITUDE: " + String.valueOf(location.getLatitude()));
                    Log.d("LOCATION", "LONG: " + String.valueOf(location.getLongitude()));
                }
            }
        };
        createLocationRequest();

    }

    private void setLocation(final Location location) {
        mCurrentLocation = location;
    }

    /*vvvvvvvvvvvv~~~~~LOCATION~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("REQUEST LOCATION", "User did NOT allow permission to request location!");
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                setLocation(location);
                                Log.d("LOCATION", location.toString());
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_LOCATIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // locations-related task you need to do.
                    requestLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d("PERMISSION DENIED", "Nothing to see or do here.");

                    //Shut down the app. In production release, you would let the user
                    //know why the app is shutting down...maybe ask for permission again?
                    finishAndRemoveTask();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /*^^^^^^^^^^^^^LOCATION~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/





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
                loadConnections();
                break;
            case R.id.nav_weather:

                setTitle("Weather");
                args.putDouble(LATITUDE_KEY, mCurrentLocation.getLatitude());
                args.putDouble(LONGITUDE_KEY, mCurrentLocation.getLongitude());
                fragment = new WeatherDateFragment();
                fragment.setArguments(args);
                break;
            case R.id.nav_chat:
                setTitle("Chat");
                loadAllChats();
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
            /* Snippet 2 removed and placed at end. */
        }
        /*after we display the fragment, close the drawer*/
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void loadConnections() {
        JSONObject memberInfo = new JSONObject();
        try {
            memberInfo.put("memberID", mCredential.getMemberID());
        } catch (JSONException e) {
            Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
        }
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.base_url))
                .appendPath(getString((R.string.ep_connections)))
                .appendPath(getString(R.string.ep_getConnections))
                .build();
        Log.w("URL for getting all connections:", uri.toString());
        new SendPostAsyncTask.Builder(uri.toString(), memberInfo)
                .onPreExecute(this::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleConnectionsOnPostExecute)
                .onCancelled(error -> Log.e("ERROR MICHELLE", error))
                .build().execute();
    }

    private void handleConnectionsOnPostExecute(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            JSONArray jsonConnections = resultsJSON.getJSONArray("connections");
            ArrayList<Connection> myConnections = new ArrayList<>();
            Bundle args = new Bundle();
            for (int i = 0; i < jsonConnections.length(); i++) {
                JSONObject connection = jsonConnections.getJSONObject(i);
                String first = connection.getString("firstname");
                String last = connection.getString("lastname");
                String username = connection.getString("username");
                String email = connection.getString("email");
                int chatid;
                try {
                    chatid = connection.getInt("chatid");
                } catch (JSONException e) {
                    chatid = -1;
                }
                myConnections.add(new Connection.Builder(email, username)
                        .addFirstName(first)
                        .addLastName(last)
                        .addChatID(chatid)
                        .build());
            }
            args.putSerializable(ConnectionListFragment.ARG_CONNECTIONS_LIST, myConnections);

            Fragment connectionListFragment = new ConnectionListFragment();
            connectionListFragment.setArguments(args);
            loadFragment(connectionListFragment);
        } catch (JSONException e) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
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
        new DeleteTokenAsyncTask().execute();
    }


    /*Helper method to load an instance of the given fragment into the current activity*/
    private void loadFragment(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_home_container, frag)
                .addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }


    /**
     * Opens a Connection fragment for the corresponding connection
     * that was clicked on in {@link ConnectionListFragment}
     *
     * @param item the connection selected
     */
    @Override
    public void onListFragmentInteraction(Connection item) {
        ConnectionFragment connectionFragment = new ConnectionFragment();
        Bundle args = new Bundle();

        //Could this be just one item being sent?
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
    public void onOpenChatInteraction(int chatID, String email, String username) {
        theOtherReceiverUsername = username;
        /*TODO: show wait fragment and connect to endpoints------*/
        if (chatID == -1) {
            theOtherReceiverEmail = email;
            createNewChat();
        } else {
            mChatID = chatID;
            loadAllMessages();

        }
        Log.w("WTF", String.valueOf(chatID));
        /*Snippet 3 placed on end*/
    }

    /*Retrieves the previous chats the user was apart of*/

    /**
     * Retrieves the previous messages that chatID has.
     * @Author Emmett Kang
     * @Version 16 November 2018
     */
    private void loadAllMessages() {
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
                .onPostExecute(this::handleAllMessagesPost)
                .onCancelled(error -> Log.e("ERROR EMMETT", error))
                .build().execute();

    }

    /**
     * Receive and process the result from the endpoint and send to chat fragment.
     * @param result JSON file from endpoint.
     * @Author Emmett Kang
     * @Version 16 November 2018
     */
    private void handleAllMessagesPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);

            JSONArray temp = resultsJSON.getJSONArray("messages");
            JSONObject grabTitle = resultsJSON.getJSONObject("chatName");
            String chatTitle = grabTitle.getString("name");

            Bundle bundle = new Bundle();

            bundle.putString(getString(R.string.key_json_array), temp.toString());
            bundle.putString(getString(R.string.key_chat_Title), chatTitle);
            loadChatFragment(bundle);

        } catch (JSONException e) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * This method creates new chat sessions.
     * @Author Emmett Kang
     * @Version 15 November 2018
     */
    private void createNewChat() {
        JSONObject chatName = new JSONObject();
        try {
            //Create default chat name for current user and user to be chatting.
            defaultChatName = mCredential.getUsername() + " & " + theOtherReceiverUsername;
            chatName.put("name", defaultChatName);
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


    /**
     * Receive and process the result from the endpoint and send to chat fragment while
     * adding users to the new chat room.
     * @param result JSON file from endpoint.
     * @Author Emmett Kang
     * @Version 15 November 2018
     */
    private void handleNewChatPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            JSONArray temp = resultsJSON.getJSONArray("newChatID");
            JSONObject tempContent = temp.getJSONObject(0);
            mChatID = tempContent.getInt("chatid");
            Log.w("CHATID", String.valueOf(mChatID));
            addChatters(mChatID, theOtherReceiverEmail);
            addChatters(mChatID, mCredential.getEmail());


            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.key_chat_Title), defaultChatName);
            loadChatFragment(bundle);

        } catch (JSONException e) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * Add the users to a certain chatroom through chatID.
     * @param chatID chatroom identifier.
     * @param email user's email to be added.
     * @Author Emmett Kang
     * @Version 15 November 2018
     */
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

    /**
     * Receive and show if succeeded through logcat.
     * @param result JSON file
     * @Author Emmett Kang
     * @Version 15 November 2018
     */
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


    /**
     * Receive bundle from other methods, send necessary information, and
     * load the chat fragment.
     * @param bundle Information chat fragment to open.
     * @Author Emmett Kang
     * @Version 16 November 2018
     */
    private void loadChatFragment(Bundle bundle) {
        Log.w("WHAT IS THIS", String.valueOf(mChatID));
        ChatFragment chatFragment = new ChatFragment();
        bundle.putSerializable(getString(R.string.key_connection_chatID), mChatID);
        //   bundle.putSerializable(getString(R.string.key_connection_email), email);
        bundle.putSerializable(getString(R.string.key_credential), mCredential);
        chatFragment.setArguments(bundle);
        loadFragment(chatFragment);
    }

    @Override
    public void onWeatherListItemFragmentInteraction(WeatherDate item) {

    }

    @Override
    public void onChatsListFragmentInteraction(Chat item) {
        mChatID = item.getChatID();
        loadAllMessages();

    }


    /**
     * Load all chats that user associates with.
     * @Author Emmett Kang
     * @Version 16 November 2018
     */
    private void loadAllChats() {
        JSONObject chatterInfo = new JSONObject();

        try {
            chatterInfo.put("email", mCredential.getEmail());
        } catch (JSONException e) {
            Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
        }

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.base_url))
                .appendPath(getString((R.string.ep_messaging)))
                .appendPath(getString(R.string.ep_getAllChats))
                .build();

        Log.w("URL for getting all chat:", uri.toString());

        new SendPostAsyncTask.Builder(uri.toString(), chatterInfo)
                .onPostExecute(this::handleAllChatsPost)
                .onCancelled(error -> Log.e("ERROR EMMETT", error))
                .build().execute();

    }


    /**
     * Receive and process the result from the endpoint and send to chat fragment.
     * @param result JSON file from endpoint.
     * @Author Emmett Kang
     * @Version 16 November 2018
     */
    private void handleAllChatsPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            JSONArray listOfAllChats = resultsJSON.getJSONArray("chats");
            //Chatroom information to be displayed.
            ArrayList<Chat> allExistingChats = new ArrayList<>();

            Bundle args = new Bundle();

            //Iterate through the JSONarray and create chat objects to display.
            for (int i = 0; i < listOfAllChats.length(); i++) {
                JSONObject chatRoom = listOfAllChats.getJSONObject(i);
                String chatName = chatRoom.getString("name");
                String receiver = chatRoom.getString("username");
                int chatid = chatRoom.getInt("chatid");
                allExistingChats.add(new Chat.Builder(chatName, receiver, chatid).build());
            }
            args.putSerializable(ChatsFragment.ARG_CHATS_LIST, allExistingChats);
            //Create chats list fragment and display.
            Fragment fragment = new ChatsFragment();
            fragment.setArguments(args);
            onWaitFragmentInteractionHide();
            loadFragment(fragment);
        } catch (JSONException e) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
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
            args.putSerializable(ConnectionListFragment.ARG_CONNECTIONS_LIST, connections);
            Fragment frag = new ConnectionListFragment();
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