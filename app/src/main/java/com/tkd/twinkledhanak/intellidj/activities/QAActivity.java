package com.tkd.twinkledhanak.intellidj.activities;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.tkd.twinkledhanak.intellidj.Common;
import com.tkd.twinkledhanak.intellidj.R;
import com.tkd.twinkledhanak.intellidj.db.LocalDatabaseHelper;
import com.tkd.twinkledhanak.intellidj.objects.Answer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.concurrent.ExecutionException;


///// THIS CLASS WILL CREATE A NODE QAMAP INSIDE FIREBASE DATABASE FOR MAPPING

public class QAActivity extends AppCompatActivity implements Common {

    FloatingActionButton fab2;
    EditText editText;
    LayoutInflater inflater;
    View layout;
    PopupWindow popupWindow;
    ImageView picture;
    private RecyclerView rv; // ADDING RECYCLER VIEW ELEMENTS
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Answer answer_db, answer_c;
    String mUsername, refQ , Qcontent , userdetails , s, acontent;
    String arry[];
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mAnswerDatabaseReference;

    private FirebaseStorage mFirebaseStorage;
    private StorageReference PhotosStorageReference;
    private LocalDatabaseHelper mDBHelper;
    Uri downloadUrl;
    MenuItem menuItem;
    private boolean isConnected = false;
    public static ArrayList<String>  listAUserID , listAnswers, listAnswersIDS ,listNames ,listBookmark , listACloudID , listUType
            , listUpvotes , dbqid , dbaid , vamap;
    Vector<String> vaname, vacontent, vacloudid, vauserid, vaquestionid, vatime , v1, v2, v3, v4, v5, v6 , v7 , vaupvote , vautype , v8;
    Vector<String>  vuemail ,UserUpvote , SetUpvote  , DBUpvote;
    String date , uid = "";
    Intent starterIntent;
    long  nextAId = 0;
     ArrayList<String> DataShared , vuname , vucloudid , vutype;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BY USING THIS, APP WONT CRASH , SIMPLY GO TO ANOTHER ACTIVITY
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_qa);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Intent in = getIntent();
        starterIntent = getIntent();
        vuname = new ArrayList<String>();
        vucloudid = new ArrayList<String>();
        vutype = new ArrayList<String>();
        UserUpvote = new Vector<String>();
        SetUpvote = new Vector<String>();
        DBUpvote = new Vector<String>();

        // DETAILS OF QUESTION
        // ALSO WE HAVE OBTAINED ALL USER DETAILS FROM CLOUD BEFOREHAND, IN Q-FRAGMENT
        // VECTORS ARE PASSED FROM THERE TO HERE VIA INTENT, WE HAVENT CALLED FOR USER'S LISTENERS HERE

        mUsername = in.getStringExtra("user"); // username -- blindly copied


        refQ = in.getStringExtra("refQ"); // question-cloud-id
        Qcontent = in.getStringExtra("Qcontent"); // content
        uid = in.getStringExtra("currentuserid");
        vuname = in.getStringArrayListExtra("username");
        vutype = in.getStringArrayListExtra("usertype");
        vucloudid = in.getStringArrayListExtra("usercloudid");

        int i;
        // disable the home button on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // do not show title of action bar

        // change the colour
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.newgreen)));

        getSupportActionBar().setElevation(0);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);
        ((TextView)v.findViewById(R.id.title)).setText("Answers");
        getSupportActionBar().setCustomView(v);

        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //   Toast.makeText(QAActivity.this, "fab2", Toast.LENGTH_SHORT).show();
                displaypopup();

            }

        }); // ACTION IS GIVEN FOR FLOATING BUTTON







