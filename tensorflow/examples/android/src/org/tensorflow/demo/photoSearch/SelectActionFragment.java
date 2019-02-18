package org.tensorflow.demo.photoSearch;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.tensorflow.demo.DetectorActivity;
import org.tensorflow.demo.R;
import org.tensorflow.demo.photoSearch.Evocation.Evocation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

/**
 * Created by mgo983 on 4/18/17.
 */

public class SelectActionFragment extends android.support.v4.app.Fragment {

    public final static String EXTRA_TEXT = "com.example.android.sunshine.MESSAGE";

    //Camera Data
    public final static String EXTRA_IMAGE = "com.example.android.sunshine.IMAGE";
    public final static String EXTRA_TARGET = "com.example.android.sunshine.TARGET";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final String applicationDirectory = "Aphasia";
    static final String WNDICT = "dict";
    static final String DOCUMENT = "document";
    static final File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), applicationDirectory);
    // had to declare a constant DOCUMENT because Environment.DIRECTORY_DOCUMENTS returns "documents" and not "document"
    static final File DocumentDir = new File(Environment.getExternalStoragePublicDirectory(DOCUMENT), WNDICT);


    String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());


    String imageFileName = "";

    File imageFile = null;

    Uri testUri = null;


    public SelectActionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.newmain, container, false);

        TextView textView = (TextView) rootView.findViewById(R.id.app_name);
        Typeface face=Typeface.createFromAsset(getActivity().getAssets(),"fonts/shortname.ttf");
        textView.setTypeface(face);

        Button cameraButton = (Button) rootView.findViewById(R.id.camera);


        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

                imageFileName = "sun" + currentDateTimeString.replace(" ","");

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Uri photoURI = null;

                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    photoURI = testUri = GetFileUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }else{
                    //delete imagefile
                    deleteFile(photoURI);
                }
            }
        });

        try {
            String path = DocumentDir.getAbsolutePath();

            URL url = new URL("file", null , path );
            //File file = File.createTempFile(WNDICT,"", DocumentDir);
            //URL url = new URL (FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".org.tensorflow.demo.provider", "dict").toString());


            //Copy dict folder to phone if it does not already exist

            // construct the dictionary object and open it
            IDictionary dict = new Dictionary( url);
            testDictionary();

            //getHypernyms(dict);
        }catch (IOException e){
            Log.e("JWI", "file not found " + e);

        }


        Button galleryButton = (Button) rootView.findViewById(R.id.gallery);

        galleryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Perform action on click
                Intent galleryActivityIntent = new Intent(getActivity(), GalleryActivity.class);
                startActivity(galleryActivityIntent);
            }
        });


        Button speakButton = (Button) rootView.findViewById(R.id.speak);

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent speakActivityIntent = new Intent (getActivity(), SpeakActivity.class);
                startActivity(speakActivityIntent);
            }
        });

        Button trackingButton = rootView.findViewById(R.id.object_detect);

        trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detectorIntent = new Intent(getActivity(), DetectorActivity.class);
                startActivity(detectorIntent);
            }
        });

        return rootView;

    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        TextView textView = (TextView) getActivity().findViewById(R.id.app_name);
        Typeface face=Typeface.createFromAsset(getActivity().getAssets(),"fonts/shortname.ttf");
        //textView.setTypeface(face);

    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
        //inflater.inflate(R.menu.main,menu);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {

            Intent detailFragment = new Intent(getActivity(), DetailActivity.class);
            //galleryAddPic(testUri.getPath());
            detailFragment.putExtra(EXTRA_IMAGE,testUri);
            startActivity(detailFragment);
        }else {
            Log.d("I was cancelled!","I was cancelled!");
            deleteFile(testUri);
        }
    }

    private void createFile(){

        imageFileName = "sun" + currentDateTimeString.replace(" ","");

        try{
            imageFile = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */);

        }catch (IOException e){
        }
    }

    private  boolean deleteFile(Uri uri){
        File currFile = new File(uri.toString());
        return  currFile.delete();
    }

    private Uri GetFileUri(){
        createFile();

        return FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".org.tensorflow.demo.provider", imageFile);

    }

//make image accessible from the system's media provider
    private void galleryAddPic(String mCurrentPhotoPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }


    public void testDictionary () throws IOException {

        // construct the URL to the Wordnet dictionary directory
        //String wnhome = "file:///android_asset";
        //String path = wnhome + File.separator + "dict";
        //copyAssets();

        String path = DocumentDir.getAbsolutePath();

        URL url = new URL("file", null , path );
        Log.d("path", url.toString());
        //File file = File.createTempFile(WNDICT,"", DocumentDir);
        //URL url = new URL (FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".org.tensorflow.demo.provider", "dict").toString());

        //Copy dict folder to phone if it does not already exist

        // construct the dictionary object and open it
        IDictionary dict = new Dictionary( url);
        dict.open();

        // look up first sense of the word "dog "
        IIndexWord idxWord = dict . getIndexWord ("dog", POS.NOUN );
        IWordID wordID = idxWord . getWordIDs ().get (0) ;
        IWord word = dict . getWord ( wordID );
        Log.d("JWI", "Id = " + wordID );
        Log.d("JWI", " Lemma = " + word . getLemma ());
        Log.d("JWI", " Gloss = " + word . getSynset (). getGloss ());

        ISynset synset = word.getSynset ();

        // get the hypernyms
        List < ISynsetID > hypernyms =
                synset.getRelatedSynsets (Pointer.HYPERNYM);

        // print out each h y p e r n y m s id and synonyms
        List<IWord > words ;
        for( ISynsetID sid : hypernyms ){
            words = dict.getSynset(sid).getWords ();
//            Log.d("",sid + " {");
            for(Iterator<IWord > i = words.iterator(); i.hasNext () ;){
                Log.d("JWI",i.next().getLemma ());
                if(i.hasNext())
                    Log.d("JWI",", ");
            }
            //Log.d ("","}");
        }
    }

    public void getHypernyms ( IDictionary dict ){

        // get the synset
        IIndexWord idxWord = dict.getIndexWord ("dog", POS. NOUN );
        IWordID wordID = idxWord.getWordIDs ().get (0) ; // 1st meaning
        IWord word = dict.getWord ( wordID );
        ISynset synset = word.getSynset ();

        // get the hypernyms
        List < ISynsetID > hypernyms =
                 synset.getRelatedSynsets (Pointer.HYPERNYM);

        // print out each h y p e r n y m s id and synonyms
        List<IWord > words ;
        for( ISynsetID sid : hypernyms ){
            words = dict.getSynset(sid).getWords ();
            Log.d("",sid + " {");
            for(Iterator<IWord > i = words.iterator(); i.hasNext () ;){
                Log.d("",i.next().getLemma ());
                 if(i.hasNext())
                    Log.d("",", ");
                }
             Log.d ("","}");
            }
         }

}

