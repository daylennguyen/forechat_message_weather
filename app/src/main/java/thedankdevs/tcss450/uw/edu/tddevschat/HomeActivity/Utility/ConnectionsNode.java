package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionListFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.Requests.RequestFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.Requests.content.Request;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

import java.util.ArrayList;


/**
 * The Connections Node; contains the primary functionality pertaining to retrieving connections
 * from the application database/server.
 */
public class ConnectionsNode {

    /**
     * The primary activity from which this object is instantiated
     */
    private HomeActivity mMaster;

    /**
     * A credentials object containing the current user's credentials
     */
    private thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials mCredential;

    private Fragment loadingFragment;

    /**
     * The constructor for the connection node
     *
     * @param Master The primary activity from which this object is instantiated
     * @param c      A credentials object containing the current user's credentials
     */
    public ConnectionsNode( HomeActivity Master, thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials c ) {
        mMaster = Master;
        mCredential = c;
    }


    /**
     * Creates a post request, asynchronously, to retrieve the
     * connection data from the application server
     */
    public void loadConnections( Fragment frag ) {
        loadingFragment = frag;
        JSONObject memberInfo = new JSONObject();
        try {
            memberInfo.put( "memberID", mCredential.getMemberID() );
        } catch ( JSONException e ) {
            Log.wtf( "JSON", "Error creating JSON: " + e.getMessage() );
        }
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( ( R.string.ep_connections ) ) )
                .appendPath( mMaster.getString( R.string.ep_getConnections ) )
                .build();
        Log.w( "URL for getting all connections:", uri.toString() );
        new SendPostAsyncTask.Builder( uri.toString(), memberInfo )
                .onPreExecute( mMaster::onWaitFragmentInteractionShow )
                .onPostExecute( this::handleConnectionsOnPostExecute )
                .onCancelled( error -> Log.e( "ERROR MICHELLE", error ) )
                .build().execute();
    }

    /**
     * After retrieving the response from the server, extract the connection data from the json
     *
     * @param result the response in json format
     */
    private void handleConnectionsOnPostExecute( String result ) {
        try {
            JSONObject            resultsJSON     = new JSONObject( result );
            JSONArray             jsonConnections = resultsJSON.getJSONArray( "connections" );
            ArrayList<Connection> myConnections   = new ArrayList<>();
            for ( int i = 0; i < jsonConnections.length(); i++ ) {
                JSONObject connection = jsonConnections.getJSONObject( i );
                String     first      = connection.getString( "firstname" );
                String     last       = connection.getString( "lastname" );
                String     username   = connection.getString( "username" );
                String     email      = connection.getString( "email" );
                int        chatid;
                try {
                    chatid = connection.getInt( "chatid" );
                } catch ( JSONException e ) {
                    chatid = -1;
                }
                myConnections.add( new Connection.Builder( email, username )
                        .addFirstName( first )
                        .addLastName( last )
                        .addChatID( chatid )
                        .isMine()
                        .build() );
            }
            Bundle args = new Bundle();
            args.putSerializable( ConnectionListFragment.ARG_CONNECTIONS_LIST, myConnections );
            args.putInt( ConnectionListFragment.ARG_CREDENTIALS, mCredential.getMemberID() );

            //Fragment connectionListFragment = new ConnectionListFragment();
            //connectionListFragment.setArguments(args);
            loadingFragment.setArguments( args );
            mMaster.onWaitFragmentInteractionHide();
            mMaster.loadFragment( loadingFragment );
            mMaster.setTitle( "Connections" );
        } catch ( JSONException e ) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e( "JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage() );
        }
    }

    /**
     * Opens a Connection fragment for the corresponding connection
     * that was clicked on in {@link ConnectionListFragment}
     *
     * @param item the connection selected
     */
    public void onListFragmentInteraction( Connection item ) {
        ConnectionFragment connectionFragment = new ConnectionFragment();
        Bundle             args               = new Bundle();

        //Could this be just one item being sent?
        args.putSerializable( mMaster.getString( R.string.key_connection_connection ), item );
        args.putSerializable( mMaster.getString( R.string.key_credential ), mCredential );
        connectionFragment.setArguments( args );
        mMaster.loadFragment( connectionFragment );
    }

    public void onConnectionsListFragmentLongInteraction( Connection item ) {
        AlertDialog.Builder builder = new AlertDialog.Builder( mMaster );
        builder.setMessage( item.getUsername() + " Options" )
                .setPositiveButton( "Remove this connection", ( dialog, id ) -> { //anonymous onclick listener
                    removeConnection( mCredential.getMemberID(), item.getEmail(), item.getChatID() );
                } )
                .setNegativeButton( "Cancel", ( dialog, id ) -> {
                    //do nothing
                } );
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeConnection( int memberID, String email, int ourChatID ) {
        JSONObject removeJson = new JSONObject();
        try {
            removeJson.put( "myMemberID", memberID );
            removeJson.put( "theirEmail", email );
            removeJson.put( "ourChatID", ourChatID );
        } catch ( JSONException e ) {
            Log.wtf( "JSON", "Error creating JSON: " + e.getMessage() );
        }
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( ( R.string.ep_connections ) ) )
                .appendPath( mMaster.getString( R.string.ep_removeConnection ) )
                .build();
        Log.w( "removeConnection() - URL for removing a connection:", uri.toString() );
        new SendPostAsyncTask.Builder( uri.toString(), removeJson )
                .onPreExecute( mMaster::onWaitFragmentInteractionShow )
                .onPostExecute( this::handleRemoveOnPostExecute )
                .onCancelled( error -> Log.e( "removeConnection() - ERROR MICHELLE", error ) )
                .build().execute();
    }

    private void handleRemoveOnPostExecute( String result ) {
        try {
            Log.w( "REMOVE CONNECTION POST RESULT", result );
            //This is the result from the web service
            JSONObject res = new JSONObject( result );
            String     toastMsg;
            if ( res.has( "success" ) && res.getBoolean( "success" ) ) {
                loadConnections( new ConnectionListFragment() );
                toastMsg = mMaster.getString( R.string.connection_removesent );
            } else {
                toastMsg = mMaster.getString( R.string.connection_removefailed );
            }
            Toast toast = Toast.makeText( mMaster.getApplicationContext(),
                    toastMsg, Toast.LENGTH_SHORT );
            mMaster.onWaitFragmentInteractionHide();
            toast.show();
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }


    //__________________________CONNECTION REQUESTS__________________________


    public void loadRequests() {
        JSONObject memberInfo = new JSONObject();
        try {
            memberInfo.put( "memberID", mCredential.getMemberID() );
        } catch ( JSONException e ) {
            Log.wtf( "JSON", "Error creating JSON: " + e.getMessage() );
        }
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( ( R.string.ep_connections ) ) )
                .appendPath( mMaster.getString( R.string.ep_getRequests ) )
                .build();
        Log.w( "loadRequests() - URL for getting all connection requests:", uri.toString() );
        new SendPostAsyncTask.Builder( uri.toString(), memberInfo )
                .onPreExecute( mMaster::onWaitFragmentInteractionShow )
                .onPostExecute( this::handleRequestsOnPostExecute )
                .onCancelled( error -> Log.e( "loadRequests() - ERROR MICHELLE", error ) )
                .build().execute();
    }

    private void handleRequestsOnPostExecute( String result ) {
        try {
            ArrayList<Request> myConnRequests       = new ArrayList<>();
            JSONObject         resultsJSON          = new JSONObject( result );
            JSONArray          jsonReceivedRequests = resultsJSON.getJSONArray( "received" );
            for ( int i = 0; i < jsonReceivedRequests.length(); i++ ) {
                JSONObject connection = jsonReceivedRequests.getJSONObject( i );
                String     username   = connection.getString( "username" );
                myConnRequests.add( new Request.Builder( username, true )
                        .build() );
            }
            JSONArray jsonSentRequests = resultsJSON.getJSONArray( "sent" );
            for ( int i = 0; i < jsonSentRequests.length(); i++ ) {
                JSONObject connection = jsonSentRequests.getJSONObject( i );
                String     username   = connection.getString( "username" );
                myConnRequests.add( new Request.Builder( username, false )
                        .build() );
            }
            Bundle args = new Bundle();
            args.putSerializable( RequestFragment.ARG_REQUESTS_LIST, myConnRequests );
            Fragment connRequestsFragment = new RequestFragment();
            connRequestsFragment.setArguments( args );
            mMaster.loadFragment( connRequestsFragment );
            mMaster.setTitle( "Pending Requests" );
        } catch ( JSONException e ) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e( "handleRequestsOnPostExecute - JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage() );
        }
    }

    public void onRequestListFragmentInteraction( String theirUsername ) {
        JSONObject acceptJson = new JSONObject();
        try {
            acceptJson.put( "myMemberID", mCredential.getMemberID() );
            acceptJson.put( "myUsername", mCredential.getUsername() );
            acceptJson.put( "theirUsername", theirUsername );
        } catch ( JSONException e ) {
            Log.wtf( "JSON", "Error creating JSON: " + e.getMessage() );
        }
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( ( R.string.ep_connections ) ) )
                .appendPath( mMaster.getString( R.string.ep_verifyConnection ) )
                .build();
        Log.w( "URL for accepting a connection request:", uri.toString() );
        new SendPostAsyncTask.Builder( uri.toString(), acceptJson )
                .onPostExecute( this::handleAcceptRequestOnPostExecute )
                .onCancelled( error -> Log.e( "onRequestListFragmentInteraction - ERROR MICHELLE", error ) )
                .build().execute();
    }

    private void handleAcceptRequestOnPostExecute( String result ) {
        try {
            Log.w( "ACCEPT CONNECTION POST RESULT", result );
            //This is the result from the web service
            JSONObject res = new JSONObject( result );
            //TODO: let the user know properly in RequestFragment
            Button acceptButton = mMaster.findViewById( R.id.btn_connectionRequest_accept );
            //acceptButton.setText("Accepted!");
            //acceptButton.setBackgroundColor(mMaster.getResources().getColor(R.color.colorBluePurple));
            //acceptButton.setTextColor(Color.WHITE);
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }

}
