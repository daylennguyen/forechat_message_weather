package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionListFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;


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
    public ConnectionsNode(HomeActivity Master, thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials c) {
        mMaster = Master;
        mCredential = c;
    }


    /**
     * Creates a post request, asynchronously, to retrieve the
     * connection data from the application server
     */
    public void loadConnections(Fragment frag) {
        loadingFragment = frag;
        JSONObject memberInfo = new JSONObject();
        try {
            memberInfo.put("memberID", mCredential.getMemberID());
        } catch (JSONException e) {
            Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
        }
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(mMaster.getString(R.string.base_url))
                .appendPath(mMaster.getString((R.string.ep_connections)))
                .appendPath(mMaster.getString(R.string.ep_getConnections))
                .build();
        Log.w("URL for getting all connections:", uri.toString());
        new SendPostAsyncTask.Builder(uri.toString(), memberInfo)
                .onPreExecute(mMaster::onWaitFragmentInteractionShow)
                .onPostExecute(this::handleConnectionsOnPostExecute)
                .onCancelled(error -> Log.e("ERROR MICHELLE", error))
                .build().execute();
    }

    /**
     * After retrieving the response from the server, extract the connection data from the json
     *
     * @param result the response in json format
     */
    private void handleConnectionsOnPostExecute(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            JSONArray jsonConnections = resultsJSON.getJSONArray("connections");
            ArrayList<Connection>myConnections = new ArrayList<>();
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
            Bundle args = new Bundle();
            args.putSerializable(ConnectionListFragment.ARG_CONNECTIONS_LIST, myConnections);
            //Fragment connectionListFragment = new ConnectionListFragment();
            //connectionListFragment.setArguments(args);
            loadingFragment.setArguments(args);
            mMaster.loadFragment(loadingFragment);
        } catch (JSONException e) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }



}
