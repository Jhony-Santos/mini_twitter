package com.example.twitter_mini;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Optional;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private EditText fieldName, fieldEmail, fieldPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Toast.makeText(this,"Conectou ao firebase??", Toast.LENGTH_SHORT).show();

        auth= FirebaseAuth.getInstance();

        fieldName=(EditText) findViewById(R.id.editText);
        fieldEmail=(EditText) findViewById(R.id.editText2);
        fieldPassword=(EditText) findViewById(R.id.editText3);

        authStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= firebaseAuth.getCurrentUser();

                if(user !=null){
                    Log.d("Log","User connected:" + user.getUid() );
                    Intent intent = new Intent(getApplicationContext(),secondScreen.class);
                    startActivity(intent);
                }
                else{
                    Log.d("Log","There is not users connected");
                }

            }
        };
    }

    @Override
    public void onStart(){
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(authStateListener != null){
            auth.removeAuthStateListener(authStateListener);
        }
    }

    public void clickLogin(View view){
        auth.signInWithEmailAndPassword(fieldEmail.getText().toString(), fieldPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Log", "Failure on the authentication");
                        }
                    }
                });
        }


    public void createNewUser(View view){

        auth.createUserWithEmailAndPassword(fieldEmail.getText().toString(), fieldPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Log", "Failure on the create new account");
                        } else {
                            // creating the user
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference ref = database.getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            //salving the user in the database firebase
                            ref.child("Name").setValue(fieldName.getText().toString());
                            ref.child("Id").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        }
                    }
                });
        }

    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_twitter,menu);
        return true;
    }

}