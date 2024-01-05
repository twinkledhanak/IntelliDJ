package com.tkd.twinkledhanak.intellidj.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.tkd.twinkledhanak.intellidj.R;
import com.tkd.twinkledhanak.intellidj.db.LocalDatabaseHelper;
import com.tkd.twinkledhanak.intellidj.notifications.MyIntentService;
import com.tkd.twinkledhanak.intellidj.objects.Document;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

public class DocumentActivity extends AppCompatActivity {

    final static int PICK_PDF_CODE = 2342;

    //these are the views
    TextView textViewStatus;
    EditText editTextFilename;
    ProgressBar progressBar;
    FloatingActionButton fab4;
    View layout;
    PopupWindow popupWindow;
    LayoutInflater inflater;

    Intent starterIntent;
    Bitmap bmp , bt;
    String[] arr ;
    String category;
    Vector<String> dname, dtime;
    String permission = "";

    Intent mServiceIntent2;
    Button fe,se,te,be , all;

    private boolean isConnected = false;
    ArrayList<Document> uploadList , documentList;

    //the firebase objects for storage and database
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    private LocalDatabaseHelper mDBHelper;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 1;

    private RecyclerView rv; // ADDING RECYCLER VIEW ELEMENTS
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        try {
            mServiceIntent2 = new Intent(DocumentActivity.this, MyIntentService.class);
            // mServiceIntent.setData(Uri.parse(dataUrl));
            startService(mServiceIntent2);
        }
        catch (Exception e)
        {

        }


        getSupportActionBar().setDisplayShowTitleEnabled(false); // do not show title of action bar

