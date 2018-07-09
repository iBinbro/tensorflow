package org.tensorflow.demo.photoSearch;

/**
 * Created by mgo983 on 11/7/17.
 */

public class AphasiaWords {

    private String id;
    private String word;
    private String fileName;

    public AphasiaWords(String _word, String _fileName){
        word = _word;
        fileName = _fileName;
    }

    public AphasiaWords(){}

    public String getId(){ return id;}
    public String getWord(){return word;}
    public String getFileName(){return fileName;}

    public void setWord(String _word){
        word = _word;
    }

    public void setFileName(String _fileName){
        word = _fileName;
    }

}
