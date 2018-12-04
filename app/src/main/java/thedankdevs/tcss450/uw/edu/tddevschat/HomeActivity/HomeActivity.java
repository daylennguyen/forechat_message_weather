package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity;

import android.app.AlertDialog;
import android.content.*;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.iid.FirebaseInstanceId;
import org.json.JSONArray;
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
import thedankdevs.tcss450.uw.edu.tddevschat.*;
import thedankdevs.tcss450.uw.edu.tddevschat.SettingsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.MyFirebaseMessagingService;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.SettingsFragment.DETERMINANT_PREF;
import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.SettingsFragment.METRIC_PREF;
import static thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility.LocationNode.*;

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
        RemoveChatMembers.OnRemoveMemberListener,
        MemberSettingsFragment.OnFragmentInteractionListener,
        ThemesFragment.OnFragmentInteractionListener {

    /*NODES are helper classes meant to encapsulate various functionality of the application*/
    public SettingsNode mSettingsNode;
    public String       date;
    String myMetricValue;
    int    myDeterminValue;
    /*******************FIELD VARIABLES*******************/
    /*User saved credentials*/
    private Credentials     mCredential;
    private LocationNode    mLocationNode;
    private ConnectionsNode mConnectionsNode;
    private ChatNode        mChatNode;
    private String          mState, mCity, mZip, weathDesc;
    private double mLon, mLat;
    private HomeFragment mHome;
    private int          current_LocationDeterminant;
    private String       current_WeatherMetric;

    /*Chat Field variables*/
    private FirebaseMessageReciever mFirebaseMessageReciever;
    private ArrayList<Integer>      notifiedChats = new ArrayList<>();
    private SharedPreferences       myLocationPref, myMetricPref;
    /*Used to toggle the opened/closed state of the nav drawer*/
    private ActionBarDrawerToggle toggle;

    private TextView high, low, city_state, forecast;

    public void stopGPS() {
        mLocationNode.stopLocationUpdates();
    }

    public double getCurrentLat() {
        return mLocationNode.getmCurrentLocation().getLatitude();
    }

    public double getCurrentLon() {
        return mLocationNode.getmCurrentLocation().getLongitude();
    }

    public void startGPS() {
        mLocationNode.startLocationUpdates();
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        Log.d("Debug Bryan", "onCreate initialized");
        super.onCreate( savedInstanceState );

        Log.d("Debug Bryan", "creating theme");
        ThemeUtils.onActivityCreateTheme( this );

        Log.d("Debug Bryan", "theme created");
        setContentView( R.layout.activity_home );
        Log.d("Debug Bryan", "content view set");
        setTitle( "Main Page" );

        /*Check for saved-sign-in info*/
        Log.d("Debug Bryan", "getting credentials");
        mCredential = ( Credentials ) getIntent().getSerializableExtra( getString( R.string.key_credential ) );
        Log.d("Debug Bryan", "Credentials: " + mCredential);

        if ( mCredential == null ) {
            mCredential = ( Credentials ) getIntent().getSerializableExtra( getString( R.string.keys_credential_member_settings ) );
        }

        Log.d("Debug Bryan", "initializing Nodes");
        initializeNodes();
        Log.d("Debug Bryan", "nodes initialized");
        /*insert option items into the tool bar and initialize the drawer*/
        Toolbar toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        DrawerLayout drawer = findViewById( R.id.drawer_layout );

        Log.d("Debug Bryan", "initializing drawer");
        initializeActionDrawerToggle( drawer, toolbar );
        Log.d("Debug Bryan", "drawer initialized, starting location updates");

        mLocationNode.startLocationUpdates();
        Log.d("Debug Bryan", "location updates successful");

        if ( savedInstanceState == null ) {
            FragmentManager fm                     = getSupportFragmentManager();
            String          connectionNotification = getIntent().getStringExtra( getString( R.string.keys_intent_notification_connections ) );
            if ( connectionNotification != null ) {
                if ( connectionNotification.equals( getString( R.string.notification_requested ) ) ) {
                    mConnectionsNode.loadRequests();
                } else if ( connectionNotification.equals( getString( R.string.notification_accepted ) ) ) {
                    mConnectionsNode.loadConnections( new ConnectionListFragment() );
                }
            }
            if ( findViewById( R.id.frame_home_container ) != null ) {

                // add homeFragment to back stack
                mHome = new HomeFragment();
                fm.beginTransaction().add( R.id.frame_home_container, mHome ).addToBackStack( null ).commit();

                Log.d( "TESTING DAYLEN", String.format( "high = %s | low = %s | cs = %s", high, low, city_state ) );
            }
        }
    }


        if ( getIntent().getBooleanExtra( getString( R.string.reload_themes ), false ) ) {
            setTitle( getString( R.string.theme_title ) );
            loadFragmentWithoutBackStack( new ThemesFragment() );
        }

        // reload member Settings fragment
        if ( getIntent().getBooleanExtra( getString( R.string.reload_member_settings ), false ) ) {
            setTitle( getString( R.string.member_settings_header ) );
            Bundle                 args = new Bundle();
            MemberSettingsFragment frag = new MemberSettingsFragment();
            args.putSerializable( getString( R.string.nav_membersettings ), mCredential );
            frag.setArguments( args );
            loadFragmentWithoutBackStack( frag );
        }
        Log.d("Debug Bryan", "onCreate done");
    }

    @Override
    protected void onStart() {
        super.onStart();


        Coordinates_getCurrentWeatherData();

    }
    /*Enables toggling of the nav drawer and displays with username within said drawer as well*/
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
        mLocationNode.startLocationUpdates();


    }

    private void reinitializeNavigationDrawer() {
        NavigationView navigationView = findViewById( R.id.nav_view );
        View           hView          = navigationView.getHeaderView( 0 );
        navigationView.setNavigationItemSelectedListener( this );
        TextView nav_user = hView.findViewById( R.id.tv_drawerheader_username );
        nav_user.setText( mCredential.getUsername() ); //Set the header username.
    }

    /*Helper class to create node objects*/
    private void initializeNodes() {
        /*Retrieve user settings*/
        mSettingsNode = new SettingsNode( this );
        /*  Connections  */
        mConnectionsNode = new ConnectionsNode( this, mCredential );
        /*   Location    */
        mLocationNode = new LocationNode( this );
        /*     Chat      */
        mChatNode = new ChatNode( this, mCredential );
        /*   Weather     */
//        mWeatherNode = new WeatherNode(this, mLocationNode);
    }


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

    @Override
    protected void onPause() {
        super.onPause();
        if ( mFirebaseMessageReciever != null ) {
            unregisterReceiver( mFirebaseMessageReciever );
        }
        mLocationNode.stopLocationUpdates();
    }

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

    @Override
    public void onRequestPermissionsResult( int requestCode, @NonNull String permissions[], @NonNull int[] grantResults ) {
        mLocationNode.onRequestPermissionsResult( requestCode, permissions, grantResults );
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.home, menu );
        MenuItem search = menu.findItem( R.id.action_search_contacts );
        search.setVisible( false );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings ) {
            loadFragment( new SettingsFragment() );
            return true;
        } else if ( id == R.id.action_logout ) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected( item );
    }

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
                    args.putDouble( LATITUDE_KEY, mLocationNode.getmCurrentLocation().getLatitude() );
                    args.putDouble( LONGITUDE_KEY, mLocationNode.getmCurrentLocation().getLongitude() );
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
                setTitle( getString( R.string.theme_title ) );
                fragment = new ThemesFragment();
                break;

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

    @Override
    public void onWaitFragmentInteractionShow() {
        /*displays the wait fragment to the user, meaning that something is loading*/
        getSupportFragmentManager()
                .beginTransaction()
                .add( R.id.frame_home_container, new WaitFragment(), "WAIT" )
                .addToBackStack( null )
                .commit();
    }

    @Override
    public void onWaitFragmentInteractionHide() {
        /*remove the wait fragment that is displayed; meaning that something is done loading*/
        getSupportFragmentManager()
                .beginTransaction()
                .remove( Objects.requireNonNull( getSupportFragmentManager().findFragmentByTag( "WAIT" ) ) )
                .commit();

    }

    /*Signs the user out of the current account*/
    private void logout() {
        new DeleteTokenAsyncTask( this ).execute();
    }

    /*Helper method to load an instance of the given fragment into the current activity*/
    public void loadFragment( Fragment frag ) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace( R.id.frame_home_container, frag, frag.getClass().getSimpleName() )
                .addToBackStack( null );
        // Commit the transaction
        transaction.commit();
