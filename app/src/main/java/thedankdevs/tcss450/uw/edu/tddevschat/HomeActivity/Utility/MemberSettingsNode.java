package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;

import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.MemberSettingsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

import java.util.Map;

public class MemberSettingsNode {

    private final String       TAG = getClass().getSimpleName();
    private       HomeActivity mMaster;
    private       Credentials  mCredentials;

    public MemberSettingsNode( HomeActivity master, Credentials credentials ) {
        mMaster = master;
        mCredentials = credentials;
    }

    public void onChangeMemberInfo( Map<String, String> updateMap ) {

        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .authority( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( R.string.ep_settings ) )
                .appendPath( mMaster.getString( R.string.ep_update_info ) )
                .build();
        Log.d( TAG, "Uri: " + uri.toString() );
        boolean    created = true;
        JSONObject json    = new JSONObject();
        try {
            json.put( "memberid", mCredentials.getMemberID() );
            json.put( "values", new JSONObject( updateMap ) );
            Log.d( TAG, json.toString() );
        } catch ( JSONException e ) {
            Log.e( TAG, "JSON object cannot be created completely" );
            created = false;
        }

        if ( created ) {
            new SendPostAsyncTask.Builder( uri.toString(), json )
                    .onCancelled( error -> Log.e( TAG, "Error/Cancelled during SendPostASyncTask: " + error ) )
                    .onPostExecute( this::handleOnPostExecute )
                    .build()
                    .execute();

        }

    }


    private void handleOnPostExecute( String result ) {
        try {

            JSONObject             jsonObject  = new JSONObject( result );
            boolean                success     = jsonObject.getBoolean( "success" );
            FragmentManager        fm          = mMaster.getSupportFragmentManager();
            MemberSettingsFragment frag        = ( MemberSettingsFragment ) fm.findFragmentByTag( "MemberSettingsFragment" );
            FragmentTransaction    transaction = fm.beginTransaction();

            if ( success ) {
                Log.d( TAG, "Member Settings Update successful" );
                if ( frag != null ) {
                    frag.successfulUpdateDialog();
                }

            } else {
                Log.d( TAG, "Member Settings Update failed" );
                if ( frag != null ) {
                    frag.unSuccessfulUpdateDialog();
                }
            }
            // a way to refresh the fragment
            transaction.detach( frag )
                    .attach( frag )
                    .commit();

        } catch ( JSONException e ) {
            Log.e( TAG, "JSON on post execute failed" );
        }
    }


}
