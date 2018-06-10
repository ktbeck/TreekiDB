package com.treeki.treekii;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
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
    private CheckBox priv;
    private boolean checked;
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
        //this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);

        //Remove notification bar

        //this.getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN, android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(R.layout.activity_journal);
        //android.app.ActionBar actionBar = getActionBar();
        //actionBar.hide();
        getSupportActionBar().hide();
        user = FirebaseAuth.getInstance().getCurrentUser();
        //get date
        Calendar cal = Calendar.getInstance();
        month = Integer.toString(cal.get(Calendar.MONTH)+1);
        day = Integer.toString(cal.get(Calendar.DATE));
        year = Integer.toString(cal.get(Calendar.YEAR));

        answer_edit = findViewById(R.id.answer);
        tags_edit = findViewById(R.id.tags);
        priv = (CheckBox) findViewById(R.id.checkBox);

        //get Database ref
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //Remove title bar


        //set content view AFTER ABOVE sequence (to avoid crash)
        //this.setContentView(R.layout.activity);
    }

    public void submit(View view) {
        if (priv.isChecked()) checked = true;
        else checked = false;

        String date = month+"-"+day+"-"+year;

//        //Save the answer
        answer = answer_edit.getText().toString();
        tag_string = tags_edit.getText().toString();
        tag_string = tag_string.replaceAll("[\\/\\#\\[\\]\\$\\.]","");
        tags = tag_string.split("\\s*,\\s*");
        Log.i(TAG,"Tags: ");
        for (int i = 0; i < tags.length; i++) {
            Log.i(TAG,tags[i]);
        }
        if (!answer.equals("")) {
            mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").child("answer").setValue(answer);
            mDatabase.child("Users").child(user.getUid()).child(date).child("Journal").child("private").setValue(checked);
            if(tags.length>0) {
                for (int i = 0; i < tags.length; i++) {
                    mDatabase.child("Users").child(user.getUid()).child("tags").child(tags[i]).child(date).setValue(true);
                }
            }
            Toast.makeText(getApplicationContext(), "Journal submitted!", Toast.LENGTH_SHORT).show();
            startMainMenu();
        }
        else {
            Toast.makeText(getApplicationContext(), "Please input an answer.", Toast.LENGTH_SHORT).show();
        }

    }
    private void startMainMenu() {
        Intent menu = new Intent(this, MainMenuTest.class);
        startActivity(menu);
    }

    public void skip(View view){
        showNotification("Treeki", "Don't forget to come back and fill in your daily question/journal.");
        startMainMenu();
    }

    void showNotification(String title, String content) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "TreekiNotification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("TREEKI_NOTIFICATION_CHANNEL");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(content)// message for notification
                .setSound(uri) // set alarm sound for notification
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), QoTD.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
    //logout button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        // the menu being referenced here is the menu.xml from res/menu/menu.xml
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    /* Here is the event handler for the menu button that I forgot in class.
    The value returned by item.getItemID() is
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, String.format("" + item.getItemId()));
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_favorite:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(),"Signed out", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, SignInRegister.class);
                startActivity(i);
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

}