//        this.overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out );
    }

    /*Helper method to load an instance of the given fragment into the current activity*/
    public void loadFragmentWithoutBackStack( Fragment frag ) {
        int backstackCount = getSupportFragmentManager().getBackStackEntryCount();
        Log.d( "BRYAN", "backstack before loading: " + backstackCount );
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction()
                .replace( R.id.frame_home_container, frag, frag.getClass().getSimpleName() );
                //.addToBackStack(null);


        if ( fm.getBackStackEntryCount() < 1 ) {
            transaction.addToBackStack( null );
        }


        // Commit the transaction
        transaction.commit();
//        this.overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out );
    }

    @Override
    public void onWeatherListItemFragmentInteraction( WeatherDate item ) {

    }

    @Override
    public void onChangeMemberInfo( Credentials credentials ) {
        mCredential = credentials;
        Log.d("Debug Bryan", "creating new intent");
        Intent intent = new Intent( this, HomeActivity.class );
        intent.putExtra( getString( R.string.keys_credential_member_settings ), mCredential );
        intent.putExtra( getString( R.string.reload_member_settings ), true );
        Log.d("Debug Bryan", "intent created");
        Log.d("Debug Bryan", "starting new activity");
        startActivity( intent );
        Log.d("Debug Bryan", "started new home activity");
        finish();
        Log.d("Debug Bryan", "ended old home activity");
//        this.overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out );
    }


    @Override
    public void onConnectionsListFragmentInteraction( Connection item ) {
        mConnectionsNode.onListFragmentInteraction( item );
    }

    @Override
    public void onConnectionsListFragmentLongInteraction( Connection item ) {
        mConnectionsNode.onConnectionsListFragmentLongInteraction( item );
    }

    @Override
    public void onRequestListFragmentInteraction( String theirUsername ) {
        mConnectionsNode.onRequestListFragmentInteraction( theirUsername );
    }

    @Override
    public void onOpenChatInteraction( int chatID, String email, String username ) {
        mChatNode.onOpenChatInteraction( chatID, email, username );
    }

    @Override
    public void onChatsListFragmentInteraction( Chat item ) {
        mChatNode.onChatsListFragmentInteraction( item );
    }

    @Override
    public void onCreateNewChatButtonPressed() {
        Fragment fragment = new CreateNewChatFragment();
        mConnectionsNode.loadConnections( fragment );

    }

    @Override
    public void onChatsListFragmentLongInteraction( Chat item ) {
        mChatNode.onChatsListFragmentLongInteraction( item );
    }

    @Override
    public void CreateNewChatInteraction( ArrayList<CheckBox> cbList, ArrayList<Connection> connectionList,
                                          String chatTitle ) {
        StringBuilder checkedBoxesSB = checkedBoxes( cbList );
        boolean       flag           = false;
        for ( CheckBox checkBox : cbList ) {
            if ( checkBox.isChecked() ) {
                flag = true;
                break;
            }
        }
        if ( flag ) {
            mChatNode.CreateNewChatInteraction( cbList, connectionList, chatTitle );
            int   duration = Toast.LENGTH_SHORT;
            Toast toast    = Toast.makeText( this, checkedBoxesSB.toString() + "  selected", duration );
            toast.show();
        } else {
            int   duration = Toast.LENGTH_SHORT;
            Toast toast    = Toast.makeText( this, "You have not selected anyone!", duration );
            toast.show();
        }

    }

    private StringBuilder checkedBoxes( List<CheckBox> list ) {
        StringBuilder sb = new StringBuilder();

        for ( int i = 0; i < list.size(); i++ ) {
            if ( list.get( i ).isChecked() ) {
                sb.append( list.get( i ).getText().toString() );
            }
        }
        return sb;
    }

    public ArrayList<Integer> getNotifiedChats() {
        return ( ArrayList<Integer> ) notifiedChats.clone();
    }


    public void updateNotifiedChats( int openedChatID ) {
        Log.w( "CRASH CHAT", String.valueOf( openedChatID ) );
        notifiedChats.remove( openedChatID );

    }


    public void notifyUI( int colorOfText, int colorOfBurger ) {
        toggle.getDrawerArrowDrawable().setColor( colorOfBurger );
        toggle.syncState();
        NavigationView  navigationView = findViewById( R.id.nav_view );
        Menu            m              = navigationView.getMenu();
        MenuItem        menuItem       = m.findItem( R.id.nav_chat );
        SpannableString s              = new SpannableString( menuItem.getTitle() );

        s.setSpan( new ForegroundColorSpan( colorOfText ),
                0, s.length(), 0 );
        menuItem.setTitle( s );
    }

    /**
     *
     */
