package org.techtown.sns_project.pushAlarm;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.techtown.sns_project.InitialActivity;
import org.techtown.sns_project.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class BackgroundAlarmService extends Service {
    NotificationManager Notifi_M;
    ServiceThread thread;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    ArrayList<String> Original;
    ArrayList<String> Updated;
    static int id_counter = 0;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notifi_M = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
        myServiceHandler handler = new myServiceHandler();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        Original = new ArrayList<>();
        Updated = new ArrayList<>();
        thread = new ServiceThread(  handler );
        Data_crawl(Original);
        Data_crawl(Updated);
        thread.start();
//        thread.stopForever();
        return START_STICKY;
    }

    //서비스가 종료될 때 할 작업
    public void onDestroy() {
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread( handler );
        thread.start();
    }

    public void start() {
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread( handler );
        thread.start();
    }

    public void stop() {
        myServiceHandler handler = new myServiceHandler();
        thread = new ServiceThread( handler );
        thread.stopForever();
    }

    public class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            NotificationManager notificationManager = (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );
            Intent intent = new Intent( BackgroundAlarmService.this, InitialActivity.class );
            intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP );
            PendingIntent pendingIntent = PendingIntent.getActivity( BackgroundAlarmService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT );
            Uri soundUri = RingtoneManager.getDefaultUri( RingtoneManager.TYPE_NOTIFICATION );

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                @SuppressLint("WrongConstant")
                NotificationChannel notificationChannel = new NotificationChannel( "my_notification", "n_channel", NotificationManager.IMPORTANCE_MAX );
                notificationChannel.setDescription( "description" );
                notificationChannel.setName( "Channel Name" );
                assert notificationManager != null;
                notificationManager.createNotificationChannel( notificationChannel );
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder( BackgroundAlarmService.this )
                    .setSmallIcon( R.drawable.appicon )
                    .setLargeIcon( BitmapFactory.decodeResource( getResources(), R.drawable.appicon ) )
                    .setContentTitle( "Title" )
                    .setContentText( "좋아요가 눌렸습니다." )
                    .setAutoCancel( true )
                    .setSound( soundUri )
                    .setContentIntent( pendingIntent )
                    .setDefaults( Notification.DEFAULT_ALL )
                    .setOnlyAlertOnce( true )
                    .setChannelId( "my_notification" )
                    .setColor( Color.parseColor( "#ffffff" ) );
            assert notificationManager != null;


            db.collection("users")
                    .document(firebaseUser.getUid()).collection("board_likes").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Updated.clear();
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        String temp = (String)documentSnapshot.getData().get("user");
                        Updated.add(temp);
                    }
                } });
            if(Updated.size() != Original.size()) {
                if(Updated.size() > Original.size()) {
                    notificationManager.notify(id_counter++, notificationBuilder.build());
                    send_Message(id_counter);
                }
                Original.clear();
                Data_crawl(Original);  // Original 리스트를 다시 변화된 값으로 최신화.
            }
        }
    }

    private void Data_crawl(ArrayList<String> param_list) {
        db.collection("users")
                .document(firebaseUser.getUid()).collection("board_likes").get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            String temp = (String)documentSnapshot.getData().get("user");
                            param_list.add(temp);
                        }
                    }
                });
    }

    private void send_Message(int id_counter) {
        Intent intent = new Intent("Alarm_Count");
        intent.putExtra("alarm_count", id_counter);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}