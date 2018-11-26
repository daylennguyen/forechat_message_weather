package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Utility;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.ChatFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.ChatsFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Chats.content.Chat;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.ConnectionListFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.Connections.content.Connection;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeActivity;
import thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity.HomeFragment;
import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;


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
    private String defaultChatName;

    //Chatroom information to be displayed.
    private ArrayList<Chat> allExistingChats = new ArrayList<>();
    private JSONArray allIndividualChats;
    private JSONArray allGroupChats;
    private StringBuilder members = new StringBuilder();
    private int groupchatID;
    private String groupchatName;
    /**
     * Current user information
     **/
    private Credentials mCredential;
    /**/
    private HomeActivity mMaster;

    public ChatNode(HomeActivity Master, Credentials mCredential) {
        this.mCredential = mCredential;
        this.mMaster = Master;
        CheckForNotification();
    }

    public void CheckForNotification() {
        //If notification received, then load all chats.
        if (mMaster.getIntent().getBooleanExtra(mMaster.getString(R.string.keys_intent_notification_msg), false)) {
            mChatID = mMaster.getIntent().getExtras().getInt(mMaster.getString(R.string.keys_intent_notification_chatID));
            loadAllMessages();
            //mMaster.onBackPressed();

        } else { // load default fragment.
            mMaster.getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame_home_container, new HomeFragment())
                    .commit();
        }
    }

    /**
     * Retrieves the previous messages that chatID has.
     *
     * @author Emmett Kang
     * @version 16 November 2018
     */
    private void loadAllMessages() {
        JSONObject chatterInfo = new JSONObject();
        try {
            chatterInfo.put("email", mCredential.getEmail());
            chatterInfo.put("chatID", mChatID);
        } catch (JSONException e) {
            Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
        }

        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(mMaster.getString(R.string.base_url))
                .appendPath(mMaster.getString((R.string.ep_messaging)))
                .appendPath(mMaster.getString(R.string.ep_getAllMessages))
                .build();

        Log.w("URL for getting all chat:", uri.toString());
        new SendPostAsyncTask.Builder(uri.toString(), chatterInfo)
                .onPostExecute(this::handleAllMessagesPost)
                .onCancelled(error -> Log.e("ERROR EMMETT", error))
                .build().execute();

    }

    /**
     * Does something when something was clicked in {@link ConnectionFragment}
     *
     * @param chatID
     */
    public void onOpenChatInteraction(int chatID, String email, String username) {

        if (chatID == -1) {
            theOtherReceiverUsernames.add(username);
            createNewChat();
        } else {
            mChatID = chatID;
            loadAllMessages();

        }
        Log.w("WTF", String.valueOf(chatID));
        /*Snippet 3 placed on end*/
    }

    /**
     * Receive and process the result from the endpoint and send to chat fragment.
     *
     * @param result JSON file from endpoint.
     * @Author Emmett Kang
     * @Version 16 November 2018
     */
    private void handleAllMessagesPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);

            JSONArray temp = resultsJSON.getJSONArray("messages");
            JSONObject grabTitle = resultsJSON.getJSONObject("chatName");
            String chatTitle = grabTitle.getString("name");

            Bundle bundle = new Bundle();

            bundle.putString(mMaster.getString(R.string.key_json_array), temp.toString());
            bundle.putString(mMaster.getString(R.string.key_chat_Title), chatTitle);
            loadChatFragment(bundle);

        } catch (JSONException e) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * This method creates new chat sessions.
     *
     * @Author Emmett Kang
     * @Version 15 November 2018
     */
    private void createNewChat() {
        JSONObject chatName = new JSONObject();
        String urlname = "";
        try {
            //Create default chat name for current user and user to be chatting.
            if (theOtherReceiverUsernames.size() == 1) {
                defaultChatName = mCredential.getUsername() + " & " + theOtherReceiverUsernames.get(0);
                urlname = mMaster.getString(R.string.ep_messaging_new_individual);
            } else {
                defaultChatName = "Group Chat";
                urlname = mMaster.getString(R.string.ep_messaging_new_group);
            }
            chatName.put("name", defaultChatName);
        } catch (JSONException e) {
            Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
        }
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(mMaster.getString(R.string.base_url))
                .appendPath(mMaster.getString((R.string.ep_messaging)))
                .appendPath(urlname)
                .build();

        Log.w("URL for create new chat:", uri.toString());
        new SendPostAsyncTask.Builder(uri.toString(), chatName)
                .onPostExecute(this::handleNewChatPost)
                .onCancelled(error -> Log.e("ERROR EMMETT", error))
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
    private void handleNewChatPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            Log.wtf("CHATID?", String.valueOf(resultsJSON));
            JSONArray temp = resultsJSON.getJSONArray("newChatID");
            JSONObject tempContent = temp.getJSONObject(0);
            mChatID = tempContent.getInt("chatid");
            Log.w("CHATID", String.valueOf(mChatID));
           theOtherReceiverUsernames.add(mCredential.getUsername());
           addChatters(mChatID, theOtherReceiverUsernames);

            Bundle bundle = new Bundle();
            bundle.putString(mMaster.getString(R.string.key_chat_Title), defaultChatName);
            loadChatFragment(bundle);

        } catch (JSONException e) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    /**
     * Add the users to a certain chatroom through chatID.
     *
     * @param chatID chatroom identifier.
     * @param usernames  user's email to be added.
     * @Author Emmett Kang
     * @Version 15 November 2018
     */
    private void addChatters(int chatID, ArrayList<String> usernames) {
        JSONObject chatterInfoObject = new JSONObject();
        JSONArray chatterInfo = new JSONArray(usernames);
        // Log.w("Adding", usernames);
        Log.w("Adding", String.valueOf(chatID));
        Log.w("jsonarray", chatterInfo.toString());
         try {
             chatterInfoObject.put("chatID", chatID);
             chatterInfoObject.put("chatters", chatterInfo);
        } catch (JSONException e) {
            Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
        }
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(mMaster.getString(R.string.base_url))
                .appendPath(mMaster.getString((R.string.ep_messaging)))
                .appendPath(mMaster.getString(R.string.ep_messaging_add))
                .build();
        Log.w("URL for create new chat:", uri.toString());

        new SendPostAsyncTask.Builder(uri.toString(), chatterInfoObject)
                .onPostExecute(this::handleAddChattersPost)
                .onCancelled(error -> Log.e("ERROR EMMETT", error))
                .build().execute();
    }

    /**
     * Receive and show if succeeded through logcat.
     *
     * @param result JSON file
     * @Author Emmett Kang
     * @Version 15 November 2018
     */
    private void handleAddChattersPost(String result) {
        try {
            Log.w("JSON result adding peeps", result);
            JSONObject resultsJSON = new JSONObject(result);
            boolean didIt = resultsJSON.getBoolean("success");
            if (didIt) {
                Log.w("Adding", "YAY WE DID IT");
            } else {
                Log.w("Adding", "DARN IT, IT DIDN'T WORK");
            }

        } catch (JSONException e) {

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
    private void loadChatFragment(Bundle bundle) {
        Log.w("WHAT IS THIS", String.valueOf(mChatID));
        ChatFragment chatFragment = new ChatFragment();
        bundle.putSerializable(mMaster.getString(R.string.key_connection_chatID), mChatID);
        //   bundle.putSerializable(getString(R.string.key_connection_email), email);
        bundle.putSerializable(mMaster.getString(R.string.key_credential), mCredential);
        chatFragment.setArguments(bundle);
        mMaster.loadFragment(chatFragment);
    }


    public void onChatsListFragmentInteraction(Chat item) {
        mChatID = item.getChatID();
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
            chatterInfo.put("email", mCredential.getEmail());
        } catch (JSONException e) {
            Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
        }
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(mMaster.getString(R.string.base_url))
                .appendPath(mMaster.getString((R.string.ep_messaging)))
                .appendPath(mMaster.getString(R.string.ep_getAllChats))
                .build();
        Log.w("URL for getting all chat:", uri.toString());
        new SendPostAsyncTask.Builder(uri.toString(), chatterInfo)
                .onPostExecute(this::handleAllChatsPost)
                .onCancelled(error -> Log.e("ERROR EMMETT", error))
                .build().execute();
    }

    /**
     * Receive and process the result from the endpoint and send to chat fragment.
     *
     * @param result JSON file from endpoint.
     * @Author Emmett Kang
     * @Version 16 November 2018
     */
    private void handleAllChatsPost(String result) {
        try {

            mMaster.onWaitFragmentInteractionHide();
            JSONObject resultsJSON = new JSONObject(result);
             allIndividualChats = resultsJSON.getJSONArray("individualChats");
            allGroupChats = resultsJSON.getJSONArray("groupChats");

            //Iterate through the JSONarray and create chat objects to display.
            for (int i = 0; i < allGroupChats.length(); i++) {
                JSONObject chatRoom = allGroupChats.getJSONObject(i);
                groupchatName = chatRoom.getString("name");
                groupchatID = chatRoom.getInt("chatid");
                getGroupchatMembers(groupchatID);
            }

        } catch (JSONException e) {
            //It appears that the web service didnt return a JSON formatted String
            // or it didn’t have what we expected in it.
            Log.e("JSON_PARSE_ERROR", result
                    + System.lineSeparator()
                    + e.getMessage());
        }
    }

    public void getGroupchatMembers (int groupChatID) {
        JSONObject groupChatInfo = new JSONObject();
        try {
            groupChatInfo.put("chatID", groupChatID);
            groupChatInfo.put("memberID", mCredential.getMemberID());
        } catch (JSONException e) {
            Log.wtf("JSON", "Error creating JSON: " + e.getMessage());
        }
        Uri uri = new Uri.Builder()
                .scheme("https")
                .appendPath(mMaster.getString(R.string.base_url))
                .appendPath(mMaster.getString((R.string.ep_messaging)))
                .appendPath(mMaster.getString(R.string.ep_getOthers))
                .build();
        Log.w("URL for getting all chat:", uri.toString());
        new SendPostAsyncTask.Builder(uri.toString(), groupChatInfo)
                .onPostExecute(this::handleGroupChatPost)
                .onCancelled(error -> Log.e("ERROR EMMETT", error))
                .build().execute();
    }

    private void handleGroupChatPost(String result) {
        try {
            JSONObject resultsJSON = new JSONObject(result);
            JSONArray membersArray = resultsJSON.getJSONArray("others");
            members = new StringBuilder();

            for (int i = 0; i < membersArray.length(); i++) {
                JSONObject firstMember = membersArray.getJSONObject(i);
                String firstMemUsername = firstMember.getString("username");
                members.append(firstMemUsername);
                if (i < membersArray.length()-1) {
                    members.append(", ");
                }
            }

            allExistingChats.add(new Chat.Builder(groupchatName, members.toString(), groupchatID).build());
            Bundle args = new Bundle();
            //Iterate through the JSONarray and create chat objects to display.
            for (int i = 0; i < allIndividualChats.length(); i++) {
                JSONObject chatRoom = allIndividualChats.getJSONObject(i);
                String chatName = chatRoom.getString("name");
                String receiver = chatRoom.getString("username");
                int chatid = chatRoom.getInt("chatid");
                allExistingChats.add(new Chat.Builder(chatName, receiver, chatid).build());
            }


            args.putSerializable(ChatsFragment.ARG_CHATS_LIST, allExistingChats);
            //Create chats list fragment and display.
            Fragment fragment = new ChatsFragment();
            fragment.setArguments(args);
            mMaster.onWaitFragmentInteractionHide();
            mMaster.loadFragment(fragment);

            allExistingChats = new ArrayList<>();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void CreateNewChatInteraction(ArrayList<CheckBox> cbList, ArrayList<Connection> connectionList) {

        for (CheckBox checkBox : cbList) {
            if (checkBox.isChecked()) {
                theOtherReceiverUsernames.add(checkBox.getText().toString());
                Log.wtf("CHECKED", checkBox.getText().toString());
            }
        }

        if (theOtherReceiverUsernames.size() == 1) {
            Log.wtf("SIZE", "Shouldn't come here");
            for (Connection c : connectionList) {
                if(c.getUsername() == theOtherReceiverUsernames.get(0)) {
                    int tempChatID = c.getChatID();
                    if (tempChatID == -1) {
                        createNewChat();
                    } else {
                        mChatID = tempChatID;
                        loadAllMessages();

                    }
                    break;
                }
            }
        } else {
            Log.wtf("SIZE", "Should come here");
            createNewChat();
        }


    }

    public void onChatsListFragmentLongInteraction(Chat item) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(getmMaster());
        builder.setMessage(item.getChatName() + " Options")
                .setPositiveButton("Remove chat member", (dialog, id) -> {
                    // FIRE ZE MISSILES!
                })
                .setNeutralButton("Cancel", (dialog, id) -> {
                    // FIRE ZE MISSILES!
                })
                .setNegativeButton("End Chat", (dialog, id) -> {
                    // User cancelled the dialog
                });
// 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public HomeActivity getmMaster() {
        return mMaster;
    }

    public void setmMaster(HomeActivity mMaster) {
        this.mMaster = mMaster;
    }
}
