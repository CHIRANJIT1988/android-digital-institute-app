package app.institute;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import app.institute.model.Message;
import app.institute.session.SessionManager;
import app.institute.sqlite.SQLiteDatabaseHelper;

import static app.institute.CommonUtilities.displayMessage;

public class MyAndroidFirebaseMsgService extends FirebaseMessagingService {

    private static final String TAG = "MyAndroidFCMService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {

        //Log data to Log Cat
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());

        try
        {

            JSONObject jsonObject = new JSONObject(remoteMessage.getData().get("post_title"));

            String message_id = jsonObject.getString("message_id");
            String message_title = jsonObject.getString("message_title");
            String message_body = jsonObject.getString("message_body");

            new SQLiteDatabaseHelper(this).insertMessage(new Message(message_id, message_title, message_body, String.valueOf(System.currentTimeMillis()), 0));

        }

        catch (JSONException e)
        {

        }

        if(new SessionManager(this).isLoggedIn())
        {
            //create notification
            createNotification(remoteMessage.getNotification().getBody());
            Log.d(TAG, "Notification Message: " + remoteMessage.getData());
            displayMessage(this, remoteMessage.getData().get("post_title"));
        }
    }


    private void createNotification( String messageBody)
    {

        Intent intent = new Intent( this , InboxActivity. class );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent resultIntent = PendingIntent.getActivity( this , 0, intent,
        PendingIntent.FLAG_ONE_SHOT);

        Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder( this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Spectrum Eduventures !!")
                        .setContentText(messageBody)
                        .setAutoCancel( true )
                        .setSound(notificationSoundURI)
                        .setContentIntent(resultIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, mNotificationBuilder.build());
    }
}