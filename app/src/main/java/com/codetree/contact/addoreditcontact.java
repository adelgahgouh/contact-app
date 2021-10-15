package com.codetree.contact;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class addoreditcontact extends AppCompatActivity {
String typeact="";
String namecontact="";
String numbercontact="";
EditText etname,ettel;
Button btnsave;
String contactid="";
    DatabaseReference ref;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addoreditcontact);
        Toolbar setupToolbar = findViewById(R.id.setupToolbar);
        etname = findViewById(R.id.namecontact);
        ettel = findViewById(R.id.contacttel);
        btnsave = findViewById(R.id.savecontact);

        typeact = getIntent().getExtras().getString("val");
        if (typeact.equals("add"))
            setupToolbar.setTitle("ADD CONTACT");
        else {
            setupToolbar.setTitle("EDIT CONTACT");
            namecontact = getIntent().getExtras().getString("name");
            numbercontact = getIntent().getExtras().getString("number");
            contactid = getIntent().getExtras().getString("id");
            etname.setText(namecontact);
            ettel.setText(numbercontact);
        }
        setSupportActionBar(setupToolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        ref =mDatabase.child("users").child(user_id).child("contacts").getRef();


        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              final   String name = etname.getText().toString()
                ,tel = ettel.getText().toString();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(tel)) {
                    if(typeact.equals("add")) {
                      String id= mDatabase.push().getKey();
                      addandeditcontact(id,name,tel);


                    }else{
                        addandeditcontact(contactid,name,tel);
                    }

                }
               // mDatabase.child("users").child(user_id).child("contacts").child("-Lzcw7PCIXcB5JZOOYUD").removeValue();
            }
        });


    }
    void addandeditcontact(String id, String name, String tel){
        Contact contact=new Contact(id,name,tel);
        mDatabase.child("users").child(user_id).child("contacts").child(id).setValue(contact);

        // i.putExtra("number","");
        //i.putExtra("name","");
        //i.putExtra("id","");
        finish();

    }
    void shmsg(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

}
