package org.tensorflow.demo.photoSearch;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.tensorflow.demo.photoSearch.AccessorsAndSetters.DeviceTesters;
import org.tensorflow.demo.R;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by mgo983 on 8/4/17.
 */

public class LoginActivity extends Activity{


    private EditText mFirstNameEditText;
    private EditText mLastNameEditText;

    public static final String USERS_CHILD = "users";
    public final static String USER_ID_NAME = "com.example.android.sunshine.USER_ID_NAME";
    public final static String USER_ID_KEY = "com.example.android.sunshine.USER_ID_KEY";


    //private DatabaseReference mFirebaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);
        setContentView(R.layout.user_login);


        Button mSignUp = (Button) findViewById(R.id.signup_submit);
        Button mSignIn = (Button) findViewById(R.id.signin_submit);
        mFirstNameEditText = (EditText) findViewById(R.id.firstName);
        mLastNameEditText = (EditText) findViewById(R.id.lastName);
        mFirstNameEditText.setText("mmachi");
        mLastNameEditText.setText("obiorah");

        final DatabaseReference mFirebaseReference = FirebaseDatabase.getInstance().getReference("users").child("");


        mSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String newFirstName = mFirstNameEditText.getText().toString();
                final String newLastName = mLastNameEditText.getText().toString();

                DeviceTesters deviceTesters = new DeviceTesters(newFirstName, newLastName);
                mFirebaseReference.child(newFirstName.toLowerCase() + newLastName.toLowerCase()).setValue(deviceTesters);

                mFirstNameEditText.setText("");
                mLastNameEditText.setText("");


            }


        });

        final Activity thisActivity = this;

        mSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DatabaseReference mFirebaseReference = FirebaseDatabase.getInstance().getReference(USERS_CHILD);
                final String newFirstName = mFirstNameEditText.getText().toString();
                final String newLastName = mLastNameEditText.getText().toString();
                final String childKey = newFirstName.toLowerCase() + newLastName.toLowerCase();

                mFirebaseReference.orderByChild("users").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (childKey.equals(dataSnapshot.getKey().toString())){
                            SharedPreferences sharedPreference;
                            SharedPreferences.Editor editor;
                            sharedPreference = getApplicationContext().getSharedPreferences(USER_ID_NAME, getApplicationContext().MODE_PRIVATE);
                            editor = sharedPreference.edit();

                            editor.putString(USER_ID_KEY, dataSnapshot.getKey());
                            editor.commit();
                            Log.v("for your glory", dataSnapshot.toString());

                            Intent intent = new Intent(thisActivity, NewMainActivity.class);
                            startActivity(intent);
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
        });
        //String userLoggedIn = new String ("User logged in");
        //Toast.makeText(getApplicationContext(),userLoggedIn, Toast.LENGTH_LONG).show();
    }


}
