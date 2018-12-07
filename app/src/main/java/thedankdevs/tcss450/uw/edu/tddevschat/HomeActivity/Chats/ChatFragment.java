package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats;


import android.annotation.SuppressLint;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.MyFirebaseMessagingService;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

import java.util.Objects;

/**
 * Fragment that deals with Receiving and Sending a chat between users.
 *
 * @Author Emmett Kang
 * @Version 19 November 2018
 */
public class ChatFragment extends Fragment {

    private static final String                  TAG = "CHAT_FRAG";
    @SuppressLint("StaticFieldLeak")
    static               View                    rootLayout;
    private              FirebaseMessageReciever mFirebaseMessageReciever;
    /**
     * input edit text to send message.
     */
    private              EditText                mMessageInputEditText;

    /**
     * User information.
     **/
    private String      mEmail;
    private String      mUsername;
    private String      mSendUrl;
    private int         mChatID;
    private Credentials mCredentials;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView( @NonNull LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {

        assert getArguments() != null;
        //Get the Chat information such as the user and the ChatID.
        mChatID = getArguments().getInt( getString( R.string.key_connection_chatID ) );
        mCredentials = ( Credentials ) getArguments().getSerializable( getString( R.string.key_credential ) );
        assert mCredentials != null;
        mEmail = mCredentials.getEmail();
        mUsername = mCredentials.getUsername();

        //Setting the UI for the fragment.
        String title = getArguments().getString( getString( R.string.key_chat_Title ) );
        Objects.requireNonNull( getActivity() ).setTitle( title );
        //Grab the root layout.
        rootLayout = inflater.inflate( R.layout.fragment_chat, container, false );
        mMessageInputEditText = rootLayout.findViewById( R.id.et_chat_message );

        //Grab the view of scrollview.
        ScrollView scrollview = rootLayout.findViewById( R.id.Scroller );
        scrollview.post( () -> scrollview.fullScroll( ScrollView.FOCUS_DOWN ) );

        //grab all of old chats and reverse them in order.
        JSONArray pastChat;
        String    pastChatString = getArguments().getString( getString( R.string.key_json_array ) );
        if ( pastChatString != null ) {
            try {
                pastChat = new JSONArray( pastChatString );

                //Dynamically create the bubble UI while grabbing the old messages information.
                for ( int i = pastChat.length() - 1; i >= 0; i-- ) {
                    LinearLayout holder = new LinearLayout( getContext() );
                    holder.setOrientation( LinearLayout.HORIZONTAL );
                    JSONObject message = pastChat.getJSONObject( i );
                    String     sender  = message.getString( "username" );
                    String     msg     = message.getString( "message" );
                    createBubbleUI( sender, msg ); //Create the bubble UI
                }
            } catch ( JSONException e ) {
                Log.w( "cant make it", "DARN IT" );
            }
        }
        rootLayout.findViewById( R.id.btn_chat_send ).setOnClickListener( this::handleSendClick );
        return rootLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                Objects.requireNonNull( getActivity() ).getSharedPreferences(
                        getString( R.string.keys_shared_prefs ),
                        Context.MODE_PRIVATE );
        if ( prefs.contains( getString( R.string.keys_prefs_email ) ) ) {
            mEmail = prefs.getString( getString( R.string.keys_prefs_email ), "" );
        } else {
            throw new IllegalStateException( "No EMAIL in prefs!" );
        }
        //We will use this url every time the user hits send. Let's only build it once, ya?
        mSendUrl = new Uri.Builder()
                .scheme( "https" )
                .appendPath( getString( R.string.base_url ) )
                .appendPath( getString( R.string.ep_messaging ) )
                .appendPath( getString( R.string.ep_messaging_send ) )
                .build()
                .toString();

        Log.w( "HERE? ", mSendUrl );

    }

    @Override
    public void onResume() {
        super.onResume();
        if ( mFirebaseMessageReciever == null ) {
            mFirebaseMessageReciever = new FirebaseMessageReciever();
        }
        IntentFilter iFilter = new IntentFilter( MyFirebaseMessagingService.RECEIVED_NEW_MESSAGE );
        Objects.requireNonNull( getActivity() ).registerReceiver( mFirebaseMessageReciever, iFilter );

    }

    @Override
    public void onPause() {
        super.onPause();
        if ( mFirebaseMessageReciever != null ) {
            Objects.requireNonNull( getActivity() ).unregisterReceiver( mFirebaseMessageReciever );
        }
    }

    /**
     * When send button is clicked, invoke endpoint to send messages.
     *
     * @param theButton sendbutton.
     */
    private void handleSendClick( final View theButton ) {
        String msg = mMessageInputEditText.getText().toString();
        Log.w( "YASS ", String.valueOf( mChatID ) );
        Log.w( "YASS ", mEmail );
        Log.w( "YASS ", msg );

        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put( "username", mUsername );
            messageJson.put( "message", msg );
            messageJson.put( "chatID", mChatID );
            messageJson.put( "memberID", mCredentials.getMemberID() );
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder( mSendUrl, messageJson )
                .onPostExecute( this::endOfSendMsgTask )
                .onCancelled( error -> Log.e( TAG, error ) )
                .build().execute();
    }

    /**
     * after sending message, clear out the input edit text.
     *
     * @param result if message was sent or not.
     */
    private void endOfSendMsgTask( final String result ) {
        try {
            //This is the result from the web service
            JSONObject res = new JSONObject( result );
            if ( res.has( "success" ) && res.getBoolean( "success" ) ) {
                //The web service got our message. Time to clear out the input EditText
                mMessageInputEditText.setText( "" );

            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }

    /**
     * This method creates the bubble UI by intaking the sender and the message that the sender sent
     * @param sender Person who sent the message
     * @param msg    content of the message
     */
    public void createBubbleUI( String sender, String msg ) {
        //Grab the linear layout (container for chats) that is vertical.
        LinearLayout ll = rootLayout.findViewById( R.id.LinLay );

        //Create a horizontal layout for each edit text.
        LinearLayout holder = new LinearLayout( getContext() );
        holder.setOrientation( LinearLayout.HORIZONTAL );
        //If the sender is the user, then create a purple bubble.
        if ( sender.equals( mCredentials.getUsername() ) ) {
            TextView messageBubble = new TextView( getContext() );
            messageBubble.setText( msg );
            messageBubble.setTextSize( 16 );
            messageBubble.setTextColor( ContextCompat.getColor( Objects.requireNonNull( getContext() ), R.color.colorLightestGrey ) );
            messageBubble.setBackgroundResource( R.drawable.my_message );
            messageBubble.setPadding( 32, 10, 32, 16 );
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            layoutParams.setMargins( 400, 16, 16, 8 );

            holder.addView( messageBubble, layoutParams );

            holder.setGravity( Gravity.END );
            ll.addView( holder ); //Add to the chat container.
        } else { //If sender is other people, create a blue bubble.
            holder.setOrientation( LinearLayout.VERTICAL );
            TextView messageBubble = new TextView( getContext() );
            TextView send          = new TextView( getContext() );
            send.setText( sender );
            messageBubble.setText( msg );
            messageBubble.setBackgroundResource( R.drawable.their_message );
            messageBubble.setPadding( 32, 10, 32, 16 );
            messageBubble.setTextColor( ContextCompat.getColor( Objects.requireNonNull( getContext() ), R.color.colorLightestGrey ) );

            messageBubble.setTextSize( 16 );

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );
            layoutParams.setMargins( 16, 16, 400, 8 );

            holder.addView( send, layoutParams );
            holder.addView( messageBubble, layoutParams );
            ll.addView( holder );

        }

    }

    /**
     * Get the current chatID for this chat.
     *
     * @return the current chatID
     */
    public int getmChatID() {
        return mChatID;
    }

    /**
     * A BroadcastReceiver setup to listen for messages sent from
     * MyFirebaseMessagingService
     * that Android allows to run all the time.
     *
     * @author Charles Bryan, Emmett Kang
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
                    if ( jObj.has( "message" ) && jObj.has( "sender" ) ) {
                        String sender = jObj.getString( "sender" );
                        String msg    = jObj.getString( "message" );
                        String chatID = jObj.getString( "chatID" );
                        int    cid    = Integer.parseInt( chatID );
                        if ( cid == mChatID ) { //If the chat ID is correct,
                            Log.i( "FCM Chat Frag", sender + " " + msg );
                            createBubbleUI( sender, msg ); //Create the bubble UI accordingly.
                        }
                    }
                    //Make the focus on the scroll view to go down, so user can easily see it.
                    ScrollView scrollview = rootLayout.findViewById( R.id.Scroller );
                    scrollview.post( () -> scrollview.fullScroll( ScrollView.FOCUS_DOWN ) );
                } catch ( JSONException e ) {
                    Log.e( "JSON PARSE", e.toString() );
                }
            }
        }
    }


}