//----------------------------------------------------------------------------------------------------------------



        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAnswerDatabaseReference = mFirebaseDatabase.getReference().child("Answer"); // REF FOR ANSWER NODES
        mFirebaseStorage = FirebaseStorage.getInstance();
        mDBHelper = new LocalDatabaseHelper(this.getApplicationContext());


        answer_c = new Answer();
        answer_db = new Answer();
        listAnswers = new ArrayList<String>();
        listAUserID = new ArrayList<String>();
        listUpvotes = new ArrayList<String >();
        listACloudID = new ArrayList<String>();
        listNames = new ArrayList<String>();
        listUType = new ArrayList<String>();
        listBookmark = new ArrayList<>();
        dbqid = new ArrayList<String>();
        dbaid = new ArrayList<String>();
        vamap = new ArrayList<String>();
        DataShared = new ArrayList<>();
        vaname = new Vector<String>();
        vacontent = new Vector<String>();
        vacloudid = new Vector<String>();
        vauserid = new Vector<String>();
        vaquestionid = new Vector<String>();
        vatime = new Vector<>();
        vaupvote = new Vector<String>();
        vautype = new Vector<String>();
        v1 = new Vector<String>();
        v2 = new Vector<String>();
        v3 = new Vector<String>();
        v4 = new Vector<String>();
        v5 = new Vector<String>();
        v6 = new Vector<>();
        v7 = new Vector<>();
        v8 = new Vector<>();
        vuemail = new Vector<>();

        rv = (RecyclerView) findViewById(R.id.ans_recycler_view);
        rv.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);// use a linear layout manager
        rv.setLayoutManager(mLayoutManager);
        // FIRST OBTAIN ALL DETAILS ABOUT ANSWERS FROM CLOUD
        try {
             // start trying it from first , we might get it till we perform other operations
            InitializeAnswers();

            new GetQAIDFromDB().execute(refQ); // gets the mapping of Q and A ids
            new GetADataFromDB().execute(vamap); // gets data for all answers
            // but user-id of answer writer is not there, so make call to cloud listeners
            // set the adapter in this method

            // next we fetch all the usernames from user-ids
            GetUpvoteFromDB getUpvoteFromDB = new GetUpvoteFromDB();
            try {
                UserUpvote = getUpvoteFromDB.execute().get();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            }


        }
        catch (Exception ex)
        {

        }
        inflater = (LayoutInflater) QAActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.activity_qapanel, (ViewGroup) findViewById(R.id.activity_qapanel));
        editText = (EditText) layout.findViewById(R.id.editText);


    }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.actionbar_qaactivity, menu);

            menuItem= menu.findItem(R.id.menu_item_share);

            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_bookmark) {
                // do action for onclick


                try {
                    new SetBookmarkFromDB().execute(refQ);
                    Snackbar snackbar = Snackbar
                            .make(findViewById(R.id.activity_qa), "Awesome!! This question is Bookmarked ", Snackbar.LENGTH_LONG);

                    snackbar.show();
                }
                catch(Exception e)
                {
                }

                return true;
            }

            else if(id == R.id.menu_item_share)
            {

                String texttobeshared = new String();
                String x = new String();

                // THIS IS THE TEXT THAT HAS TO BE SHARED
                // OUR DATA IS INSIDE A LIST CALLED DATASHARED
                if(DataShared.size() == 0)
                {
                    // data not selected
                    // Alert Dialog here
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QAActivity.this);
                    alertDialogBuilder.setMessage("Please select answers to be shared !!");
                    alertDialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();


                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();


                    alertDialog.show();

                }
                else {


                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "IntelliDJ Answers");

                 /*   int i;
                    for(i=0;i<DataShared.size();i++) {

                        x = x + (i+1)+"."+DataShared.get(i); //   1. Yes,ok that would be great
                      texttobeshared = (i+1)+"."+ texttobeshared + DataShared.get(i);
                    }
                    */
                    texttobeshared =   DataShared.get(0);


                    sharingIntent.putExtra(Intent.EXTRA_TEXT,texttobeshared);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }

                return true;
            }



            return super.onOptionsItemSelected(item);
        }


    public void InitializeAnswers() {

        /// WE CHANGED THE STATEMENT FROM DATABASE REF  TO  QUERY. ORDER BY CHILD RETURNS RESULTS IN ASCENDING ORDER
        Query ref1 = FirebaseDatabase.getInstance().getReference().child("Answer").orderByChild("upvote");

        // for ANSWERS
        ref1.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {
                          //  Toast.makeText(QAActivity.this, "No answer in cloud server", Toast.LENGTH_SHORT).show();
                        } else {
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                System.out.println(dataSnapshot1.getKey());
                                System.out.println(dataSnapshot1.child("name").getValue());
                                System.out.println(dataSnapshot1.child("content").getValue());

                                System.out.println(dataSnapshot1.child("user_id").getValue());

                                // FOR NOW, PUT ALL VALUES TOGETHER IN DIFFERENT VECTORS
                                vacloudid.add(dataSnapshot1.getKey());

                                vaname.add(dataSnapshot1.child("name").getValue().toString());
                                vacontent.add(dataSnapshot1.child("content").getValue().toString());

                                vauserid.add(dataSnapshot1.child("user_id").getValue().toString()); //blind user-ids (multiple users)

                                vaquestionid.add(dataSnapshot1.child("questionId").getValue().toString());

                                vaupvote.add(dataSnapshot1.child("upvote").getValue().toString());
                               // adding this same data to different vectors everytime listner is called, above vectors clear their data after inserting
                                // into db, we just update the values of below vectors instead of clearing them

                                v1.add(dataSnapshot1.getKey());
                                v2.add(dataSnapshot1.child("name").getValue().toString());
                                v3.add(dataSnapshot1.child("content").getValue().toString());

                                v4.add(dataSnapshot1.child("user_id").getValue().toString()); // blind user-ids (multiple users)

                                v5.add(dataSnapshot1.child("questionId").getValue().toString());
                                v7.add(dataSnapshot1.child("upvote").getValue().toString());

                            }
                            new InsertDataIntoLocalDB().execute();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(QAActivity.this, "Oops! Error occured while fetching Answers", Toast.LENGTH_SHORT).show();


                    }
                }
        );
    }

    public void displaypopup() // to display pop-up when we want to type a queation // LATER MODIFY TO ANSWER***********?????????
    {
        userdetails = "";
        String name = "";

        try {
            popupWindow = new PopupWindow(layout, RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT, true);
            popupWindow.showAtLocation(layout, Gravity.CENTER_VERTICAL, 0, 0);
            editText.setText("");

            // hide q category types
            RelativeLayout relay = (RelativeLayout)layout.findViewById(R.id.hide);
            relay.setVisibility(View.GONE);

            // hide notice types
            RelativeLayout relay1 = (RelativeLayout)layout.findViewById(R.id.hidenotice);
            relay1.setVisibility(View.GONE);

            int  screenHeight;
            Display display = getWindowManager().getDefaultDisplay();
            screenHeight = display.getHeight();

            EditText text = (EditText)layout.findViewById(R.id.editText);
            text.getLayoutParams().height = screenHeight - 700;

            TextView textView = (TextView)layout.findViewById(R.id.item_name);
            textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_answer));

            userdetails = "";
            GetUserDetails getUserDetails = new GetUserDetails(); // GETTING TYPE FROM DATABASE
            s = getUserDetails.execute().get();
            arry = new String[5];
            arry = s.split("#");
            name = arry[1];
            // SETTING BOTH USERNAME AND TYPE OF USER IF AVAILABLE

            if (arry[0].matches("Student")) {
                userdetails = name + ", Student";

            } else if (arry[0].matches("Professor")) {
                userdetails = name + ", Professor";
            }
            else if (arry[0].matches("HOD"))
            {
                userdetails = name + ", HOD";
            }
            else if (arry[0].matches("Class-in-charge"))
            {
                userdetails = name + ", Class-In-Charge";
            }
            else if (arry[0].matches("None")  | arry[0].matches("NONE") | arry[0].matches("none")   ) {
                userdetails = arry[1];
            }
            textView.setText("  " + userdetails);

            ImageView imagev = (ImageView)layout.findViewById(R.id.image_border);
            imagev.setBackgroundDrawable(getResources().getDrawable(R.drawable.profpic_answer));

            ImageView imageView = (ImageView)layout.findViewById(R.id.item_image);
            imageView.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_pgreen));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int isNetworkAvailable(Context context) {
        int flag = 0;

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {

                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if (!isConnected) {

                            //internetCheck.setIcon(R.drawable.ic_action_circle_green);
                            // do nothing
                            flag = 1;

                        }
                    }
                }
            }
        }

        if (flag == 0) {

            // not connected
            // display a snack
            //internetCheck.setIcon(R.drawable.ic_action_circle_red);


        }
        return flag;
    }

    public void submitQA(View v)
    {
        if(isNetworkAvailable(QAActivity.this) == 1) // internet connection
        {
 acontent = editText.getText().toString(); // content to be passed
            acontent = acontent.trim();

            if (!acontent.matches("")) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QAActivity.this);
                alertDialogBuilder.setMessage("Submit Answer?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      //  Toast.makeText(QAActivity.this, "Answer is:" + acontent, Toast.LENGTH_SHORT).show();

                        answer_db.setContent(acontent);
                        answer_db.setName(mUsername);

                        answer_c.setContent(acontent);
                        answer_c.setName(mUsername); // this has to be fetched from localdb

                        answer_c.setUserId(arry[2]); // ID OBTAINED FROM DB

                        //  answer_c.setUsertype(arry[1]);
                        answer_c.setQuestionId(refQ); // this answer is for question whose id is refQ


                        // initially set upvote to zero
                        answer_c.setUpvote(0);
                        answer_db.setUpvote(0);

                        if (downloadUrl == null) {

                            mAnswerDatabaseReference.push().setValue(answer_c);
                        } else {
                            answer_c.setAnsPhotourl(downloadUrl.toString());
                            mAnswerDatabaseReference.push().setValue(answer_c);
                        }
                        editText.setText("");

                        Toast.makeText(QAActivity.this, "Your Answer has been submitted! Please refresh to view it!", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();

                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {
                Toast.makeText(this, "Answer cannot be empty!", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(QAActivity.this,"Please connect to internet to submit your answer!",Toast.LENGTH_SHORT).show();
        }
    }


    public void showfile(View v)
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE); // newly added line
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }


    public void CancelQA(View v)

    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QAActivity.this);
        alertDialogBuilder.setMessage("Stop Writing?");
        alertDialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                popupWindow.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed()
    {
     //   super.onBackPressed();

      Intent intn = new Intent(QAActivity.this, IntelliDj.class);
        intn.setFlags(11);
        startActivity(intn);
        overridePendingTransition(R.anim.open2,R.anim.close2);

    }


    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data)

    {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK)
        {
            // if this is true, a uri will be returned to user, not actual file
            Uri uri= null;
            downloadUrl = null;
            if(data !=null)
            {
                uri = data.getData();
                // for both image and text , written inside same try block bcoz
                // for settext method, both image and txt file are read in encoded form
                // if code for image written iside catch, it never gets get executed and only soe encodings for image are displayed

                ContentResolver cR = this.getContentResolver();
                MimeTypeMap mime = MimeTypeMap.getSingleton();
                String type = mime.getExtensionFromMimeType(cR.getType(uri));


                if(type.equals("jpeg") || type.equals("jpg"))
                {
                    try {
                        picture.setImageBitmap(getBitmapFromUri(uri));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                StorageReference photosref = PhotosStorageReference.child(uri.getLastPathSegment());
                photosref.putFile(uri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       downloadUrl =  taskSnapshot.getDownloadUrl();
                    }
                });
            }
        }

    }
    // this function reads and returns bitmap type data, which is an image
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = this.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


    // to insert answer details into database
    public class InsertDataIntoLocalDB extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... params) {

            SQLiteDatabase sql = mDBHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

          //  last_user_id = sql.insert("user", null, values);
            try {
                sql.delete("answer", null, null);
            } catch (Exception e) {
            }

            int i = 0;
            for (i = 0; i < vaname.size(); i++) {
                values.put("content", vacontent.elementAt(i));
                values.put("cloud_id", vacloudid.elementAt(i));
                values.put("user_id", vauserid.elementAt(i));
                values.put("question_id", vaquestionid.elementAt(i));

                values.put("upvote",vaupvote.elementAt(i));

                nextAId = sql.insert("answer", null, values);
            }
            // for NOW, JUST REMOVED THIS FROM HERE TO MAKE SURE WE PASS DATA TO OTHER ACTIVITY

            vaname.removeAllElements();
            vaname.clear();
            vacontent.removeAllElements();
            vacontent.clear();
            vacloudid.removeAllElements();
            vacloudid.clear();
            vauserid.removeAllElements();
            vacloudid.clear();
            vaquestionid.removeAllElements();
            vaquestionid.clear();
            vatime.removeAllElements();
            vatime.clear();
            vaupvote.clear(); vaupvote.removeAllElements();

        return null;
        }
    }

    // may be used for QA MAP
    public class GetQAIDFromDB extends AsyncTask<String,Void,Void>
    {

        @Override
        protected Void doInBackground(String... params) {

            String reference = params[0]; // actual ref id
            SQLiteDatabase sqLiteDatabase = mDBHelper.getReadableDatabase();
            int i;
            vamap.clear();

            final Cursor c1 = sqLiteDatabase.rawQuery("SELECT * FROM answer",new String[]{});

            if(c1.moveToNext())
            {
                // data exists
                // retrieve all needed data , question_id and answer_id
                do {
                    dbqid.add(c1.getString(c1.getColumnIndexOrThrow("question_id")));
                    dbaid.add(c1.getString(c1.getColumnIndexOrThrow("cloud_id"))); // blind - user -id
                }
                while (c1.moveToNext());
            }
            c1.close();
            // now compare all of them with reference question

            for (i = 0; i < dbqid.size(); i++) {


                if (reference.matches(dbqid.get(i))) {
                    vamap.add(dbaid.get(i));
                    // which ans is for our ref q
                } else {
                }
            }
            return null;
        }
    }

        public class GetADataFromDB extends AsyncTask<ArrayList<String>,Void,Void>
    {

        @Override
        protected Void doInBackground(ArrayList<String>... params) {

            ArrayList<String> list = new ArrayList<>(params[0]);
            SQLiteDatabase sqLiteDatabase = mDBHelper.getReadableDatabase();
            int i;

            listAnswers.clear();
            listAUserID.clear();
            listUpvotes.clear();
            listACloudID.clear();
            listUType.clear();

            // OBTAIN CONTENT OF ALL ANSWERS AND CLOUD-ID OF THEIR RESP. USERS
            for (i = 0; i < list.size(); i++) {

                final Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM answer WHERE cloud_id = ?",
                        new String[]{list.get(i)});



                if (c.moveToFirst()) {
                    // some rows exists
                    do {
                        listAnswers.add(c.getString(c.getColumnIndexOrThrow("content")));
                        listAUserID.add(c.getString(c.getColumnIndexOrThrow("user_id")));
                        listUpvotes.add(c.getString(c.getColumnIndexOrThrow("upvote")));
                        listACloudID.add(c.getString(c.getColumnIndexOrThrow("cloud_id")));

                    }
                    while (c.moveToNext());
          }
                c.close();

            }
        int j = 0;
            for(i = 0; i< listAUserID.size() ; i++)
        {
                for(j=0; j< vucloudid.size(); j ++ ) {
                                // check with our cloud-details
                                if (listAUserID.get(i).matches(vucloudid.get(j))) // if matching user-id is found, user is found
                                {
                                    // get his name
                                    try {
                                        listNames.add(vuname.get(j));
                                        listUType.add(vutype.get(j));
                                        break; // no need to traverse the entire list
                                    } catch (Exception e) {
                                    }
                                } else {
                                }
                }

        }
            for(j =0;j<listACloudID.size();j++)
            {
                if(UserUpvote.contains(listACloudID.get(j)))
                {
                    // already upvoted
                    SetUpvote.add("true");
                }
                else
                {
                    SetUpvote.add("false");

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {

            try {
                if (listAUserID.size() > 0) {

                    // ALL DATA OBTAINED IN THESE LISTS IS IN ASCENDING ORDER, WE NEED TO CHANGE THEM IN DESCENDING ORDET
                    // SO WE USE METHOD ,, Collections.reverse(list)

                    Collections.reverse(listAnswers);
                    Collections.reverse(listNames);
                    Collections.reverse(listUpvotes);
                    Collections.reverse(listACloudID);
                    Collections.reverse(listUType);
                    Collections.reverse(SetUpvote);

                    mAdapter = new AnswerAdapter(listAnswers, listNames,  listUpvotes, listACloudID ,listUType, SetUpvote );


                    TextView textCount = (TextView) findViewById(R.id.answer_count);
                    textCount.setText("Showing all " + listAnswers.size() + " answer(s)...");

                    rv.setAdapter(mAdapter);
                } else {

                    TextView textCount = (TextView) findViewById(R.id.answer_count);
                    textCount.setText("Showing all 0 answer(s)...");
                    Toast.makeText(QAActivity.this, "Loading answers (if any!)", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
            }

        }


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

                    sx = (Cr.getString(Cr.getColumnIndexOrThrow("type"))) + "#" + (Cr.getString(Cr.getColumnIndexOrThrow("name")))
                    + "#" +(Cr.getString(Cr.getColumnIndexOrThrow("cloud_id"))) ;



                }
                while (Cr.moveToNext());
            }
            Cr.close();
            return sx;
        }
    }



    public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder>

    {




        ArrayList<String> listAData , listAUser , listUpvotes , listACloudID , listUserType , listSetUpvote;
        int selection =0 , flag = 0;
                                     class ViewHolder extends RecyclerView.ViewHolder //implements View.OnClickListener// class inside class---subclass
                                            {
                                                ImageView itemImage;
                                                TextView itemTitle;
                                                TextView itemName;
                                                ImageButton itemUpvote;
                                                private Context context;
                                                TextView itemUpvoteCount;
                                                private View V;

                                                public ViewHolder(View view) // constrcutor of inner class
                                                {
                                                    super(view);
                                                    itemImage = (ImageView) view.findViewById(R.id.item_image);
                                                    itemTitle = (TextView) view.findViewById(R.id.item_title);
                                                    itemName = (TextView) view.findViewById(R.id.item_name);
                                                    //itemTime = (TextView)view.findViewById(R.id.itemTime);
                                                    itemUpvote = (ImageButton)view.findViewById(R.id.item_upvote);
                                                    itemUpvoteCount = (TextView)view.findViewById(R.id.upvotecount);
                                                    context = view.getContext();
                                                    V = view;
                                                   }

                                            }

        public AnswerAdapter() {

        }

        // created, so that data can be passed
        public AnswerAdapter(ArrayList<String> Acontent, ArrayList<String> Auser , ArrayList<String> Aupvote
        , ArrayList<String> ACloudID ,ArrayList<String> AUType , Vector<String> UpVote ) {
           listAData = new ArrayList<String >(Acontent);
            listAUser = new ArrayList<String >(Auser);

            try{}
            catch (Exception ex) { }

            listUpvotes = new ArrayList<>(Aupvote);
            listACloudID = new ArrayList<>(ACloudID);
            listUserType = new ArrayList<>(AUType);
            listSetUpvote = new ArrayList<>(UpVote);

        }




        @Override
        public AnswerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_answer, parent, false);
            AnswerAdapter.ViewHolder viewHolder = new AnswerAdapter.ViewHolder(v);


            return viewHolder;
        }

        @Override
        public void onBindViewHolder( final AnswerAdapter.ViewHolder holder,final   int position) {


            if (listUserType.size() > 0) {
                holder.itemTitle.setText(listAData.get(position)); // data

                if (listUserType.get(position).matches("") || listUserType.get(position).matches("none")) {
                    holder.itemName.setText(listAUser.get(position));
                } else {
                    holder.itemName.setText(""+listAUser.get(position) + ", " + listUserType.get(position)); // username
                }
                holder.itemUpvoteCount.setText(listUpvotes.get(position) + " Upvotes"); // no of upvotes
            }
            else
            {
                Intent in = new Intent(QAActivity.this,IntelliDj.class);
                startActivity(in);

            }



            holder.itemTitle.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.itemTitle.setMaxLines(Integer.MAX_VALUE);
                        }
                    }
            );

            if (UserUpvote.contains(listACloudID.get(position)))
            {
                // already uvoted answer

                holder.itemUpvote.setBackgroundResource(R.mipmap.ic_upvote2); // image button to give upvote to an answer
                holder.itemUpvote.setClickable(false);

            }
            else
            {
                holder.itemUpvote.setClickable(true);
                holder.itemUpvote.setBackgroundResource(R.mipmap.ic_upvote); // image button to give upvote to an answer

            }

         //   holder.V.setBackgroundColor(model.isSelected() ? Color.CYAN : Color.WHITE);
            holder.itemTitle.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(selection < 1) {

                        holder.V.setBackgroundColor(getResources().getColor(R.color.newcol));
                        DataShared.add(listAData.get(position) + "\n"); // put the data inside the list
                        selection = selection + 1;

                    }

                    else if(selection >1)
                    {
                        Toast.makeText(QAActivity.this,"Cannot share more than 1 question!",Toast.LENGTH_SHORT).show();
                    }
                    return false;

                }
            });



            if (holder.itemUpvote.isClickable() == true)
            {
            holder.itemUpvote.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // current answer-cloud-id is needed

                            // UPVOTE ONLY IF INTERNET IS CONNECTED
                            if(isNetworkAvailable(QAActivity.this) == 1)
                            {
                                // set field upvoted=true inside localdb
                                try {
                                    new SetUpvoteTrueInDB().execute(listACloudID.get(position));
                                    long vote = Long.parseLong(listUpvotes.get(position));
                                    mAnswerDatabaseReference.child(listACloudID.get(position)).child("upvote")
                                            .setValue(vote + 1); // increment value of upvote by 1
                                    holder.itemUpvote.setBackgroundResource(R.mipmap.ic_upvote2);
                                    holder.itemUpvote.setClickable(false); // disabled , only till you come to this page again
                                    flag = 1;

                                    Toast.makeText(QAActivity.this, "Upvoted this answer!!", Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
                                }
                            }
                            else
                            {
                                Toast.makeText(QAActivity.this,"Cannot upvote answer in offline mode!",Toast.LENGTH_SHORT).show();
                            }


                        }
                    }


            );
            }
            else
            {
               // Toast.makeText(QAActivity.this,"You have already upvoted this answer!",Toast.LENGTH_SHORT).show();
            }

            if (flag==1)
            {
                // already upvoted, so disable
                holder.itemUpvote.setClickable(false);
            }


        }

        @Override
        public int getItemCount() {

            return listAData.size();
        }


    }

    public class SetUpvoteTrueInDB extends AsyncTask<String,Void,Integer>
    {
        SQLiteDatabase sqLiteDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        @Override
        protected Integer doInBackground(String... params) {
            try {
                String refAnswer = params[0]; // cloud id of the answer
                String[] args = {String.valueOf(refAnswer)};
          final Cursor cr = sqLiteDatabase.rawQuery("SELECT * FROM upvotelist",
                        new String[]{});

                if(cr.moveToNext())
                {
                    // data exists
                    // check with existing data
                    do {
                        DBUpvote.add(cr.getString(cr.getColumnIndexOrThrow("answer_id")));

                    }
                    while (cr.moveToNext());
                    int i; boolean flag = false;
                    for(i=0;i<DBUpvote.size();i++) {
                        if (refAnswer.matches(DBUpvote.get(i))) {
                            // question already exists
                            // NO INSERTION NEEDED
                            flag = true;
                            break;

                        }
                    }

                    if(flag==false)
                    {
                        // does not exist
                        // insert

                        values.put("answer_id",refAnswer);
                        sqLiteDatabase.insert("upvotelist", null, values);


                    }




                }
                else  // no data exists at all
                {
                    values.put("answer_id",refAnswer);
                    sqLiteDatabase.insert("upvotelist", null, values);


                }
                values.clear();
                cr.close();

            }
            catch (Exception e)
            {
                }
            return null;
        }
    }


    public class GetUpvoteFromDB extends AsyncTask<String,Void,Vector>
    {
        SQLiteDatabase sqLiteDatabase = mDBHelper.getWritableDatabase();


        @Override
        protected Vector doInBackground(String... params) {
                final Cursor cr = sqLiteDatabase.rawQuery("SELECT * FROM upvotelist",
                    new String[]{});
            DBUpvote.removeAllElements();


            if(cr.moveToNext()) {
                // data exists
                // check with existing data

                // IF WE WRITE THIS WITHOUT DO - WHILE, THEN ONLY 1ST ITEM GETS SELECTED
                do {


                    DBUpvote.add(cr.getString(cr.getColumnIndexOrThrow("answer_id")));
                }
                while(cr.moveToNext());

            }
            cr.close();
            return DBUpvote;
        }
    }




    public class SetBookmarkFromDB extends AsyncTask<String,Void,Void>
    {


        SQLiteDatabase sqLiteDatabase = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        @Override
        protected Void doInBackground(String... params) {


            // we have to avoid duplicate insertion of bookmarks
            // first check the existign values
            // then insert if needed
            String bookmarkQ = params[0];
      final Cursor cr = sqLiteDatabase.rawQuery("SELECT * FROM bookmark",
                    new String[]{});

            if(cr.moveToNext())
            {
                // data exists
                // check with existing data
                do {
                    listBookmark.add(cr.getString(cr.getColumnIndexOrThrow("question_id")));

                }
                while (cr.moveToNext());
                int i; boolean flag = false;
                for(i=0;i<listBookmark.size();i++) {
                    if (bookmarkQ.matches(listBookmark.get(i))) {
                        // question already exists
                        // NO INSERTION NEEDED
                        flag = true;
                        break;

                    }
                }

                if(flag==false)
                    {
                        // does not exist
                        // insert
                        values.put("question_id",bookmarkQ);
                        sqLiteDatabase.insert("bookmark", null, values);

                    }




            }
            else  // no data exists at all
            {
                values.put("question_id",bookmarkQ);
                sqLiteDatabase.insert("bookmark", null, values);


            }
            values.clear();
            cr.close();
            return null;
        }
    }


    // to avoid crashes
    public class ExceptionHandler implements
            java.lang.Thread.UncaughtExceptionHandler {
        private final Activity myContext;
        private final String LINE_SEPARATOR = "\n";

        public ExceptionHandler(Activity context) {
            myContext = context;
        }

        public void uncaughtException(Thread thread, Throwable exception) {
            StringWriter stackTrace = new StringWriter();
            exception.printStackTrace(new PrintWriter(stackTrace));
            StringBuilder errorReport = new StringBuilder();

            Intent intent = new Intent(myContext, IntelliDj.class);
            intent.putExtra("error", errorReport.toString());
            myContext.startActivity(intent);

            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }

    }
}