        // change the colour
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.newyellow)));

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);
        ((TextView)v.findViewById(R.id.title)).setText("Documents");
        getSupportActionBar().setCustomView(v);



        starterIntent = getIntent();

        //getting firebase objects
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Document");

        mDBHelper = new LocalDatabaseHelper(this.getApplicationContext());


        inflater = (LayoutInflater) DocumentActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.upload, (ViewGroup) findViewById(R.id.upload));

        //getting the views
        textViewStatus = (TextView) layout.findViewById(R.id.textViewStatus);
        editTextFilename = (EditText) layout.findViewById(R.id.editTextFileName);
        progressBar = (ProgressBar) layout.findViewById(R.id.progressbar);
       // listView = (ListView) findViewById(R.id.listView);

        uploadList = new ArrayList<Document>();
        documentList = new ArrayList<Document>();
        dname = new Vector<String>();
        dtime = new Vector<String>();

        fe = (Button) layout.findViewById(R.id.fe);
        se = (Button) layout.findViewById(R.id.se);
        te = (Button) layout.findViewById(R.id.te);
        be = (Button) layout.findViewById(R.id.be);
        all = (Button) layout.findViewById(R.id.all);

        fab4 = (FloatingActionButton) findViewById(R.id.fab4);


        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        if (!hasPermissions(this, PERMISSIONS))

        {
            ActivityCompat.requestPermissions(this, PERMISSIONS, MY_PERMISSIONS_REQUEST_STORAGE);
        }



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

        fab4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isNetworkAvailable(DocumentActivity.this) == 1)  // internet connection
                {
                    displaypopup();
                }
                else
                {
                    Toast.makeText(DocumentActivity.this,"Please connect to internet to upload PDF!",Toast.LENGTH_SHORT).show();
                }


            }

        });



        //retrieving upload data from firebase database
        mDatabaseReference.addValueEventListener(new ValueEventListener() {



            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {


                    GetUserDetails getUserDetails = new GetUserDetails();
                    String s = "none";
                    try {
                        s = getUserDetails.execute().get();
                        // returns the year of user
                        arr = s.split("#");

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                   int  i = 0;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        if (dataSnapshot1.child("type").getValue().toString().matches("all"))
                        {
                            dname.add(dataSnapshot1.child("name").getValue().toString());
                            dtime.add(dataSnapshot1.child("timestamp").getValue().toString());
                            documentList.add(dataSnapshot1.getValue(Document.class));

                        }

                        if (dataSnapshot1.child("type").getValue().toString().matches("fe") && arr[1].matches("fe"))
                        {
                            dname.add(dataSnapshot1.child("name").getValue().toString());
                            dtime.add(dataSnapshot1.child("timestamp").getValue().toString());
                            documentList.add(dataSnapshot1.getValue(Document.class));
                        }

                       if (dataSnapshot1.child("type").getValue().toString().matches("se") && arr[1].matches("se"))
                        {
                            dname.add(dataSnapshot1.child("name").getValue().toString());
                            dtime.add(dataSnapshot1.child("timestamp").getValue().toString());
                            documentList.add(dataSnapshot1.getValue(Document.class));
                        }

                         if (dataSnapshot1.child("type").getValue().toString().matches("te") && arr[1].matches("te"))
                        {
                            dname.add(dataSnapshot1.child("name").getValue().toString());
                            dtime.add(dataSnapshot1.child("timestamp").getValue().toString());
                            documentList.add(dataSnapshot1.getValue(Document.class));
                        }

                       if (dataSnapshot1.child("type").getValue().toString().matches("be") && arr[1].matches("be"))
                        {
                            dname.add(dataSnapshot1.child("name").getValue().toString());
                            dtime.add(dataSnapshot1.child("timestamp").getValue().toString());
                            documentList.add(dataSnapshot1.getValue(Document.class));
                        }



                    }

                    String uploads [] = new String[dname.size()];
                    String uploadtime [] = new String[dname.size()];


                    for(i=0;i<dname.size();i++)
                    {
                        uploads[i] = dname.get(i);
                        uploadtime[i] = dtime.get(i);

                    }

                    rv = (RecyclerView) findViewById(R.id.doc_recycler_view);
                    rv.setHasFixedSize(true);
                    mLayoutManager = new LinearLayoutManager(DocumentActivity.this);// use a linear layout manager
                    rv.setLayoutManager(mLayoutManager);

                    MyAdapter myAdapter = new MyAdapter(DocumentActivity.this, uploads, imageId, uploadtime);
                    rv.setAdapter(myAdapter);
                    Toast.makeText(DocumentActivity.this, "Refreshing all Documents!", Toast.LENGTH_SHORT).show();
  }
                else
                {
                    Toast.makeText(DocumentActivity.this,"No Documents yet!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



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
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
                break;
        }
    }

    public boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(DocumentActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }




    public void uploadFile(View v) {


        if (!editTextFilename.getText().toString().trim().matches("") &&  !category.matches("none"))
        {
            getPDF();

        }
        else
        {
            Toast.makeText(DocumentActivity.this,"Please enter a name for the file / Please select a category!",Toast.LENGTH_SHORT).show();

        }
    }


    public void displaypopup()
    {

        if (permission.matches("Student") )
        {
            Toast.makeText(DocumentActivity.this,"Students cannot submit documents!",Toast.LENGTH_SHORT).show();
        }
        else {


            try {


                fe.setBackgroundResource(R.drawable.profile_yellow);
                se.setBackgroundResource(R.drawable.profile_yellow);
                te.setBackgroundResource(R.drawable.profile_yellow);
                be.setBackgroundResource(R.drawable.profile_yellow);
                all.setBackgroundResource(R.drawable.profile_yellow);

                fe.setClickable(true);
                se.setClickable(true);
                te.setClickable(true);
                be.setClickable(true);
                all.setClickable(true);
                category = "none";

             //   Toast.makeText(this, "DIsplaypopup", Toast.LENGTH_SHORT).show();
                popupWindow = new PopupWindow(layout, RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT, true);
                popupWindow.showAtLocation(layout, Gravity.CENTER_VERTICAL, 0, 0);

                category = "none";
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    //this function will get the pdf from the storage
    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_CODE);

    }

    @Override
    public void onBackPressed()
    {
          super.onBackPressed();
        documentList.clear();
        dname.clear();
        dtime.clear();
        overridePendingTransition(R.anim.open2,R.anim.close2);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                uploadFile(data.getData());
            } else {
                Toast.makeText(this, "No file chosen yet!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //this method is uploading the file
    //the code is same as the previous tutorial
    //so we are not explaining it
    private void uploadFile(Uri data) {


        Toast.makeText(DocumentActivity.this,"Give Storage Permission from App Info. Ignore if already given.",Toast.LENGTH_SHORT).show();

        progressBar.setVisibility(View.VISIBLE);
        StorageReference sRef = mStorageReference.child("Document/" + System.currentTimeMillis() + ".pdf");
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        textViewStatus.setText("File Uploaded Successfully !!");
                        Long tsLong = System.currentTimeMillis() / 1000;
                        tsLong = tsLong * 1000;
                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                        cal.setTimeInMillis(tsLong);
                        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
                        String newdate[] = date.split("-");
                        String month = "";
                        switch (newdate[1])
                        {
                            case "01":
                                month =  "Jan";break;
                            case "02":
                                month = "Feb" ; break;
                            case "03":
                                month = "Mar" ; break;
                            case "04":
                                month = "Apr" ; break;
                            case "05":
                                month = "May" ; break;
                            case "06":
                                month = "Jun" ; break;
                            case "07":
                                month = "Jul" ; break;
                            case "08":
                                month = "Aug" ; break;
                            case "09":
                                month = "Sep" ; break;
                            case "10":
                                month = "Oct" ; break;
                            case "11":
                                month = "Nov" ; break;
                            case "12":
                                month = "Dec" ; break;
                        }
                        date = date.replace(newdate[1],month);
                        Document dupload = new Document(editTextFilename.getText().toString(), taskSnapshot.getDownloadUrl().toString()
                        ,date , category );
                        mDatabaseReference.child(mDatabaseReference.push().getKey()).setValue(dupload);

                        // refresh activity to avoid multiple gridview items to be shown on the screen
                        popupWindow.dismiss();
                        finish();
                        startActivity(starterIntent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        textViewStatus.setText((int) progress + " % Uploading...");
                    }
                });

    }

    int[] imageId = {
            R.drawable.mypdf};



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



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        Context mContext;
        public String[] web;
        public int[] Imageid;
        public String[] webTime;

                                class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener// class inside class---subclass
                                {
                                    ImageButton itemImage;
                                    TextView itemTitle;
                                    TextView itemTimestamp;

                                    private Context context;


                                    public ViewHolder(View view) // constrcutor of inner class
                                    {
                                        super(view);
                                        itemImage = (ImageButton) view.findViewById(R.id.item_thumbnail);
                                        itemTitle = (TextView) view.findViewById(R.id.item_title);
                                        itemTimestamp = (TextView) view.findViewById(R.id.item_timestamp);
                                        context = view.getContext();
                                        itemImage.setOnClickListener(this); // instead set it on a different object


                                    }

                                    @Override
                                    public void onClick(View v)
                                    {

                                        //   Toast.makeText(DocumentActivity.this,"HUURRAAYYYY",Toast.LENGTH_SHORT).show();
                                        //Document upload = uploadList.get(position);

                                        int p = getLayoutPosition(); // gets item position

                                        Document upload = documentList.get(p);

                                        //Opening the upload file in browser using the upload url
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(upload.getUrl()));
                                        startActivity(intent);
                                    }


                                }

        // THESE CONSTRUCTORS HELP TO HAVE ALL DATA REQUIRED FOR QUESTIONS AND ANSWERS
        // THEY HAVE TO BE USED FOR MAPPING OR SENT TO ANOTHER ACTIVITY

        public MyAdapter(Context c,String[] web,int[] Imageid , String[] webTime )
        {
            mContext = c;
            this.Imageid = Imageid;
            this.web = web;
            this.webTime = webTime;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_pdf, viewGroup, false);
            MyAdapter.ViewHolder viewHolder = new MyAdapter.ViewHolder(v);


            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {


           // viewHolder.itemTitle.setText(listQuestions.get(i));

            // random function logic to change colours
            int colourArr[] = {R.color.newblue , R.color.newgreen , R.color.newred , R.color.newyellow };
            Random x = new Random();
            int y = x.nextInt(4); // 4 is exclusive, so generate between 0 to 3


            viewHolder.itemTitle.setText(web[i]);
            viewHolder.itemTimestamp.setText(webTime[i]);
            viewHolder.itemImage.setBackgroundColor(getResources().getColor(colourArr[y]));

            viewHolder.itemTitle.setBackgroundColor(getResources().getColor(colourArr[y]));
            viewHolder.itemTimestamp.setBackgroundColor(getResources().getColor(colourArr[y]));
        }

        @Override
        public int getItemCount() {

            return web.length;
        }


    }




}

