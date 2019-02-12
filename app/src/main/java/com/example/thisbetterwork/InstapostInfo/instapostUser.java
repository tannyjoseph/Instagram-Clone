package com.example.thisbetterwork.InstapostInfo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.thisbetterwork.InstaHome.instaPosts;
import com.example.thisbetterwork.R;
import com.example.thisbetterwork.profileUser.userProfile;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class instapostUser extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 3;
    private static final int SELECT_FILE = 2;

    Uri imageHoldUri = null;

    ProgressDialog mprogress;

    ImageView addpostimage;

    Button post;

    EditText Caption;

    DatabaseReference reference;
    StorageReference storageReference;

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instapost_user);

        addpostimage = (ImageView) findViewById(R.id.addpostimage);
        auth = FirebaseAuth.getInstance();


        post = (Button) findViewById(R.id.postimage);
        Caption = (EditText) findViewById(R.id.caption);

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());




        addpostimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    addImage();

            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                savepost();
            }
        });
    }

    private void savepost() {



            final String cap;


            cap = Caption.getText().toString().trim();
            Log.i("blah", cap);

            if(!TextUtils.isEmpty(cap)){


                if(imageHoldUri != null){


                    String url = imageHoldUri.getLastPathSegment();

                    storageReference = FirebaseStorage.getInstance().getReference().child("posts").child(url);


                    reference.child("Posts").child("Caption").push().setValue(cap);


                    startActivity(new Intent(instapostUser.this, instaPosts.class));
                }else{
                    Toast.makeText(this, "Please add an image to the post.", Toast.LENGTH_SHORT).show();
                }



                }else{
                Toast.makeText(this, "Please add a caption!", Toast.LENGTH_SHORT).show();
            }

    }





    private void addImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(instapostUser.this);
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

                addpostimage.setImageURI(imageHoldUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();

                Log.i("Error", error.toString());


            }
        }

    }
}
