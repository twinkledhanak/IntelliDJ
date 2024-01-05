package com.tkd.twinkledhanak.intellidj.objects;

/**
 * Created by twinkle dhanak on 3/27/2017.
 */

public class Question {
    // writing the paramteres required for a question

    private String content,name,photourl,cloud_id,user_id , timestamp , category , usertype;
    int id;

    public Question()
    {

    }

    public Question(String content , String name )
    {
        this.content = content;
        this.name = name;

    }
// no need for param constructor as we will set the data using methods



    public void setUserId(String user_id)
    {
        this.user_id = user_id;
    }
    public String getUser_id() { return this.user_id = user_id;}

    public void setCloud_id(String cloud_id) { this.cloud_id = cloud_id;}
    public String getCloud_id()
    {
        return  cloud_id;
    }

    public void setContent(String content ){this.content=content; }
    public String getContent()

    {
        return content;
    }

    public void setName(String name)

    {
        this.name=name;
    }
    public String getName()

    {
        return name;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }
    public String getTimestamp(){ return timestamp;}


    public void setCategory(String category ) {this.category = category;}
   public String getCategory() { return   category ;}

    public void setUsertype(String user)
    {
        this.usertype = user ;
    }
    public String getUsertype()
    {
        return this.usertype;
    }

////////////get methods for all

    public int getId()
    {
        return id;
    }















}
