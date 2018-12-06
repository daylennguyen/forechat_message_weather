package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A Fragment that utilizes a RecyclerView to display local connections
 * of the currently logged in user. This Fragment is also used
 * by the Search Connections functionality of the app that
 * searches through both local and the database of connections
 * that matches the search query typed in by the user.
 *
 * @author Michelle Brown
 * @author Bryan Santos
 * @version 12/05/2018
 */
public class ConnectionListFragment extends Fragment implements SearchView.OnQueryTextListener {

    public static final String ARG_CONNECTIONS_LIST = "connections list";
    public static final String ARG_CREDENTIALS      = "credentials";
    boolean mExistsLocally;
    /**
     * List of existing connections used by mLocalAdapter
     */
    private ArrayList<Connection>             mConnections;
    /**
     * Adapter for existing connections
     */
    private ConnectionListRecyclerViewAdapter mLocalAdapter;
    /**
     * Adapter used for sending post requests to server to retrieve global connections
     * when user is searching for users that they are not currently connected with
     */
    private ConnectionListRecyclerViewAdapter mGlobalAdapter;
    /**
     * Used as a field to change adapters between local and global
     */
    private RecyclerView                      mRecyclerView;
    private int                               mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private SearchView                        mSearchView;

    private MenuItem mSearch;
    private       String                      mPreviousQuery;

    private int mMemberId;



    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConnectionListFragment() {
    }


