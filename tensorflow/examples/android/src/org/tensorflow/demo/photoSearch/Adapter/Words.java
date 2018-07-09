package org.tensorflow.demo.photoSearch.Adapter;

/**
 * Created by mgo983 on 11/13/17.
 */

public class Words {

    private String word;
    private String url;
    private String category;
    private String imageSource;

    public Words(String _word, String _url, String _category, String _source){
        word = _word;
        url = _url;
        category = _category;
        imageSource = _source;

    }

    public Words(){}

    public void setWord(String _word){ word = _word;}
    public void setUrl(String _url){url = _url;}
    public void setCategory(String _category){category = _category;}
    public void setImageSource(String _imageSource){imageSource = _imageSource;}



    public String getWord(){return  word;}
    public String getUrl(){return url;}
    public String getCategory(){return category;}
    public String getImageSource(){return imageSource;}

}
