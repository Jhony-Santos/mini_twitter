package com.example.twitter_mini;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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

    private String myUid;
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

                ref.child("users").child(myUid).child("seguindo").setValue(following);
            }
        });

    }

    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_twitter,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int option=item.getItemId();

       if(option==R.id.feed){

       }else if(option==R.id.tweet){

       }else if(option==R.id.logout){
           FirebaseAuth.getInstance().signOut();
           finish();
       }

       return super.onOptionsItemSelected(item);
    }

}