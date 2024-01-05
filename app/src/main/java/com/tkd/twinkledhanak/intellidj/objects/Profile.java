package com.tkd.twinkledhanak.intellidj.objects;

import android.app.ProgressDialog;

/**
 * Created by twinkle dhanak on 3/24/2018.
 */

public class Profile
{
    public String name;
    public String url;

    public Profile()
    {

    }


    public Profile(String url) // we mostly have only url for the image
    {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

}
