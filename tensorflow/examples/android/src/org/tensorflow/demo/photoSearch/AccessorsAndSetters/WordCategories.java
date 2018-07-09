package org.tensorflow.demo.photoSearch.AccessorsAndSetters;

/**
 * Created by mgo983 on 11/6/17.
 */

public class WordCategories {

    private String id;
    private String wordCategory;

    public WordCategories(String category){ wordCategory = category;}

    public String getWordCategory(){ return wordCategory;}
    public String setId(){ return id;}

    public void setWordCategory(String category){
        wordCategory = category;
    }

}


