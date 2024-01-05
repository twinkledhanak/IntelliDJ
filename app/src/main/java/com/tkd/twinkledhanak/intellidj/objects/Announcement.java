package com.tkd.twinkledhanak.intellidj.objects;

/**
 * Created by twinkle dhanak on 12/31/2017.
 */

public class Announcement
{
    String content, cloud_id , uname ,timestamp , url , type , usertype;
    int sent;

    public Announcement()
    {
        // default constructor
    }

    public void setContent(String s) // actual notice content
    {
        this.content = s;
    }

    public void setCloud_id(String c) // cloud-id of notice node in cloud
    {
        this.cloud_id = c;
    }


    public void setUname(String u) // user who gave the announcement
    {
        this.uname = u;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getContent()
    {
        return this.content;
    }

    public String getCloud_id()
    {
        return this.cloud_id;
    }

    public String getUname()
    {
        return this.uname;
    }

    public String getTimestamp() { return  this.timestamp; }

    public void setUrl(String url){ this.url = url;}

    public String getUrl() { return  this.url; }

    public String getType() {return  this.type; }

    public void  setType(String type) {this.type = type; }



    public void setUsertype(String user)
    {
        this.usertype = user ;
    }

    public String getUsertype (){ return  this.usertype; }


}
