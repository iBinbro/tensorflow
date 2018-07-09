package org.tensorflow.demo.photoSearch.AccessorsAndSetters;

import android.util.Log;

import java.util.HashMap;

/**
 * Created by mgo983 on 6/15/17.
 */

public class Color {

    HashMap<String,String> ColorDepot = new HashMap<>();

    public Color(){
        ColorDepot.put("navy","navy");
        ColorDepot.put("blue","blue");
        ColorDepot.put("aqua","aqua");
        ColorDepot.put("teal","teal");
        ColorDepot.put("olive","olive");
        ColorDepot.put("green","green");
        ColorDepot.put("lime","lime");
        ColorDepot.put("yellow","yellow");
        ColorDepot.put("orange","orange");
        ColorDepot.put("red","red");
        ColorDepot.put("maroon","maroon");
        ColorDepot.put("fuchsia","fuchsia");
        ColorDepot.put("purple","purple");
        ColorDepot.put("black","black");
        ColorDepot.put("gray","gray");
        ColorDepot.put("grey","grey");
        ColorDepot.put("silver","silver");
        ColorDepot.put("white","white");
        ColorDepot.put("brown","brown");

    }

    public boolean searchColor(String searchColor){
        return !(ColorDepot.get(searchColor) == null);
    }
}
