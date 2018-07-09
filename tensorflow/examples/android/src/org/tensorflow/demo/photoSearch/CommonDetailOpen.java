package org.tensorflow.demo.photoSearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.tensorflow.demo.photoSearch.Adapter.ButtonTextAdapter;
import org.tensorflow.demo.R;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by mgo983 on 9/21/17.
 */

public class CommonDetailOpen extends AppCompatActivity implements TextToSpeech.OnInitListener, SafeAction.OnokOrCancel{

    //public final static String IMGFILENAME = "com.example.android.sunshine.IMG_FILE_NAME";
    //public final static String IMGFILEKEY = "com.example.android.sunshine.IMG_FILE_KEY";

    public boolean ONLONGCLICKMODE = false;
    public boolean ONEDITMODE = false;

    public String[] listOfWords;
    public String TxtFileName;
    public String _FileName;

    ButtonTextAdapter adapter = null;

    ListView list = null;

/*        @Override
        public void okOrCancel(boolean okOrCancel, String menuID){

        }*/
    @Override
    public void onInit(int initStatus){

    }

    @Override
    public void onBackPressed(){
        if (ONLONGCLICKMODE){
            adapter.makeItemsEditDeleteInvisible();
            ONLONGCLICKMODE = false;
        }else if (ONEDITMODE){
            FrameLayout listItem =  (FrameLayout) list.getChildAt(adapter.EDITED_POSITION);
            adapter.hideEditElements(
                    (EditText) listItem.findViewById(R.id.list_item_word_editview),
                    (ImageButton) listItem.findViewById(R.id.list_reject_edit_button),
                    (ImageButton) listItem.findViewById(R.id.list_accept_edit_button),
                    this,
                    (TextView) listItem.findViewById(R.id.list_item_word_textview)
            );
            ONEDITMODE = false;

        }else
        {
            super.onBackPressed();
        }
    }

    @Override
    public void okOrCancel(boolean okOrCancel, String menuID){
        if (okOrCancel){

            String newPhrase = "";
            String eachWord = listOfWords[0];
            int phraseLength = listOfWords.length;
            int beginAt;

            FrameLayout listItem =  (FrameLayout) list.getChildAt(adapter.EDITED_POSITION);

            switch (menuID){
                case "delete":
                    if(adapter.EDITED_POSITION - 2 == 0){
                        newPhrase = listOfWords[1];
                        beginAt = 2;
                    }else{
                        newPhrase = listOfWords[0];
                        beginAt = 1;
                    }

                    for(int i = beginAt; i < phraseLength; i++){
                        if (i != adapter.EDITED_POSITION - 2){
                            eachWord = listOfWords[i];
                            newPhrase = newPhrase + " " +eachWord;
                        }
                    }
                    break;
                case "edit":
                    EditText editedWordView = (EditText) listItem.findViewById(R.id.list_item_word_editview);
                    String editedWord = editedWordView.getText().toString();

                    //eliminate trailing spaces
                    if (adapter.EDITED_POSITION - 2 == 0){
                        newPhrase = editedWord;
                    } else{
                        newPhrase = eachWord;
                    }

                    for (int i = 1; i < listOfWords.length; i++){

                        if (i == adapter.EDITED_POSITION - 2){
                            eachWord = editedWordView.getText().toString();
                        }else {
                            eachWord = listOfWords[i];
                        }
                        newPhrase += " " + eachWord ;
                    }
                    break;
            }


            writeToFile(newPhrase, TxtFileName ,this);
            reloadActivity();

        }else{
            onBackPressed();
        }
    }
    private void reloadActivity(){
        finish();
        Intent OpenGalleryActivityIntent = new Intent(getApplicationContext(), OpenGalleryObjectActivity.class);

        //use sharedpreference to save data so that back button works
        SharedPreferences sharedPreference;
        SharedPreferences.Editor editor;
        sharedPreference = getApplicationContext().getSharedPreferences(GalleryActivity.IMGFILENAME, getApplicationContext().MODE_PRIVATE);
        editor = sharedPreference.edit();

        editor.putString(GalleryActivity.IMGFILEKEY,_FileName);
        editor.commit();
        startActivity(OpenGalleryActivityIntent);
        //startActivity(getIntent());
    }

    public void writeToFile(String data, String fileName, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


}
