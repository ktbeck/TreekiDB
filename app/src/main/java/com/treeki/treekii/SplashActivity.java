package com.treeki.treekii;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class SplashActivity extends Activity {
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private static final String TAG = "SplashActivity";

    String question;
    String month;
    String day;
    String year;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH)+1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));
        date = month+"-"+day+"-"+year;

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            goToNextActivity();
        }
        else {
            startSignIn();
        }
    }
    private void startQoTD() {
        mDatabase.child("Questions").child(month).child(day).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //get question at /Questions/date
                        question = dataSnapshot.getValue(String.class);

                        //error handling
                        if (question == null) {
                            Log.e(TAG, "Question at "+month+"/"+day+" is unexpectedly null");
                            Toast.makeText(getApplicationContext(),"can't fetch question",Toast.LENGTH_SHORT).show();
                        }
                        //if no err, send question
                        else {
                            Log.i(TAG, "Question at"+month+"/"+day+" is: "+question);
                            Intent QoTD = new Intent(SplashActivity.this,QoTD.class);
                            QoTD.putExtra("question",question);
                            startActivity(QoTD);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "get Question onCancelled", databaseError.toException());
                    }
                }
        );
    }
    private void startJournal() {
        Intent Journal = new Intent(SplashActivity.this,Journal.class);
        startActivity(Journal);
        finish();
    }


    private void startSignIn() {
        Intent SignIn = new Intent(SplashActivity.this,SignInRegister.class);
        startActivity(SignIn);
        finish();
    }
    private void goToNextActivity() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG,"Signed in: "+user.getUid());

        mDatabase.child("Users").child(user.getUid()).child(date).child("QoTD").child("answer").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //get question at /Questions/0101
                        String answer = dataSnapshot.getValue(String.class);

                        //error handling
                        if (answer == null) {
                            Log.i(TAG,"User signed in but has not filled in QoTD.");
                            startQoTD();
                        }

                        else {

                            mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").child("answer").addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            //get question at /Questions/0101
                                            String answer = dataSnapshot.getValue(String.class);

                                            //error handling
                                            if (answer == null) {
                                                Log.i(TAG,"User signed in and finished QoTD but not journal");
                                                startJournal();
                                            }
                                            //if no err, change the question
                                            else {
                                                //GOTO MAIN MENU
                                                //TODO: MAIN MENU INTENT GOES HERE
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            Log.w(TAG, "get Journal answer onCancelled", databaseError.toException());
                                        }
                                    }
                            );


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "get QoTD answer onCancelled", databaseError.toException());
                    }
                }
        );

    }
}
