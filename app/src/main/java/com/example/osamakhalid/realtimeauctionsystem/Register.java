package com.example.osamakhalid.realtimeauctionsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.osamakhalid.realtimeauctionsystem.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private EditText name;
    private EditText email;
    private EditText password;
    private Button signupbutton;
    private ProgressDialog progressDialog;
    private EditText contactno;
    private Spinner categorySpinner;
    private String categoryValue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseDatabase= FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        name = (EditText) findViewById(R.id.name_signup);
        email = (EditText) findViewById(R.id.email_signup);
        password = (EditText) findViewById(R.id.password_signup);
        signupbutton = (Button) findViewById(R.id.signup_button);
        contactno = (EditText) findViewById(R.id.contactno_signup);
        categorySpinner=(Spinner) findViewById(R.id.category);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Register.this, R.array.category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        categoryValue=categorySpinner.getSelectedItem().toString();
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                categoryValue = categorySpinner.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                if (TextUtils.isEmpty(email.getText().toString())) {
                    Toast.makeText(Register.this, "Please enter e-mail.", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(password.getText().toString())) {
                    Toast.makeText(Register.this, "Please enter password.", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        User user= new User(name.getText().toString(),contactno.getText().toString(),firebaseAuth.getCurrentUser().getUid(),categoryValue);
                                        databaseReference.child(firebaseAuth.getCurrentUser().getUid()).setValue(user);
                                        progressDialog.dismiss();
                                        Toast.makeText(Register.this, "Successfully registered.", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(Register.this, LoginScreen.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        i.putExtra("type", user.getType());
                                        startActivity(i);
                                        finish();
                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(Register.this, "Failed to register.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }

            }
        });
    }
}
