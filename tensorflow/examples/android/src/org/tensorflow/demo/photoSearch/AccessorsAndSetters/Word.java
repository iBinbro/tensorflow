package org.tensorflow.demo.photoSearch.AccessorsAndSetters;

import android.net.Uri;

/**
 * Created by mgo983 on 3/22/18.
 */

public class Word {

    private String id;
    private String fileName;
    private String word;
    private Uri uri;

    public Word(){}

    public Word(String mFileName, String mWord){
        fileName = mFileName;
        word = mWord;
    }

    public String getId(){ return  id;}
    public Uri getUri(){return uri;}
    public String getFileName(){return fileName;}
    public String getWord(){return word;}

    public void setId(String mId){
        id = mId;
    }

    public void setFileName(String mFileName){
        fileName = mFileName;
    }

    public void setWord(String mWord){
        word = mWord;
    }

    public void setUri(Uri mUri){ uri = mUri;}


}
