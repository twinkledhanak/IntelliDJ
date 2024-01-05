package com.tkd.twinkledhanak.intellidj.objects;

/**
 * Created by twinkle dhanak on 3/21/2018.
 */

public class Document
{
    public String name , url , timestamp , type; // type is of user , fe, se, te , be , all

    public Document()
    {

    }
    public Document(String name, String url , String timestamp , String category)
    {
        this.name = name;
        this.url = url;
        this.timestamp = timestamp;
        this.type = category;
    }
    public String getName()
    {
        return  this.name;
    }
    public String getUrl()
    {
        return this.url;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getTimestamp() {return  this.timestamp;}
    public void setTimestamp(String timestamp) {this.timestamp = timestamp;}

    public String getType() {return  this.type; }

    public void  setType(String type) {this.type = type; }



}
