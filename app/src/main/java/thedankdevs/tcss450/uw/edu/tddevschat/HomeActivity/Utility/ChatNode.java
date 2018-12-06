package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;

import android.app.AlertDialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.CheckBox;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.ChatFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.ChatsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.content.Chat;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

import java.util.ArrayList;
import java.util.Objects;


/**
 * The Chat Node; contains the primary functionality pertaining to retrieving Chat information
 * from the application database/server.
 */
public class ChatNode {
    /**
     * ChatId to be updated to load which chatID.
     **/
    private int mChatID;

    /**
     * Fields to be set when new chat is created
     **/

    private ArrayList<String> theOtherReceiverUsernames = new ArrayList<>();
    private String            defaultChatName;

    /**
     * Information to display all on going chats.
     */
    private ArrayList<Chat> allExistingChats = new ArrayList<>();
    private JSONArray       allIndividualChats;
    private JSONArray       allGroupChats;

    /**
     * Current user information
     **/
    private Credentials mCredential;

    private HomeActivity mMaster;

    /**
     * When this node is instantiated, pass around the information.
     *
     * @param Master      HomeActivity Node.
     * @param mCredential User information.
     */
    public ChatNode( HomeActivity Master, Credentials mCredential ) {
        this.mCredential = mCredential;
        this.mMaster = Master;
        CheckForNotification(); //Check if there is notification.
    }

    /**
     * Check if there was a notification, and if it there is, load all messages and chat room.
     */
    private void CheckForNotification() {
        //If notification received, then load all chats.
        if ( mMaster.getIntent().getBooleanExtra( mMaster.getString( R.string.keys_intent_notification_msg ), false ) ) {
            mChatID = Objects.requireNonNull( mMaster.getIntent().getExtras() ).getInt( mMaster.getString( R.string.keys_intent_notification_chatID ) );
            loadAllMessages();


        } else { // load default fragment.
            mMaster.getSupportFragmentManager().beginTransaction()
                    .add( R.id.frame_home_container, new HomeFragment() )
                    .commit();
        }
    }

    /**
     * Retrieves the previous messages that chat session contains.
     *
     * @author Emmett Kang
     * @version 16 November 2018
     */
    private void loadAllMessages() {

        JSONObject chatterInfo = new JSONObject();
        try {
            chatterInfo.put( "email", mCredential.getEmail() );
            chatterInfo.put( "chatID", mChatID );
        } catch ( JSONException e ) {
            Log.wtf( "JSON", "Error creating JSON: " + e.getMessage() );
        }

        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( ( R.string.ep_messaging ) ) )
                .appendPath( mMaster.getString( R.string.ep_getAllMessages ) )
                .build();

