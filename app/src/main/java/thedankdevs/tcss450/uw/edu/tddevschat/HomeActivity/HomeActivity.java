package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.ChatFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.ChatsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.CreateNewChatFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.content.Chat;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionListFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.Requests.RequestFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.ChatNode;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.ConnectionsNode;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.LocationNode;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.RemoveChatMembers;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Weather.WeatherDate;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Weather.WeatherDateFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.Settings.SettingsNode;
import thedankdevs.tcss450.uw.edu.tddevschat.SettingsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.WaitFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.MyFirebaseMessagingService;

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
        CreateNewChatFragment.OnCreateNewChatButtonListener,
        WaitFragment.OnFragmentInteractionListener,
        RequestFragment.OnListFragmentInteractionListener,
        RemoveChatMembers.OnRemoveMemberListener {


    /*Node for retrieving the weather data and displaying the information*/
//    private WeatherNode mWeatherNode;
    /*Node for retrieving and setting the user settings*/
    public SettingsNode mSettingsNode;
    /**
     * Current user information
     */
    private Credentials mCredential;
    /**Node for retrieving/fetching the location information of the user*/
    private LocationNode mLocationNode;
    /**Node for retrieving connection information*/
    private ConnectionsNode mConnectionsNode;
    /**Node for retrieving information on user chats*/
    private ChatNode mChatNode;
    ActionBarDrawerToggle toggle;

    private ArrayList<Integer> notifiedChats = new ArrayList<>();

    private FirebaseMessageReciever mFirebaseMessageReciever;

    @Override
    protected void onResume() {
        super.onResume();
        if (mFirebaseMessageReciever == null) {
            mFirebaseMessageReciever = new FirebaseMessageReciever();
        }
        IntentFilter iFilter = new IntentFilter(MyFirebaseMessagingService.RECEIVED_NEW_MESSAGE);
        registerReceiver(mFirebaseMessageReciever, iFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFirebaseMessageReciever != null){
            unregisterReceiver(mFirebaseMessageReciever);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Main Page");
        findViewById(R.id.nav_view);

        /**/
        mCredential = (Credentials) getIntent().getSerializableExtra(getString(R.string.key_credential));
        initializeNodes();
        /*insert option items into the tool bar*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);


        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        TextView nav_user = hView.findViewById(R.id.tv_drawerheader_username);
        nav_user.setText(mCredential.getUsername()); //Set the header username.
        mLocationNode.startLocationUpdates();
    }

    /*Helper class to create node objects*/
    private void initializeNodes() {
        /*Retrieve user settings*/
        mSettingsNode = new SettingsNode(this);
        /*  Connections  */
        mConnectionsNode = new ConnectionsNode(this, mCredential);
        /*   Location    */
        mLocationNode = new LocationNode(this);
        /*     Chat      */
        mChatNode = new ChatNode(this, mCredential);
        /*   Weather     */
//        mWeatherNode = new WeatherNode(this, mLocationNode);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_home_container);
            if (currentFragment instanceof ChatFragment) {
                mChatNode.loadAllChats();
                Log.wtf("wtf", "NOT ENTERING");
            } else {
                super.onBackPressed();
            }
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
            loadFragment(new SettingsFragment());
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
                Fragment frag = new ConnectionListFragment();
                mConnectionsNode.loadConnections(frag);
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
                notifyUI(Color.BLACK, Color.WHITE);
                onWaitFragmentInteractionShow();
                loadingFromDifferentMethods = true;
                break;

            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;

            case R.id.nav_connectionRequests:
                setTitle("Pending Requests");
                mConnectionsNode.loadRequests();
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

    /*Helper method to load an instance of the given fragment into the current activity*/
    public void loadFragmentWithoutBackStack(Fragment frag) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_home_container, frag);
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
        mConnectionsNode.onListFragmentInteraction(item);
    }

    @Override
    public void onRequestListFragmentInteraction(String theirUsername) {
        mConnectionsNode.onRequestListFragmentInteraction(theirUsername);
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
    public void onCreateNewChatButtonPressed() {
        Fragment fragment = new CreateNewChatFragment();
        mConnectionsNode.loadConnections(fragment);

    }

    @Override
    public void onChatsListFragmentLongInteraction(Chat item) {
        mChatNode.onChatsListFragmentLongInteraction(item);
    }

    @Override
    public void CreateNewChatInteraction(ArrayList<CheckBox> cbList, ArrayList<Connection> connectionList) {
        StringBuilder checkedBoxesSB = checkedBoxes(cbList);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this,  checkedBoxesSB.toString() + "  selected", duration);
        toast.show();
        mChatNode.CreateNewChatInteraction(cbList, connectionList);

    }

    private StringBuilder checkedBoxes(List<CheckBox> list) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isChecked()) {
                sb.append(list.get(i).getText().toString());
            }
        }
        return sb;
    }

    public ArrayList<Integer> getNotifiedChats(){
        return (ArrayList<Integer>) notifiedChats.clone();
    }


    public void updateNotifiedChats(int openedChatID ) {
        notifiedChats.remove(notifiedChats.indexOf(openedChatID));

    }


    public void notifyUI(int colorOfText, int colorOfBurger) {
        toggle.getDrawerArrowDrawable().setColor(colorOfBurger);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu m = navigationView.getMenu();
        MenuItem menuItem = m.findItem(R.id.nav_chat);
        SpannableString s = new SpannableString(menuItem.getTitle());

        s.setSpan(new ForegroundColorSpan(colorOfText),
                0, s.length(), 0);
        menuItem.setTitle(s);
    }

    @Override
    public void RemoveMemberInteraction(ArrayList<String> users, int theChatID) {
        mChatNode.RemoveMembersFromChat(users, theChatID);
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

    /**
     * A BroadcastReceiver setup to listen for messages sent from
     MyFirebaseMessagingService
     * that Android allows to run all the time.
     */
    private class FirebaseMessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("FCM Chat Frag", "start onRecieve");
            if (intent.hasExtra("DATA")) {
                String data = intent.getStringExtra("DATA");
                Log.w("FCM DATA", data);
                JSONObject jObj = null;
                try {
                    jObj = new JSONObject(data);
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_home_container);
                    if (jObj.has("message") && jObj.has("sender")) {
                        String sender = jObj.getString("sender");
                        Log.wtf("sender", sender);
                        Log.wtf("username", mCredential.getUsername());
                        int chatID = Integer.valueOf(jObj.getString("chatID"));
                        if (currentFragment instanceof ChatFragment) {
                            if (!sender.equals(mCredential.getUsername())) {
                                int currentChatID = ((ChatFragment) currentFragment).getmChatID();
                                Log.wtf("currChatID: ", String.valueOf(currentChatID));
                                if (currentChatID != chatID) {
                                    notifiedChats.add(chatID);
                                    notifyUI(getResources().getColor(R.color.colorLightBluePurple),
                                            getResources().getColor(R.color.colorLightBluePurple));
                                }
                            }
                        } else {
                            notifiedChats.add(chatID);
                            notifyUI(getResources().getColor(R.color.colorLightBluePurple),
                                    getResources().getColor(R.color.colorLightBluePurple));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}