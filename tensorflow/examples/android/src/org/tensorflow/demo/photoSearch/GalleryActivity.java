package org.tensorflow.demo.photoSearch;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NavUtils;

import android.support.v7.app.ActionBar;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.GridView;

import android.widget.Toast;


import org.tensorflow.demo.photoSearch.AccessorsAndSetters.Photos;
import org.tensorflow.demo.photoSearch.Adapter.ImageGridAdapter;
import org.tensorflow.demo.photoSearch.data.AddWord;
import org.tensorflow.demo.R;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * Created by mgo983 on 4/21/17.
 */

public class GalleryActivity extends AppCompatActivity implements  SafeAction.OnokOrCancel{

    public final static String IMGFILENAME = "com.example.android.sunshine.IMG_FILE_NAME";
    public final static String IMGFILEKEY = "com.example.android.sunshine.IMG_FILE_KEY";

    public final static String USER_ID_NAME = "com.example.android.sunshine.USER_ID_NAME";
    public final static String USER_ID_KEY = "com.example.android.sunshine.USER_ID_KEY";

    public final static String EXTRA_SAFE_ACTION_MSG = "com.example.android.sunshine.safeActionMessage";
    public final static String EXTRA_SAFE_ACTION_MENU_ITEM = "com.example.android.sunshine.safeActionMenuItem";

    public static final String PHOTOS_CHILD = "photos";
    //private static final int REQUEST_IMAGE = 2;
    private static final String TAG = "GalleryActivity";
    //private static final int REQUEST_CODE = 1;

    public static boolean ONLONGCLICKMODE = false;
    public static boolean SELECTALLIMAGES = false;


    //GalleryPagerAdapter galleryPagerAdapter;

    ImageGridAdapter imageGridAdapter;



    String ExternalStorageDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            .getAbsolutePath();
    String targetPath = ExternalStorageDirectoryPath + "/Aphasia/";
    File targetDirectory = new File(targetPath);

    File[] files = targetDirectory.listFiles();
    int fileLength = files.length;
    String[] NewFileNames = new String[fileLength];



