package org.tensorflow.demo.photoSearch;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.tensorflow.demo.photoSearch.AccessorsAndSetters.WordCategories;
import org.tensorflow.demo.photoSearch.data.AddWord;
import org.tensorflow.demo.R;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mgo983 on 11/6/17.
 */

public class TextDialog extends DialogFragment{

    public static final String DATABASECALLERTYPE = "com.example.android.sunshine.app.DATABASECALLERTYPE";
    public static final String ADDCATEGORY = "addCategory";
    public static final String ADDWORD = "addWord";
    public static final String WORDCATEGORY = "com.example.android.sunshine.app.WORDCATEGORY";
    public static final String UPDATEWORD = "com.example.android.sunshine.app.UPDATEWORD";
    public static final String CREATE_WORD_DATABASE = "com.example.android.sunshine.app.CREATE_WORD_DATABASE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.text_dialog, container, false);

        Button cancelButton = (Button) rootView.findViewById(R.id.text_dialog_cancel);

        Bundle bundle = this.getArguments();

        switchCaller(rootView,bundle);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // Inflate the layout to use as dialog or embedded fragment
        return rootView;
    }

    /** The system calls this only when creating the layout in a dialog. */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.


        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        return dialog;
    }


    public void switchCaller(View rootView, final Bundle bundle){

        Button okButton = (Button)  rootView.findViewById(R.id.text_dialog_ok);

        final String caller = bundle.getString(DATABASECALLERTYPE);

        final EditText textViewCategory = (EditText) rootView.findViewById(R.id.text_dialog_category);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String category = textViewCategory.getText().toString();
                switch (caller){
                    case ADDCATEGORY:
                        final DatabaseReference mFirebaseReference = FirebaseDatabase.getInstance().getReference(AddWord.WORD_CATEGORY_CHILD).child("");
                        WordCategories wordCategories = new WordCategories(category);
                        mFirebaseReference.child(category).setValue(wordCategories);
                        break;
                    case ADDWORD:
                        /*final String wordCategory = bundle.getString(WORDCATEGORY);
                        final String  imageFileName = category;
                        final AphasiaWords aphasiaWords = new AphasiaWords(imageFileName);
                        final DatabaseReference newFirebaseReference = FirebaseDatabase.getInstance()
                                .getReference(AddWord.WORD_CATEGORY_CHILD + "/" + wordCategory)
                                .child("");
                               newFirebaseReference.child(imageFileName).setValue(aphasiaWords);*/

                        break;
                    case UPDATEWORD:
                        //Updates words in the database.
                        final AssetManager assetManager = getActivity().getAssets();
                        try{
                            String [] listOfAssets = assetManager.list("aphasiaWords");
                            List<String> mLines = new ArrayList<String>();
                            for (int i = 0; i < listOfAssets.length; i++)
                            {
                                Log.d("List of assets: ", listOfAssets[i]);
                                InputStream is = assetManager.open("aphasiaWords/" + listOfAssets[i]);
                                String wordCategory = listOfAssets[i].replace(".txt","");
                                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                                String line;

                                //mDatabase = FirebaseDatabase.getInstance().getReference();
                                //String key = mDatabase.child("posts").push().getKey();

                                while ((line = reader.readLine()) != null){
                                    mLines.add(line);
                                    String word = getWordString(line);

                                    final AphasiaWords aphasiaWords = new AphasiaWords(word, line);
                                    final DatabaseReference newFirebaseReference = FirebaseDatabase.getInstance()
                                            .getReference(AddWord.WORD_CATEGORY_CHILD + "/" + wordCategory);

                                    final String key = newFirebaseReference.child(wordCategory).push().getKey();

                                    newFirebaseReference.child(key).setValue(aphasiaWords);
                                    Log.d("Words in text ", word);
                                }
                            }

                        }catch(IOException e){

                    }



                        break;

                    case CREATE_WORD_DATABASE:
                        /*final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference(AddWord.WORD_REFERENCE);
                        mDatabaseReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                HashMap UniqueFileName = new HashMap();
                                for (DataSnapshot child: dataSnapshot.getChildren()){
                                    if (child.getValue() instanceof String){
                                        String databasePath = (String) child.getValue();
                                        String[] getFileName = databasePath.split("/");
                                        String fileName = getFileName[2];
                                        String key = getFileName[1];
                                        String category = getFileName[0];
                                        Log.d("The FileNames: ", fileName + UniqueFileName.get(fileName));
                                        if (UniqueFileName.get(fileName + category) == null){
                                            // place in UniqueFileName
                                            UniqueFileName.put(fileName + category, category);

                                        }else if (UniqueFileName.get(fileName + category).equals(category)){
                                            //remove from word category
                                            child.getRef().removeValue();
                                            //Log.d("Word Reference: ", child.getRef().toString());
                                            //remove from word
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("word_categories/" + category ).child(key);
                                            ref.removeValue();
                                        }
                                    }

                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        //remove children with smarty symbol children
                        /*final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference(AddWord.WORD_CATEGORY_CHILD);
                        mDatabaseReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                dataSnapshot.getRef().addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        String key = dataSnapshot.getKey();
                                        if (dataSnapshot.getValue() instanceof HashMap) {
                                            HashMap map = (HashMap) dataSnapshot.getValue();

                                            if ((map.get("word") != null) && (map.get("word").toString().equals("smarty symbols logo  heads"))) {
                                                dataSnapshot.getRef().removeValue();
                                                Log.e("Entries", map.get("fileName").toString());

                                            }
                                        }
                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });*/

                        //created all of the words children
                        /*final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference(AddWord.WORD_CATEGORY_CHILD);
                        final DatabaseReference wordDatabaseReference = FirebaseDatabase.getInstance().getReference(AddWord.WORD_REFERENCE).child("");
                        final String WORD_IMAGE_REFERENCE  = "symbols";
                        mDatabaseReference.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                final String categories =  dataSnapshot.getKey().toString();
                                mDatabaseReference.child(categories).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                        HashMap aphasiaWords = new HashMap();
                                        if(dataSnapshot.getValue() instanceof HashMap){
                                            aphasiaWords = (HashMap) dataSnapshot.getValue();
                                        }
                                        String fileName = (String) aphasiaWords.get("fileName");
                                        String word = (String) aphasiaWords.get("word");
                                        if (word != null){
                                            DatabaseReference newDatabaseReference =  wordDatabaseReference.child(word.replace(".",""));
                                            String key =newDatabaseReference.push().getKey();
                                            newDatabaseReference.child(key).setValue(categories + "/" + dataSnapshot.getKey() + "/" + fileName);
                                        }


                                    }

                                    @Override
                                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });*/
                        break;

                }

            }
        });

    }

    String getWordString(String imageFileName){
        String wordString = imageFileName.toLowerCase();

        //remove .png after -
        if(wordString.contains("-")) wordString = wordString.substring(0, wordString.lastIndexOf("-"));
        if (wordString.contains("-al"))wordString = wordString.replace("-al","");
        if (wordString.contains("_al"))wordString = wordString.replace("_al","");
        if (wordString.contains("-")) wordString = wordString.replace("-", " ");
        if(wordString.contains("_")) wordString = wordString.replace("_"," ");

        //remove .png if there is no "-"
        if(wordString.contains(".png")) wordString = wordString.replace(".png", "");

        //remove numbers
        if(wordString.contains("0")) wordString = wordString.replace("0","");
        if(wordString.contains("1")) wordString = wordString.replace("1","");
        if(wordString.contains("2")) wordString = wordString.replace("2","");
        if(wordString.contains("3")) wordString = wordString.replace("3","");
        if(wordString.contains("4")) wordString = wordString.replace("4","");
        if(wordString.contains("5")) wordString = wordString.replace("5","");
        if(wordString.contains("6")) wordString = wordString.replace("6","");
        if(wordString.contains("7")) wordString = wordString.replace("7","");
        if(wordString.contains("8")) wordString = wordString.replace("8","");
        if(wordString.contains("9")) wordString = wordString.replace("9","");

        if(wordString.contains("maria")) wordString = wordString.replace("maria","");

        if(wordString.contains("violet")) wordString = wordString.replace("violet","");

        if(wordString.contains("will") & !wordString.equals("will")) wordString = wordString.replace("will", "");


        return wordString.trim();
    }
}
