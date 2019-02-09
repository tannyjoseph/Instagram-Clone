package com.example.thisbetterwork;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thisbetterwork.InstaHome.InstaHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText email, password;
    LinearLayout login, createAcc;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    ProgressDialog mprogress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView imageView = (ImageView) findViewById(R.id.instaic);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        login = (LinearLayout) findViewById(R.id.LoginMain);
        createAcc = (LinearLayout) findViewById(R.id.createAcc);

        mprogress = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = mAuth.getCurrentUser();

                if(user != null){

                    Intent home = new Intent(MainActivity.this, InstaHomeActivity.class);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(home);

                }

            }
        };

        mAuth.addAuthStateListener(mAuthListener);


        TextView googleSign = (TextView) findViewById(R.id.SignInGoogle);
        TextView or = (TextView) findViewById(R.id.or);

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this, RegisterUser.class));

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mprogress.setTitle("Please wait");
                mprogress.setMessage("Logging in ..");
                mprogress.show();

                loginUser();

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

    private void loginUser() {

        String useremail, userpass;

        useremail = email.getText().toString().trim();
        userpass = password.getText().toString().trim();

        if(!TextUtils.isEmpty(useremail) && !TextUtils.isEmpty(userpass)){

            mAuth.signInWithEmailAndPassword(useremail,userpass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
               
                    if(task.isSuccessful()){

                        mprogress.dismiss();

                        Intent home = new Intent(MainActivity.this, InstaHomeActivity.class);
                        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(home);                        
                        
                    }else{

                        mprogress.dismiss();

                        Toast.makeText(MainActivity.this, "Unable to login user..", Toast.LENGTH_SHORT).show();
                        
                    }
                    
                }
            });



            Intent home = new Intent(MainActivity.this, InstaHomeActivity.class);
            home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(home);

        }else{
            mprogress.dismiss();

            Toast.makeText(MainActivity.this, "Please enter email and password ", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
