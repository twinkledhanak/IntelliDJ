package com.tkd.twinkledhanak.intellidj.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tkd.twinkledhanak.intellidj.R;
import com.tkd.twinkledhanak.intellidj.activities.QAActivity;
import com.tkd.twinkledhanak.intellidj.db.LocalDatabaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.ExecutionException;



    public class QuestionFragment extends Fragment {


    private RecyclerView rv; // ADDING RECYCLER VIEW ELEMENTS
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private LocalDatabaseHelper mDBHelper;

    private boolean isConnected = false;
    ArrayList<String> u, co, cl , ts , ct ,utype;

    ArrayList<String> vuname , vucloudid , vuimg , vutype , vusapid;
    String  permisssion = "" , year = "" , date = "" , sapid = "";
    public QuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            u = new ArrayList<String>(getArguments().getStringArrayList("user")); // username
            co = new ArrayList<String>(getArguments().getStringArrayList("content")); // content
            cl = new ArrayList<String>(getArguments().getStringArrayList("cloudid")); // question-cloud-id
            ts = new ArrayList<String>(getArguments().getStringArrayList("timestamp"));
            ct = new ArrayList<String>(getArguments().getStringArrayList("category"));
            utype = new ArrayList<String>(getArguments().getStringArrayList("usertype"));


            try
            {
                InitializeUsers();


            }
            catch (Exception e)
            {

            }


            // WE RETRIEVE USER TYPE FOR PERMISSIONS
            GetUserDetails getUserDetails = new GetUserDetails();

            try {
                String sy = getUserDetails.execute().get();
                String arr[] = sy.split("#");
                permisssion = arr[1];
                year = arr[2];
                sapid = arr[5];
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            } catch (ExecutionException e1) {
                e1.printStackTrace();
            }


            mDBHelper = new LocalDatabaseHelper(getActivity());

        } else {
        }


    }

    public static QuestionFragment newInstance(Vector<String> v1, Vector<String> v2, Vector<String> v3, Vector<String> v4, Vector<String> v5,
            Vector<String> vucid , Vector<String> vuimg , Vector<String> v6 ) {



        QuestionFragment qf = new QuestionFragment();
        Bundle args = new Bundle();
        args.putStringArrayList("user", new ArrayList<String>(v1)); // username (blindly copied from cloud)
        args.putStringArrayList("content", new ArrayList<String>(v2)); // content
        args.putStringArrayList("cloudid", new ArrayList<String>(v3)); // question-cloud-id
        args.putStringArrayList("timestamp",new ArrayList<String>(v4)); // time
        args.putStringArrayList("category",new ArrayList<String>(v5)); // category
        args.putStringArrayList("usertype",new ArrayList<String>(v6)); // category

      qf.setArguments(args);
        return qf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        vuname = new ArrayList<String>();

        vucloudid = new ArrayList<String>();
        vuimg = new ArrayList<String>();
        vutype = new ArrayList<String>();
        vusapid = new ArrayList<String>();
        mDBHelper = new LocalDatabaseHelper(getActivity());

        View view = inflater.inflate(R.layout.fragment_question, container, false);

        rv = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        mLayoutManager = new LinearLayoutManager(getContext());// use a linear layout manager
        rv.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(new Vector<>(u), new Vector<>(co), new Vector<>(cl) , new Vector<>(ts) , new Vector<>(ct) , new Vector<>(utype));
                      //   new Vector<>(ucid) , new Vector<>(ucid));

        rv.setAdapter(mAdapter);




        return view;
    }


    public void InitializeUsers()
    {
        DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("User");

        ref2.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.exists())
                        {
                            // no user
                        }
                        else
                        {
                            // user exists
                            for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                            {

                                vucloudid.add(dataSnapshot1.getKey());  // blind - cloud- id of user
                                vuname.add(dataSnapshot1.child("name").getValue().toString()); // name
                                vutype.add(dataSnapshot1.child("type").getValue().toString()); // type of user
                               // vusapid.add(dataSnapshot1.child("sapid").getValue().toString());
                            }
                            // we are not inserting this value anywhere...its just innocent storing of values in vectors



                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );




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


    public class GetUserDetails extends AsyncTask<Void, Void, String> {
        // selects details only of current user
        // so retrives details of only the one using app

        @Override
        protected String doInBackground(Void... params) {


            mDBHelper = new LocalDatabaseHelper(getActivity());
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
                             +(Cr.getString(Cr.getColumnIndexOrThrow("sapid")));



                }
                while (Cr.moveToNext());
            }
            Cr.close();
            return sx;
        }
    }



    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        ArrayList<String> listNames;
        ArrayList<String> listQuestions;
        ArrayList<String> listQuestionIDS;
        ArrayList<String> listTimestamp;
        ArrayList<String> listCategory;
        ArrayList<String> listUimg;
        ArrayList<String> listUcid;
        ArrayList<String> listUType;
        Intent in;

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();


        // RECYCLER VIEW WILL WORK ONLY WITH ARRAY LIST BECAUSE ARRAY-LISTS START FROM INDEX -1
        // THIS WILL HELP TO SOLVE PROBLEM OF ARRAY INDEX OUT OF BOUNDS
        // I HAVE USED VECTOR TO COLLECT DATA FROM CLOUD
        // SIMPLY CONVERT THIS VECTOR INTO ARRAYLIST


        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener// class inside class---subclass
        {
            ImageView itemImage;
            TextView itemTitle;
            TextView itemName;
            TextView itemcategory;
            TextView itemTime;
            private Context context;


            public ViewHolder(View view) // constrcutor of inner class
            {
                super(view);
                itemImage = (ImageView) view.findViewById(R.id.item_image);
                itemTitle = (TextView) view.findViewById(R.id.item_title);
                itemName = (TextView) view.findViewById(R.id.item_name);
                itemTime = (TextView) view.findViewById(R.id.itemTime);
                itemcategory = (TextView) view.findViewById(R.id.category);
               context = view.getContext();
               itemTitle.setOnClickListener(this); // instead set it on a different object


            }

            @Override
            public void onClick(View v) {

                int position = getLayoutPosition(); // gets item position

                Long tsLong = System.currentTimeMillis() / 1000;
                tsLong = tsLong * 1000;
                Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                cal.setTimeInMillis(tsLong);
                date = DateFormat.format("dd-MM-yyyy", cal).toString();
                String newdate[] = date.split("-"); // newdate[1] has the month number , newdate[2] has year
                int currentMonth = Integer.parseInt(newdate[1]); // 01, 02 etc indicate octal, so , use 1 , 2 while comparing
                int currentYear = Integer.parseInt(newdate[2].substring(2,newdate[2].length())); // 18 , 19 whatever is current

                if (isNetworkAvailable(context) == 1) // internet is available
                        {

                            if (permisssion.matches("") || permisssion.matches("none") || permisssion.matches("None")) // type
                            {
                                Toast.makeText(context,"Please update your profile first!",Toast.LENGTH_SHORT).show();
                            }
                            else if (permisssion.matches("Student") && year.matches("be") )   // some obsolete user
                            {
                                // calculate diff here, we are sure, sapid here is updated and not null
                                int vy = Integer.parseInt(sapid.substring(5,7)); // 14, 15 etc
                                int diff = currentYear - vy;

                                if (diff == 4 && currentMonth >=6)
                                {
                                    // obsolete user
                                    Toast.makeText(context,"You are an obsolete user now!",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {   // We can access the data within the views
                                    // data to be shown on next page
                                    String data = listQuestions.get(position);
                                    // user who asked the question
                                    String user = listNames.get(position);
                                    String uid = sharedPreferences.getString("currentuserid", "");
                                    String qid = listQuestionIDS.get(position);
                                    String timestamp = listTimestamp.get(position);
                                    String category = listCategory.get(position);
                                 //   Toast.makeText(context, "Position of card_question clicked is" + position, Toast.LENGTH_SHORT).show();
                                    in = new Intent(context, QAActivity.class);
                                    in.putExtra("user", user); //username -- blindly copied
                                    in.putExtra("refQ", qid);  // question-cloud-id
                                    in.putExtra("Qcontent", data); // content
                                    in.putStringArrayListExtra("username", vuname);
                                    in.putStringArrayListExtra("usercloudid", vucloudid);
                                    in.putStringArrayListExtra("usertype", vutype);
                                    in.putExtra("currentuserid", uid);// current-cloud-id
                                    try {
                                        context.startActivity(in);


                                    } catch (Exception ex
                                            ) {
                                    }
                                }

                            }

                            else {

                                // We can access the data within the views
                                // data to be shown on next page
                                String data = listQuestions.get(position);
                                // user who asked the question
                                String user = listNames.get(position);
                                String uid = sharedPreferences.getString("currentuserid", "");
                               String qid = listQuestionIDS.get(position);
                                String timestamp = listTimestamp.get(position);
                                String category = listCategory.get(position);
                              //  Toast.makeText(context, "Position of card_question clicked is" + position, Toast.LENGTH_SHORT).show();
                                in = new Intent(context, QAActivity.class);
                                in.putExtra("user", user); //username -- blindly copied
                                in.putExtra("refQ", qid);  // question-cloud-id
                                in.putExtra("Qcontent", data); // content
                                in.putStringArrayListExtra("username", vuname);
                                in.putStringArrayListExtra("usercloudid", vucloudid);
                                in.putStringArrayListExtra("usertype", vutype);
                               in.putExtra("currentuserid", uid);// current-cloud-id
                                try {
                                    context.startActivity(in);
                                } catch (Exception ex
                                        ) {
                                }
                            }
                }
                else
                {
                    Toast.makeText(context,"Cannot read Answers in offline mode!",Toast.LENGTH_SHORT).show();
                }

            }


        }

        // THESE CONSTRUCTORS HELP TO HAVE ALL DATA REQUIRED FOR QUESTIONS AND ANSWERS
        // THEY HAVE TO BE USED FOR MAPPING OR SENT TO ANOTHER ACTIVITY

        public MyAdapter(Vector<String> N, Vector<String> Q, Vector<String> QID , Vector<String > Ts , Vector<String> CT, Vector<String> TYPE
                     // Vector<String> VID , Vector<String> VIMG
        ) {
            listNames = new ArrayList<>(N); // blindly copied name
            listQuestions = new ArrayList<>(Q);
            listQuestionIDS = new ArrayList<>(QID); // dont set this anywhere on any view// ONLY FOR MAPPING PURPOSE
            listTimestamp = new ArrayList<>(Ts);
            listCategory = new ArrayList<>(CT);
            listUType = new ArrayList<>(TYPE);
       //     listUimg = new ArrayList<>(VIMG);
        //    listUcid = new ArrayList<>(VID);


        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_question, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);


            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {


            viewHolder.itemTitle.setText(listQuestions.get(i));

            if (listUType.get(i).matches("") || listUType.get(i).matches("none"))
            {

                viewHolder.itemName.setText(listNames.get(i));

            }
            else
            {
                viewHolder.itemName.setText(listNames.get(i) +" , "+ listUType.get(i));
            }

            viewHolder.itemTime.setText(listTimestamp.get(i));
            viewHolder.itemcategory.setText(listCategory.get(i));
        }

        @Override
        public int getItemCount() {

            return listQuestions.size();
        }


        }



    }






