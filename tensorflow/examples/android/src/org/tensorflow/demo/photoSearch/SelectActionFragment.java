package org.tensorflow.demo.photoSearch;

import android.content.Intent;
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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

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
    static final File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), applicationDirectory);


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
                Uri photoURI = testUri = GetFileUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }


            }
        });


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
            detailFragment.putExtra(EXTRA_IMAGE,testUri);
            startActivity(detailFragment);


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

    private Uri GetFileUri(){
        createFile();
        return FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".org.tensorflow.demo.provider", imageFile);

    }

}