    @Override
    public void onCreate(Bundle savedInstanceState){


        Toast.makeText(getApplicationContext(), targetPath, Toast.LENGTH_LONG).show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //galleryPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager());
        //viewPager = (ViewPager) findViewById(R.id.gallery_container);

        File[] files = targetDirectory.listFiles();
        int fileLength = files.length;
        final String[] fileNames = new String[fileLength];
        final Long[] fileDateModified = new Long[fileLength];

        //enter value and index into hash map
        HashMap<Long, Integer> NameAndDateModified = new HashMap<>();
        HashMap<File, Integer> newFiles = new HashMap<>();


        for (int i = 0; i < fileLength; i++){
            fileNames[i] = files[i].getAbsolutePath();
            fileDateModified[i] = files[i].lastModified();
            NameAndDateModified.put(fileDateModified[i], i);
            Log.v("Date modified", "date modified" + fileDateModified[i]);
        }


       //order by date modified
        List<Long> fileDateModifiedList = Arrays.asList(fileDateModified);
        Collections.sort(fileDateModifiedList);

        NewFileNames = new String[fileLength];

        for (int i = 0; i < fileLength; i++){
            NewFileNames[i] = fileNames[NameAndDateModified.get(fileDateModifiedList.get(fileLength - i - 1))];
        }

        CheckBox imageButton = (CheckBox) findViewById(R.id.btn_slide);

        imageGridAdapter = new ImageGridAdapter(this, NewFileNames, imageButton);
        final GridView gridView = (GridView) findViewById(R.id.image_gridview);
        gridView.setAdapter(imageGridAdapter);


        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);

    }



    public void imageAnimation(GridView gridView, int position){
        final FrameLayout child = (FrameLayout) gridView.getChildAt(position);
        if (child != null){
            child.animate().scaleX(0.8f);
            child.animate().scaleY(0.8f);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    child.animate().scaleX(1.0f);
                    child.animate().scaleY(1.0f);

                }
            }, 200);
        }

    }

    public void checkCurrentItem(View view){
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.image_checkbox);
        checkBox.setVisibility(View.VISIBLE);
        checkBox.setChecked(true);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (ONLONGCLICKMODE){
            menu.getItem(0).setVisible(true);
            menu.getItem(1).setVisible(true);
            menu.getItem(2).setVisible(true);
            menu.getItem(3).setVisible(true);
            menu.getItem(4).setVisible(true);

        }else {

            menu.getItem(0).setVisible(false);
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(4).setVisible(true);


        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_item_save:
                safeAction("Save your selection?", "save");
                break;
            case R.id.menu_item_delete:
                safeAction("Delete your selection?", "delete");
                break;
            case R.id.menu_item_add_category:
                addCategory();
                //create a dialog
                //retrieve item from dialog and save
                break;
            case R.id.menu_item_add_word:
                Intent intentAddWord = new Intent(this, AddWord.class);
                startActivity(intentAddWord);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    ;
    private void addCategory(){
        android.app.DialogFragment newFragment = new TextDialog();
        Bundle bundle = new Bundle();
        bundle.putString(TextDialog.DATABASECALLERTYPE, "nil"/*TextDialog.UPDATEWORD*/);
        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(), "TextDialog");
    }


    private void safeAction(String message, String menuID){
        SafeAction newFragment = SafeAction.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_SAFE_ACTION_MSG, message);
        bundle.putString(EXTRA_SAFE_ACTION_MENU_ITEM, menuID);

        newFragment.setArguments(bundle);
        newFragment.show(getFragmentManager(),"SAFE_ACTION_MSG");

    }

    void deleteImgAndTxtFiles(){
        final GridView gridView = (GridView) findViewById(R.id.image_gridview);
        ArrayList<Integer> checkedPhotosPosition = imageGridAdapter.getCheckedboxes(gridView);

        for (Integer position : checkedPhotosPosition){
            String fileNamePath = NewFileNames[position];
            File jpgDeletionFile = new File(fileNamePath);
            File txtDeletionFile = new File(getFileIdForDeletion(fileNamePath)[0]);
            //String[] fileName = getFileIdForDeletion(fileNamePath);
            if (jpgDeletionFile.delete() && txtDeletionFile.delete()){
                Log.v("Deleted file","Deleted file");
            }else{
                //Log.v(fileName[0], fileName[1]);
            }
        }
            //(deleteFile(fileName + ".jpg") && deleteFile(fileName + ".jpg"));

    }

    void sendMessage(){
        //final DatabaseReference mFirebaseReference = FirebaseDatabase.getInstance().getReference(PHOTOS_CHILD).child("");
        SharedPreferences sharedPreferences;
        String userName;
        sharedPreferences = getApplicationContext().getSharedPreferences(USER_ID_NAME, Context.MODE_PRIVATE);
        userName = sharedPreferences.getString(USER_ID_KEY, null);
        final GridView gridView = (GridView) findViewById(R.id.image_gridview);
        ArrayList<Integer> checkedPhotosPosition = imageGridAdapter.getCheckedboxes(gridView);
        String mPhotoId;
        Photos photos;




        for (Integer position : checkedPhotosPosition){
            final String oldFileName = NewFileNames[position];
            final String[] newName = getFileId(oldFileName);
            String fileCaption = MyFileReader.readFromFile(this, newName[1]);

            photos = new Photos(
                    userName, //users
                    "", //date_taken
                    "", //time_taken
                    fileCaption, //name_caption
                    oldFileName //photo_url

            );
            mPhotoId = newName[0];

            final DatabaseReference mFirebaseReference = FirebaseDatabase.getInstance().getReference(PHOTOS_CHILD).child("");
            final Uri fileUri = Uri.fromFile(new File(oldFileName));
            //Photos photos = new Photos();
            mFirebaseReference.child(mPhotoId).setValue(photos, new DatabaseReference.CompletionListener(){
                @Override
                public void onComplete(DatabaseError databaseError,
                                       DatabaseReference databaseReference){
                    StorageReference storageReference = FirebaseStorage.getInstance()
                            .getReference(PHOTOS_CHILD)
                            .child(newName[0])
                            .child(oldFileName);
                    storageReference.putFile(fileUri);
                }
            });

        }
    }



    public String[] getFileId (String oldFileName){

        String fileNameSubstring = oldFileName.substring(oldFileName.lastIndexOf("/") + 1);

        String[] newFileName = new String[2];

        newFileName[0] = fileNameSubstring.replace(",","")
                .replace(":","")
                .replace(".jpg","");
        newFileName[1] = fileNameSubstring.replace(".jpg", ".txt");
        Log.v("new file name", " " + newFileName);

        return newFileName;

    }

    public String[] getFileIdForDeletion(String oldFileName){

        String[] newFileName = new String[2];
        String fileNameSubstring = oldFileName.substring(oldFileName.lastIndexOf("/") + 1);

        newFileName[0] = fileNameSubstring.replace(",","")
        .replace(":","")
        .replace(".jpg",".txt");
        newFileName[1] = fileNameSubstring;

        return newFileName;
    }

    boolean OKCANCEL = false;
    @Override
    public void okOrCancel(boolean okOrCancel, String menuID){
        Log.v(TAG, "menu item: " + OKCANCEL);
        if (okOrCancel){
           switch (menuID){
               case "save":
                   sendMessage();
                   break;
               case "delete":
                   deleteImgAndTxtFiles();
                   break;
           }
            onBackPressed();
        }

    }

    @Override
    public void onBackPressed(){
        GridView gridView = (GridView) findViewById(R.id.image_gridview);
        CheckBox imageButton = (CheckBox) findViewById(R.id.btn_slide);

        if (ONLONGCLICKMODE){
            ONLONGCLICKMODE = false;
            SELECTALLIMAGES = false;
            invalidateOptionsMenu();
            imageGridAdapter.notifyDataSetChanged();
            //imageGridAdapter.invisibleCheckboxes(gridView);
            //imageButton.setVisibility(View.INVISIBLE);

            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_HOME_AS_UP);
            actionBar.setDisplayShowHomeEnabled(true);

        }else{
            super.onBackPressed();
        }
    }


}
