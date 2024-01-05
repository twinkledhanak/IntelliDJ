package com.tkd.twinkledhanak.intellidj.activities;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tkd.twinkledhanak.intellidj.R;
import com.tkd.twinkledhanak.intellidj.objects.Profile;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class ProfileSetUp extends AppCompatActivity {

    EditText n , e ,t , y;
    String d = "";
    String uname,uemail,type,dept,year , currentuser ,   uploadId ,   date , validYear ="" ;
    ImageButton picture;
    Profile upload;
    String imageurl ;
    TextView  text_year , text_type , title_year ;
    TextView text_dept ;
    EditText text_sapid;
    Button submitProfile;
    View view_year;
    int s1, s2, s3, s4 , student = 0 , hpc = 0;
    private boolean isConnected = false;
    //uri to store file
    private Uri filePath;

    //firebase objects
    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    Intent inn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_set_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        getSupportActionBar().setDisplayShowTitleEnabled(false); // do not show title of action bar

        // change the colour
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(getResources().getColor(R.color.newblue)));

        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.titleview, null);
        ((TextView)v.findViewById(R.id.title)).setText("Edit Profile");
        getSupportActionBar().setCustomView(v);

        year = "None";
       text_dept = (TextView) findViewById(R.id.text_dept);
        text_year = (TextView) findViewById(R.id.text_year);
        text_type = (TextView) findViewById(R.id.text_type);
        title_year = (TextView) findViewById(R.id.title_year);
        submitProfile = (Button) findViewById(R.id.submitProfile);
       // view_year = (View) findViewById(R.id.view_year);
        text_sapid = (EditText) findViewById(R.id.text_sapid) ;

        text_year.setVisibility(View.GONE);
        title_year.setVisibility(View.GONE);
