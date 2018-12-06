package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.MemberSettingsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

import java.util.Map;


/**
 * Helper class to alleviate code from HomeActivity. Used in connection
 * with the Fragment interaction with MemberSettingsFragment.
 * Sends Post Request to an endpoint and sends the corresponding response.
 *
 * @author Bryan Santos
 * @version 12/05/2018
 */
public class MemberSettingsNode {

    private final String       TAG = getClass().getSimpleName();
    private       HomeActivity mMaster;

    public MemberSettingsNode( HomeActivity master ) {
        mMaster = master;
    }

    /**
     * Creates the URI string of the endpoint to update
     * currently logged in user's personal information. Also creates
     * the necessary JSON object to the endpoint. It sends the
     * post request asynchronously.
     *
     * @param updateMap Storage that contains the information be updated
     * @param memberId  MemberId of the user whose information will be updated
     */
    public void onChangeMemberInfo( Map<String, String> updateMap, int memberId ) {

        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .authority( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( R.string.ep_settings ) )
                .appendPath( mMaster.getString( R.string.ep_update_info ) )
                .build();
        Log.d( TAG, "Uri: " + uri.toString() );

        JSONObject json = new JSONObject();
        try {
            json.put( "memberid", memberId );
            json.put( "values", new JSONObject( updateMap ) );
            Log.d( TAG, json.toString() );
        } catch ( JSONException e ) {
            Log.e( TAG, "JSON object cannot be created completely" );
            return;
        }

        new SendPostAsyncTask.Builder( uri.toString(), json )
                .onCancelled( error -> Log.e( TAG, "Error/Cancelled during SendPostASyncTask: " + error ) )
                .onPostExecute( this::handleOnPostExecute )
                .build()
                .execute();

    }

    /**
     * Method that handles the post request and
     * parses the result from the endpoint.
     *
     * @param result JSON string of the post response
     */
    private void handleOnPostExecute( String result ) {
        FragmentManager        fm   = mMaster.getSupportFragmentManager();
        MemberSettingsFragment frag = ( MemberSettingsFragment ) fm.findFragmentByTag( "MemberSettingsFragment" );
        try {

            JSONObject jsonObject = new JSONObject( result );
            boolean    success    = jsonObject.getBoolean( "success" );


            if ( success ) {
                Log.d( TAG, "Member Settings Update successful" );
                frag.successfulUpdateDialog();


            } else {
                Log.d( TAG, "Member Settings Update failed" );
                frag.unSuccessfulUpdateDialog();
            }

        } catch ( JSONException e ) {
            Log.e( TAG, "JSON on post execute failed" );
            frag.unSuccessfulUpdateDialog();
        }
    }

}
