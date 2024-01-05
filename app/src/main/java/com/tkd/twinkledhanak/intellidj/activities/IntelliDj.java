package com.tkd.twinkledhanak.intellidj.activities;


import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.ToxicBakery.viewpager.transforms.BackgroundToForegroundTransformer;
import com.bumptech.glide.Glide;
import com.tkd.twinkledhanak.intellidj.notifications.MyIntentService;
import com.tkd.twinkledhanak.intellidj.fragments.BookmarkFragment;
import com.tkd.twinkledhanak.intellidj.fragments.QuestionFragment;
import com.tkd.twinkledhanak.intellidj.R;
import com.tkd.twinkledhanak.intellidj.db.LocalDatabaseHelper;
import com.tkd.twinkledhanak.intellidj.objects.Answer;
import com.tkd.twinkledhanak.intellidj.objects.Question;
import com.tkd.twinkledhanak.intellidj.objects.User;
import com.firebase.ui.auth.AuthUI;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.victor.loading.book.BookLoading;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.ExecutionException;


public  class IntelliDj extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Intent inn , starterIntent, intn ;
    String date;
    TextView welcomename, welcomeemail, NoQYet;
    ImageView welcomeimage ;
    String permisssion = "" , CurrentID = "" , sx , flagCategory = "" , userdetails , s = "" , sy = "" ,category  , q = "", year = "", sapid = "";
    ViewPagerAdapter adapter;
    boolean questionpressed = true; // ASSUMING THAT FLOATING BUTTON IS PRESSED, TO ASK A QUESTION
    NavigationView navigationView;
    private String mUsername;
    private static final int RC_SIGN_IN = 1;
    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 5000;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mQuestionDatabaseReference;
    private DatabaseReference tempDatabaseReference; // to hold temporary ref to either question or answer
    private DatabaseReference userDatabaseReference;

    private DatabaseReference mCareerReference;
    private DatabaseReference mExamReference;
    private DatabaseReference mGeneralReference;

    private ValueEventListener mValueEventListener;

    private FirebaseAuth mFirebaseAuth; // this is used to attach the auth state listener
    private FirebaseAuth.AuthStateListener mAuthStateListener; // this is actual auth state listener
    FirebaseUser user;
    private DatabaseReference mQAReference;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference PhotosStorageReference;


    private LocalDatabaseHelper mDBHelper;  // KEEP ONLY ONE COPY OF THIS VARIABLE OF OPENHELPER
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    long last_user_id = 0, nextQId = 0;

    User u_db, u_c, ProfileUser, newUser;
    Question question_db, question_c;
    Answer answer_db, answer_c;

    Vector<String>  vuname, vuemail, vucloudid , vuimg , vusapid , vqmap, vamap , v1,  vq1, vq2, vq3, vq4, vq5 , vq6 , vq7 , vu;
    ArrayList<String> listBookmark, BN, BQ, BID, BTS , BCT , BUTYPE;

    ViewPager viewPager;
    TabLayout tabLayout;
    EditText editText;
    LayoutInflater inflater;
    View layout;
    PopupWindow popupWindow;
    View headerView;
    ImageButton submit, cancel_content;
    String arry[];
    boolean exists = false;
    private boolean isConnected = false;
    FloatingActionButton fab;
    TapTargetSequence tap;

    Intent mServiceIntent , mServiceIntent2;
    private MyIntentService mMyService;
    private MyIntentService mMyService2;
    Context ctx;
    Button career , exam, general;


    public Context getCtx() {
        return ctx;
    }

    public IntelliDj() {
        // default constructor
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }

/// minimum api requirement made as kitkat

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intelli_dj);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        starterIntent = getIntent();

       fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaypopup();
            }
        }); // ACTION IS GIVEN FOR FLOATING BUTTON

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //  drawer.setDrawerListener(toggle);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDBHelper = new LocalDatabaseHelper(this.getApplicationContext());

  //-------------------------------------------------------------------------------------------------------------------
        // cancel notifications from tray
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(001);


 //-----------------------------------------------------------------------------------------------------------------------

        GetUserDetails getUserDetails = new GetUserDetails();
        String s = null;
        try {
            s = getUserDetails.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String arry[] = s.split("#");
        if (arry.length == 1)
        {
                // new user

            tap = new TapTargetSequence(this);
            tap.targets(
                    TapTarget.forView(fab, "Ask Questions!", "Later tap on question to view its answers!")
                            .outerCircleColor(R.color.GREEN)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                            .targetCircleColor(R.color.WHITE)   // Specify a color for the target circle
                            .titleTextSize(18)                  // Specify the size (in sp) of the title text
                            .titleTextColor(R.color.BLACK)      // Specify the color of the title text
                            .descriptionTextSize(15) // Specify the size (in sp) of the description text
                            .descriptionTextColor(R.color.GREY)  // Specify the color of the description text
                            .textColor(R.color.BLACK)            // Specify a color for both the title and description text
                            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                            .dimColor(R.color.BLACK)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(true)                   // Whether to tint the target view's color
                            .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                            .icon(getResources().getDrawable(R.mipmap.ic_askq))                     // Specify a custom drawable to draw as the target
                            .targetRadius(60)                // Specify the target radius (in dp)
                    ,
                    TapTarget.forToolbarOverflow(toolbar, "Refresh after asking questions!")
                            .outerCircleColor(R.color.GREEN)      // Specify a color for the outer circle
                            .outerCircleAlpha(0.96f)
                            .dimColor(R.color.BLACK)
                            .outerCircleColor(R.color.GREEN)
                            .outerCircleAlpha(0.96f)
                            .targetCircleColor(R.color.WHITE)
                            .titleTextSize(18)                  // Specify the size (in sp) of the title text
                            .titleTextColor(R.color.BLACK)      // Specify the color of the title text
                            // Specify the size (in sp) of the description text
                            .descriptionTextColor(R.color.GREY)  // Specify the color of the description text
                            // Specify a color for both the title and description text
                            .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                            .dimColor(R.color.BLACK)            // If set, will dim behind the view with 30% opacity of the given color
                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                            .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                            .tintTarget(true)                   // Whether to tint the target view's color
                            .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                            // Specify a custom drawable to draw as the target
                            .targetRadius(60)
                            .textColor(R.color.BLACK)


            )
                    .listener(new TapTargetSequence.Listener() {
                        // This listener will tell us when interesting(tm) events happen in regards
                        // to the sequence
                        @Override
                        public void onSequenceFinish() {
                            // Yay
                            tap.cancel();
                            new InsertUserIntoLocalDB().execute("tutorial");
                        }

                        @Override
                        public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                        }


                        @Override
                        public void onSequenceCanceled(TapTarget lastTarget) {
                            // Boo
                            tap.cancel();
                            new InsertUserIntoLocalDB().execute("tutorial");
                        }
                    }).start();
        }



        NotificationManager mNotifyMgr2 =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr2.cancel(002);


