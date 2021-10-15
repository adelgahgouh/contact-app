package com.codetree.contact;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
ImageView imgmain;
TextView tvname,tvcount;
    FloatingActionButton fab;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    String user_id;
    ListView lv;
    ArrayList<Contact> contacts;
    ArrayAdapter<Contact> contactadapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar setupToolbar = findViewById(R.id.setupToolbar);

        setSupportActionBar(setupToolbar);
        fab=findViewById(R.id.floating);
        imgmain=findViewById(R.id.imgmain);
        lv=findViewById(R.id.lv);
        tvcount=findViewById(R.id.tvcount);
        tvname=findViewById(R.id.tvname);
lv=findViewById(R.id.lv);
contacts=new ArrayList<>();
contactadapter=new ArrayAdapter<Contact>(getApplicationContext(),R.layout.carditem,contacts){
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.carditem, parent, false);
        }
        // Lookup view for data population
        TextView cardname=convertView.findViewById(R.id.cardname);
        TextView cardtell=convertView.findViewById(R.id.cardtel);
        ImageView imag=convertView.findViewById(R.id.imgcarditem);
        ImageView carddel=convertView.findViewById(R.id.carddel);
        carddel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context,"click pos="+contacts.get(position).getId()+" name="+contacts.get(position).getContactname(),Toast.LENGTH_SHORT).show();

                                 mDatabase.child("users").child(user_id).child("contacts").child(contacts.get(position).getId()).removeValue();
     contactadapter.notifyDataSetChanged();

            }
        });
        cardname.setText(contacts.get(position).getContactname());
        cardtell.setText(contacts.get(position).getContacttel());
        Glide.with(getApplicationContext())
                .load(R.drawable.ic_account)
                .into(imag);
        Glide.with(getApplicationContext())
                .load(R.drawable.ic_delete)
                .into(carddel);
        return convertView;    }
};
lv.setAdapter(contactadapter);

        firebaseAuth = FirebaseAuth.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        DatabaseReference ref =mDatabase.child("users").child(user_id).getRef();



        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                String name = user.getUsername();
                String image = user.getImg();


                tvname.setText("Full name:"+name);
                RequestOptions placeholderRequest = new RequestOptions();
                placeholderRequest.placeholder(R.drawable.default_image);
                Glide.with(MainActivity.this).setDefaultRequestOptions(placeholderRequest).load(image).into(imgmain);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
DatabaseReference dbcontacts=FirebaseDatabase.getInstance().getReference().child("users").child(user_id).child("contacts").getRef();
dbcontacts.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        tvcount.setText("Number of Contacts:"+dataSnapshot.getChildrenCount());
        contactadapter.clear();

        for(DataSnapshot ds : dataSnapshot.getChildren()) {
            Contact c=ds.getValue(Contact.class);
            contactadapter.add(c);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
});
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),addoreditcontact.class);
                i.putExtra("val","add");
               // i.putExtra("number","");
                //i.putExtra("name","");
                //i.putExtra("id","");

                startActivity(i);
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(getApplicationContext(),addoreditcontact.class);
                i.putExtra("val","edit");
                 i.putExtra("number",contacts.get(position).getContacttel());
                i.putExtra("name",contacts.get(position).getContactname());
                i.putExtra("id",contacts.get(position).getId());

                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
            { Intent i=new Intent(getApplicationContext(),addoreditcontact.class);
                startActivity(i);}
                return true;
            case R.id.logout:
                Toast.makeText(getApplicationContext(),"logout",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.account:
                Intent i=new Intent(getApplicationContext(),SetupActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
