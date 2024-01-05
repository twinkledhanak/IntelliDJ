package com.tkd.twinkledhanak.intellidj.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tkd.twinkledhanak.intellidj.R;
public class ProfileActivity extends AppCompatActivity {

    String uname, uemail,dept,type,year , image , currentuser ;
    Intent in;
    ImageView imageView;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageView = (ImageView)findViewById(R.id.header_cover_image);

        getSupportActionBar().setElevation(0);



        // receive data to be set on this page
         in = getIntent();
        if (in.getFlags()==1){
            uname = in.getStringExtra("uname");
            uemail = in.getStringExtra("uemail");
            dept = in.getStringExtra("dept");
            type = in.getStringExtra("type");
            year = in.getStringExtra("year");
            image = in.getStringExtra("image");
            currentuser = in.getStringExtra("currentuser");
            setProfile(uname,uemail,dept,type,year, image);
        }
        else
        {
            uname = in.getStringExtra("uname");
            uemail = in.getStringExtra("uemail");
            currentuser = in.getStringExtra("currentuser");
            setProfile(uname,uemail,"","","","");

        }



    }

    public void setProfile(String s1, String s2, String s3, String s4,String s5, String s6)
    {
        TextView n = (TextView)findViewById(R.id.uname);
        TextView e = (TextView)findViewById(R.id.uemail);
        TextView t = (TextView)findViewById(R.id.type);
        TextView d = (TextView)findViewById(R.id.dept);
        TextView y = (TextView)findViewById(R.id.year);

        n.setText(s1);
        e.setText(s2);
       if(s3.equals("") || s3.equals("none") || s3.equals("null")) {d.setText("-Department-");} else { d.setText(s3); }

        if (s4.equals("") || s4.equals("none") || s4.equals("null")) {t.setText("-Type Of User-");} else {t.setText(s4);}
        if(s5.equals("") || s5.equals("none")
               ||  s5.equals("null")) {y.setText("-Student Year-");}  else {y.setText(s5.toUpperCase());}

        try
        {
            Glide.with(this).load(s6).into(imageView);
         }
        catch (Exception exe)
        {

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed()
    {
           super.onBackPressed();
             overridePendingTransition(R.anim.open2,R.anim.close2);

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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click


        switch (item.getItemId()) {
            case R.id.action_edit_profile:

            {
                if(isNetworkAvailable(ProfileActivity.this) == 1) // internet connection
                {
                    Intent inn = new Intent(ProfileActivity.this, ProfileSetUp.class);

                    if (in.getFlags() == 1) {
                        inn.putExtra("uname", uname);
                        inn.putExtra("uemail", uemail);
                        inn.putExtra("dept", dept);
                        inn.putExtra("type", type);

                        inn.putExtra("year", year);
                        inn.putExtra("image", image);
                        inn.putExtra("currentuser", currentuser);
                        inn.setFlags(2);
                    } else {
                        inn.putExtra("uname", uname);
                        inn.putExtra("uemail", uemail);
                        inn.putExtra("currentuser", currentuser);
                    }
                    startActivity(inn);
                }
                else
                {
                    Toast.makeText(ProfileActivity.this,"Please connect to internet to edit your profile!",Toast.LENGTH_SHORT).show();
                }
            }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
