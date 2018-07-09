package org.tensorflow.demo.photoSearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mgo983 on 6/1/17.
 */

public class OpenClipArtJSONHandler extends JSONHandler {
    @Override
    public String[] getImageUrl(String JSONString) throws JSONException {

        try{
            final String[] ImageUrl;
            final JSONObject obj = new JSONObject(JSONString);
            final JSONArray payLoad = obj.getJSONArray("payload");

            int lengthOfJSONEntries = payLoad.length();
            if (lengthOfJSONEntries == 0) return null;
            int noOfObjects = lengthOfJSONEntries >= 10 ? 10: lengthOfJSONEntries;

            ImageUrl = new String[noOfObjects];
            for (int i = 0; i < noOfObjects; i++){
                JSONObject ithElement = payLoad.getJSONObject(i);
                ImageUrl[i] = ithElement.getJSONObject("svg").getString("png_thumb");
                Log.v("JSON Url", ImageUrl[i]);
            }


            return ImageUrl;
        }catch (JSONException e){
            Log.e("A JSON Exception: ", "the error: " + e);
        }

        return null;

    }
}