//-------------------------------------------------------------------------------------------------------------------------

        // THIS SECTION IS FOR INITIALIZING ALL THE VARIABLES FOR FIREBASE
        mUsername = ANONYMOUS;
        // now initializing two important variables
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();   // now for authentication, we initialse mFirebaseAuth
        mFirebaseStorage = FirebaseStorage.getInstance();

        mQuestionDatabaseReference = mFirebaseDatabase.getReference().child("Question"); // REF FOR QUESTION NODE
        userDatabaseReference = mFirebaseDatabase.getReference().child("User");
        mCareerReference = mFirebaseDatabase.getReference().child("Question").child("Career");
        mExamReference = mFirebaseDatabase.getReference().child("Question").child("Exam");
        mGeneralReference = mFirebaseDatabase.getReference().child("Question").child("General");

        PhotosStorageReference = mFirebaseStorage.getReference().child("photos");


        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();


//-------------------------------------------------------------------------------------------------------------------------

        // THIS SECTION IS FOR INITIALIZING VARIABLES OF LOCAL DATABASE

        question_db = new Question(); // for local databaase
        answer_db = new Answer();
        answer_c = new Answer();
        v1 = new Vector<String>();
        vuname = new Vector<>();
        vuemail = new Vector<>();
        vucloudid = new Vector<>();
        vusapid = new Vector<>();
        vqmap = new Vector<String>();
        vamap = new Vector<String>();
        vq1 = new Vector<String>();
        vq2 = new Vector<String>();
        vq3 = new Vector<String>();
        vq4 = new Vector<String>();
        vq5 = new Vector<>();
        vq6 = new Vector<>();
        vq7 = new Vector<>();
        vu = new Vector<String>();
        listBookmark = new ArrayList<String>();
        BN = new ArrayList<>();
        BQ = new ArrayList<>();
        BID = new ArrayList<>();
        BTS = new ArrayList<>();
        BCT = new ArrayList<>();
        BUTYPE = new ArrayList<>();
        ProfileUser = new User();
        newUser = new User();

///-----------------------------------------------------------------------------------------------------------------------------

        // THIS IS USED WHEN WE WANT TO ENABLE SEND BUTTON ONLY WHEN SOME TEXT IS TYPED IN EDITTEXT
        // AND WE ARE TAKING REFERENCE OF VARIOUS OBJECTS IN ONCREATE SO THAT NO NEED FOR LATER DOING IT



        inflater = (LayoutInflater) IntelliDj.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layout = inflater.inflate(R.layout.activity_qapanel, (ViewGroup) findViewById(R.id.activity_qapanel));
        editText = (EditText) layout.findViewById(R.id.editText);
        submit = (ImageButton) layout.findViewById(R.id.submit);
        cancel_content = (ImageButton) layout.findViewById(R.id.cancel_content);
        career = (Button) layout.findViewById(R.id.career);
        exam = (Button) layout.findViewById(R.id.exam);
        general = (Button) layout.findViewById(R.id.general);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        NoQYet = (TextView) findViewById(R.id.no_q_yet);
        NoQYet.setVisibility(View.INVISIBLE);


        headerView = navigationView.getHeaderView(0);
        welcomeimage = (ImageView) headerView.findViewById(R.id.imageView);
        ctx = this;
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {

                } else {
                   }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

