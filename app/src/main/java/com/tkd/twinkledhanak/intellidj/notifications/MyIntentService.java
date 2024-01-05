package com.tkd.twinkledhanak.intellidj.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.tkd.twinkledhanak.intellidj.R;
import com.tkd.twinkledhanak.intellidj.activities.IntelliDj;
import com.tkd.twinkledhanak.intellidj.db.LocalDatabaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

// THERE ARE 5 DIFFERENT TYPES OF NOTICES, BE, TE, SE , FE , (ALL)


public class MyIntentService extends Service
{
    private LocalDatabaseHelper mDBHelper;
    String s = "none";
    ArrayList<String> ListNotice , ListDoc;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public MyIntentService()
    {

    }

    public MyIntentService(Context applicationContext) {
        super();
        ListNotice = new ArrayList<String>();
        ListDoc = new ArrayList<String>();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        ListNotice = new ArrayList<String>();
        ListDoc = new ArrayList<String>();

        // listener and generate notification
        mDBHelper = new LocalDatabaseHelper(this.getApplicationContext());


        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Announcement");
       ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


               GetUserDetails getUserDetails = new GetUserDetails();
                 s = "none"; // dont keep it null, might give PattermMatch error , if Pattern class gets null
                try {
                    s = getUserDetails.execute().get();   // returns the year of user

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    s = "none";
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    s = "none";
                }

                try {

                    ListNotice = new GetNoticeDetails().execute().get();
                    if (dataSnapshot.exists())

                    {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                        {

                            if (dataSnapshot1.child("type").getValue().toString().matches("none")
                                    || dataSnapshot1.child("type").getValue().toString().matches("None")
                             || dataSnapshot1.child("type").getValue().toString().matches("")  )
                            {
                                // do nothing for these users, they have not yet udated their rofile
                            }

                        if ((dataSnapshot1.child("type").getValue().toString().matches("all"))) // all users
                            {
                                if(ListNotice.size() == 0)
                                {

                                    generateNoticeNotif();
                                    new InsertNoticeDetails().execute(dataSnapshot1.getKey());


                                }
                                else if(!ListNotice.contains(dataSnapshot1.getKey()))
                                {
                                    generateNoticeNotif();
                                    new InsertNoticeDetails().execute(dataSnapshot1.getKey());


                                }


                            }

                            if (dataSnapshot1.child("type").getValue().toString().matches("fe")
                                    && s.matches("fe"))
                            {
                                     if(ListNotice.size() == 0)
                                {

                                    generateNoticeNotif();
                                    new InsertNoticeDetails().execute(dataSnapshot1.getKey());

                                }
                                else if(!ListNotice.contains(dataSnapshot1.getKey()))
                                {
                                    generateNoticeNotif();
                                    new InsertNoticeDetails().execute(dataSnapshot1.getKey());


                                }

                            }

                            if (dataSnapshot1.child("type").getValue().toString().matches("se")
                                    && s.matches("se"))
                            {

                                if(ListNotice.size() == 0)
                                {

                                    generateNoticeNotif();
                                    new InsertNoticeDetails().execute(dataSnapshot1.getKey());


                                }
                                else if(!ListNotice.contains(dataSnapshot1.getKey()))
                                {
                                    generateNoticeNotif();
                                    new InsertNoticeDetails().execute(dataSnapshot1.getKey());


                                }

                            }
                            if (dataSnapshot1.child("type").getValue().toString().matches("te")
                                    && s.matches("te"))
                            {

                                if(ListNotice.size() == 0)
                                {

                                    generateNoticeNotif();
                                    new InsertNoticeDetails().execute(dataSnapshot1.getKey());
                                    Log.d("nonoticesoinsert","nonoticesoinsert");

                                }
                                else if(!ListNotice.contains(dataSnapshot1.getKey()))
                                {
                                    generateNoticeNotif();
                                    new InsertNoticeDetails().execute(dataSnapshot1.getKey());


                                }

                            }
                            if (dataSnapshot1.child("type").getValue().toString().matches("be")
                                    && s.matches("be"))
                            {


                                if(ListNotice.size() == 0)
                                {

                                    generateNoticeNotif();
                                    new InsertNoticeDetails().execute(dataSnapshot1.getKey());

                                }
                                else if(!ListNotice.contains(dataSnapshot1.getKey()))
                                {
                                    generateNoticeNotif();
                                    new InsertNoticeDetails().execute(dataSnapshot1.getKey());

                                }

                            }



                        }
                    }

                    else
                    {
                       // Toast.makeText(MyIntentService.this, "No Notice", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (Exception e)
                {
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Document");
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               GetUserDetails getUserDetails = new GetUserDetails();
                s = "none"; // dont keep it null, might give PattermMatch error , if Pattern class gets null
                try {
                    s = getUserDetails.execute().get();   // returns the year of user

                } catch (InterruptedException e) {
                    e.printStackTrace();
                    s = "none";
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    s = "none";
                }

                try {

                    ListDoc = new GetDocDetails().execute().get();
                    if (dataSnapshot.exists())

                    {

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                        {


                            if (dataSnapshot1.child("type").getValue().toString().matches("none")
                                    || dataSnapshot1.child("type").getValue().toString().matches("None")
                                    || dataSnapshot1.child("type").getValue().toString().matches("")     )
                            {
                                // do nothing for these users, they have not yet udated their rofile
                            }



                            if ((dataSnapshot1.child("type").getValue().toString().matches("all"))) // all users
                            {
                                if(ListDoc.size() == 0)
                                {

                                    generateDocNotif();
                                    new InsertDocDetails().execute(dataSnapshot1.getKey());

                                }
                                else if(!ListDoc.contains(dataSnapshot1.getKey()))
                                {
                                    generateDocNotif();
                                    new InsertDocDetails().execute(dataSnapshot1.getKey());

                                }


                            }

                            if (dataSnapshot1.child("type").getValue().toString().matches("fe")
                                    && s.matches("fe"))
                            {
                                if(ListDoc.size() == 0)
                                {

                                    generateDocNotif();
                                    new InsertDocDetails().execute(dataSnapshot1.getKey());


                                }
                                else if(!ListDoc.contains(dataSnapshot1.getKey()))
                                {
                                    generateDocNotif();
                                    new InsertDocDetails().execute(dataSnapshot1.getKey());


                                }

                            }

                            if (dataSnapshot1.child("type").getValue().toString().matches("se")
                                    && s.matches("se"))
                            {

                                if(ListDoc.size() == 0)
                                {

                                    generateDocNotif();
                                    new InsertDocDetails().execute(dataSnapshot1.getKey());
                                }
                                else if(!ListDoc.contains(dataSnapshot1.getKey()))
                                {
                                    generateDocNotif();
                                    new InsertDocDetails().execute(dataSnapshot1.getKey());

                                }

                            }
                            if (dataSnapshot1.child("type").getValue().toString().matches("te")
                                    && s.matches("te"))
                            {


                                if(ListDoc.size() == 0)
                                {

                                    generateDocNotif();
                                    new InsertDocDetails().execute(dataSnapshot1.getKey());
                                }
                                else if(!ListDoc.contains(dataSnapshot1.getKey()))
                                {
                                    generateDocNotif();
                                    new InsertDocDetails().execute(dataSnapshot1.getKey());


                                }

                            }
                            if (dataSnapshot1.child("type").getValue().toString().matches("be")
                                    && s.matches("be"))
                            {

                                if(ListDoc.size() == 0)
                                {

                                    generateDocNotif();
                                    new InsertDocDetails().execute(dataSnapshot1.getKey());


                                }
                                else if(!ListDoc.contains(dataSnapshot1.getKey()))
                                {
                                    generateDocNotif();
                                    new InsertDocDetails().execute(dataSnapshot1.getKey());


                                }

                            }



                        }
                    }

                    else
                    {
                       // Toast.makeText(MyIntentService.this, "No Document", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (Exception e)
                {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


      return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent restartService = new Intent("MyReceiver");
        sendBroadcast(restartService);
    }


    public void generateNoticeNotif()
    {
        // trigger notification
        // code for notif
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_stat_notice)
                        .setContentTitle("New Announcement!")
                        .setColor(getResources().getColor(R.color.newred))
                        .setContentText("");

        Intent resultIntent = new Intent(getApplicationContext(), IntelliDj.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());



    }


    public class GetUserDetails extends AsyncTask<Void, Void, String> {
        // selects details only of current user
        // so retrives details of only the one using app

        @Override
        protected String doInBackground(Void... params) {
            SQLiteDatabase sqLiteDatabase = mDBHelper.getWritableDatabase();
            final Cursor Cr = sqLiteDatabase.rawQuery("SELECT * FROM user",
                    new String[]{});
            String sx = new String();

            if(Cr.moveToNext())
            {
                // data exists
                do {
                    // retreive the data

                    sx = (Cr.getString(Cr.getColumnIndexOrThrow("year")));


                }
                while (Cr.moveToNext());
            }
            Cr.close();
            return sx;
        }
    }


    public class InsertNoticeDetails extends  AsyncTask<String , Void , Void> {
        SQLiteDatabase sqLiteDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        @Override
        protected Void doInBackground(String... params) {


            // we have to avoid duplicate insertion of bookmarks
            // first check the existign values
            // then insert if needed
            String Notice = params[0];
            final Cursor cr = sqLiteDatabase.rawQuery("SELECT * FROM notice",
                    new String[]{});

            if (cr.moveToNext()) {
                // data exists
                // check with existing data
                do {
                    ListNotice.add(cr.getString(cr.getColumnIndexOrThrow("notice_id")));

                }
                while (cr.moveToNext());
                int i;
                boolean flag = false;
                for (i = 0; i < ListNotice.size(); i++) {
                    if (Notice.matches(ListNotice.get(i))) {
                        // question already exists
                        // NO INSERTION NEEDED
                        flag = true;
                        break;

                    }
                }

                if (flag == false) {
                    // does not exist
                    // insert
                    values.put("notice_id", Notice);
                    sqLiteDatabase.insert("notice", null, values);

                }


            } else  // no data exists at all
            {
                values.put("notice_id", Notice);
                sqLiteDatabase.insert("notice", null, values);


            }
            values.clear();
            cr.close();
            ListNotice.clear();
            return null;
        }

        }



    public void generateDocNotif()
    {
        // trigger notification
        // code for document
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_stat_notice)
                        .setContentTitle("New Document!")
                        .setColor(getResources().getColor(R.color.newyellow))
                        .setContentText("");

        Intent resultIntent = new Intent(getApplicationContext(), IntelliDj.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        // Sets an ID for the notification
        int mNotificationId = 002;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

    }


    public class GetNoticeDetails extends AsyncTask<Void, Void , ArrayList<String>>
    {

        SQLiteDatabase sqLiteDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();


        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            final Cursor cr = sqLiteDatabase.rawQuery("SELECT * FROM notice",
                    new String[]{});
//            ListNotice.clear();

            if (cr.moveToNext()) {
                // data exists
                // check with existing data
                do {
                    ListNotice.add(cr.getString(cr.getColumnIndexOrThrow("notice_id")));

                }
                while (cr.moveToNext());

            }
            else
            {
                // no data
            }

                cr.close();
                return ListNotice;
        }
    }


    public class InsertDocDetails extends AsyncTask<String , Void , Void>
    {
        SQLiteDatabase sqLiteDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        @Override
        protected Void doInBackground(String... params) {


            // we have to avoid duplicate insertion of bookmarks
            // first check the existign values
            // then insert if needed
            String Doc = params[0];

            final Cursor cr = sqLiteDatabase.rawQuery("SELECT * FROM document",
                    new String[]{});

            if (cr.moveToNext()) {
                // data exists
                // check with existing data
                do {
                    ListDoc.add(cr.getString(cr.getColumnIndexOrThrow("document_id")));

                }
                while (cr.moveToNext());
                int i;
                boolean flag = false;
                for (i = 0; i < ListDoc.size(); i++) {
                    if (Doc.matches(ListDoc.get(i))) {
                        // question already exists
                        // NO INSERTION NEEDED
                        flag = true;
                        break;

                    }
                }

                if (flag == false) {
                    // does not exist
                    // insert
                    values.put("document_id", Doc);
                    sqLiteDatabase.insert("document", null, values);

                }


            } else  // no data exists at all
            {
                values.put("document_id", Doc);
                sqLiteDatabase.insert("document", null, values);


            }
            values.clear();
            cr.close();
            ListDoc.clear();
            return null;
        }

    }



    public class GetDocDetails extends  AsyncTask<Void , Void , ArrayList<String>>
    {
        SQLiteDatabase sqLiteDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        @Override
        protected ArrayList<String> doInBackground(Void... params) {

            final Cursor cr = sqLiteDatabase.rawQuery("SELECT * FROM document",
                    new String[]{});
//            ListDoc.clear();

            if (cr.moveToNext()) {
                // data exists
                // check with existing data
                do {
                    ListDoc.add(cr.getString(cr.getColumnIndexOrThrow("document_id")));

                }
                while (cr.moveToNext());

            }
            else
            {
                // no data
            }

            cr.close();
            return ListDoc;

        }
    }






}




