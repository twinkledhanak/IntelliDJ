package com.tkd.twinkledhanak.intellidj.activities;

import android.app.Activity;
import android.content.ContentResolver;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tkd.twinkledhanak.intellidj.db.LocalDatabaseHelper;
import com.tkd.twinkledhanak.intellidj.notifications.MyIntentService;
import com.tkd.twinkledhanak.intellidj.R;
import com.tkd.twinkledhanak.intellidj.objects.Announcement;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class AnnouncementActivity extends AppCompatActivity {


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAnnDatabaseReference;
    private LocalDatabaseHelper mDBHelper;
    FloatingActionButton fab3;
    EditText editText;
    LayoutInflater inflater;
    ImageView picture;
    View layout;
    PopupWindow popupWindow;
    Intent mServiceIntent;
    private RecyclerView rv; // ADDING RECYCLER VIEW ELEMENTS
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String uname , date , category , userdetails , s , ProfileImage  , permission = "" ;
    Announcement announcement;
    Vector<String> vnuname , vncontent , vntime , vnuimg , vnid , vntype , vnusertype;
    ArrayList<String> vuname , vucloudid, vuimg  ;

    String arry[];
    Button fe,se,te,be , all;
    String[] arr ;
    TapTargetSequence tap;


    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        mServiceIntent = new Intent(AnnouncementActivity.this, MyIntentService.class);
       startService(mServiceIntent);

        Intent in = getIntent();
        uname = in.getStringExtra("uname");
        ProfileImage = in.getStringExtra("image");
        vnuname = new Vector<String>();
        vncontent = new Vector<String>();
        vntime = new Vector<String>();
        vnuimg = new Vector<String>();
        vnid = new Vector<>();
        vntype = new Vector<String>();
        vnusertype = new Vector<String>();

        vuname = new ArrayList<String>();
        vucloudid = new ArrayList<String>();
        vuimg = new ArrayList<String>();
        mDBHelper = new LocalDatabaseHelper(this.getApplicationContext());

        inflater = (LayoutInflater) AnnouncementActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.activity_qapanel, (ViewGroup) findViewById(R.id.activity_qapanel));
        editText = (EditText) layout.findViewById(R.id.editText);
        fe = (Button) layout.findViewById(R.id.fe);
        se = (Button) layout.findViewById(R.id.se);
        te = (Button) layout.findViewById(R.id.te);
        be = (Button) layout.findViewById(R.id.be);
        all = (Button) layout.findViewById(R.id.all);
      fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             //   Toast.makeText(AnnouncementActivity.this, "fab3", Toast.LENGTH_SHORT).show();
                displaypopup();


            }

        }); // ACTION IS GIVEN FOR FLOATING BUTTON
        // CALL TO ADAPTER TO SET DATA FOR RV


        getSupportActionBar().setDisplayShowTitleEnabled(false); // do not show title of action bar
        getSupportActionBar().setElevation(0);
        // change the colour
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.newred)));

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);
        ((TextView)v.findViewById(R.id.title)).setText("Announcements");
        getSupportActionBar().setCustomView(v);


        rv = (RecyclerView) findViewById(R.id.notice_recycler_view);
        rv.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);// use a linear layout manager
        rv.setLayoutManager(mLayoutManager);
        getAllNotices();


        GetUserDetails getUserDetails = new GetUserDetails();
        String s = "none";
        try {
            s = getUserDetails.execute().get();
            // returns the year of user
            arr  = s.split("#");
            permission = arr[0];
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //------------------------------------------------------------------------------------

    }

    public void getAllNotices()
    {   DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Announcement");
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try
                {

                    vnuname.clear(); vnuname.removeAllElements();
                    vncontent.clear(); vnuname.removeAllElements();
                    vntime.clear(); vntime.removeAllElements();
                    vntype.clear(); vntype.removeAllElements();
                    vnusertype.clear(); vnusertype.removeAllElements();

                }
                catch (Exception e)
                {

                }

                if(!dataSnapshot.exists())
                {
                    Toast.makeText(AnnouncementActivity.this,"No Announcements!!",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Toast.makeText(AnnouncementActivity.this,"Fetching all Announcements!",Toast.LENGTH_SHORT).show();
                    GetUserDetails getUserDetails = new GetUserDetails();
                    String s = "none";
                    try {
                        s = getUserDetails.execute().get();
                        // returns the year of user
                        arr  = s.split("#");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }


                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {

                        vntype.add(dataSnapshot1.child("type").getValue().toString()); // just for notifs, not to set anywhere


                                if (dataSnapshot1.child("type").getValue().toString().matches("all")) // set notice for everybody
                                {
                                    // ONLY THEN ADD THE DETAILS OF NOTICE
                                    vnid.add(dataSnapshot1.getKey());
                                    vnuname.add(dataSnapshot1.child("uname").getValue().toString());
                                    vncontent.add(dataSnapshot1.child("content").getValue().toString());
                                    vntime.add(dataSnapshot1.child("timestamp").getValue().toString());
                                    vnusertype.add(dataSnapshot1.child("usertype").getValue().toString());

                                }

                                if (dataSnapshot1.child("type").getValue().toString().matches("fe") && arr[1].matches("fe")) // FE notice
                                {
                                    // general notices for all
                                    vnid.add(dataSnapshot1.getKey());
                                    vnuname.add(dataSnapshot1.child("uname").getValue().toString());
                                    vncontent.add(dataSnapshot1.child("content").getValue().toString());
                                    vntime.add(dataSnapshot1.child("timestamp").getValue().toString());

                                    vnusertype.add(dataSnapshot1.child("usertype").getValue().toString());
                                }

                                 if (dataSnapshot1.child("type").getValue().toString().matches("se") && arr[1].matches("se")) // FE notice
                                {
                                    // general notices for all
                                    vnid.add(dataSnapshot1.getKey());
                                    vnuname.add(dataSnapshot1.child("uname").getValue().toString());
                                    vncontent.add(dataSnapshot1.child("content").getValue().toString());
                                    vntime.add(dataSnapshot1.child("timestamp").getValue().toString());

                                    vnusertype.add(dataSnapshot1.child("usertype").getValue().toString());
                                }

                                 if (dataSnapshot1.child("type").getValue().toString().matches("te") && arr[1].matches("te")) // FE notice
                                {
                                    // general notices for all
                                    vnid.add(dataSnapshot1.getKey());
                                    vnuname.add(dataSnapshot1.child("uname").getValue().toString());
                                    vncontent.add(dataSnapshot1.child("content").getValue().toString());
                                    vntime.add(dataSnapshot1.child("timestamp").getValue().toString());

                                    vnusertype.add(dataSnapshot1.child("usertype").getValue().toString());
                                }


                                 if (dataSnapshot1.child("type").getValue().toString().matches("be") && arr[1].matches("be")) // FE notice
                                {
                                    // general notices for all
                                    vnid.add(dataSnapshot1.getKey());
                                    vnuname.add(dataSnapshot1.child("uname").getValue().toString());
                                    vncontent.add(dataSnapshot1.child("content").getValue().toString());
                                    vntime.add(dataSnapshot1.child("timestamp").getValue().toString());

                                    vnusertype.add(dataSnapshot1.child("usertype").getValue().toString());
                                }



                    }
      mAdapter = new AnnounceAdapter(vncontent,vnuname,vntime , vntype , vnusertype); // vntype is added to filter the data by notice type
                    rv.setAdapter(mAdapter);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(AnnouncementActivity.this, "Oops! Error occured while fetching Announcements!", Toast.LENGTH_SHORT).show();
            }
        });


    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        vnuname.clear(); vnuname.removeAllElements();
        vncontent.clear(); vnuname.removeAllElements();

        overridePendingTransition(R.anim.open2,R.anim.close2);
    }

    public void displaypopup() // to display pop-up when we want to type a queation // LATER MODIFY TO ANSWER***********?????????
    {
        if (permission.matches("Student") )
        {
            Toast.makeText(AnnouncementActivity.this,"Students cannot submit notice!",Toast.LENGTH_SHORT).show();
        }
        else {
            try {

                fe.setBackgroundResource(R.drawable.profile_notice);
                se.setBackgroundResource(R.drawable.profile_notice);
                te.setBackgroundResource(R.drawable.profile_notice);
                be.setBackgroundResource(R.drawable.profile_notice);
                all.setBackgroundResource(R.drawable.profile_notice);

                fe.setClickable(true);
                se.setClickable(true);
                te.setClickable(true);
                be.setClickable(true);
                all.setClickable(true);


                popupWindow = new PopupWindow(layout, RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT, true);
                popupWindow.showAtLocation(layout, Gravity.CENTER_VERTICAL, 0, 0);
                editText.setText("");
                category = "none";
                RelativeLayout relay = (RelativeLayout) layout.findViewById(R.id.hide);
                relay.setVisibility(View.GONE);

                int  screenHeight;
                Display display = getWindowManager().getDefaultDisplay();

                screenHeight = display.getHeight();

                EditText text = (EditText) layout.findViewById(R.id.editText);
                text.getLayoutParams().height = screenHeight - 700;

                TextView textView = (TextView) layout.findViewById(R.id.item_name);
                textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_notice));

                userdetails = "";
                GetUserDetails getUserDetails = new GetUserDetails(); // GETTING TYPE FROM DATABASE
                s = getUserDetails.execute().get();

                arr = new String[5];
                arry = s.split("#");
                //  on splitting , at index 1 , ie , 2nd value returned is type
                // SETTING BOTH USERNAME AND TYPE OF USER IF AVAILABLE

                if (arry[0].matches("Student")) {
                    userdetails = uname + ", Student";

                } else if (arry[0].matches("Professor")) {
                    userdetails = uname + ", Professor";
                } else if (arry[0].matches("HOD")) {
                    userdetails = uname + ", HOD";
                } else if (arry[0].matches("Class-in-charge")) {
                    userdetails = uname + ", Class-In-Charge";
                } else {
                    userdetails = uname;
                }
                textView.setText("  " + userdetails);

                ImageView imagev = (ImageView) layout.findViewById(R.id.image_border);
                imagev.setBackgroundDrawable(getResources().getDrawable(R.drawable.profpic_notice));

                ImageView imageView = (ImageView) layout.findViewById(R.id.item_image);
                // Glide.with(this).load(ProfileImage).into(imageView);
                imageView.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_pred));

                editText.setText("");
                category = "none";


            } catch (Exception e) {
            }
        }
    }

    public void cardFE(View v)
    {
        category = "fe";
        fe.setBackgroundResource(R.drawable.profile_answer);
        se.setClickable(false);
        te.setClickable(false);
        be.setClickable(false);
        all.setClickable(false);


    }

    public void cardSE(View v)
    {
        category = "se";
        se.setBackgroundResource(R.drawable.profile_answer);
        fe.setClickable(false);
        te.setClickable(false);
        be.setClickable(false);
        all.setClickable(false);

    }

    public void cardTE(View v) {
        category = "te";
        te.setBackgroundResource(R.drawable.profile_answer);
        fe.setClickable(false);
        se.setClickable(false);
        be.setClickable(false);
        all.setClickable(false);
    }

    public void cardBE(View v) {
        category = "be";
        be.setBackgroundResource(R.drawable.profile_answer);
        fe.setClickable(false);
        se.setClickable(false);
        te.setClickable(false);
        all.setClickable(false);

    }

    public  void cardAll(View v)
    {
        category = "all";
        all.setBackgroundResource(R.drawable.profile_answer);
        fe.setClickable(false);
        se.setClickable(false);
        te.setClickable(false);
        be.setClickable(false);

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

        }
        return flag;
    }


    public void submitQA(View v) {

        if(isNetworkAvailable(AnnouncementActivity.this) == 1) // internet connection
        {

            if (!editText.getText().toString().trim().matches("") && !category.matches("none")) {


                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AnnouncementActivity.this);
                alertDialogBuilder.setMessage("Submit Notice?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mFirebaseDatabase = FirebaseDatabase.getInstance();
                        mAnnDatabaseReference = mFirebaseDatabase.getReference().child("Announcement");
                        announcement = new Announcement();
                        announcement.setContent(editText.getText().toString().trim());
                        announcement.setUname(uname);
                        announcement.setType(category);
                        announcement.setUsertype(arry[0]);

                        Long tsLong = System.currentTimeMillis() / 1000;
                        tsLong = tsLong * 1000;
                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(tsLong);
                        date = DateFormat.format("dd-MM-yyyy", cal).toString();
                        String newdate[] = date.split("-");
                        String month = "";
                        switch (newdate[1]) {
                            case "01":
                                month = "Jan";
                                break;
                            case "02":
                                month = "Feb";
                                break;
                            case "03":
                                month = "Mar";
                                break;
                            case "04":
                                month = "Apr";
                                break;
                            case "05":
                                month = "May";
                                break;
                            case "06":
                                month = "Jun";
                                break;
                            case "07":
                                month = "Jul";
                                break;
                            case "08":
                                month = "Aug";
                                break;
                            case "09":
                                month = "Sep";
                                break;
                            case "10":
                                month = "Oct";
                                break;
                            case "11":
                                month = "Nov";
                                break;
                            case "12":
                                month = "Dec";
                                break;
                        }
                        date = date.replace(newdate[1], month);

                        announcement.setTimestamp(date);
                        mAnnDatabaseReference.push().setValue(announcement);
                        editText.setText("");
                        Toast.makeText(AnnouncementActivity.this, "Your Notice has been submitted!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Notice cannot be empty / Please select a category!", Toast.LENGTH_SHORT).show();
            }

        }
        else
        {
            Toast.makeText(AnnouncementActivity.this, "Please connect to internet to submit your notice!", Toast.LENGTH_SHORT).show();
        }


    }

    public void showfile(View v)
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE); // newly added line
        intent.setType("*/*");
        startActivityForResult(intent, 1);
    }



    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data)

    {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK)
        {
            // if this is true, a uri will be returned to user, not actual file
            Uri uri= null;

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


    public void CancelQA(View v)

    {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AnnouncementActivity.this);
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



    public class AnnounceAdapter extends RecyclerView.Adapter<AnnounceAdapter.ViewHolder>

    {
        ArrayList<String> listNContent,listUname,listTimestamp , listNType , listNUserType;


                                    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener// class inside class---subclass
                                    {
                                        ImageView itemImage;
                                        TextView itemTitle;
                                        TextView itemName;
                                        TextView itemTime;
                                        private Context context;

                                        public ViewHolder(View view) // constrcutor of inner class
                                        {
                                            super(view);
                                            itemImage = (ImageView) view.findViewById(R.id.item_image);
                                            itemTitle = (TextView) view.findViewById(R.id.item_title);
                                            itemName = (TextView) view.findViewById(R.id.item_name);
                                            itemTime = (TextView) view.findViewById(R.id.itemTime);
                                            context = view.getContext();
                                            itemTitle.setOnClickListener(this); // instead set it on a different object
                                        }


                                        @Override
                                        public void onClick(View v) {

                                        }

                                    }


        public AnnounceAdapter() {

        }

        // created, so that data can be passed
        public AnnounceAdapter(Vector<String> vncontent, Vector<String> vnuname , Vector<String > vntime, Vector<String> vntype
         , Vector<String> vnusertye) {
            listNContent = new ArrayList<String >(vncontent);
            listUname = new ArrayList<String >(vnuname);
            listTimestamp = new ArrayList<>(vntime);
            listNType = new ArrayList<>(vntype);
            listNUserType = new ArrayList<String>(vnusertye);
        }


        @Override
        public AnnounceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_announcement, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);


            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            // already filtered data present in the vectors

                holder.itemTitle.setText(listNContent.get(position)); // data

                if(listNUserType.get(position).matches("none") || listNUserType.get(position).matches("") )
                {
                    holder.itemName.setText(listUname.get(position));
                }
            else {
                    holder.itemName.setText(listUname.get(position) + " , " + listNUserType.get(position)); // username
                }
                holder.itemTime.setText(listTimestamp.get(position));
            holder.itemTitle.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.itemTitle.setMaxLines(Integer.MAX_VALUE);  // notice needs expansion for more than 5 lines
                        }
                    }
            );

        }

        @Override
        public int getItemCount() {

            return listNContent.size();
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
            String sx = "";

            if(Cr.moveToNext())
            {
                // data exists
                do {
                    // retreive the data

                    sx = (Cr.getString(Cr.getColumnIndexOrThrow("type"))) + "#" +
                            (Cr.getString(Cr.getColumnIndexOrThrow("year")));


                }
                while (Cr.moveToNext());
            }
            Cr.close();
            return sx;
        }
    }


}




