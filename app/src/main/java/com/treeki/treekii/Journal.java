package com.treeki.treekii;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Journal extends AppCompatActivity {

    private EditText answer_edit;
    private EditText tags_edit;
    private String answer;
    private String tag_string;
    private String[] tags;
    private DatabaseReference mDatabase;
    private FirebaseUser user;
    private static final String TAG = "QoTD_Activity";
    String month;
    String day;
    String year;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        user = FirebaseAuth.getInstance().getCurrentUser();
        //get date
        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH)+1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));

        answer_edit = findViewById(R.id.answer);
        tags_edit = findViewById(R.id.tags);

        //get Database ref
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void submit(View view) {
        if (month.length() == 1) month = "0"+month;
        if (day.length() == 1) day = "0"+day;
        String date = month+"-"+day+"-"+year;
//
//        //Save the answer
        answer = answer_edit.getText().toString();
        tag_string = tags_edit.getText().toString();
        tags = tag_string.split("\\s*,\\s*");
        if (!answer.equals("")) {
            mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").child("answer").setValue(answer);
            if(tags.length>0) {
                for (int i = 0; i < tags.length; i++) {
                    mDatabase.child("Users").child(user.getUid()).child(tags[i]).child(date).setValue(true);
                }
            }
            Toast.makeText(getApplicationContext(), "Journal submitted!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Please input an answer.", Toast.LENGTH_SHORT).show();
        }

    }
}
