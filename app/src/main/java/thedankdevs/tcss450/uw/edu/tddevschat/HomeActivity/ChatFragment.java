package thedankdevs.tcss450.uw.edu.tddevschat.HomeActivity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.function.Consumer;

import thedankdevs.tcss450.uw.edu.tddevschat.R;
import thedankdevs.tcss450.uw.edu.tddevschat.model.Credentials;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.MyFirebaseMessagingService;
import thedankdevs.tcss450.uw.edu.tddevschat.utils.SendPostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private FirebaseMessageReciever mFirebaseMessageReciever;

    private static final String TAG = "CHAT_FRAG";
  //  private static final String CHAT_ID = "1";
    private TextView mMessageOutputTextView;
    private EditText mMessageInputEditText;
    private String mEmail;
    private String mSendUrl;
    private int mChatID;
    private Credentials mCredentials;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //mEmail = getArguments().getString(getString(R.string.key_connection_email));

        mChatID = getArguments().getInt(getString(R.string.key_connection_chatID));
        mCredentials = (Credentials) getArguments().getSerializable(getString(R.string.key_credential));
        mEmail = mCredentials.getEmail();
        getActivity().setTitle("Chat number " + mChatID);

        View rootLayout = inflater.inflate(R.layout.fragment_chat, container, false);

        mMessageOutputTextView = rootLayout.findViewById(R.id.tv_chat_display);
        mMessageInputEditText = rootLayout.findViewById(R.id.et_chat_message);

        JSONArray pastChat;
        String pastChatString = getArguments().getString(getString(R.string.key_json_array));
        if (pastChatString != null) {
            try {
                 pastChat = new JSONArray(pastChatString);

                for (int i = pastChat.length()-1; i >= 0; i--) {
                    JSONObject message = pastChat.getJSONObject(i);
                    String sender = message.getString("email");
                    String msg = message.getString("message");
                    mMessageOutputTextView.append(sender + ": " + msg);
                    mMessageOutputTextView.append(System.lineSeparator());
                    mMessageOutputTextView.append(System.lineSeparator());
                }
            } catch (JSONException e) {
                Log.w("cant make it", "DARN IT");
            }
        }



        rootLayout.findViewById(R.id.btn_chat_send).setOnClickListener(this::handleSendClick);
        return rootLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs =
                getActivity().getSharedPreferences(
                        getString(R.string.keys_shared_prefs),
                        Context.MODE_PRIVATE);
        if (prefs.contains(getString(R.string.keys_prefs_email))) {
            mEmail = prefs.getString(getString(R.string.keys_prefs_email), "");
        } else {
            throw new IllegalStateException("No EMAIL in prefs!");
        }
        //We will use this url every time the user hits send. Let's only build it once, ya?
        mSendUrl = new Uri.Builder()
                .scheme("https")
                .appendPath(getString(R.string.base_url))
                .appendPath(getString(R.string.ep_messaging))
                .appendPath(getString(R.string.ep_messaging_send))
                .build()
                .toString();

        Log.w("HERE? ", mSendUrl);

    }

    private void handleSendClick(final View theButton) {
        String msg = mMessageInputEditText.getText().toString();
        Log.w("YASS ", String.valueOf(mChatID));
        Log.w("YASS ", mEmail);
        Log.w("YASS ", msg);

        JSONObject messageJson = new JSONObject();

        try {
            messageJson.put("email", mEmail);
            messageJson.put("message", msg);
            messageJson.put("chatID", mChatID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new SendPostAsyncTask.Builder(mSendUrl, messageJson)
                .onPostExecute(this::endOfSendMsgTask)
                .onCancelled(error -> Log.e(TAG, error))
                .build().execute();
    }

    private void endOfSendMsgTask(final String result) {
        try {
            Log.w("IS IT HERE", result);
            //This is the result from the web service
            JSONObject res = new JSONObject(result);

            if (res.has("success") && res.getBoolean("success")) {
                //The web service got our message. Time to clear out the input EditText
                mMessageInputEditText.setText("");

                //its up to you to decide if you want to send the message to the output here
//or wait for the message to come back from the web service.
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (mFirebaseMessageReciever == null) {
            mFirebaseMessageReciever = new FirebaseMessageReciever();
        }
        IntentFilter iFilter = new IntentFilter(MyFirebaseMessagingService.RECEIVED_NEW_MESSAGE);
        getActivity().registerReceiver(mFirebaseMessageReciever, iFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mFirebaseMessageReciever != null){
            getActivity().unregisterReceiver(mFirebaseMessageReciever);
        }
    }

    /**
     * A BroadcastReceiver setup to listen for messages sent from
     MyFirebaseMessagingService
     * that Android allows to run all the time.
     */
    private class FirebaseMessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i("FCM Chat Frag", "start onRecieve");
            if(intent.hasExtra("DATA")) {

                String data = intent.getStringExtra("DATA");
                Log.w("FCM DATA", data);
                JSONObject jObj = null;
                try {
                    jObj = new JSONObject(data);
                    if(jObj.has("message") && jObj.has("sender")) {
                        String sender = jObj.getString("sender");
                        String msg = jObj.getString("message");
                        String chatID = jObj.getString("chatID");
                        int cid = Integer.parseInt(chatID);
                        if (cid == mChatID) {
                            Log.i("FCM Chat Frag", sender + " " + msg);
                            mMessageOutputTextView.append(sender + ": " + msg);
                            mMessageOutputTextView.append(System.lineSeparator());
                            mMessageOutputTextView.append(System.lineSeparator());
                        }
                    }
                } catch (JSONException e) {
                    Log.e("JSON PARSE", e.toString());
                }
            }
        }
    }


}