//    private void SuccessOrFailToast() {
//
//    }
    @Override
    public void RemoveMemberInteraction( ArrayList<String> users, int theChatID ) {
        mChatNode.RemoveMembersFromChat( users, theChatID );
    }

    @Override
    public void onChangeTheme( String theme ) {

        Intent intent = new Intent( this, HomeActivity.class );
        intent.putExtra( getString( R.string.key_credential ), mCredential );
        intent.putExtra( getString( R.string.reload_themes ), true );
        finish();
        startActivity( intent );
//        this.overridePendingTransition( android.R.anim.fade_in, android.R.anim.fade_out );

    }

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

    public void getSharedPrefAndValue() {
        SharedPreferences myLocationPref = getSharedPreferences( METRIC_PREF, Context.MODE_PRIVATE );
        SharedPreferences myMetricPref   = getSharedPreferences( DETERMINANT_PREF, Context.MODE_PRIVATE );


        myMetricValue = myMetricPref.getString( METRIC_PREF, "C" );
        myDeterminValue = myLocationPref.getInt( DETERMINANT_PREF, SettingsNode.GPS_DATA );
        Log.d( "DAYY", myMetricValue + myDeterminValue );
    }

    /*Generates a URI string. Changes the endpoint depending on location determinant*/
    private String constructRequestLocation() {
        SharedPreferences myDeterPref = Objects.requireNonNull( getApplicationContext() ).getSharedPreferences( DETERMINANT_PREF, Context.MODE_PRIVATE );

        Uri.Builder uri_builder = new Uri.Builder()
                .scheme( "https" )
                .appendPath( getString( R.string.base_url ) )
                .appendPath( getString( R.string.ep_weather ) );
        switch ( myDeterPref.getInt( DETERMINANT_PREF, SettingsNode.GPS_DATA ) ) {
            case SettingsNode.CITY_STATE:
                uri_builder.appendPath( getString( R.string.ep_weather_citystate ) );
                break;
            case SettingsNode.GPS_DATA:
                uri_builder.appendPath( getString( R.string.ep_weather_bycoordinate ) );
                break;
            case SettingsNode.SELECT_FROM_MAP:
                uri_builder.appendPath( getString( R.string.ep_weather_bycoordinate ) );
                break;
            case SettingsNode.POSTAL_CODE:
                uri_builder.appendPath( getString( R.string.ep_weather_postalcode ) );
                break;
        }
        return uri_builder.build().toString();
    }

    /*returns a json object, dependent on the location determinant*/
    private JSONObject constructRequestJSON() {
        JSONObject        request      = new JSONObject();
        SharedPreferences myDeterPref  = Objects.requireNonNull( getApplicationContext() ).getSharedPreferences( DETERMINANT_PREF, Context.MODE_PRIVATE );
        SharedPreferences myLocatePref = Objects.requireNonNull( getApplicationContext() ).getSharedPreferences( SettingsNode.LOCATIONPREF, 0 );
        myMetricPref = Objects.requireNonNull( getApplicationContext() ).getSharedPreferences( METRIC_PREF, 0 );

        String mCity, mState, mZip;
        double mLat, mLon;
        try {
            switch ( myDeterPref.getInt( DETERMINANT_PREF, SettingsNode.GPS_DATA ) ) {
                case SettingsNode.CITY_STATE:
                    mCity = myLocatePref.getString( CITY_KEY, "TACOMA" );
                    mState = myLocatePref.getString( STATE_KEY, "WA" );
                    Log.w( "DAYLEN LOCATION BASED ON PREFERENCE", mCity + mState );
                    request.put( getString( R.string.weather_lcase_city ), mCity );
                    request.put( getString( R.string.weather_lcase_state ), mState );
                    break;
                case SettingsNode.GPS_DATA:
                    mLat = mLocationNode.getmCurrentLocation().getLatitude();
                    mLon = mLocationNode.getmCurrentLocation().getLongitude();
                    Log.w( "DAYLEN LOCATION BASED ON PREFERENCE", mLat + " mlatlon " + mLon );

                    request.put( getString( R.string.weather_lon_json ), mLon );
                    request.put( getString( R.string.weather_lat_json ), mLat );
                    break;
                case SettingsNode.SELECT_FROM_MAP:
                    mLat = myLocatePref.getFloat( MAP_LAT_KEY, 0 );
                    mLon = myLocatePref.getFloat( MAP_LON_KEY, 0 );
                    Log.w( "DAYLEN LOCATION BASED ON PREFERENCE", mLat + " mlatlon " + mLon );
                    request.put( getString( R.string.weather_lon_json ), mLon );
                    request.put( getString( R.string.weather_lat_json ), mLat );
                    break;
                case SettingsNode.POSTAL_CODE:
                    mZip = myLocatePref.getString( ZIP_KEY, "98422" );
                    request.put( getString( R.string.weather_json_postal ), mZip );
                    Log.w( "DAYLEN LOCATION BASED ON PREFERENCE", mZip + " MZIP " );


                    break;
            }
            /*DEFAULT UNIT IS CELSIUS*/
            if ( !Objects.requireNonNull( myMetricPref.getString( DETERMINANT_PREF, "C" ) ).equals( SettingsNode.CELSIUS ) ) {
                switch ( Objects.requireNonNull( myMetricPref.getString( METRIC_PREF, "C" ) ) ) {
                    case SettingsNode.FAHRENHEIT:
                        request.put( "units", myMetricPref.getString( METRIC_PREF, "C" ) );
                        break;
                    case SettingsNode.KELVIN:
                        request.put( "units", myMetricPref.getString( METRIC_PREF, "C" ) );
                        break;
                }
            }
        } catch ( Exception e ) {
            Log.e( "WEATHER", String.valueOf( e ) );
        }
//        getMetricReqString( request );
        return request;
    }

    private void Coordinates_getCurrentWeatherData() {
        JSONObject request  = constructRequestJSON();
        String     mSendUrl = constructRequestLocation();
        Log.d( "daylen weather", mSendUrl );
        if ( request != null ) {
            Log.d( "daylen weather", request.toString() );
        }
        new SendPostAsyncTask.Builder( mSendUrl, request )
                .onPreExecute( () -> {
                    high = findViewById( R.id.high_temp_home );
                    low = findViewById( R.id.low_temp_home );
                    city_state = findViewById( R.id.city_state_home );
                    current_LocationDeterminant = getSharedPreferences( DETERMINANT_PREF, Context.MODE_PRIVATE ).getInt( DETERMINANT_PREF, SettingsNode.GPS_DATA );
                    current_WeatherMetric = getSharedPreferences( METRIC_PREF, Context.MODE_PRIVATE ).getString( METRIC_PREF, SettingsNode.FAHRENHEIT );


                } )
                .onPostExecute( this::PostWeatherRequest )
                .onCancelled( error -> Log.e( "daylen weather", error ) )
                .build()
                .execute();
    }


    private void PostWeatherRequest( String result ) {
        //parse the post request
        Log.i( "DAYLEN_WEATH_DATE_FRAG", "LOCATIONDETERMINANT = ".concat( String.valueOf( current_LocationDeterminant ) ) );
        Log.i( "DAYLEN_WEATH_DATE_FRAG", "WEATHER METRIC = ".concat( String.valueOf( current_WeatherMetric ) ) );
        Log.i( "json return", result );


        try {


            // This is the result from the web service
            JSONObject res = new JSONObject( result );
            Log.i( "Daylen", String.valueOf( res ) );

            // retrieve the city and state of the current user from the
            // latitude and longitude
            mState = res.getString( "state_code" );
            mCity = res.getString( "city_name" );
            Log.i( "DAYLEN_WEATH_DATE_FRAG", mCity + mState );
            // response will contain a json array containing 16 day forecast
            JSONArray data = res.getJSONArray( "data" );
            // data will be in the form of a json object
            JSONObject currentDay = ( JSONObject ) data.get( 0 );
            // extract json values from response
            date = currentDay.getString( "datetime" );

            double     avg       = currentDay.getDouble( "temp" );
            double     min       = currentDay.getDouble( "min_temp" );
            double     max       = currentDay.getDouble( "max_temp" );
            JSONObject weather   = currentDay.getJSONObject( "weather" );
            String     weathDesc = weather.getString( "description" );
            Log.w( "WEATHER CURRENT", "\n[  ]\n\tDate: " + date + "\n\tAvg:" + avg + "\n\tMin:" + min + "\n\tMax" + max + "\n\tdesc:" + weathDesc );

            ( ( TextView ) findViewById( R.id.city_state_home ) ).setText( String.format( "in %s, %s, you can expect", mCity, mState ) );
            ( ( TextView ) findViewById( R.id.low_temp_home ) ).setText( MessageFormat.format( "{0}°{1}", String.valueOf( min ), current_WeatherMetric ) );
            ( ( TextView ) findViewById( R.id.high_temp_home ) ).setText( MessageFormat.format( "{0}°{1}", String.valueOf( max ), current_WeatherMetric ) );
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }

    class DeleteTokenAsyncTask extends AsyncTask<Void, Void, Void> {

        private HomeActivity mMaster;

        DeleteTokenAsyncTask( HomeActivity master ) {
            mMaster = master;
        }

        @Override
        protected Void doInBackground( Void... voids ) {
            //since we are already doing stuff in the background, go ahead
            //and remove the credentials from shared prefs here.
            SharedPreferences prefs = mMaster.getSharedPreferences( mMaster.getString( R.string.keys_shared_prefs ), Context.MODE_PRIVATE );
            prefs.edit().remove( mMaster.getString( R.string.keys_prefs_password ) ).apply();
            prefs.edit().remove( mMaster.getString( R.string.keys_prefs_email ) ).apply();
            prefs.edit().putBoolean(( getString( R.string.reload_member_settings )), false );
            prefs.edit().putBoolean( getString( R.string.reload_themes ), false );
//            prefs.edit().putBoolean()
            try {
                //this call must be done asynchronously.
                FirebaseInstanceId.getInstance().deleteInstanceId();
            } catch ( IOException e ) {

                Log.e( "FCM", "Delete error!" );
                e.printStackTrace();

            } catch (Exception e) {
                Log.e("ERROR", "MAJOR ERROR");
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
            //close the app
            mMaster.finishAndRemoveTask();
        }
    }

    /**
     * A BroadcastReceiver setup to listen for messages sent from
     * MyFirebaseMessagingService
     * that Android allows to run all the time.
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
                        if ( currentFragment instanceof ChatFragment ) {
                            if ( !sender.equals( mCredential.getUsername() ) ) {
                                int currentChatID = ( ( ChatFragment ) currentFragment ).getmChatID();
                                Log.wtf( "currChatID: ", String.valueOf( currentChatID ) );
                                if ( currentChatID != chatID ) {
                                    notifiedChats.add( chatID );
                                    notifyUI( ContextCompat.getColor( Objects.requireNonNull( getApplicationContext() ), R.color.colorLightPurple ),
                                            ContextCompat.getColor( Objects.requireNonNull( getApplicationContext() ), R.color.colorLightPurple ) );
                                }
                            }
                        } else {
                            notifiedChats.add( chatID );
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