        Log.w( "URL for getting all chat:", uri.toString() );
        new SendPostAsyncTask.Builder( uri.toString(), chatterInfo )
                .onPostExecute( this::handleAllMessagesPost )
                .onCancelled( error -> Log.e( "ERROR EMMETT", error ) )
                .build().execute();

    }

    /**
     * If there aren't any ongoing chat happening, create new chat, else
     * load al messages and open existing chat.
     *
     * @param chatID Chat session identifier.
     * @author Emmett Kang
     * @version 1 December 2018
     */
    public void onOpenChatInteraction( int chatID, String username ) {
        if ( chatID == -1 ) {
            theOtherReceiverUsernames.add( username );
            createNewChat( "NA" ); //Default chat name.
        } else {
            mChatID = chatID;
            loadAllMessages();

        }
    }

    /**
     * Receive and process the result from the endpoint and send to chat fragment.
     *
     * @param result JSON file from endpoint.
     * @Author Emmett Kang
     * @Version 16 November 2018
     */
    private void handleAllMessagesPost( String result ) {
        try {
            JSONObject resultsJSON = new JSONObject( result );

            JSONArray  temp      = resultsJSON.getJSONArray( "messages" );
            JSONObject grabTitle = resultsJSON.getJSONObject( "chatName" );
            String     chatTitle = grabTitle.getString( "name" );

            Bundle bundle = new Bundle();

            bundle.putString( mMaster.getString( R.string.key_json_array ), temp.toString() );
            bundle.putString( mMaster.getString( R.string.key_chat_Title ), chatTitle );
            loadChatFragment( bundle );

        } catch ( JSONException e ) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e( "JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage() );
        }
    }

    /**
     * This method creates new chat sessions.
     *
     * @Author Emmett Kang
     * @Version 15 November 2018
     */
    private void createNewChat( String chatRoomName ) {
        JSONObject chatName = new JSONObject();
        String     urlname  = "";
        try {
            if ( chatRoomName.equals( "NA" ) || chatRoomName.equals( "" ) ) {
                if ( theOtherReceiverUsernames.size() == 1 ) {
                    defaultChatName = mCredential.getUsername() + " & " + theOtherReceiverUsernames.get( 0 );
                    urlname = mMaster.getString( R.string.ep_messaging_new_individual );
                } else {
                    defaultChatName = "Group Chat";
                    urlname = mMaster.getString( R.string.ep_messaging_new_group );
                }

            } else {
                defaultChatName = chatRoomName;
                if ( theOtherReceiverUsernames.size() == 1 ) {
                    urlname = mMaster.getString( R.string.ep_messaging_new_individual );
                } else {
                    urlname = mMaster.getString( R.string.ep_messaging_new_group );
                }

            }

            //Create default chat name for current user and user to be chatting.
            chatName.put( "name", defaultChatName );
        } catch ( JSONException e ) {
            Log.wtf( "JSON", "Error creating JSON: " + e.getMessage() );
        }
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( ( R.string.ep_messaging ) ) )
                .appendPath( urlname )
                .build();

        Log.w( "URL for create new chat:", uri.toString() );
        new SendPostAsyncTask.Builder( uri.toString(), chatName )
                .onPostExecute( this::handleNewChatPost )
                .onCancelled( error -> Log.e( "ERROR EMMETT", error ) )
                .build().execute();
    }


    /**
     * Receive and process the result from the endpoint and send to chat fragment while
     * adding users to the new chat room.
     *
     * @param result JSON file from endpoint.
     * @Author Emmett Kang
     * @Version 15 November 2018
     */
    private void handleNewChatPost( String result ) {
        try {
            JSONObject resultsJSON = new JSONObject( result );
            JSONArray  temp        = resultsJSON.getJSONArray( "newChatID" );
            JSONObject tempContent = temp.getJSONObject( 0 );
            mChatID = tempContent.getInt( "chatid" );
            theOtherReceiverUsernames.add( mCredential.getUsername() );
            addChatters( mChatID, theOtherReceiverUsernames );

            Bundle bundle = new Bundle();
            bundle.putString( mMaster.getString( R.string.key_chat_Title ), defaultChatName );
            theOtherReceiverUsernames = new ArrayList<>();
            loadChatFragment( bundle );

        } catch ( JSONException e ) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e( "JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage() );
        }
    }

    /**
     * Add the users to a certain chatroom through chatID.
     *
     * @param chatID    chatroom identifier.
     * @param usernames user's email to be added.
     * @Author Emmett Kang
     * @Version 15 November 2018
     */
    private void addChatters( int chatID, ArrayList<String> usernames ) {
        JSONObject chatterInfoObject = new JSONObject();

        JSONArray chatterInfo = new JSONArray( usernames );
        try {
            chatterInfoObject.put( "chatID", chatID );
            chatterInfoObject.put( "chatters", chatterInfo );
        } catch ( JSONException e ) {
            Log.wtf( "JSON", "Error creating JSON: " + e.getMessage() );
        }
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( ( R.string.ep_messaging ) ) )
                .appendPath( mMaster.getString( R.string.ep_messaging_add ) )
                .build();
        Log.w( "URL for adding:", uri.toString() );

        new SendPostAsyncTask.Builder( uri.toString(), chatterInfoObject )
                .onPostExecute( this::handleAddChattersPost )
                .onCancelled( error -> Log.e( "ERROR EMMETT", error ) )
                .build().execute();
    }

    /**
     * Receive and show if succeeded through logcat.
     *
     * @param result JSON file
     * @Author Emmett Kang
     * @Version 15 November 2018
     */
    private void handleAddChattersPost( String result ) {

        JSONObject resultsJSON;
        try {
            resultsJSON = new JSONObject( result );
            boolean didIt = resultsJSON.getBoolean( "success" );
            if ( didIt ) {

            } else {
                Log.w( "Adding", "DARN IT, IT DIDN'T WORK" );
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }

    }


    /**
     * Receive bundle from other methods, send necessary information, and
     * load the chat fragment.
     *
     * @param bundle Information chat fragment to open.
     * @Author Emmett Kang
     * @Version 16 November 2018
     */
    private void loadChatFragment( Bundle bundle ) {
        ChatFragment chatFragment = new ChatFragment();
        bundle.putSerializable( mMaster.getString( R.string.key_connection_chatID ), mChatID );
        bundle.putSerializable( mMaster.getString( R.string.key_credential ), mCredential );
        chatFragment.setArguments( bundle );
        mMaster.loadFragment( chatFragment );
    }

    /**
     * If chat is selected, check if chatID is notified, then unnotify the
     * chat, else just open normal.
     *
     * @param item chat object itself.
     */
    public void onChatsListFragmentInteraction( Chat item ) {
        mChatID = item.getChatID();
        ArrayList<Integer> notifiedChats = mMaster.getNotifiedChats();
        try {
            for ( int cid : notifiedChats ) {
                if ( cid == mChatID ) {
                    mMaster.updateNotifiedChats( mChatID );
                    mMaster.notifyUI( Color.BLACK, Color.WHITE );
                }
            }
        } catch ( Exception e ) {
            Log.e( "ERROR IN CHAT LIST LISTENER", String.valueOf( e ) );
        }
        loadAllMessages();
    }

    /**
     * Load all chats that user associates with.
     *
     * @Author Emmett Kang
     * @Version 16 November 2018
     */
    public void loadAllChats() {

        JSONObject chatterInfo = new JSONObject();
        try {
            chatterInfo.put( "email", mCredential.getEmail() );
        } catch ( JSONException e ) {
            Log.wtf( "JSON", "Error creating JSON: " + e.getMessage() );
        }
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( ( R.string.ep_messaging ) ) )
                .appendPath( mMaster.getString( R.string.ep_getAllChats ) )
                .build();
        Log.w( "URL for getting all chat:", uri.toString() );
        new SendPostAsyncTask.Builder( uri.toString(), chatterInfo )
                .onPostExecute( this::handleAllChatsPost )
                .onCancelled( error -> Log.e( "ERROR EMMETT", error ) )
                .build().execute();
    }

    /**
     * Receive and process the result from the endpoint and send to chat fragment.
     *
     * @param result JSON file from endpoint.
     * @Author Emmett Kang
     * @Version 16 November 2018
     */
    private void handleAllChatsPost( String result ) {
        try {

            mMaster.onWaitFragmentInteractionHide();
            JSONObject resultsJSON = new JSONObject( result );
            allIndividualChats = resultsJSON.getJSONArray( "individualChats" );
            allGroupChats = resultsJSON.getJSONArray( "groupChats" );

            getGroupchatMembers();


        } catch ( JSONException e ) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e( "JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage() );
        }
    }

    /**
     * Get all of the members who are part of group chats
     *
     * @author Emmett Kang
     * @version 23 November 2018
     */
    private void getGroupchatMembers() {
        JSONObject empty = new JSONObject();
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( ( R.string.ep_messaging ) ) )
                .appendPath( mMaster.getString( R.string.ep_getOthers ) )
                .build();
        Log.w( "URL for getting all chat:", uri.toString() );
        new SendPostAsyncTask.Builder( uri.toString(), empty )
                .onPostExecute( this::handleGroupChatMemPost )
                .onCancelled( error -> Log.e( "ERROR EMMETT", error ) )
                .build().execute();
    }

    /**
     * Receiving the result from the post request. Add to existing chats list so that
     * we can use that to show the list of chats.
     *
     * @param result JSON array of group members.
     * @author Emmett Kang
     * @version 23 November 2018
     */
    private void handleGroupChatMemPost( String result ) {
        try {
            JSONObject resultsJSON  = new JSONObject( result );
            JSONArray  membersArray = resultsJSON.getJSONArray( "groupmem" );

            //Iterate through the group chats list and create chat objects to display.
            for ( int i = 0; i < allGroupChats.length(); i++ ) {
                StringBuilder members       = new StringBuilder();
                JSONObject    chatRoom      = allGroupChats.getJSONObject( i );
                String        groupchatName = chatRoom.getString( "name" );
                int           groupchatID   = chatRoom.getInt( "chatid" );
                for ( int j = 0; j < membersArray.length(); j++ ) { // go through members array
                    JSONObject member     = membersArray.getJSONObject( j );
                    int        tempChatID = Integer.parseInt( member.getString( "chatid" ) );

                    if ( tempChatID == groupchatID ) { // if member is in group chat, append names as receiver.
                        String tempUsername = member.getString( "username" );
                        if ( !tempUsername.equals( mCredential.getUsername() ) ) {
                            members.append( " " ).append( tempUsername ).append( " " );
                        }
                    }
                }
                //Create new chat object and add it to the existing list.
                allExistingChats.add( new Chat.Builder( groupchatName, members.toString(), groupchatID ).build() );
            }
            //Go through all of individual chats and create chats, and add to the list.
            for ( int i = 0; i < allIndividualChats.length(); i++ ) {
                JSONObject chatRoom = allIndividualChats.getJSONObject( i );
                String     chatName = chatRoom.getString( "name" );
                String     receiver = chatRoom.getString( "username" );
                int        chatid   = chatRoom.getInt( "chatid" );
                allExistingChats.add( new Chat.Builder( chatName, receiver, chatid ).build() );
            }
            //Grab all notified chats
            ArrayList<Integer> notichat = mMaster.getNotifiedChats();
            if ( notichat.size() > 0 ) {
                for ( int i = 0; i < notichat.size(); i++ ) {
                    for ( int j = 0; j < allExistingChats.size(); j++ ) {
                        if ( notichat.get( i ) == allExistingChats.get( j ).getChatID() ) {
                            allExistingChats.get( j ).notifiedChat(); //Notify the title of the chat.
                        }
                    }
                }
            }

            Bundle args = new Bundle();
            args.putSerializable( ChatsFragment.ARG_CHATS_LIST, allExistingChats );
            //Create chats list fragment and display.
            Fragment fragment = new ChatsFragment();
            fragment.setArguments( args ); //Pass information to next fragment.
            mMaster.onWaitFragmentInteractionHide();
            mMaster.loadFragmentWithoutBackStack( fragment );
            allExistingChats = new ArrayList<>(); //reset the list of existing chats.
        } catch ( JSONException e ) {
            e.printStackTrace();
        }

    }

    /**
     * When button for create new chat is pressed, this method is called. It will contain
     * the connections that are checked and create new chats or open new chats if
     * user already has an ongoing chat.
     *
     * @param cbList         checkboxes that uses connection's username,
     * @param connectionList list of connections the user have.
     * @param chatTitle      chatroom title.
     * @author Emmett Kang
     * @version 1 December 2018
     */
    public void CreateNewChatInteraction( ArrayList<CheckBox> cbList, ArrayList<Connection> connectionList,
                                          String chatTitle ) {
        //grab all of the users that are checked.
        for ( CheckBox checkBox : cbList ) {
            if ( checkBox.isChecked() ) {
                theOtherReceiverUsernames.add( checkBox.getText().toString() );
            }
        }

        //If this is one on one, check for ongoing chats
        if ( theOtherReceiverUsernames.size() == 1 ) {
            for ( Connection c : connectionList ) {
                if ( c.getUsername().equals( theOtherReceiverUsernames.get( 0 ) ) ) {
                    int tempChatID = c.getChatID();
                    if ( tempChatID == -1 ) { //If there aren't any chats, create new one.
                        createNewChat( chatTitle );
                    } else { //else, load the existing chat.
                        mChatID = tempChatID;
                        loadAllMessages();
                    }
                    break;
                }
            }
        } else { //If it is a group chat, create a new group chat.
            createNewChat( chatTitle );
        }
    }

    /**
     * When the user long-presses one of the chats, this method will create the a up
     * to provide users some options regarding to the chatrooms.
     *
     * @param item the chat object.
     * @author Emmett Kang
     * @version 27 November 2018
     */
    public void onChatsListFragmentLongInteraction( Chat item ) {
        // building a new alert dialog box
        AlertDialog.Builder builder = new AlertDialog.Builder( getmMaster() );
        //Get Chat members
        String   chatMems          = item.getChatMembers();
        String[] tokenizedChatMems = chatMems.split( " " ); //split them
        if ( tokenizedChatMems.length > 1 ) { //If there are more than 1 receiver,
            builder.setMessage( item.getChatName() + " Options" ) //provide remove chatmember option.
                    .setPositiveButton( "Remove chat member", ( dialog, id ) -> displayRemoveChatMember( item.getChatID() ) )
                    .setNeutralButton( "Cancel", ( dialog, id ) -> {

                    } )
                    .setNegativeButton( "End Chat", ( dialog, id ) -> endChatSession( item.getChatID() ) );
        } else { //Else, give the user only the ability to end the chat.
            builder.setMessage( item.getChatName() + " Options" )
                    .setPositiveButton( "End Chat", ( dialog, id ) -> endChatSession( item.getChatID() ) )
                    .setNegativeButton( "Cancel", ( dialog, id ) -> {
                    } );
        }

        //Create the alertbox.
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * When user presses end chat, this method gets called to set the status of the
     * chat to ended.
     *
     * @param theChatID chat session identifier being ended.
     * @author Emmett Kang
     * @version 22 November 2018
     */
    private void endChatSession( int theChatID ) {
        JSONObject chatInfo = new JSONObject();
        try {
            chatInfo.put( "chatID", theChatID );
        } catch ( JSONException e ) {
            e.printStackTrace();
        }

        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( ( R.string.ep_messaging ) ) )
                .appendPath( mMaster.getString( R.string.ep_messaging_end_chat ) )
                .build();
        Log.w( "URL for ending chat:", uri.toString() );
        new SendPostAsyncTask.Builder( uri.toString(), chatInfo )
                .onPostExecute( this::handleEndChatPost )
                .onCancelled( error -> Log.e( "ERROR EMMETT", error ) )
                .build().execute();
    }

    /**
     * After ending te chats, reload the list of chats that user is in.
     *
     * @param result
     * @author Emmett Kang
     * @version 22 November 2018
     */
    private void handleEndChatPost( String result ) {
        loadAllChats();
    }


    /**
     * This method is called when user wishes to remove the chat member.
     * Send the group chatID so we can retrive who's in the group chat.
     *
     * @param groupChatID Chatroom identifier
     * @author Emmett Kang
     * @version 21 November 2018
     */
    private void displayRemoveChatMember( int groupChatID ) {
        JSONObject groupChatInfo = new JSONObject();
        try {
            groupChatInfo.put( "chatID", groupChatID );
            groupChatInfo.put( "memberID", mCredential.getMemberID() );
        } catch ( JSONException e ) {
            Log.wtf( "JSON", "Error creating JSON: " + e.getMessage() );
        }

        mChatID = groupChatID;
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( ( R.string.ep_messaging ) ) )
                .appendPath( mMaster.getString( R.string.ep_getOthersinGC ) )
                .build();
        Log.w( "URL for getting all chat:", uri.toString() );
        new SendPostAsyncTask.Builder( uri.toString(), groupChatInfo )
                .onPostExecute( this::handleGroupChatPost )
                .onCancelled( error -> Log.e( "ERROR EMMETT", error ) )
                .build().execute();
    }

    /**
     * Get all of the members who are in the chat, and create a new fragment
     * so that user can choose who they would like to remove.
     *
     * @param result others in the group chat.
     * @author Emmett Kang
     * @version 21 November 2018
     */
    private void handleGroupChatPost( String result ) {
        try {

            JSONObject resultsJSON = new JSONObject( result );

            JSONArray membersArray = resultsJSON.getJSONArray( "others" );


            Bundle args = new Bundle();

            //create a new checkbox list so that we can provide user the UI.
            ArrayList<CheckBox> usersInChatCheckBox = new ArrayList<>();
            for ( int i = 0; i < membersArray.length(); i++ ) {
                CheckBox checkBox = new CheckBox( mMaster );
                checkBox.setTextSize( 20 );
                JSONObject temp   = membersArray.getJSONObject( i );
                String     member = temp.getString( "username" );
                checkBox.setText( member );
                checkBox.setFontFeatureSettings( String.valueOf( R.font.roboto ) );
                usersInChatCheckBox.add( checkBox ); //Add the check box to the list.
            }
            //Send information to remove chatmembers fragment.
            args.putSerializable( mMaster.getString( R.string.key_array_list ), usersInChatCheckBox );
            args.putSerializable( "chatID", mChatID );
            RemoveChatMembers fragment = new RemoveChatMembers();
            fragment.setArguments( args );
            mMaster.loadFragment( fragment ); //Load the removeChatMembers fragment.

        } catch ( JSONException e ) {
            e.printStackTrace();
        }
    }

    private HomeActivity getmMaster() {
        return mMaster;
    }

    public void setmMaster( HomeActivity mMaster ) {
        this.mMaster = mMaster;
    }


    /**
     * Receive the members to be removed from the chat and the chatID and call
     * a post request to remove members from the chat through server side.
     *
     * @param members   Members to be removed.
     * @param theChatID Chats that is going to remove members
     * @author Emmett Kang
     * @version 21 November 2018
     */
    public void RemoveMembersFromChat( ArrayList<String> members, int theChatID ) {
        JSONObject membersRemovingObject = new JSONObject();
        JSONArray  membersToBeRemoved    = new JSONArray( members );
        try {
            Log.wtf( "names", String.valueOf( theChatID ) );
            membersRemovingObject.put( "chatters", membersToBeRemoved );
            membersRemovingObject.put( "chatID", theChatID );

        } catch ( JSONException e ) {
            Log.wtf( "JSON", "Error creating JSON: " + e.getMessage() );
        }
        Uri uri = new Uri.Builder()
                .scheme( "https" )
                .appendPath( mMaster.getString( R.string.base_url ) )
                .appendPath( mMaster.getString( ( R.string.ep_messaging ) ) )
                .appendPath( mMaster.getString( R.string.ep_messaging_remove_members ) )
                .build();
        Log.w( "URL for removing:", uri.toString() );

        new SendPostAsyncTask.Builder( uri.toString(), membersRemovingObject )
                .onPostExecute( this::handleRemoveChattersPost )
                .onCancelled( error -> Log.e( "ERROR EMMETT", error ) )
                .build().execute();
    }

    /**
     * After removing the members, reload the chats list.
     *
     * @param result result from post request.
     * @author Emmett Kang
     * @version 21 Novebmer 2018
     */
    private void handleRemoveChattersPost( String result ) {
        loadAllChats();
    }


}
