package com.tkd.twinkledhanak.intellidj.objects;

/**
 * Created by twinkle dhanak on 4/2/2017.
 */

public class Answer {
    private String cloud_id,content,photourl,question_id,user_id,name , usertype;
   int id;
    long  upvote;
    public Answer()
    {

    }



    public void setContent(String content) { this.content=content;}

    public void setName(String  username){ this.name=username; }

    public void setId(int id) {this.id = id;}


    public void setAnsPhotourl(String photourl) {this.photourl=photourl;}

    public  void setQuestionId(String questionnode) { this.question_id = questionnode; }

    public void setUserId(String user_id)
    {
        this.user_id = user_id;
    }

    public void setUpvote(long upvote ) {this.upvote = upvote;}

////////////get methods for all


    public String getContent()

    {
        return content;
    }

    public String getName()

    {
        return name;
    }

    public String getAnsPhotourl() {return this.photourl;}

    public  String getQuestionId() {return this.question_id ; }

    public String getUser_id()
    {
        return this.user_id ;
    }

    public long getUpvote() {return this.upvote;}

}
