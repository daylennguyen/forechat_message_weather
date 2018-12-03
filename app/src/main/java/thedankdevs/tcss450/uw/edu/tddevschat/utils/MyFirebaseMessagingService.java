package thedankdevs.tcss450.uw.edu.tddevschat.utils;

/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Intent;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final  String RECEIVED_NEW_MESSAGE = "new message from fcm";
    private static final String TAG                  = "FCM: MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived( RemoteMessage remoteMessage ) {

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        Log.d( TAG, "From: " + remoteMessage.getFrom() );
        super.onMessageReceived( remoteMessage );

        // Check if message contains a data payload.
        if ( remoteMessage.getData().size() > 0 ) {
            Log.d( TAG, "Message data payload: " + remoteMessage.getData() );

            JSONObject obj = new JSONObject( remoteMessage.getData() );

            //create an Intent to broadcast a message.
            Intent i = new Intent( RECEIVED_NEW_MESSAGE );
            i.putExtra( "DATA", obj.toString() );
            sendBroadcast( i );
        }

        // Check if message contains a notification payload.
        if ( remoteMessage.getNotification() != null ) {
            Log.d( TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody() );
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    // [START on_new_token]

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken( String token ) {
        Log.d( TAG, "Refreshed token: " + token );

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer( token );
    }
    // [END on_new_token]


    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer( String token ) {
        // TODO: Implement this method to send token to your app server.
        Log.i( "FCM NEW TOKEN: ", token.substring( 100 ) );
    }


}