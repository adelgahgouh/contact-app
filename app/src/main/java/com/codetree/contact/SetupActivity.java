package com.codetree.contact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;



public class SetupActivity extends AppCompatActivity {
    private ImageButton setupImage;
    private Uri mainImageURI = null;

    private String user_id;

    private boolean isChanged = false;

    private EditText setupName;
    private Button setupBtn;
    private ProgressBar setupProgress;

    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        setupToolbar.setTitle("Account settings");
        setSupportActionBar(setupToolbar);

        setupImage = findViewById(R.id.setup_image);
        setupName = findViewById(R.id.setup_name);
        setupBtn = findViewById(R.id.setup_btn);
        setupProgress = findViewById(R.id.setup_progress);
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setupProgress.setVisibility(View.INVISIBLE);
      //  setupBtn.setEnabled(false);

        // get data profile

       // FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference ref = database.getReference("users").child(user_id);
        DatabaseReference ref =mDatabase.child("users").child(user_id).getRef();
                ref.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            User user = dataSnapshot.getValue(User.class);
            String name = user.getUsername();
            String image = user.getImg();
            shmsg(name+" "+image);
            setupName.setText(name);
            RequestOptions placeholderRequest = new RequestOptions();
            placeholderRequest.placeholder(R.drawable.default_image);
            Glide.with(SetupActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(setupImage);
            setupProgress.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        String error = databaseError.getMessage();
        Toast.makeText(SetupActivity.this, "(FIRESTORE Retrieve Error) : " + error, Toast.LENGTH_LONG).show();
        setupProgress.setVisibility(View.INVISIBLE);
        setupBtn.setEnabled(true);
    }

});

        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 final String user_name = setupName.getText().toString();

                if (!TextUtils.isEmpty(user_name) && mainImageURI != null) {

                    setupProgress.setVisibility(View.VISIBLE);

                    if (isChanged) {

                        user_id = firebaseAuth.getCurrentUser().getUid();
                      //  Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
                        Uri file=mainImageURI;
                        StorageReference riversRef = storageReference.child("images").child(user_id + ".jpg");

                        riversRef.putFile(file)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        // Get a URL to the uploaded content

                                                taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String  downloadUrl =uri.toString();
                                               // shmsg(downloadUrl);
                                                storeFirestore( user_name,downloadUrl);
                                            }
                                        });

                                        //
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        // ...
                                        String error = exception.getMessage();
                                        Toast.makeText(SetupActivity.this, "(IMAGE Error) : " + error, Toast.LENGTH_LONG).show();

                                        setupProgress.setVisibility(View.INVISIBLE);

                                    }
                                });

                    } else {

                        storeFirestore(null, user_name);

                    }

                }

            }

        });

        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              isStoragePermissionGranted();

            }

        });


    }
    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                BringImagePicker();
                return true;
            } else {

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            BringImagePicker();
            return true;
        }
    }
    private void storeFirestore(String user_name , String img) {



//
writeNewUser(user_name,img);

    }
    private void writeNewUser(String name, String img) {
        User user = new User(name, img);
        //shmsg("user="+user.username+" img="+img);
//        Map<String, Object> userValues = user.toMap();

        mDatabase.child("users").child(user_id).setValue(user);
       // mDatabase.child("users").child(user_id).child("img").setValue(img);

        setupProgress.setVisibility(View.INVISIBLE);
        //shmsg(name+" "+img);
    }

    private void BringImagePicker() {

Intent i=new Intent(Intent.ACTION_PICK);
i.setType("image/*");
startActivityForResult(i,2);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //resume tasks needing this permission
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {

            if (resultCode == RESULT_OK) {

                mainImageURI = data.getData();
                setupImage.setImageURI(mainImageURI);

                isChanged = true;

            }
        }

    }
    void shmsg(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

}