package com.example.osamakhalid.realtimeauctionsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.osamakhalid.realtimeauctionsystem.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Spinner mySpinner;
    private String categoryValue = "";
    private EditText email;
    private EditText password;
    private Button loginbutton;
    private ProgressDialog progressDialog;
    private String emailadmin = "admin@parking.com";
    private String passwordadmin = "123456";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(this);
        databaseReference = firebaseDatabase.getReference().child("Users");

        if (firebaseAuth.getCurrentUser() != null) {
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            System.out.println("checkff checking=" +databaseReference.orderByChild("key").equalTo(firebaseAuth.getCurrentUser().getUid()));
            Query query=databaseReference;
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot snap:dataSnapshot.getChildren()){
                        User user = snap.getValue(User.class);
                        if(user!=null &&  user.getUserid().equals(firebaseAuth.getCurrentUser().getUid())){
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Signed In.", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(MainActivity.this, LoginScreen.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("type", user.getType());
                            startActivity(i);
                            finish();
                            break;
                            }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    System.out.println("checkff coming");
//
//                    }
//
//                    @Override
//                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//                    }
//
//                    @Override
//                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
        }
        loginbutton=(Button) findViewById(R.id.loginbutton);
        email=(EditText) findViewById(R.id.email);
        password=(EditText) findViewById(R.id.password);

        Button signup=(Button) findViewById(R.id.signupbutton);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i1= new Intent(MainActivity.this,Register.class);
                i1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i1);
                finish();
            }
        });
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                if(TextUtils.isEmpty(email.getText().toString())){
                    email.setError("Enter email");
                    progressDialog.dismiss();
                }
                if(TextUtils.isEmpty(password.getText().toString())){
                    password.setError("Enter password");
                    progressDialog.dismiss();
                }
                else{
                    System.out.println("checkff email=" +email.getText().toString());
                    System.out.println("checkff password=" +password.getText().toString());
                    firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        databaseReference = firebaseDatabase.getReference().child("Users");
                                        Query query = databaseReference;
                                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                User user = null;
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    if (firebaseAuth.getCurrentUser().getUid().equals(snapshot.getKey())) {
                                                        user = snapshot.getValue(User.class);
                                                        break;
                                                    }
                                                }
                                                if (user == null) {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "Failed to Sign In, please try again1", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    progressDialog.dismiss();
                                                    Toast.makeText(MainActivity.this, "Signed In.", Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(MainActivity.this, LoginScreen.class);
                                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    i.putExtra("type", user.getType());
                                                    startActivity(i);
                                                    finish();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(MainActivity.this, "Failed to Sign In, please try again2", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            });
                }
            }
        });
    }
}
