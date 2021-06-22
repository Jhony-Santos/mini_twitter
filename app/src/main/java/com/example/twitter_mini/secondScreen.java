package com.example.twitter_mini;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class secondScreen extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter arrayAdapter;

    private ArrayList<String> users;
    private ArrayList<String> userids;
    private ArrayList<String> following;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();

    private ChildEventListener childEventListener;
    private ValueEventListener valueEventListener;

    private String myId;
    private String myName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_second_screen);

        following = new ArrayList<>();
        users = new ArrayList<>();
        userids = new ArrayList<>();

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, users);
        listView = (ListView) findViewById(R.id.listView);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()) {
                    following.add(userids.get(position));
                } else {
                    following.remove(following.indexOf(userids.get(position)));
                }

                ref.child("users").child(myId).child("following").setValue(following);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user == null) {
            finish();
        } else {
            myId = user.getUid();
            System.out.println(myId);
            ref.child("users").child(myId).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    myName = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            userids.clear();
            users.clear();
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if(dataSnapshot.child("id").getValue(String.class)!=null){

                        if(!dataSnapshot.child("id").getValue(String.class).equals(myId)){
                            users.add(dataSnapshot.child("name").getValue(String.class));
                            userids.add(dataSnapshot.child("id").getValue(String.class));
                            arrayAdapter.notifyDataSetChanged();
                            updateList();
                        }


                    }

                    else{
                        System.out.println("VALOR NULO");
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
            };
            ref.child("users").addChildEventListener(childEventListener);

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    following.clear();
                    for(DataSnapshot data:dataSnapshot.getChildren()){
                        following.add(data.getValue(String.class));
                    }
                    Log.d("Log", "following: " + following);
                    updateList();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            ref.child("users").child(myId).child("following").addValueEventListener(valueEventListener);
        }
    }

    public void updateList(){
        for(String uid:userids){
            if(following.contains(uid)){
                listView.setItemChecked(userids.indexOf(uid), true);
            } else {
                listView.setItemChecked(userids.indexOf(uid), false);
            }
        }
    }

    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_twitter,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        int option = item.getItemId();

        if (option == R.id.feed) {

            Intent i = new Intent(getApplicationContext(), MyFeeds.class);
            i.putStringArrayListExtra("following", following);
            startActivity(i);


        }else if (option == R.id.tweet) {


            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Send a tweet");
            final EditText conteudoTweet = new EditText(this);
            builder.setView(conteudoTweet);

            builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Map<String, Object> tweet = new HashMap<>();
                    tweet.put("message", conteudoTweet.getText().toString());
                    tweet.put("id", myId);
                    tweet.put("data", -1 * System.currentTimeMillis());
                    tweet.put("name", myName);
                    ref.child("tweets").push().setValue(tweet);

                    Toast.makeText(getApplicationContext(), "tweet send", Toast.LENGTH_LONG).show();
                }
            });

            builder.setNegativeButton("canceled", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();

                }
            });

            builder.show();

            return true;


        } else if (option == R.id.logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

}