    @Override
    public void onAttach( Context context ) {
        super.onAttach( context );
        if ( context instanceof OnListFragmentInteractionListener ) {
            mListener = ( OnListFragmentInteractionListener ) context;
        } else {
            throw new RuntimeException( context.toString()
                    + " must implement OnListFragmentInteractionListener" );
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setHasOptionsMenu( true );
        if ( getArguments() != null ) {
            mConnections = ( ArrayList ) getArguments().getSerializable( ARG_CONNECTIONS_LIST );
            mMemberId = getArguments().getInt( ARG_CREDENTIALS );
            mPreviousQuery = "";

        }
    }

    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        View view = inflater.inflate( R.layout.fragment_connections_list, container, false );
        Log.d( getClass().getSimpleName(), "I am on create view" );

        // Set the adapter
        if ( view instanceof RecyclerView ) {
            Context context = view.getContext();
            mRecyclerView = ( RecyclerView ) view;
            if ( mColumnCount <= 1 ) {
                mRecyclerView.setLayoutManager( new LinearLayoutManager( context ) );
            } else {
                mRecyclerView.setLayoutManager( new GridLayoutManager( context, mColumnCount ) );
            }
            /*  set local adapter to be the primary list used so that this list
                is displayed first and so that search is looked through this list first */
            mLocalAdapter = new ConnectionListRecyclerViewAdapter( mConnections, mListener );
            mGlobalAdapter = null;
            mRecyclerView.setAdapter( mLocalAdapter );

        }
        if ( mConnections.size() == 0 ) {
            //Alert the user that they don't have any connections and give them the option to search
            //for ones to add
            AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
            builder.setMessage( "You don't have any connections!" )
                    .setPositiveButton( "Search for new connections", ( dialog, id ) -> { //anonymous onclick listener
                        mSearch.expandActionView();
                        mSearchView.setQueryHint( "username, email, first or last" );
                    } )
                    .setNegativeButton( "Cancel", ( dialog, id ) -> {
                        //do nothing
                    } );
            // 3. Get the AlertDialog from create()
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Uses menu of parent. Sets search view visible to user. Also sets
     * QueryTextListener to this class.
     *
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {
        super.onCreateOptionsMenu( menu, inflater );
        mSearch = menu.findItem( R.id.action_search_contacts );
        mSearch.setVisible( true );

        mSearchView = ( SearchView ) mSearch.getActionView();
        mSearchView.setVisibility( View.VISIBLE );
        mSearchView.setOnQueryTextListener( this );
    }

    @Override
    public boolean onQueryTextSubmit( String s ) {
        return false;
    }

    @Override
    public boolean onQueryTextChange( String text ) {
        text = text.toLowerCase();
        mExistsLocally = mLocalAdapter.filter( text );
        if ( !mExistsLocally ) {


            boolean sendRequest = true;
            /*
                since global adapter was previously initialized and that list
                contains the text query, check if it can be reused.
             */
            if ( mGlobalAdapter != null) {
                if (mGlobalAdapter.filter( text )) { // reuse old global adapter
                    Log.d( getClass().getSimpleName(), "I am setting mGlobalAdapter to be my adapter" );
                    mRecyclerView.setAdapter( mGlobalAdapter );
                    sendRequest = false;
                }
                // display no connections found and don't send new post request
                else if(mPreviousQuery.contains(text) || text.contains(mPreviousQuery)) {
                    mGlobalAdapter.setEmptyConnections();
                    mRecyclerView.setAdapter(mGlobalAdapter);
                    sendRequest = false;
                    Log.d(getClass().getSimpleName(), "Not making Requests");
                }

            }

            /*
                otherwise, make post request to search through database
                that current user is not connected with
             */
           if (sendRequest) {

                Log.d( getClass().getSimpleName(), "I'm sending a post request" );
                requestForAllContacts( text );
            }

            /*
                if local adapter contains the textQuery and globalAdapter was previously set,
                reset the adapter to local
             */

        } else if ( mExistsLocally && !Objects.requireNonNull( mRecyclerView.getAdapter() ).equals( mLocalAdapter ) ) {

            mRecyclerView.setAdapter( mLocalAdapter );
        }
        mPreviousQuery = text;

        Log.d( getClass().getSimpleName(), "current Adapter: " + mRecyclerView.getAdapter() );
        Log.d( getClass().getSimpleName(), "is my equals method working:" +
                " check with LocalAdapter: " + mRecyclerView.getAdapter().equals( mLocalAdapter ) );
        return false;
    }

    /**
     * Helper method to send post request to web service to retrieve all connections
     * from the database based on the text query.
     *
     * @param text search query
     */
    private void requestForAllContacts( String text ) {
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .authority( getString( R.string.base_url ) )
                .appendPath( getString( R.string.ep_search ) )
                .appendPath( getString( R.string.ep_contacts ) )
                .build();

        JSONObject searchAllContacts = new JSONObject();
        try {
            searchAllContacts.put( "values", text );
            searchAllContacts.put( "memberid", mMemberId );
            Log.d( getClass().getSimpleName(), "JSON request: " + searchAllContacts.toString() );
        } catch ( JSONException e ) {
            Log.e( getClass().getSimpleName(), "JSON object creation failed: " + e );
        }

        new SendPostAsyncTask.Builder( uri.toString(), searchAllContacts )
                .onCancelled( this::handleErrorsInTask )
                .onPostExecute( this::handleOnSearchPost )
                .build()
                .execute();
    }

    private void handleErrorsInTask( String result ) {
        Log.e( "ASYNC_TASK_ERROR", result );
    }


    private void handleOnSearchPost( final String result ) {
        Log.d( getClass().getSimpleName(), "I am handling Post Response" );
        try {
            JSONObject       jsonResults = new JSONObject( result );
            boolean          success     = jsonResults.getBoolean( "success" );
            List<Connection> globalConnections;
            JSONArray        membersArray;
            if ( success ) {
                globalConnections = new ArrayList<>();
                membersArray = jsonResults.getJSONArray( "members" );
                for ( int i = 0; i < membersArray.length(); i++ ) {
                    JSONObject member    = membersArray.getJSONObject( i );
                    String     firstName = member.getString( "firstname" );
                    String     lastName  = member.getString( "lastname" );
                    String     username  = member.getString( "username" );
                    String     email     = member.getString( "email" );

                    Connection c;
                    if ( mExistsLocally ) {
                        c = new Connection.Builder( email, username )
                                .addFirstName( firstName )
                                .addLastName( lastName )
                                .isMine()
                                .build();
                    } else {
                        c = new Connection.Builder( email, username )
                                .addFirstName( firstName )
                                .addLastName( lastName )
                                .build();
                    }
                    globalConnections.add( c );

                }

                mGlobalAdapter = new ConnectionListRecyclerViewAdapter( globalConnections, mListener );
                mRecyclerView.setAdapter( mGlobalAdapter );


                Log.d( getClass().getSimpleName(), "Successfully parsed JSON: " + globalConnections );
            } else {
                Log.d( getClass().getSimpleName(), "Not successful!!!! Fault coming from programmer" );
            }
        } catch ( JSONException e ) {
            Log.e( getClass().getSimpleName(), "Post Response failed to be parsed: " + e );
        }

        Log.d( getClass().getSimpleName(), "I'm done handling post response" );
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnListFragmentInteractionListener {
        void onConnectionsListFragmentInteraction( Connection item );

        void onConnectionsListFragmentLongInteraction( Connection item );
    }
}
