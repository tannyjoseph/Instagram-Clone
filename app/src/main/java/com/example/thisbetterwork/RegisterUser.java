package com.example.thisbetterwork;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.thisbetterwork.InstaHome.InstaHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterUser extends AppCompatActivity {

    EditText emailUser, passwordUser;
    LinearLayout create;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        emailUser = (EditText) findViewById(R.id.emailreg);
        passwordUser = (EditText) findViewById(R.id.passwordreg);

        create = (LinearLayout) findViewById(R.id.createUser);

        mProgress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    Intent home = new Intent(RegisterUser.this, InstaHomeActivity.class);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(home);
                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgress.setTitle("Creating Account");
                mProgress.setMessage("Please wait while account is being created...");
                mProgress.show();

                createUserAccount();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void createUserAccount() {

        final String useremail, userpass;

        useremail = emailUser.getText().toString().trim();
        userpass = passwordUser.getText().toString().trim();

        if(!TextUtils.isEmpty(useremail) && !TextUtils.isEmpty(userpass)){
            mAuth.createUserWithEmailAndPassword(useremail, userpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        Toast.makeText(RegisterUser.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        mProgress.dismiss();

                        Intent home = new Intent(RegisterUser.this, InstaHomeActivity.class);
                        home.putExtra("Email",useremail);
                        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(home);
                    }else{

                        Toast.makeText(RegisterUser.this, "Account creation failed.", Toast.LENGTH_SHORT).show();
                        mProgress.dismiss();
                    }

                }
            });
        }

    }
}
