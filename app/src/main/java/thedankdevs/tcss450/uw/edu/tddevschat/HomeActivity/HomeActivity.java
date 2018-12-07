package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity;

import android.app.AlertDialog;
import android.content.*;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONException;
import org.json.JSONObject;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.ChatFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.ChatsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.CreateNewChatFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.content.Chat;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionListFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.Requests.RequestFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.*;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Weather.WeatherDate;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Weather.WeatherDateFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.MemberSettingsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.SignInActivity.SignInActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.WaitFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.MyFirebaseMessagingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.LocationNode.LATITUDE_KEY;
import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.LocationNode.LONGITUDE_KEY;
import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.SettingsNode.Weather_Preference;
import static thedankdevs.tcss450.uw.edu.tddevschat.R.string.reload_member_settings;

/**
 * One of the ONLY activities contained within this application. why?
 * Because we are individuals whom learn from our mistakes.
 *
 * @author TEAM DANK DEVS HOO RAA!
 * go dawgs wooh :>
 * @version 12/6/2018
 */
public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ConnectionListFragment.OnListFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        WeatherDateFragment.OnListFragmentInteractionListener,
        ChatsFragment.OnChatsListFragmentInteractionListener,//HOLY MOLLY LOOK AT ALL THOSE INTERFACES
        ConnectionFragment.OnConnectionFragmentInteractionListener,
        CreateNewChatFragment.OnCreateNewChatButtonListener,
        WaitFragment.OnFragmentInteractionListener,
        RequestFragment.OnListFragmentInteractionListener,
        RemoveChatMembers.OnRemoveMemberListener,
        MemberSettingsFragment.OnFragmentInteractionListener {

    /* ******************FIELD VARIABLES*******************/

    /*NODES are helper classes meant to encapsulate various functionality of the application*/
    public  SettingsNode    mSettingsNode;
    public  String          date;
    private LocationNode    mLocationNode;
    private ConnectionsNode mConnectionsNode;
    private ChatNode        mChatNode;

    /*User saved credentials*/
    private Credentials mCredential;

    /*Chat Field variables*/
    private FirebaseMessageReciever mFirebaseMessageReciever;
    private ArrayList<Integer>      notifiedChats = new ArrayList<>();

    /*Used to toggle the opened/closed state of the nav drawer*/
    private ActionBarDrawerToggle toggle;
    private Switch                mSwitch;

    /* ******** CONSTRUCTOR AND METHOD CALLS **************/

    /**
     * Mandatory default constructor; because whoever said rules are meant to be broken is dumb.
     *
     * @author Daylen Nguyen
     */
    public HomeActivity() {
    }

    /**
     * @author Daylen Nguyen
     * Tell location node to stop with the GPS updates....yo
     */
    public void stopGPS() {
        mLocationNode.stopLocationUpdates();
    }

    /**
     * @author Daylen Nguyen
     * Ask nicely, for the location node to give the latitude, please and thank you
     */
    public double getCurrentLat() {
        return mLocationNode.getmCurrentLocation().getLatitude();
    }

    /**
     * @author Daylen Nguyen
     * Ask nicely, for the location node to give the user's longitude, please and thank you
     */
    public double getCurrentLon() {
        return mLocationNode.getmCurrentLocation().getLongitude();
    }

    /**
     * @author Daylen Nguyen
     * Tell location node to start up those GPS updates....yo
     */
    public void startGPS() {
        mLocationNode.startLocationUpdates();
    }

    /**
     * Method which is called on creation
     * sets the title, initial view and initializes the nodes.
     *
     * @param savedInstanceState the user's state which was previously saved when the user visited the app
     */
    //  LIFE CYCLE METHODS///////////////////////////
    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        ThemeUtils.onActivityCreateTheme( this );
        setContentView( R.layout.activity_home );
        /*Check for saved-sign-in info*/
        mCredential = ( Credentials ) getIntent().getSerializableExtra( getString( R.string.key_credential ) );
        if ( mCredential == null ) {
            mCredential = ( Credentials ) getIntent().getSerializableExtra( getString( R.string.keys_credential_member_settings ) );
        }
        setTitle( "Hi there, " + mCredential.getFirstName() + "" );
        Log.d( "Debug Bryan", "initializing Nodes" );
        Log.d( "Debug Bryan", "nodes initialized" );
        /*insert option items into the tool bar and initialize the drawer*/
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        Log.d( "Debug Bryan", "initializing drawer" );
        initializeActionDrawerToggle( drawer, toolbar );
        Log.d( "Debug Bryan", "drawer initialized, starting location updates" );
        Log.d( "Debug Bryan", "location updates successful" );
        if ( savedInstanceState == null ) {
            FragmentManager fm = getSupportFragmentManager();
            //handle connection notifications
            String connectionNotification = getIntent().getStringExtra( getString( R.string.keys_intent_notification_connections ) );
            if ( connectionNotification != null ) {
                if ( connectionNotification.equals( getString( R.string.notification_requested ) ) ) {
                    mConnectionsNode.loadRequests();
                } else if ( connectionNotification.equals( getString( R.string.notification_accepted ) ) ) {
                    mConnectionsNode.loadConnections( new ConnectionListFragment() );
                }
            }
            if ( findViewById( R.id.frame_home_container ) != null ) {
                HomeFragment home = new HomeFragment();
                fm.beginTransaction().add( R.id.frame_home_container, home, HomeFragment.class.getSimpleName() ).addToBackStack( null ).commit();
                loadFragment( home );
            }
        }

        /*INITIALIZE THE NODES CONTAINING FUNCTIONALITY CORRESPONDING TO SPECIFIC FEATURES*/
        initializeNodes();
        mLocationNode.startLocationUpdates();
        // reload member Settings fragment
        if ( getIntent().getBooleanExtra( getString( reload_member_settings ), false ) ) {
            setTitle( getString( R.string.member_settings_header ) );
            Bundle                 args = new Bundle();
            MemberSettingsFragment frag = new MemberSettingsFragment();
            args.putSerializable( getString( R.string.nav_membersettings ), mCredential );
            frag.setArguments( args );
            loadFragmentWithoutBackStack( frag );
        }
        Log.d( "Debug Bryan", "onCreate done" );
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * Enables toggling of the nav drawer and displays with username within said drawer as well
     * as initializes the navigation and menu. Furthermore, reinitializes the theme if it has been previously set
     */
    private void initializeActionDrawerToggle( DrawerLayout drawer, Toolbar toolbar ) {
        Log.d( "BRYAN", "Credentials during intializeDrawer: " + mCredential.getUsername() );
        toggle = new ActionBarDrawerToggle( this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close );
        drawer.addDrawerListener( toggle );
        toggle.syncState();
        NavigationView navigationView = findViewById( R.id.nav_view );
        View           hView          = navigationView.getHeaderView( 0 );
        navigationView.setNavigationItemSelectedListener( this );
        TextView nav_user = hView.findViewById( R.id.tv_drawerheader_username );
        nav_user.setText( mCredential.getUsername() ); //Set the header username.
        Menu     mainMenu     = navigationView.getMenu();
        Menu     settingsMenu = mainMenu.findItem( R.id.settings_menu_item ).getSubMenu();
        MenuItem switchItem   = settingsMenu.findItem( R.id.nav_theme );
        mSwitch = switchItem.getActionView().findViewById( R.id.view_switch_theme );


        /*Depending on whether the user has changed theme preferences; set the preference*/
        SharedPreferences sharedPref = getSharedPreferences( getString( R.string.current_theme ), Context.MODE_PRIVATE );
        mSwitch.setOnClickListener( v -> {
            Log.d( "Paolo", "I hit the switch" );
            Switch switchView = ( Switch ) v;
            String theme      = ThemeUtils.THEME_CLASSIC;
            if ( switchView.isChecked() ) {
                theme = ThemeUtils.THEME_MINT;
            }

            sharedPref.edit().putString( getString( R.string.current_theme ), theme ).apply();
            sharedPref.edit().putBoolean( getString( R.string.reload_theme_switch ), switchView.isChecked() ).apply();
            onChangeTheme();
        } );
        String theme = sharedPref.getString( getString( R.string.current_theme ), ThemeUtils.THEME_CLASSIC );
        assert theme != null;
        switch ( theme ) {
            case ThemeUtils.THEME_MINT:
                mSwitch.setChecked( true );
            default:

        }


    }

    /**
     * Helper class to create node objects which contain functionality pertaining
     * to various features
     */
    private void initializeNodes() {
        /*Retrieve user settings*/
        mSettingsNode = new SettingsNode( this );
        /*  Connections  */
        mConnectionsNode = new ConnectionsNode( this, mCredential );
        /*   Location    */
        mLocationNode = new LocationNode( this );
        /*     Chat      */
        mChatNode = new ChatNode( this, mCredential );
    }

    /**
     * When the back button is pressed update the backstack
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer         = findViewById( R.id.drawer_layout );
        int          backstackCount = getSupportFragmentManager().getBackStackEntryCount();
        Log.d( "BRYAN", "backstack count: " + backstackCount );
        if ( drawer.isDrawerOpen( GravityCompat.START ) ) {
            drawer.closeDrawer( GravityCompat.START );
        } else {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById( R.id.frame_home_container );
            if ( currentFragment instanceof ChatFragment ) {
                mChatNode.loadAllChats();
            } else {
                int i = 1;
                while ( i < backstackCount ) {
                    getSupportFragmentManager().popBackStackImmediate();
                    i++;
                }
            }
            backstackCount = getSupportFragmentManager().getBackStackEntryCount();
            Log.d( "BRYAN", "backstack after popping: " + backstackCount );
            if ( backstackCount == 1 ) {
                loadFragmentWithoutBackStack( new HomeFragment() );
            }
            super.onBackPressed();
        }
    }

    /**
     * When the activity is paused, tell fire base and pause gps updates
     */
    @Override
    protected void onPause() {
        super.onPause();
        if ( mFirebaseMessageReciever != null ) {
            unregisterReceiver( mFirebaseMessageReciever );
        }
        mLocationNode.stopLocationUpdates();
    }

    /**
     * when the activity resumes, tell firebase the user received the message
     */
    @Override
    protected void onResume() {
        super.onResume();
        if ( mFirebaseMessageReciever == null ) {
            mFirebaseMessageReciever = new FirebaseMessageReciever();
        }
        IntentFilter iFilter = new IntentFilter( MyFirebaseMessagingService.RECEIVED_NEW_MESSAGE );
        registerReceiver( mFirebaseMessageReciever, iFilter );
        mLocationNode.stopLocationUpdates();
    }


    /**
     * Listener method to which is called in response to user input to the permissions request
     *
     * @param requestCode  Request code corresponding to the specific permissions requested
     * @param permissions  permissions being requested
     * @param grantResults user's response to the permission request
     */
    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String permissions[], @NonNull int[] grantResults ) {
        mLocationNode.onRequestPermissionsResult( requestCode, grantResults );
    }

    /**
     * Method which is executed when the options menu is created; inflates the option view and hides the search view
     * contained within the connections tab
     *
     * @param menu the menu which is being created
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.home, menu );
        MenuItem search = menu.findItem( R.id.action_search_contacts );
        search.setVisible( false );
        return true;
    }

    /**
     * Conditionally displays a fragment depending on which menu item is selected
     *
     * @param item the menu item which was selected
     * @return the the return of the overridden method
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings ) {
            loadFragment( new thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.SettingsFragment() );
            return true;
        } else if ( id == R.id.action_logout ) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected( item );
    }


    /**
     * Conditionally displays a fragment depending on the which menu item is selected within the nav drawer
     *
     * @param item the menu item which was selected
     * @return whether the method has executed successfully
     */
    @Override
    public boolean onNavigationItemSelected( @NonNull MenuItem item ) {
        // Handle navigation view item clicks here.
        Bundle   args     = new Bundle();
        Fragment fragment = new HomeFragment();

        /*depending on the ID of the nav_item, route them to the appropriate fragment*/
        boolean loadingFromDifferentMethods = false;
        switch ( item.getItemId() ) {
            case R.id.nav_home:
                setTitle( "Main Page" );
                fragment = new HomeFragment();
                break;
            case R.id.nav_connections:
                Fragment frag = new ConnectionListFragment();
                mConnectionsNode.loadConnections( frag );
                break;
            case R.id.nav_weather:
                setTitle( "Weather" );
                if ( mLocationNode.getmCurrentLocation() != null ) {
                    args.putString( LATITUDE_KEY, String.valueOf( mLocationNode.getmCurrentLocation().getLatitude() ) );
                    args.putString( LONGITUDE_KEY, String.valueOf( mLocationNode.getmCurrentLocation().getLongitude() ) );
                }
                fragment = new WeatherDateFragment();
                fragment.setArguments( args );
                break;
            case R.id.nav_chat:
                setTitle( "Chat" );
                mChatNode.loadAllChats();
                notifyUI( Color.BLACK, Color.WHITE );
                onWaitFragmentInteractionShow();
                loadingFromDifferentMethods = true;
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            case R.id.nav_member_settings:
                setTitle( getString( R.string.member_settings_header ) );
                fragment = new MemberSettingsFragment();
                args.putSerializable( getString( R.string.nav_membersettings ), mCredential );
                fragment.setArguments( args );
                break;
            case R.id.nav_connectionRequests:
                mConnectionsNode.loadRequests();
                break;
            case R.id.nav_theme:
                return false;
            default:
        }
        if ( !loadingFromDifferentMethods ) {
            /*Send the args to the fragment before displaying*/
            fragment.setArguments( args );
            /*display the fragment*/
            loadFragmentWithoutBackStack( fragment );

        }
        /*after we display the fragment, close the drawer*/
        DrawerLayout drawer = findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
        return true;
    }

    /**
     * Displays the wait fragment usually used during async tasks.
     * Furthermore, may be removed with the use of the backbutton
     */
    @Override
    public void onWaitFragmentInteractionShow() {
        /*displays the wait fragment to the user, meaning that something is loading*/
        getSupportFragmentManager()
                .beginTransaction()
                .add( R.id.frame_home_container, new WaitFragment(), "WAIT" )
                .addToBackStack( null )
                .commit();
    }

    /**
     * Hides the wait fragment usually used during async tasks.
     * Furthermore, may be removed with the use of the backbutton
     */
    @Override
    public void onWaitFragmentInteractionHide() {
        /*remove the wait fragment that is displayed; meaning that something is done loading*/
        getSupportFragmentManager()
                .beginTransaction()
                .remove( Objects.requireNonNull( getSupportFragmentManager().findFragmentByTag( "WAIT" ) ) )
                .commit();
    }

    /**
     * Signs the user out of the current account
     */
    private void logout() {
        new DeleteTokenAsyncTask( this ).execute();
    }

    /**
     * Helper method to load an instance of the given fragment into the current activity
     */
    public void loadFragment( Fragment frag ) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace( R.id.frame_home_container, frag, frag.getClass().getSimpleName() )
                .addToBackStack( null );
        // Commit the transaction
        transaction.commit();
    }

    /**
     * Helper method to load an instance of the given fragment into the current activity
     *
     * @param frag fragment to be loaded
     * @author Emmett Kang, Bryan Santos
     * @version 30 November 2018
     */
    public void loadFragmentWithoutBackStack( Fragment frag ) {
        int backstackCount = getSupportFragmentManager().getBackStackEntryCount();
        Log.d( "BRYAN", "backstack before loading: " + backstackCount );
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction()
                .replace( R.id.frame_home_container, frag, frag.getClass().getSimpleName() );
        if ( fm.getBackStackEntryCount() < 1 ) {
            transaction.addToBackStack( null );
        }
        // Commit the transaction
        transaction.commit();
    }

    /**
     * Unused listener method. Responds to a press on the weather list items within the weather fragment.
     *
     * @param item the weather list item which was selected
     */
    @Override
    public void onWeatherListItemFragmentInteraction( WeatherDate item ) {
    }

    @Override
    public void onChangeMemberInfo( Credentials credentials ) {
        mCredential = credentials;
        Log.d( "Debug Bryan", "creating new intent" );
        Intent intent = new Intent( this, HomeActivity.class );
        intent.putExtra( getString( R.string.keys_credential_member_settings ), mCredential );
        intent.putExtra( getString( reload_member_settings ), true );
        Log.d( "Debug Bryan", "intent created" );
        Log.d( "Debug Bryan", "starting new activity" );
        startActivity( intent );
        Log.d( "Debug Bryan", "started new home activity" );
        finish();
        Log.d( "Debug Bryan", "ended old home activity" );
//        this.overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out );
    }

    /**
     * Responds to when the connections list is interacted with.
     *
     * @param item the connections which was chosen
     */
    @Override
    public void onConnectionsListFragmentInteraction( Connection item ) {
        mConnectionsNode.onListFragmentInteraction( item );
    }

    /**
     * Responds to when the connections list is interacted with.
     *
     * @param item the connections which was chosen
     */
    @Override
    public void onConnectionsListFragmentLongInteraction( Connection item ) {
        mConnectionsNode.onConnectionsListFragmentLongInteraction( item );
    }

    /**
     * Responds to when the user would like to send a connection request
     *
     * @param theirUsername the user which was selected to receive a request
     */
    @Override
    public void onRequestListFragmentInteraction( String theirUsername ) {
        mConnectionsNode.onRequestListFragmentInteraction( theirUsername );
    }

    /**
     * When chat option is pressed, through connections, handle
     * the interaction from chatnode.
     *
     * @author Emmett Kang, Daylen Nguyen
     * @version 20 November 2018
     */
    @Override
    public void onOpenChatInteraction( int chatID, String username ) {
        mChatNode.onOpenChatInteraction( chatID, username );
    }

    /**
     * When chat is pressed, use the method fro chat node to handle
     * the interaction.
     *
     * @param item Chat Object
     * @author Emmett Kang, Daylen Nguyen
     * @version 20
     */
    @Override
    public void onChatsListFragmentInteraction( Chat item ) {
        mChatNode.onChatsListFragmentInteraction( item );
    }

    /**
     * Load the connections in order to display the connections
     * that the user has, so the user gets to select their connection
     * and load the CreateNewChat Fragment.
     *
     * @author Emmett Kang
     * @version 26 November 2018
     */
    @Override
    public void onCreateNewChatButtonPressed() {
        Fragment fragment = new CreateNewChatFragment();
        mConnectionsNode.loadConnections( fragment );

    }

    /**
     * When chat is long-pressed, handle the interaction
     * in mChatNode.
     *
     * @param item Chat object
     * @author Emmett Kang
     * @version 27 November 2018
     */
    @Override
    public void onChatsListFragmentLongInteraction( Chat item ) {
        mChatNode.onChatsListFragmentLongInteraction( item );
    }

    /**
     * When user have selected the users to add into the new chat
     * Check if the user have checked at least one user, and if they
     * haven't, warn them with toast.
     *
     * @param cbList         Checkbox list of connection's usernames.
     * @param connectionList connections that user has.
     * @param chatTitle      chat room title.
     * @author Emmett Kang
     * @version 25 November 2018
     */
    @Override
    public void CreateNewChatInteraction( ArrayList<CheckBox> cbList, ArrayList<Connection> connectionList,
                                          String chatTitle ) {
        StringBuilder checkedBoxesSB = checkedBoxes( cbList );
        boolean       flag           = false;

        //Check if the user has checked any of the check box.
        for ( CheckBox checkBox : cbList ) {
            if ( checkBox.isChecked() ) {
                flag = true;
                break;
            }
        }
        if ( flag ) {//If they have checked at least one, create new chat.
            mChatNode.CreateNewChatInteraction( cbList, connectionList, chatTitle );
            int   duration = Toast.LENGTH_SHORT;
            Toast toast    = Toast.makeText( this, checkedBoxesSB.toString() + "  selected", duration );
            toast.show();
        } else {//If not, warn them and do nothing.
            int   duration = Toast.LENGTH_SHORT;
            Toast toast    = Toast.makeText( this, "You have not selected anyone!", duration );
            toast.show();
        }

    }

    /**
     * Helper method to generate string of usernames that are checked.
     *
     * @param list list of usernames that are checked
     * @return appended string of usernames.
     * @author Emmett Kang
     * @version 25 November 2018
     */
    private StringBuilder checkedBoxes( List<CheckBox> list ) {
        StringBuilder sb = new StringBuilder();

        //Check if the box is checked, then append to string builder to display name
        for ( int i = 0; i < list.size(); i++ ) {
            if ( list.get( i ).isChecked() ) {
                sb.append( list.get( i ).getText().toString() );
            }
        }
        return sb;
    }

    /**
     * This is a getter method to return the notified chats
     *
     * @return list of notified chats
     * @author Emmett Kang
     * @version 27 November 2018
     */
    public ArrayList<Integer> getNotifiedChats() {
        return ( ArrayList<Integer> ) notifiedChats.clone();
    }


    /**
     * Update the notified chat list when user has read that chat.
     *
     * @param openedChatID chat room that was opened.
     * @author Emmett Kang
     * @version 27 November 2018
     */
    public void updateNotifiedChats( int openedChatID ) {
        //User read, so remove the chatroom that was notified from the list.
        notifiedChats.remove( openedChatID );
    }


    /**
     * When notification is received through firebase, notify the
     * hamburger button and color it so the user knows they've received a
     * message.
     *
     * @param colorOfText   color of menu item's text.
     * @param colorOfBurger color of burger button
     * @author Emmett Kang
     * @version 25 November 2018
     */
    public void notifyUI( int colorOfText, int colorOfBurger ) {
        toggle.getDrawerArrowDrawable().setColor( colorOfBurger );
        toggle.syncState();
        NavigationView  navigationView = findViewById( R.id.nav_view );
        Menu            m              = navigationView.getMenu();
        MenuItem        menuItem       = m.findItem( R.id.nav_chat );
        SpannableString span           = new SpannableString( menuItem.getTitle() );

        span.setSpan( new ForegroundColorSpan( colorOfText ),
                0, span.length(), 0 );
        menuItem.setTitle( span );
    }

    /**
     * Use chatnode's remove members from chat method in order to
     * remove members from the chat.
     *
     * @param users     users to be removed.
     * @param theChatID chat that will remove the users.
     * @author Emmett Kang
     * @version 21 November 2018
     */
    @Override
    public void RemoveMemberInteraction( ArrayList<String> users, int theChatID ) {
        mChatNode.RemoveMembersFromChat( users, theChatID );
    }

    public void onChangeTheme() {
        Intent intent = new Intent( this, HomeActivity.class );
        intent.putExtra( getString( R.string.key_credential ), mCredential );


        intent.putExtra( getString( R.string.reload_theme_switch ), mSwitch.isChecked() );
        finish();
        startActivity( intent );
//        this.overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out );

    }

    /**
     * Will display an alert dialog to the user that lets the user know
     * some connection request action happened and gives them the option to respond.
     *
     * @param msg      the message to display in the notification
     * @param positive the message representing the positive/affirmative action for the user to choose
     * @param action   the Runnable that we want to execute when the user chooses the positive action
     * @author Michelle Brown
     */
    private void ShowConnectionRequestAlert( String msg, String positive, final Runnable action ) {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );
        builder.setMessage( msg )
                .setPositiveButton( positive, ( dialog, id ) -> { //anonymous onclick listener
                    action.run();
                } )
                .setNegativeButton( "View Later", ( dialog, id ) -> {
                    //do nothing
                } );
        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * This class was given by Charles Bryan, when the user logs out,
     * we would like to delete the token from the database,
     * so we can grant another token when the user logs in.
     *
     * @author Charles Bryan
     */
    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {

        private HomeActivity mMaster;

        DeleteTokenAsyncTask( HomeActivity master ) {
            mMaster = master;
        }

        @Override
        protected Void doInBackground( Void... voids ) {
            //since we are already doing stuff in the background, go ahead
            //and remove the credentials from shared prefs here.
            SharedPreferences prefs = mMaster.getSharedPreferences( Weather_Preference, Context.MODE_PRIVATE );
            prefs.edit().remove( mMaster.getString( R.string.keys_prefs_password ) ).apply();
            prefs.edit().remove( mMaster.getString( R.string.keys_prefs_email ) ).apply();
            prefs.edit().putBoolean( ( getString( reload_member_settings ) ), false ).apply();

            prefs.edit().putBoolean( getString( R.string.reload_theme_switch ), false ).apply();
            try {
                //this call must be done asynchronously.
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch ( IOException e ) {
                Log.e( "FCM", "Delete error!" );
                e.printStackTrace();
            } catch ( Exception e ) {
                Log.e( "ERROR", "MAJOR ERROR" );
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMaster.onWaitFragmentInteractionShow();
        }

        @Override
        protected void onPostExecute( Void aVoid ) {
            super.onPostExecute( aVoid );
            Intent intent = new Intent( mMaster, SignInActivity.class );
            mMaster.startActivity( intent );
            mMaster.finish();
        }
    }

    /**
     * A BroadcastReceiver setup to listen for messages sent from
     * MyFirebaseMessagingService
     * that Android allows to run all the time.
     *
     * @author Charles Bryan, Emmett Kang, Michelle Brown
     */
    private class FirebaseMessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive( Context context, Intent intent ) {
            Log.i( "FCM Chat Frag", "start onRecieve" );
            if ( intent.hasExtra( "DATA" ) ) {
                String data = intent.getStringExtra( "DATA" );
                Log.w( "FCM DATA", data );
                JSONObject jObj;
                try {
                    jObj = new JSONObject( data );
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById( R.id.frame_home_container );
                    if ( jObj.has( "message" ) && jObj.has( "sender" ) ) {
                        String sender = jObj.getString( "sender" );
                        Log.wtf( "sender", sender );
                        Log.wtf( "username", mCredential.getUsername() );
                        int chatID = Integer.valueOf( jObj.getString( "chatID" ) );
                        if ( currentFragment instanceof ChatFragment ) {  //If user is in a chat fragment
                            if ( !sender.equals( mCredential.getUsername() ) ) {  //and message isn't from the user,
                                int currentChatID = ( ( ChatFragment ) currentFragment ).getmChatID();
                                if ( currentChatID != chatID ) { //check if notification is from other chats.
                                    notifiedChats.add( chatID ); //Put this chat as notified.

                                    //change the hamburger UI and menu item for chat so user gets notified.
                                    notifyUI( ContextCompat.getColor( Objects.requireNonNull( getApplicationContext() ), R.color.colorAccent ),
                                            ContextCompat.getColor( Objects.requireNonNull( getApplicationContext() ), R.color.colorAccent ) );
                                }
                            }
                        } else { //If user somewhere in homeactivity,
                            notifiedChats.add( chatID ); //put this chat as notified
                            //Notify the UI so user knows they've gotten a mesage.
                            notifyUI( ContextCompat.getColor( Objects.requireNonNull( getApplicationContext() ), R.color.colorLightBluePurple ),
                                    ContextCompat.getColor( Objects.requireNonNull( getApplicationContext() ), R.color.colorLightBluePurple ) );
                        }

                    } else if ( jObj.getString( "type" ).contains( "sent" ) ) {
                        Log.i( "HomeActivity", "we have a new request" );
                        //show the dialog
                        ShowConnectionRequestAlert( jObj.getString( "sender" ) +
                                        " has sent you a connection request!",
                                "View Request", mConnectionsNode::loadRequests );

                    } else if ( jObj.getString( "type" ).contains( "accepted" ) ) {
                        Log.i( "HomeActivity", "someone accepted our request" );
                        //show the dialog
                        ShowConnectionRequestAlert( jObj.getString( "sender" ) +
                                        " has accepted your connection request!",
                                "View Connection",
                                () -> mConnectionsNode.loadConnections( new ConnectionListFragment() ) );

                    }
                } catch ( JSONException e ) {
                    e.printStackTrace();
                }
            }
        }

    }
}