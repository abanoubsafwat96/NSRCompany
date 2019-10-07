package com.safwat.abanoub.nsrcompany;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";
    private String messageMessage, messageSender_id;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        //
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            String messageTitle = remoteMessage.getNotification().getTitle();
            String messageBody = remoteMessage.getNotification().getBody();
//            Uri messageImageURL = remoteMessage.getNotification().getImageUrl();
//            String message_click_action = remoteMessage.getNotification().getClickAction();

            //The message which i send will have keys named [message, sender_id] and corresponding values.
            //You can change as per the requirement.

            // Check if message contains a data payload.
//            if (remoteMessage.getData().size() > 0) {
//                Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//
//                messageMessage = remoteMessage.getData().get("message");
//                messageSender_id = remoteMessage.getData().get("sender_id");
//            }

            sendNotification(messageTitle, messageBody);
        }
    }


    private void sendNotification(String messageTitle, String messageBody) {

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent mIntent = new Intent("com.safwat.abanoub.nsrcompany.PointsActivty");
//        mIntent.putExtra("message", messageMessage);
//        mIntent.putExtra("sender_id", messageSender_id);

        PendingIntent pendingIntent = PendingIntent.getActivity(this
                , 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this
                , getString(R.string.default_notification_channel_id))
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setPriority(8)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setDefaults(Notification.DEFAULT_VIBRATE);

        // If the build version is greater than JELLY_BEAN, set the notification's priority to PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        // Get a NotificationManager
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // passing the Notification object to the system
        notificationManager.notify(1138, notificationBuilder.build());
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Get updated InstanceID token.

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        Toast.makeText(this, "need to send the new token to server", Toast.LENGTH_SHORT).show();
    }
}