//-------*************** CODE FOR AUTHENTICATION HAS TO BE INSIDE ONCREATE METHOD ONLY, AS ONRESUME METHOD WILL CALL ONCREATE ONLY
        /// I AM ALSO ADDING CODE FOR AUTHENTICATION HERE, only the listener code is written here, but attach and detach is
        // done in onResume and onPause respectively
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // this method will determine whether user is signed in or signed out
                user = firebaseAuth.getCurrentUser(); // firebaseAuth is the parameter passed, not user defined

                if (user != null) {
                    // SIGNED IN
                    Toast.makeText(IntelliDj.this, "You have successfully signed in!", Toast.LENGTH_SHORT).show();
                    isNetworkAvailable(IntelliDj.this);
                    onSignedInInitialize(user.getDisplayName());

                    // User: class
                    // u : object of same class
                    // user: object of firebase class
                    // CREATING THE USER OBJECT HERE AND PASSING VALUES

                    u_db = new User();

                    // cloud-id gets updated later, insert none for now, TO MAINTAIN THE LENGTH OF STRING RETRIEVED
                    u_db.setCloud_id("none");

                    u_db.setName(mUsername);    // here , insert only these two fields
                    u_db.setEmail(user.getEmail());

                    u_db.setDepartment("none");
                    u_db.setType("none");
                    u_db.setYear("none");

                    u_db.setImage("none");

                    u_db.setSapid("none");

                    welcomename = (TextView) headerView.findViewById(R.id.welcomename);
                    welcomeemail = (TextView) headerView.findViewById(R.id.welcomeemail);
                    welcomename.setText("  "+mUsername);
                    welcomeemail.setText(user.getEmail());


                    // trying to show loading animation
                    BookLoading bk = (BookLoading) findViewById(R.id.bookloading);
                    bk.start();


                    // whenever activity is started, try setting welcome image
                    try {
                    GetUserDetails getUserDetails = new GetUserDetails();
                        String s = getUserDetails.execute().get();
                        String arry[] = s.split("#");

                        Glide.with(IntelliDj.this).load(arry[3]).into(welcomeimage);
                    }
                    catch (Exception e)
                    {
                        welcomeimage.setBackgroundResource(R.mipmap.ic_editprofpic);

                    }

                    CheckIfUserExists checkIfUserExists = new CheckIfUserExists();
                    exists = checkIfUserExists.doInBackground(u_db); // USE COMBINATION OF THESE TWO
                    // TO SEE IF USER ALREADY EXISTS IN LOCALDATABASE
                    // NEVER DELETE USERS FROM THE CLOUD!!!!!!!!!!!!!!!!!


                    if (exists == true) // OLD USER
                    {

                    } else {


                            /// INSERT USER INTO CLOUD
                        //    Toast.makeText(IntelliDj.this,"Inserting user in cloud!!",Toast.LENGTH_SHORT).show();
                            // ASSIGN USER REF TO HIM


                            new InsertUserIntoLocalDB(u_db).execute("u");
                            u_c = new User();
                            u_c.setName(mUsername);
                            u_c.setEmail(user.getEmail());
                            u_c.setDepartment("none");
                            u_c.setType("none");
                            u_c.setYear("none");


                            userDatabaseReference.push().setValue(u_c); // RANDOM ID FOR EACH NODE

                            String random_id = userDatabaseReference.push().getKey();

                    }
                } else {
                    // SIGNED OUT
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setTheme(R.style.FullscreenTheme)
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }

            }
        };

        // DELETE CACHE DIRECTORY DATA TO SAVE SPACE FOR USER
        deleteCache(this);

        // FOR USER PROFILE

       inn = getIntent();
        ProfileUser.setName(inn.getStringExtra("uname"));
        ProfileUser.setEmail(inn.getStringExtra("uemail"));
        ProfileUser.setDepartment(inn.getStringExtra("dept"));
        ProfileUser.setType(inn.getStringExtra("type"));
        ProfileUser.setYear(inn.getStringExtra("year"));
        ProfileUser.setImage(inn.getStringExtra("image"));
        ProfileUser.setSapid(inn.getStringExtra("sapid"));


        try {
            // seeting the picture at navigation drawer
            headerView = navigationView.getHeaderView(0);
            Glide.with(this).load(ProfileUser.getImage()).into(welcomeimage);


        }
        catch (Exception i) { }

        intn = getIntent();
        if(intn.getFlags() == 11)
        {
            // our answer flag
            // write code for refresh
            starterIntent = getIntent();
            finish();
            startActivity(starterIntent);


        }


