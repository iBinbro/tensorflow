package org.tensorflow.demo.photoSearch.Evocation;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.google.firebase.auth.ActionCodeResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tensorflow.demo.photoSearch.AphasiaWords;
import org.tensorflow.demo.photoSearch.data.AddWord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by mgo983 on 7/31/18.
 */

public class Evocation {

    private Context context;

    public Evocation(Context context){
        this.context = context;
    }

    public ArrayList<String> findEvocationWords(String word) {

        ArrayList evocative_words = new ArrayList<String>();

        try {
            final AssetManager assetManager = ((Activity) context).getAssets();
            InputStream inputStream = assetManager.open("evocation/controlled.synsets.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String dictLine;

            // get the votes for each word
            InputStream inputStreamVotes = assetManager.open("evocation/controlled.raw.txt");
            BufferedReader bufferedReaderVotes = new BufferedReader(new InputStreamReader(inputStreamVotes));
            String allVotes;

            int count = 0;
            while(((dictLine = bufferedReader.readLine()) != null) && ((allVotes = bufferedReaderVotes.readLine()) != null) /*&& count < 20*/){
                String [] testWord = dictLine.split("%");
                String [] arrayOfVotes = allVotes.split(" ");
                float med = median(arrayOfVotes);
                count++;
                if (word.equals(testWord[0]) && (med >= 50.0)){
                    String evoke = testWord[1].split(" ")[1];
                    evocative_words.add(evoke);
                    Log.d("whole line: ", dictLine);
                    Log.d("line: ", evoke);
                }

            }
            inputStreamVotes.close();
            inputStream.close();

            bufferedReaderVotes.close();
            bufferedReader.close();

        }catch (IOException e) {

        }finally {

        }

        return evocative_words;
    }

private float median(String [] arrayOfVotes){
    Float floatArrayOfVotes [] = convertStringArrayToFloat(arrayOfVotes);
    Arrays.sort(floatArrayOfVotes);
    int count = floatArrayOfVotes.length;
    return Collections.max(Arrays.asList(floatArrayOfVotes));
}


private Float [] convertStringArrayToFloat(String [] arrayOfVotes){
    Float floatArray [] = new Float[arrayOfVotes.length];
    for (int i = 0; i < arrayOfVotes.length; i++){
        try{
            floatArray[i] =  Float.parseFloat(arrayOfVotes[i].trim());
        }catch (RuntimeException e){
            floatArray[i] = new Float(0.0);
        }
        Log.d("array of votes ", arrayOfVotes[i] + " " + floatArray[i]);

    }
    return floatArray;
}

}
