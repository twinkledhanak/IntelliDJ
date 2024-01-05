package com.tkd.twinkledhanak.intellidj.objects;

/**
 * Created by twinkle dhanak on 5/7/2017.
 */

public class User {
    private String name , email , sapid , department , type , cloud_id , year , image;
    private long id;



    public User() {

    }

    public long getId()
    {
        return  id;
    }
    public void setId(long id)
    {
        this.id = id;
    }

    public String getCloud_id()
    {
        return cloud_id;
    }

    public void setCloud_id(String cloud_id)
    {
        this.cloud_id = cloud_id;
    }

    public String getName()
    {
        return  name;
    }
     public void setName(String name)
     {
         this.name = name;
     }

    public String getEmail()
    {
        return  email;
    }
    public  void setEmail(String email)
    {
        this.email = email;
    }

    public String getSapid()
    {
        return  sapid;
    }
    public void setSapid(String sapid)
    {
        this.sapid = sapid;
    }

    public String getDepartment()
    {
        return  department;
    }
    public void setDepartment(String department)
    {
        this.department = department;
    }

    public String getType()
    {
        return  type;
    }
    public void setType(String type)
    {
        this.type = type;
    }

    public String getYear() {return  year;}
    public void setYear(String year) { this.year = year;


}

    public String getImage() {return  image; }
    public void setImage(String image) {this.image = image ;}

}