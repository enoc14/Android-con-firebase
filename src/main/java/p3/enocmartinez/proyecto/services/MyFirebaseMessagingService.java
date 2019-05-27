package p3.enocmartinez.proyecto.services;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

import p3.enocmartinez.proyecto.ConexionSQLiteHelper;
import p3.enocmartinez.proyecto.MainActivity;
import p3.enocmartinez.proyecto.utilidades.Utilidades;
import p3.enocmartinez.proyecto.utils.NotificationUtils;
import p3.enocmartinez.proyecto.vo.NotificationVO;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgingService";
    private static final String TITLE = "title";
    private static final String EMPTY = "";
    private static final String MESSAGE = "message";
    private static final String IMAGE = "image";
    private static final String ACTION = "action";
    private static final String DATA = "data";
    private static final String ACTION_DESTINATION = "action_destination";
    DatabaseReference ref;
    ConexionSQLiteHelper admin = new ConexionSQLiteHelper(this);

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        ref = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("FIREBASETOKEN","Refreshed Token: " + s);
        sendRegistrationToServer(s);
    }

    private void sendRegistrationToServer(String s) {
        if(getFolio() == 0)
            insertNewData(s);
        else
            insertData(s);
    }

    private void insertData(String tkn) {
        SQLiteDatabase db = admin.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Utilidades.TOKEN, tkn);
        db.update("capitan",values,Utilidades.HACK + "='uno'",null);
        db.close();
    }

    private void insertNewData(String tkn) {
        SQLiteDatabase db = admin.getWritableDatabase();
        String insert = "insert into capitan("+Utilidades.TOKEN+","+Utilidades.HACK+") values ('"+tkn+"','uno')";
        db.execSQL(insert);
        db.close();
    }

    private long getFolio() {
        SQLiteDatabase db = admin.getReadableDatabase();
        long cont = DatabaseUtils.queryNumEntries(db, "capitan");
        db.close();
        return cont;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> data = remoteMessage.getData();
            handleData(data);

        } else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification());
        }// Check if message contains a notification payload.

    }

    private void handleNotification(RemoteMessage.Notification RemoteMsgNotification) {
        String message = RemoteMsgNotification.getBody();
        String title = RemoteMsgNotification.getTitle();
        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notificationVO, resultIntent);
        notificationUtils.playNotificationSound();
    }

    private void handleData(Map<String, String> data) {
        String title = data.get(TITLE);
        String message = data.get(MESSAGE);
        String iconUrl = data.get(IMAGE);
        String action = data.get(ACTION);
        String actionDestination = data.get(ACTION_DESTINATION);
        NotificationVO notificationVO = new NotificationVO();
        notificationVO.setTitle(title);
        notificationVO.setMessage(message);
        notificationVO.setIconUrl(iconUrl);
        notificationVO.setAction(action);
        notificationVO.setActionDestination(actionDestination);

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.displayNotification(notificationVO, resultIntent);
        notificationUtils.playNotificationSound();
    }
}
