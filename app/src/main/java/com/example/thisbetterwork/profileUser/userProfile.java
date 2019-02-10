package com.example.thisbetterwork.profileUser;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.thisbetterwork.InstaHome.InstaHomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.example.thisbetterwork.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.URI;
import java.net.URL;

public class userProfile extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 3;
    private static final int SELECT_FILE = 2;

    EditText username, status;
    ImageView userImage;
    LinearLayout saveProfile;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;


    DatabaseReference userDatabase;
    StorageReference mStorageref;

    Uri imageHoldUri = null;

    ProgressDialog mprogress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        username = (EditText) findViewById(R.id.userProfileName);
        status = (EditText) findViewById(R.id.userProfStatus);
        saveProfile = (LinearLayout) findViewById(R.id.saveProf);
        userImage = (ImageView) findViewById(R.id.userImage);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                //Check user

                FirebaseUser user = mAuth.getCurrentUser();

                if(user != null){

                    finish();
                    Intent home = new Intent(userProfile.this, InstaHomeActivity.class);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(home);
                }

            }
        };



        mprogress = new ProgressDialog(this);

        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());;
        mStorageref = FirebaseStorage.getInstance().getReference();

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveprof();

            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pickProfPic();
            }
        });

    }

    private void saveprof() {

        final String name, stat;

        name = username.getText().toString().trim();
        stat = status.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(stat)){

            if(imageHoldUri != null){

                mprogress.setTitle("Saving profile");
                mprogress.setMessage("Please wait");
                mprogress.show();

                String profpicurl = imageHoldUri.getLastPathSegment();
                StorageReference mChildStorage = mStorageref.child("User Profile").child(profpicurl);

                mChildStorage.putFile(imageHoldUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        final Task<Uri> imageUri = mStorageref.getDownloadUrl();

                        userDatabase.child("Username").setValue(name);
                        userDatabase.child("Status").setValue(stat);
                        userDatabase.child("userId").setValue(mAuth.getCurrentUser().getUid());
                        userDatabase.child("ImageUrl").setValue(imageUri.toString());

                        mprogress.dismiss();

                        finish();

                        Intent home = new Intent(userProfile.this, InstaHomeActivity.class);
                        home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(home);

                        

                    }
                });


            }else {

                Toast.makeText(this, "Please select profile pic", Toast.LENGTH_SHORT).show();
                
            }

        }else{

            Toast.makeText(this, "Please enter username and status", Toast.LENGTH_SHORT).show();

        }

    }


    private void pickProfPic() {

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(userProfile.this);
        builder.setTitle("Add Photo!");

        //SET ITEMS AND THERE LISTENERS
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();


    }

    private void cameraIntent() {

        //CHOOSE CAMERA
        Log.d("gola", "entered here");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void galleryIntent() {

        //CHOOSE IMAGE FROM GALLERY
        Log.d("gola", "entered here");
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_FILE);
    }



        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);


            //SAVE URI FROM GALLERY
            if(requestCode == 2 && resultCode == RESULT_OK)
            {
                Uri imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);

            }else if (requestCode == 1 && resultCode == RESULT_OK ){
                //SAVE URI FROM CAMERA

                Uri imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);

            }


            //image crop library code
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    imageHoldUri = result.getUri();

                    userImage.setImageURI(imageHoldUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();

                    Log.i("Error", error.toString());


                }
            }

        }




}