//  FOR STICKY SERVICE
        mMyService = new MyIntentService(getCtx());
        mServiceIntent = new Intent(getCtx(), mMyService.getClass());
        if (!isMyServiceRunning(mMyService.getClass())) {
            startService(mServiceIntent);
        }

        mMyService2 = new MyIntentService(getCtx());
        mServiceIntent2 = new Intent(getCtx(), mMyService2.getClass());
        if (!isMyServiceRunning(mMyService2.getClass())) {
            startService(mServiceIntent2);
        }


    }

    ///   METHODS OUTSIDE ON CREATE ARE WRITTEN BELOW
    //--------------------------------------------------------------------------------------------------------


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isDirectory()) {
            return dir.delete();
        }
        else if(dir.isFile()) {
            return dir.delete();
        }
        else
        {
            return false;
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
            Snackbar snackbar = Snackbar
                    .make((CoordinatorLayout) findViewById(R.id.cordlayout), "No internet! Please Check your internet connection! ", Snackbar.LENGTH_LONG);

            snackbar.show();


        }
        return flag;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            //super.onBackPressed();
            // call ondestroy for the app
           // onDestroy();
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.intelli_dj, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.menu_refresh) {
            try {
                finish();
                startActivity(starterIntent);

            } catch (Exception e) {

            }
        }



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Long tsLong = System.currentTimeMillis() / 1000;
        tsLong = tsLong * 1000;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(tsLong);
        date = DateFormat.format("dd-MM-yyyy", cal).toString();
        String newdate[] = date.split("-"); // newdate[1] has the month number , newdate[2] has year
        int currentMonth = Integer.parseInt(newdate[1]); // 01, 02 etc indicate octal, so , use 1 , 2 while comparing
        int currentYear = Integer.parseInt(newdate[2].substring(2,newdate[2].length())); // 18 , 19 whatever is current


        GetUserDetails getUserDetails1 = new GetUserDetails();

            try {
                sy = getUserDetails1.execute().get();

                String arr[] = sy.split("#");
                year = arr[2];
                sapid = arr[5];
                if (arr.length == 1) {
                    permisssion = arr[0];

                }
                else
                {
                    permisssion = arr[1];
                  }
            } catch (InterruptedException e1) {
                permisssion = "";
            } catch (ExecutionException e1) {
                permisssion = "";
            }




        if (id == R.id.nav_profile)
        {
                   Intent in = new Intent(IntelliDj.this, ProfileActivity.class);
                  // 2.  UPDATE WITH DETAILS
                  try{
                            // try sending updated values from cloud

                                in.putExtra("uname", mUsername);
                                in.putExtra("uemail", user.getEmail());
                                GetUserDetails getUserDetails = new GetUserDetails();

                                 sx = getUserDetails.execute().get();
                                String array[] = sx.split("#");
                                in.putExtra("dept", array[0]);
                                in.putExtra("type", array[1]);
                                in.putExtra("year", array[2]);
                                in.putExtra("image",array[3]); // has path to image
                                in.putExtra("currentuser",array[4]); // current user -id
                                in.setFlags(1);

                    }
                  catch (Exception e)
                  {
                                e.printStackTrace();
                                    // send default values

                                    GetUserDetails getUserDetails = new GetUserDetails();

                                 try {
                                     String sy = getUserDetails.execute().get();
                                      String arr[] = sy.split("#");
                                     in.putExtra("currentuser",arr[4]);

                                 } catch (InterruptedException e1) {
                                     e1.printStackTrace();
                                 } catch (ExecutionException e1) {
                                     e1.printStackTrace();
                                 }
                                 in.putExtra("uname", mUsername);
                                 in.putExtra("uemail", user.getEmail());


                    }
                   startActivity(in);

            }



        else if(id == R.id.document)
        {


            if (permisssion.matches("") || permisssion.matches("none") || permisssion.matches("None"))
            {
                Toast.makeText(IntelliDj.this,"Please update your profile first!",Toast.LENGTH_SHORT).show();
            }

            else if (permisssion.matches("Student") && year.matches("be"))
            {
                // calculate diff here, we are sure, sapid here is updated and not null
                int vy = Integer.parseInt(sapid.substring(5,7)); // 14, 15 etc
                int diff = currentYear - vy;

                if (diff == 4 && currentMonth >=6)
                {
                    // obsolete user
                    Toast.makeText(IntelliDj.this,"You are an obsolete user now!",Toast.LENGTH_SHORT).show();
                }
                else
                {

                   // Toast.makeText(this,"Clicked Docs",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(IntelliDj.this, DocumentActivity.class);
                    startActivity(intent);

                }

            }

            else
            {

               // Toast.makeText(this,"Clicked Docs",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(IntelliDj.this, DocumentActivity.class);
                startActivity(intent);
            }

        }
        else if (id == R.id.nav_notice)
        {
            if (permisssion.matches("") || permisssion.matches("none") || permisssion.matches("None"))
            {
                Toast.makeText(IntelliDj.this,"Please update your profile first!",Toast.LENGTH_SHORT).show();
            }

            else if (permisssion.matches("Student") && year.matches("be"))
            {
                // calculate diff here, we are sure, sapid here is updated and not null
                int vy = Integer.parseInt(sapid.substring(5,7)); // 14, 15 etc
                int diff = currentYear - vy;

                if (diff == 4 && currentMonth >=6)
                {
                    // obsolete user
                    Toast.makeText(IntelliDj.this,"You are an obsolete user now!",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    Intent in = new Intent(IntelliDj.this, AnnouncementActivity.class);
                    in.putExtra("uname", mUsername);
                    startActivity(in);
                }

            }

            else {
                Intent in = new Intent(IntelliDj.this, AnnouncementActivity.class);
                in.putExtra("uname", mUsername);
                startActivity(in);
            }


        }

        else if (id == R.id.feedback)
        {
            //Intent in = new Intent(IntelliDj.this,Feedback.class);
            //startActivity(in);

            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "developer21296@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear ..." + "");
//emailIntent.putExtra(Intent.EXTRA_HTML_TEXT, body); //If you are using HTML in your body text

            startActivity(Intent.createChooser(emailIntent, "Send Feedback"));


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onActivityResult(int requestcode, int resultcode, Intent data) {
        super.onActivityResult(requestcode, resultcode, data);
      if(requestcode == RC_SIGN_IN)
        {
            if(resultcode == RESULT_OK)
            {
               // Toast.makeText(this,"You have successfully signed in",Toast.LENGTH_SHORT).show();

            }
            else if(resultcode == RESULT_CANCELED)
            {
                Toast.makeText(this,"Not Signed in.Please try again!",Toast.LENGTH_SHORT).show();
            }
        }
    }


    //---------------------------------------------------------------------------------------------------------
    // BELOW ARE ALL USER DEFINED METHODS AS PER THE UI

    // FOR TABBED ACTIVITY
    private void setupViewPager(ViewPager viewPager) throws ExecutionException, InterruptedException {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());


        // reason to pass vuname and vucloudid:
        // 1. we need to extract user details in QAactivity
        // 2. we cannot call cloud methods from there
        // 3. we have to pass it: IntelliDj -- Q Fragment -- QA activity

        adapter.addFragment(new QuestionFragment().newInstance(vq2, vq3, vq1, vq5,vq6,vucloudid,vuimg , vq7), "Questions"); // vq5 : timestamp, vq6: category
        try {
            adapter.notifyDataSetChanged();
        } catch (Exception e) {

        }

        adapter.addFragment(new BookmarkFragment().newInstance(BN, BQ, BID, BTS,BCT,BUTYPE), "Bookmarks");

        try {
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
        }

        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(true, new BackgroundToForegroundTransformer());
        // trying to show loading animation
        BookLoading bk = (BookLoading) findViewById(R.id.bookloading);
        bk.stop();
        bk.removeAllViews();



    }


    // FOR TABBED ACTIVITY
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            try{
                super.finishUpdate(container);
            } catch (NullPointerException nullPointerException){
                System.out.println("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    public void displaypopup() // to display pop-up when we want to type a queation // LATER MODIFY TO ANSWER***********?????????
    {

        Long tsLong = System.currentTimeMillis() / 1000;
        tsLong = tsLong * 1000;
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(tsLong);
        date = DateFormat.format("dd-MM-yyyy", cal).toString();
        String newdate[] = date.split("-"); // newdate[1] has the month number , newdate[2] has year
        int currentMonth = Integer.parseInt(newdate[1]); // 01, 02 etc indicate octal, so , use 1 , 2 while comparing
        int currentYear = Integer.parseInt(newdate[2].substring(2,newdate[2].length())); // 18 , 19 whatever is current


        GetUserDetails getUserDetails2 = new GetUserDetails();

        try {
            sy = getUserDetails2.execute().get();

            String arr[] = sy.split("#");
            if (arr.length == 1) {
                permisssion = arr[0];

            }
            else
            {
                permisssion = arr[1];

                year = arr[2];
                sapid = arr[5];
            }
        } catch (InterruptedException e1) {
            permisssion = "";
        } catch (ExecutionException e1) {
            permisssion = "";
        }




        if (permisssion.matches("") || permisssion.matches("none") || permisssion.matches("None"))
        {
            Toast.makeText(IntelliDj.this,"Please update your profile first!",Toast.LENGTH_SHORT).show();
        }
        else if (permisssion.matches("Student") && year.matches("be"))
        {
            // calculate diff here, we are sure, sapid here is updated and not null
            int vy = Integer.parseInt(sapid.substring(5,7)); // 14, 15 etc
            int diff = currentYear - vy;

            if (diff == 4 && currentMonth >=6)
            {
                // obsolete user
                Toast.makeText(IntelliDj.this,"You are an obsolete user now!",Toast.LENGTH_SHORT).show();
            }
            else
            {
                try {

                    career.setBackgroundResource(R.drawable.profile_question);
                    exam.setBackgroundResource(R.drawable.profile_question);
                    general.setBackgroundResource(R.drawable.profile_question);
                    career.setClickable(true);
                    exam.setClickable(true);
                    general.setClickable(true);
                    popupWindow = new PopupWindow(layout, RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT, true);
                    popupWindow.showAtLocation(layout, Gravity.CENTER_VERTICAL, 0, 0);
                    // hide the layout for notice
                    RelativeLayout relay = (RelativeLayout) layout.findViewById(R.id.hidenotice);
                    relay.setVisibility(View.GONE);
                    TextView textView = (TextView) layout.findViewById(R.id.item_name);
                    textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_question));
                    userdetails = "";

                    GetUserDetails getUserDetails = new GetUserDetails(); // GETTING TYPE FROM DATABASE
                    s = getUserDetails.execute().get();
                    arry = new String[5];
                    arry = s.split("#");
                    //  on splitting , at index 1 , ie , 2nd value returned is type
                    // SETTING BOTH USERNAME AND TYPE OF USER IF AVAILABLE

                    if (arry[1].matches("Student")) {
                        userdetails = mUsername + ", Student";

                    } else if (arry[1].matches("Professor")) {
                        userdetails = mUsername + ", Professor";
                    } else if (arry[1].matches("HOD")) {
                        userdetails = mUsername + ", HOD";
                    } else if (arry[1].matches("Class-in-charge")) {
                        userdetails = mUsername + ", Class-in-Charge";
                    } else {
                        userdetails = mUsername;
                    }
                    textView.setText("  "+userdetails);



                        ImageView imagev = (ImageView) layout.findViewById(R.id.image_border);
                        imagev.setBackgroundDrawable(getResources().getDrawable(R.drawable.profpic_question));

                        ImageView imageView = (ImageView) layout.findViewById(R.id.item_image);
                        imageView.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_pblue));




                    editText.setText("");
                    category = "none";

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }


        else {
            try {

                career.setBackgroundResource(R.drawable.profile_question);
                exam.setBackgroundResource(R.drawable.profile_question);
                general.setBackgroundResource(R.drawable.profile_question);
                career.setClickable(true);
                exam.setClickable(true);
                general.setClickable(true);
                popupWindow = new PopupWindow(layout, RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT, true);
                popupWindow.showAtLocation(layout, Gravity.CENTER_VERTICAL, 0, 0);
                // hide the layout for notice
                RelativeLayout relay = (RelativeLayout) layout.findViewById(R.id.hidenotice);
                relay.setVisibility(View.GONE);
                TextView textView = (TextView) layout.findViewById(R.id.item_name);
                textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.profile_question));
                userdetails = "";

                GetUserDetails getUserDetails = new GetUserDetails(); // GETTING TYPE FROM DATABASE
                s = getUserDetails.execute().get();
                arry = new String[5];
                arry = s.split("#");
                //  on splitting , at index 1 , ie , 2nd value returned is type
                // SETTING BOTH USERNAME AND TYPE OF USER IF AVAILABLE

                if (arry[1].matches("Student")) {
                    userdetails = mUsername + ", Student";

                } else if (arry[1].matches("Professor")) {
                    userdetails = mUsername + ", Professor";
                } else if (arry[1].matches("HOD")) {
                    userdetails = mUsername + ", HOD";
                } else if (arry[1].matches("Class-in-charge")) {
                    userdetails = mUsername + ", Class-in-Charge";
                } else {
                    userdetails = mUsername;
                }
                textView.setText("  "+userdetails);
                ImageView imagev = (ImageView) layout.findViewById(R.id.image_border);
                imagev.setBackgroundDrawable(getResources().getDrawable(R.drawable.profpic_question));

                ImageView imageView = (ImageView) layout.findViewById(R.id.item_image);
                imageView.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_pblue));

                editText.setText("");
                category = "none";

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void CancelQA(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IntelliDj.this);
        alertDialogBuilder.setMessage("Stop Writing?");

        alertDialogBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                career.setBackgroundResource(R.drawable.profile_question);
                exam.setBackgroundResource(R.drawable.profile_question);
                general.setBackgroundResource(R.drawable.profile_question);
                career.setClickable(true);exam.setClickable(true);general.setClickable(true);

                dialog.dismiss();
                popupWindow.dismiss();

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }
    public void card1(View v)
    {
        category = "Career";
        career.setBackgroundResource(R.drawable.profile_answer);
        exam.setClickable(false);
        general.setClickable(false);



    }

    public void card2(View v)
    {
        category = "Exam";
       exam.setBackgroundResource(R.drawable.profile_answer);
        career.setClickable(false);
        general.setClickable(false);

    }

    public void card3(View v) {
        category = "General";
        general.setBackgroundResource(R.drawable.profile_answer);
        career.setClickable(false);
        exam.setClickable(false);

    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void submitQA(View v) // THIS  WILL RETREIVE THE CONTENT FROM POP UP AND SET IT IN CONVERSATION SCREEN
    // ALSO THIS WILL PUT THE CONTENT INSIDE DATABASE
    {
        if(isNetworkAvailable(IntelliDj.this)==1)  // internet connection
        {

          //  Toast.makeText(IntelliDj.this, "Inside Intellidj", Toast.LENGTH_SHORT).show();

            if (questionpressed == true) {

                // for category
                if (category.matches("none")) {
                    // please select some category
                    Toast.makeText(this, "Please select question category!", Toast.LENGTH_SHORT).show();
                } else if ((category.matches("Career") && !category.matches("Exam") && !category.matches("General")) ||
                        (!category.matches("Career") && category.matches("Exam") && !category.matches("General")) ||
                        (!category.matches("Career") && !category.matches("Exam") && category.matches("General"))
                        )

                {
                    // go ahead with other part


                    q = editText.getText().toString();
                    q = q.trim();

                    if (q.matches("")) {
                        Toast.makeText(IntelliDj.this, "Question cannot be empty!", Toast.LENGTH_SHORT).show();
                    } else {
                        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(IntelliDj.this);
                        alertDialogBuilder.setMessage("Submit Question?");
                        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                question_db.setContent(q);
                                question_db.setName(mUsername);

                                question_c = new Question(q, mUsername);
                                question_c.setUserId(CurrentID); // ALSO ADDING THE USER-ID TO THE QUESTION NODE
                                question_c.setUsertype(arry[1]);
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

                                question_c.setTimestamp(date);
                                question_db.setTimestamp(date);

                                //mQuestionDatabaseReference.push().setValue(question_c); //--->CLOUD
                                if (category.matches("Career")) {
                                    question_c.setCategory(category);
                                    question_db.setCategory(category);
                                    mCareerReference.push().setValue(question_c);
                                } else if (category.matches("Exam")) {
                                    question_c.setCategory(category);
                                    question_db.setCategory(category);
                                    mExamReference.push().setValue(question_c);
                                } else if (category.matches("General")) {
                                    question_c.setCategory(category);
                                    question_db.setCategory(category);
                                    mGeneralReference.push().setValue(question_c);
                                }


                                editText.setText("");
                                flagCategory = ""; // for next one to be selected
                                dialog.dismiss();
                               Toast.makeText(IntelliDj.this, "Your Question has been submitted! Please refresh to view it!", Toast.LENGTH_SHORT).show();
                                popupWindow.dismiss();

                            }
                        });
                        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }

                }
            }
        }
        else
        {
            Toast.makeText(IntelliDj.this,"Please connect to internet to submit your question!",Toast.LENGTH_SHORT).show();
        }

    }

    public void showfile(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE); // newly added line
        intent.setType("*/*");
        startActivityForResult(intent, 2);
    }


    // THIS WILL ATTACH AUTH STATE LISTENER TO APP
    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }



    // THIS WILL DETACH AUTH STATE LISTENER FROM APP
    @Override
    protected void onPause() {
        super.onPause();

       if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDBListener();
 }

    @Override
    public void onDestroy() {
        mDBHelper.close();
        stopService(mServiceIntent);
        stopService(mServiceIntent2);

        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {

                return true;
            }
        }
        return false;
    }


    //______________________FLOW STARTS FROM HERE ALWAYS_______________________

    private void onSignedInInitialize(String username) {
        mUsername = username; // after users signed in, their name was still shown as anonymous, so to avoid that, username
        // has to be set as part of signed in

        // 2. only when the users sign in, call must be given to database to listen for the changes, so we are copying child event
        // listener code here, BUT MOVE IT INSIDE ANOTHER METHOD CALLED ATTACHDBLISTENER
        attachDBListener();

        //-------------------


        // whenever a user signs in , he must have all the questions contained in databse, loaded inside app

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Question"); // get ref of root
        // NEVER WRITE HERE ANY CODE THAT DEALS WITH INDIVIDUAL NODES , DO NOT MANIPULATE THEM HERE
        // TRY TO MANIPULATE THEM IN METHOS WHERE THEIR DATA-SNAP-SHOT IS AVAILABLE

        //  TO KEEP LISTENING TO THE REF, USE ADDVALUELISTENER
        ref.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        syncQData(dataSnapshot,"Career");
                        syncQData(dataSnapshot,"Exam");
                        syncQData(dataSnapshot,"General");

                      //  new InsertDataIntoLocalDB().execute("q"); // insertion is performed together after mass collection
//

                        try { CollectQuestions(); // pass maximum info to adapter
                        } catch (ExecutionException e1) {
                            e1.printStackTrace();
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        // it will be needed in the nnext activity to get displayed


                    }

                    // for QUESTIONA
                        public void syncQData(DataSnapshot dataSnapshot,String childValue) // this method collects all data together in one place
                        {
   /////////most important part of the code
                        if (!dataSnapshot.exists())
                            {
                                NoQYet.setVisibility(View.VISIBLE);
                             }
                        else {
                            //  Toast.makeText(IntelliDj.this, "Copying questions into localdatabase !!", Toast.LENGTH_SHORT).show();
                            for (DataSnapshot dataSnapshot1 : dataSnapshot.child(childValue).getChildren()) {

                                vq1.add(dataSnapshot1.getKey());
                                vq2.add(dataSnapshot1.child("name").getValue().toString());
                                vq3.add(dataSnapshot1.child("content").getValue().toString());
                                vq4.add(dataSnapshot1.child("user_id").getValue().toString()); // blind user-id (multiple users)
                                vq5.add(dataSnapshot1.child("timestamp").getValue().toString());
                                vq6.add(dataSnapshot1.child("category").getValue().toString());
                                vq7.add(dataSnapshot1.child("usertype").getValue().toString());

                            }

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                        Toast.makeText(IntelliDj.this, "Oops! Error occured while fetching questions!", Toast.LENGTH_SHORT).show();
                    }
                });

    }


    public void CollectQuestions() throws ExecutionException, InterruptedException

    {

        // COLLECTING BOOKMARKS AS WELL ALONG WITH QUESTION

        // get all question_id (which are bookmarked and hence inside SQLITE), fetch details
        // about them from vq vectors, add it to bookmark vectors
       new GetBookmarkFromDB().execute().get();
       setupViewPager(viewPager);
       tabLayout.setupWithViewPager(viewPager);


    }


    // ATTACHING A LISTENER TO DATABASE TO READ DIFFERENT STATES OF CHILD BEING ADDED OR REMOVED, IS A HANDY METHOD
    // SO IT IS WRITTEN INSIDE A DIFFERENT METHOD , AND LATER ON CALL IS MADE TO THIS METHOD TO ATTACH LISTENER
    //************FUTURE, MAY BE WE CAN PASS PARAMTERES TO ATTACH LISTENER FOR DIFFERENT TYPES OF NODES
    private void attachDBListener()// for now this handles only questions
    {
        // HERE, I AM ALSO ADDING THE CODE THAT WILL TRY TO READ FROM DATABASE ALL THE QUESTIONS AS WELL AS ANSWERS

        // ******************************FOR  EACH UNIQUE  USER
       // if (mChildEventListener2 == null)
      //  {

            // lets try using ValueEventListener
            final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("User");


            mValueEventListener =   new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // datasnapshot of multiple users received here
                            // match details with the user name and email and obtain his cloud id

                            if (!dataSnapshot.exists()) {
                                // no user in database
                            //    Toast.makeText(IntelliDj.this, "No user exists in cloud", Toast.LENGTH_SHORT).show();
                            } else {
                                // users exists , collect details of all users and find our matching user

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    vucloudid.add(dataSnapshot1.getKey());  // blind - cloud- id of user
                                    vuname.add(dataSnapshot1.child("name").getValue().toString()); // name
                                    vuemail.add(dataSnapshot1.child("email").getValue().toString()); // email

                                 }

                                // call asynctask method to match the details
                                MatchUserDetails matchUserDetails = new MatchUserDetails();
                                try {
                                    CurrentID = matchUserDetails.execute(u_db.getName() + "#" + u_db.getEmail()).get();
                                    editor.putString("currentuserid", CurrentID);
                                    editor.apply();

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                                // this is CORRECT PLACE TO UPDATE USER-CLOUD-ID INSIDE LOCAL DATABASE
                                //----------this may be executed many times, but each time we will get correct id of our user
                                u_db.setCloud_id(CurrentID + "");
                                new UpdateQULocalDB(u_db).execute("u");

                                ///// BELOW CODE IS DIFFERENT, ITS FOR OTHER VALUES LIKE DEPT AND YEAR
                                try {
                                    Intent in = getIntent();
                                    int one = in.getFlags();
                                    if (one == 1) {
                                        // now time to update the user details in cloud and db
                                          // 1. cloud , only 2 fields for now
                                        userDatabaseReference.child(CurrentID).child("department")
                                                .setValue(ProfileUser.getDepartment());
                                        userDatabaseReference.child(CurrentID).child("type")
                                                .setValue(ProfileUser.getType());

                                        userDatabaseReference.child(CurrentID).child("year")
                                                .setValue(ProfileUser.getYear());
                                         ///// UPDATE VALUE HERE FOR LOCAL DATABASE

                                        u_db.setDepartment(ProfileUser.getDepartment());
                                        u_db.setType(ProfileUser.getType());
                                        u_db.setYear(ProfileUser.getYear());
                                        u_db.setImage(ProfileUser.getImage());
                                        u_db.setSapid(ProfileUser.getSapid());

                                        try {
                                            new UpdateQULocalDB(u_db).execute("U");
                                        } catch (Exception ex) {
                                        }

                                        // remove the value event listener now
                                        ref2.removeEventListener(mValueEventListener);


                                    }


                                }
                                catch (Exception e) {}
                            }
                        }



                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
        ref2.addValueEventListener(mValueEventListener);




    }


    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        detachDBListener();

    }

    private void detachDBListener() {

            tempDatabaseReference = mQuestionDatabaseReference;

    }




    // this is to avoid same user getting inserted multiple times into db
    // only one user exists per db, multiple user exists in cloud
    public class CheckIfUserExists extends AsyncTask<User, Void, Boolean> {

        @Override
        protected Boolean doInBackground(User... params) {
            String n = params[0].getName();
            String em = params[0].getEmail();
            SQLiteDatabase sqLiteDatabase = mDBHelper.getReadableDatabase();

            boolean value = false;

            // search in local db
            final Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM user WHERE name = ? AND email = ?",
                    new String[]{n, em});

            // search in cloud
            // obtain a snapshot of users already existing in cloud

            if (c.moveToFirst()) {
                // user exista,
                value = true;

            } else {
                value = false;
            }
            c.close();

            return value;
        }
    }


    // DIFFERENT CLASS, SO ITS OKK TO NOT USE sqLITEDatabase
    public class InsertUserIntoLocalDB extends AsyncTask<String, Void, Void> {


        SQLiteDatabase sql = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        public InsertUserIntoLocalDB() {

        }

        public InsertUserIntoLocalDB(User currentuser) {
            u_db = currentuser;
        }



        public void gatherUserData() {
            // SINCE ID IS AUTOINCREMENT , we just insert other paramters , NONE IS INSERTED TO MAINTAIN LENGTH OF STRING


            values.put("cloud_id", u_db.getCloud_id());
            values.put("name", u_db.getName());
            values.put("email", u_db.getEmail());
            values.put("department",u_db.getDepartment());
            values.put("type",u_db.getType());
            values.put("year",u_db.getYear());
            values.put("image",u_db.getImage());
            values.put("sapid",u_db.getSapid());



        }


        @Override
        protected Void doInBackground(String... params) {

            String identifier = params[0];
            if (identifier.matches("u")) {
                gatherUserData();
                last_user_id = sql.insert("user", null, values);
                //Toast.makeText(IntelliDj.this, "Last inserted row id: " + last_user_id, Toast.LENGTH_SHORT).show();

            }

            return null;
        }

    }


    public class UpdateQULocalDB extends AsyncTask<String, Void, Void> {
        SQLiteDatabase sql = mDBHelper.getWritableDatabase();
        ContentValues values = new ContentValues();


        public UpdateQULocalDB(User currentuser) {
            u_db = currentuser;
        }


        String selection = "id = ?"; // statement with names of variables


        @Override
        protected Void doInBackground(String... params) {

            String identifier = params[0];

            if (identifier.matches("u"))
            {
                String[] args = {String.valueOf(last_user_id)}; // actual values of the variables we know , WHERE ID IS AN INTEGER
                values.put("cloud_id", u_db.getCloud_id()); // values  --- value of table that has to be updated
                sql.update("user", values, selection, args);


            }
            else if (identifier.matches("U"))
            {
                try {
                    String[] args = {String.valueOf(u_db.getCloud_id())};
                    values.put("department", u_db.getDepartment());
                    values.put("type", u_db.getType());
                    values.put("year", u_db.getYear());
                    values.put("image",u_db.getImage());
                    values.put("sapid",u_db.getSapid());

                    /////  MOST IMPORTANT LINE FOR TODAY
                    sql.update("user", values, "cloud_id = ?", args);
                } catch (Exception e) {

                }

            } else if (identifier.matches("q")) {
                //  ????? String args[] = {String.valueOf(question.getCloud_id())};

                String args[] = {String.valueOf(nextQId)}; // nextQID is id of current inserted question
                values.put("cloud_id", question_db.getCloud_id());
                sql.update("question", values, selection, args);
            }

            return null;
        }
    }


    public class GetBookmarkFromDB extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            // first get all bookmarks from db

            SQLiteDatabase sqLiteDatabase = mDBHelper.getWritableDatabase();

            final Cursor Cr = sqLiteDatabase.rawQuery("SELECT * FROM bookmark",
                    new String[]{});
            if (Cr.moveToNext()) {
                // data exists

                do {
                    // retreive the data
                    listBookmark.add(Cr.getString(Cr.getColumnIndexOrThrow("question_id")));

                }
                while (Cr.moveToNext());

                //  listBookmark has list of ids of question
                // this is s list of cloud_ids of question
                int i, j;
                for (i = 0; i < listBookmark.size(); i++) {


                    for (j = 0; j < vq1.size(); j++) {
                        if (listBookmark.get(i).matches(vq1.get(j))) // vq1 is list of all cloud-ids of question
                        {
                            // retrieve other details for it
                            // details needed - name, content, id

                            BN.add(vq2.elementAt(j));
                            BQ.add(vq3.elementAt(j));
                            BID.add(vq1.elementAt(j));
                            BTS.add(vq5.elementAt(j)); // timestamp
                            BCT.add(vq6.elementAt(j)); // category
                            BUTYPE.add(vq7.elementAt(j));


                        }
                    }
                }

            }
            Cr.close();

            return BN.size();
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
                    sx =  (Cr.getString(Cr.getColumnIndexOrThrow("department"))) + "#"
                    +(Cr.getString(Cr.getColumnIndexOrThrow("type"))) + "#"
                    + (Cr.getString(Cr.getColumnIndexOrThrow("year"))) + "#"
                    + (Cr.getString(Cr.getColumnIndexOrThrow("image"))) + "#"
                           + (Cr.getString(Cr.getColumnIndexOrThrow("cloud_id"))) + "#"
                    +       (Cr.getString(Cr.getColumnIndexOrThrow("sapid"))) ;

                }
                while (Cr.moveToNext());
            }
            Cr.close();
        return sx;
        }
    }


    public class MatchUserDetails extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... params) {

            // current name and current email already available
            String[] details = params[0].split("#");

            int i;
            for(i=0;i<vuname.size();i++)
            {
                if(details[0].matches(vuname.get(i)) && details[1].matches(vuemail.get(i))) // only when both details match
                {
                    // we found our user
                    // extract his cloud id
                    CurrentID = vucloudid.get(i);
                    break;  // no need to traverse other records

                }
            }

            return CurrentID;
        }

    }

}
    //-----------------DENOTES END OF PUBLIC CLASS INTELLIDJ HERE----WRITE ALL METHODS ABOVE THIS



