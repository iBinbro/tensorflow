package org.tensorflow.demo.photoSearch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mgo983 on 6/1/17.
 */

public class PixabayJSONHandler extends JSONHandler{
    @Override
    public String[] getImageUrl(String JSONString) throws JSONException {
        try{
            final String[] ImageUrl;
            final JSONObject obj = new JSONObject(JSONString);
            final JSONArray hits = obj.getJSONArray("hits");

            int lengthOfJSONEntries = hits.length();
            if (lengthOfJSONEntries == 0) return null;
            int noOfObjects = lengthOfJSONEntries >= 10 ? 10: lengthOfJSONEntries;

            ImageUrl = new String[noOfObjects];
            for (int i = 0; i < noOfObjects; i++){
                JSONObject ithElement = hits.getJSONObject(i);
                ImageUrl[i] = ithElement.getString("previewURL");
                Log.v("JSON Url", ImageUrl[i]);
            }


            return ImageUrl;
        }catch (JSONException e){
            Log.e("A JSON Exception: ", "the error: " + e);
        }

        return null;
    }
}