//        view_year.setVisibility(View.GONE);

        picture = (ImageButton)findViewById(R.id.imageview_id_picture);
        imageurl = "none";

        inn = getIntent();

        if(inn.getFlags()==2) {
            uname = inn.getStringExtra("uname");
            uemail = inn.getStringExtra("uemail");
            dept = inn.getStringExtra("dept");
            type = inn.getStringExtra("type");
            year = inn.getStringExtra("year");
            currentuser = inn.getStringExtra("currentuser");
            setTempProfile(uname, uemail, dept, type,year);
        }
        else
        {
            uname = inn.getStringExtra("uname");
            uemail = inn.getStringExtra("uemail");
            currentuser = inn.getStringExtra("currentuser");
            setTempProfile(uname, uemail, "","","");
        }

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference("User").child(currentuser);

        Toast.makeText(ProfileSetUp.this,"Please fill all details carefully!",Toast.LENGTH_SHORT).show();



    }
    @Override
    public void onBackPressed() {

        Toast.makeText(this,"You cannot leave this page without updating profile!",Toast.LENGTH_SHORT).show();
    }

    public void setTempProfile(String s1, String s2, String s3, String s4, String s5)// name ,email , dept , type , year
    {
        n = (EditText)findViewById(R.id.uname);
        e = (EditText)findViewById(R.id.uemail);


        e.setEnabled(false);

        n.setText(s1);
        e.setText(s2);
        if (s4.matches("") || s4.matches("none") || s4.matches("None") || s4.matches("NONE") || s4.matches("-Type Of User-"))
        {

        }
        else // if some type exists, do not let it change ,
        {
            text_type.setText(s4);
            text_type.setClickable(false);
        }

        if(s4.matches("Student")) // specific case, set visibility as true
        {
            title_year.setVisibility(View.VISIBLE);
            text_year.setVisibility(View.VISIBLE);
           // view_year.setVisibility(View.VISIBLE);
        }



    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            default:
                return super.onOptionsItemSelected(item);
        }
    }





    public void displaydept(View v)
    {
        // display a list of options
        final CharSequence colors[] = new CharSequence[]{"Computer"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select your department");
        builder.setItems(colors, new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
            //    Toast.makeText(ProfileSetUp.this,"position "+which,Toast.LENGTH_SHORT).show();
                d = (String) colors[which];
                text_dept.setText(colors[which]);

            }
        });
        builder.show();
    }

    public void displaytype(View v)
    {
        final CharSequence usertype[] = new CharSequence[]{"Student","HOD","Professor","Class-in-charge"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select your user type");
        builder.setItems(usertype, new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
              //  Toast.makeText(ProfileSetUp.this,"position "+which,Toast.LENGTH_SHORT).show();
                type = (String) usertype[which];
                text_type.setText(usertype[which]);

                if (type.matches("Student")) // i type is student , only then show the field for year / position
                {
                    title_year.setVisibility(View.VISIBLE);
                    text_year.setVisibility(View.VISIBLE);
//                    view_year.setVisibility(View.VISIBLE);
                }
                else // if they select any other type after selecting student, dont give them option to enter year
                {
                    title_year.setVisibility(View.INVISIBLE);
                    text_year.setVisibility(View.INVISIBLE);
//                    view_year.setVisibility(View.INVISIBLE);

                }



            }
        });
        builder.show();

    }


    public void  displayyear(View v)
    {
        final CharSequence yyear[] = new CharSequence[]{"First Year (FE)","Second Year (SE)","Third Year (TE)", "Final Year (BE)"};
        final String[]  yr = new String[]{"fe","se","te","be"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select year");
        builder.setItems(yyear, new DialogInterface.OnClickListener()
        {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //
              //  Toast.makeText(ProfileSetUp.this,"position "+which,Toast.LENGTH_SHORT).show();
                 year =  yr[which];
                text_year.setText(yyear[which]);

            }
        });
        builder.show();
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

    public void rollback(View v)
    {

        int result = 0;
            if(isNetworkAvailable(ProfileSetUp.this) == 1) // internet connection
            {

                // first validate details for sapid
                try
                { result =  validateSap(text_sapid.getText().toString().trim());


                 if(result == 1)  // valid usapid
                 {

                                  //   Toast.makeText(ProfileSetUp.this,"Correct sapid",Toast.LENGTH_SHORT).show();
                                     // from here only we insert user details
                                     n = (EditText) findViewById(R.id.uname);
                                     e = (EditText) findViewById(R.id.uemail);
                                     //   y = (EditText)findViewById(R.id.year);
                                     text_type = (TextView) findViewById(R.id.text_type);
                                     //   yptext = (EditText)findViewById(R.id.yptext);


                                     // first validate sapid of user


                                     if (d.matches("") || type.matches("") || text_sapid.getText().toString().matches("")
                                             )  // assume no type selected , image not selected
                                     {
                                         AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileSetUp.this);
                                         alertDialogBuilder.setMessage("Please fill all * marked fields!!");
                                         alertDialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialog, int which) {
                                                 dialog.dismiss();
                                             }
                                         });
                                         AlertDialog alertDialog = alertDialogBuilder.create();


                                         alertDialog.show();


                                     } else if (type.matches("Student") && year.matches(""))   // assume type is student but year is not selected
                                     {
                                         AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileSetUp.this);
                                         alertDialogBuilder.setMessage("Please fill all * marked fields!!");
                                         alertDialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                             @Override
                                             public void onClick(DialogInterface dialog, int which) {
                                                 dialog.dismiss();
                                             }
                                         });
                                         AlertDialog alertDialog = alertDialogBuilder.create();


                                         alertDialog.show();

                                     } else
                                     {
                                         if(hpc == 1 && type.matches("Student"))
                                         {
                                             Toast.makeText(ProfileSetUp.this,"Please check your user type!",Toast.LENGTH_SHORT).show();
                                         }
                                         if (student == 1 && !type.matches("Student"))
                                         {
                                             Toast.makeText(ProfileSetUp.this,"Please check your user type!",Toast.LENGTH_SHORT).show();
                                         }

                                         if (hpc == 1 && !type.matches("Student"))
                                                 {


                                                     Intent in = new Intent(ProfileSetUp.this, IntelliDj.class);
                                                     in.putExtra("uname", n.getText().toString());
                                                     in.putExtra("uemail", e.getText().toString());
                                                     in.putExtra("dept", d);
                                                     in.putExtra("type", type);
                                                     in.putExtra("year", "none"); // year has to be sent as null

                                                     // IF I OBTAIN A UPLOAD URL, THAT MEANS USER HAS PUT AN IMAGE
                                                     if (!imageurl.matches("none")) {
                                                         in.putExtra("image", upload.getUrl());
                                                         in.setFlags(1);


                                                         text_type.setClickable(true);
                                                         text_year.setClickable(true);
                                                         submitProfile.setBackgroundResource(R.drawable.profile_answer);
                                                         submitProfile.setBackgroundResource(R.drawable.profile_answer);

                                                         Toast.makeText(this, "Your Profile has been updated!", Toast.LENGTH_SHORT).show();
                                                         startActivity(in);
                                                     } else {
                                                         Toast.makeText(ProfileSetUp.this, "Please select a profile picture!", Toast.LENGTH_SHORT).show();
                                                     }
                                                 }
                                         if ( student == 1  && type.matches("Student"))
                                         {
                                             // type is properly selected, i e , student ,  but year is wrong
                                               if ( year.matches(validYear) ) {
                                                   Intent in = new Intent(ProfileSetUp.this, IntelliDj.class);
                                                   in.putExtra("uname", n.getText().toString());
                                                   in.putExtra("uemail", e.getText().toString());
                                                   in.putExtra("dept", d);
                                                   in.putExtra("type", type);
                                                   in.putExtra("year", year);
                                                   in.putExtra("sapid",text_sapid.getText().toString().trim());

                                                   // IF I OBTAIN A UPLOAD URL, THAT MEANS USER HAS PUT AN IMAGE
                                                   if (!imageurl.matches("none")) {
                                                       in.putExtra("image", upload.getUrl());
                                                       in.setFlags(1);


                                                       text_type.setClickable(true);
                                                       text_year.setClickable(true);
                                                       submitProfile.setBackgroundResource(R.drawable.profile_answer);
                                                       submitProfile.setBackgroundResource(R.drawable.profile_answer);

                                                       submitProfile.setBackgroundResource(R.drawable.profile_answer);
                                                       Toast.makeText(this, "Your Profile has been updated!", Toast.LENGTH_SHORT).show();
                                                       startActivity(in);
                                                   } else {
                                                       Toast.makeText(ProfileSetUp.this, "Please select a profile picture!", Toast.LENGTH_SHORT).show();
                                                   }
                                               }
                                             else
                                               {
                                                   Toast.makeText(ProfileSetUp.this,"Please select a valid year!",Toast.LENGTH_SHORT).show();
                                               }
                                         }

                                     }
                 }
                else
                 {
                     Toast.makeText(ProfileSetUp.this,"Please enter valid Sap-ID!",Toast.LENGTH_SHORT).show();
                 }
                }
                catch (Exception e)
                {
                   // Toast.makeText(ProfileSetUp.this,"Please enter only numbers",Toast.LENGTH_SHORT).show();


                }
            }
        else
            {
                Toast.makeText(ProfileSetUp.this,"Please connect to internet!",Toast.LENGTH_SHORT).show();
            }
    }


    public int validateSap(String sapid)
    {
        int valid = 0;
        student = 0; hpc = 0;
        validYear = ""; // so that prev value is erased
         s1 = 0; s2 = 0; s3 = 0; s4 = 0;

        // 1. validate sap first
        if(sapid.length() == 0)
        {
            Toast.makeText(ProfileSetUp.this,"Please enter your Sap-ID!",Toast.LENGTH_SHORT).show();
        }
        if(sapid.length() == 11)
        {
            // student
            String vd = sapid.substring(0,5);
            int vy = Integer.parseInt(sapid.substring(5,7)); // 14, 15 etc
            int nd = Integer.parseInt(sapid.charAt(7) + ""); // 0 or 8
            int roll = Integer.parseInt(sapid.substring(8,sapid.length())); // last 3 digits or roll no
           // calculate his year
            //1. get the current timestamp
            Long tsLong = System.currentTimeMillis() / 1000;
            tsLong = tsLong * 1000;
            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
            cal.setTimeInMillis(tsLong);
            date = DateFormat.format("dd-MM-yyyy", cal).toString();
            String newdate[] = date.split("-"); // newdate[1] has the month number , newdate[2] has year
            int currentMonth = Integer.parseInt(newdate[1]); // 01, 02 etc indicate octal, so , use 1 , 2 while comparing
            int currentYear = Integer.parseInt(newdate[2].substring(2,newdate[2].length())); // 18 , 19 whatever is current

            // user's sign-in doesnt matter, always depends on current month while profile is being edited

            // first sort diploma and normal students
            if(nd == 0 )
            {
                if (roll >= 0 && roll <= 130) {
                    s3 = 1;
                }
            }
            else if (nd == 8)
            {
                if (roll >= 0 && roll <= 30) {
                    s3 = 1;
                }
            }


            if (currentYear >= vy ) {

                int diff = currentYear - vy;
                if (diff == 0) // odd sem 1
                {
                    if(nd == 0) // normal student
                    {

                        if (currentMonth >= 1 && currentMonth <= 5) // nothing in even sem
                        {
                            validYear = "";
                        }
                        if (currentMonth >= 6 && currentMonth <= 12) // only in odd sem
                        {
                            validYear = "fe";
                        }
                    }
                    else
                    {
                        // diploma student

                        if (currentMonth >= 1 && currentMonth <= 5) // nothing in even sem
                        {
                            validYear = "";
                        }
                        if (currentMonth >= 6 && currentMonth <= 12) // only in odd sem
                        {
                            validYear = "se";
                        }

                    }
                }

                if (diff == 1)
                {
                    if(nd == 0)  // normal student
                    {
                        if (currentMonth >= 1 && currentMonth <= 5)  // even sem
                        {
                            validYear = "fe";
                        }

                        if (currentMonth >= 6 && currentMonth <= 12)// odd sem
                        {
                            validYear = "se";
                        }
                    }
                    else
                    {
                        if (currentMonth >= 1 && currentMonth <= 5)  // even sem
                        {
                           validYear = "se";
                        }

                        if (currentMonth >= 6 && currentMonth <= 12)// odd sem
                        {
                           validYear = "te";
                        }

                    }


                }

                if (diff == 2)
                {
                    if(nd == 0) {
                        if (currentMonth >= 1 && currentMonth <= 5) {
                            validYear = "se";
                        }

                        if (currentMonth >= 6 && currentMonth <= 12) {
                            validYear = "te";
                        }
                    }
                    else
                    {
                        if (currentMonth >= 1 && currentMonth <= 5) {
                            validYear = "te";
                        }

                        if (currentMonth >= 6 && currentMonth <= 12) {
                            validYear = "be";
                        }
                    }
                }

                if (diff == 3)
                {
                    if(nd == 0) {
                        if (currentMonth >= 1 && currentMonth <= 5) {
                            validYear = "te";
                        }

                        if (currentMonth >= 6 && currentMonth <= 12) {
                            validYear = "be";
                        }
                    }
                    else
                    {
                        if (currentMonth >= 1 && currentMonth <= 5) {
                            validYear = "be";
                        }

                        if (currentMonth >= 6 && currentMonth <= 12) {
                            validYear = "";
                            //obsolete user
                            Toast.makeText(ProfileSetUp.this, "Sorry Obselete user!", Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                if (diff == 4)
                {
                    if(nd == 0) {
                        if (currentMonth >= 1 && currentMonth <= 5) {
                            validYear = "be";
                        }

                        if (currentMonth >= 6 && currentMonth <= 12) {
                            validYear = "";
                            //obsolete user
                            Toast.makeText(ProfileSetUp.this, "Sorry Obselete user!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        validYear = "";
                        //obsolete user
                        Toast.makeText(ProfileSetUp.this, "Sorry Obselete user!", Toast.LENGTH_SHORT).show();

                    }
                }
                if (diff >= 5 || diff < 0) { // handling extreme future year and ancient year, valid years have diff of max 4 years
                    //obsolete user
                   // for both type of users
                    validYear = "";
                    Toast.makeText(ProfileSetUp.this, "Sorry Obselete user!", Toast.LENGTH_SHORT).show();
                }
    }




            if(vd.matches("60004")
                    )
            {
                // valid department
                s1 = 1;
            }
            if(validYear.matches("fe") || validYear.matches("se") || validYear.matches("te") || validYear.matches("be"))
            // year is not empty, ie , we have identified something for it
           // it shows that year part in sapid is valid
            {
                s2 = 1;
            }



            if(s1 == 1  && s2 == 1 && s3 ==1)
            {
                // yes valid student
                valid = 1;
                student = 1;
            }
            else
            {
                valid = 0;
            }




        }


        if(sapid.length() == 8)
        {
            // hod, prof , cic
            String faculty = sapid.substring(0,5);
            int rollno = Integer.parseInt(sapid.substring(5,sapid.length()));

            if(faculty.matches("19210") )
            {
                s1 = 1;
            }
            if(rollno >= 0 && rollno<=300)
            {
                s2 = 1;
            }

            if(s1 == 1 && s2==1)
            {
                valid = 1;
                hpc = 1;
            }
            else
            {
                valid = 0;
            }

        }



        return valid;

    }

    public void showfile(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE); // newly added line
        intent.setType("image/*"); // VALIDATION TO SELECT ONLY IMAGES AND NOT OTHER MEDIA FORMS
        startActivityForResult(intent, 2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                picture.setImageBitmap(bitmap);
                // automatically upload file to storage and later to database
                uploadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference
            StorageReference sRef = storageReference.child("Profile/" + System.currentTimeMillis() + "." + getFileExtension(filePath));

            //adding the file to reference
            sRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded! ", Toast.LENGTH_LONG).show();

                            //creating the upload object to store uploaded image details
                           // Profile upload = new Profile(editTextName.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString());
                             upload = new Profile(taskSnapshot.getDownloadUrl().toString());


                            //adding an upload to firebase database
                           uploadId = mDatabase.push().getKey();
                            //mDatabase.child(uploadId).setValue(upload);
                            mDatabase.child("Image").setValue(upload.getUrl());
                            imageurl = upload.getUrl();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }
    }


}
