package com.msjatoi.cab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomerSettingsActivity extends AppCompatActivity {

    private EditText mNameFeild,mPhoneField;
    private Button mback,mConfirm;

    private FirebaseAuth mAuth;
    private DatabaseReference mCustomerDatabase;
    private String userID;
    private String mName;
    private String mPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_customer_settings );

        mNameFeild = findViewById( R.id.name );
        mPhoneField = findViewById( R.id.phone );

        mConfirm = findViewById( R.id.confirm );
        mback= findViewById( R.id.back);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();
        mCustomerDatabase = FirebaseDatabase.getInstance().getReference().child( "Users" ).child( "Customers" ).child( userID );

        getUserInfo();


        mConfirm.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        } );
        mback.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        } );


    }

    private void getUserInfo(){
        mCustomerDatabase.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()  &&  dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map = (Map<String, Object>) dataSnapshot.getValue();
                     if (map.get( "name" )!= null){
                         mName = map.get( "name" ).toString();
                         mNameFeild.setText( mName );
                     }

                    if (map.get( "phone " )!= null){
                        mPhone = map.get( "phone " ).toString();
                        mPhoneField.setText( mPhone );
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
    private void saveUserInformation() {
        mName = mNameFeild.getText().toString();
        mPhone = mPhoneField.getText().toString();


        Map userInfo = new HashMap();
        userInfo.put( "name",mName );
        userInfo.put( "phone",mPhone );
        mCustomerDatabase.updateChildren( userInfo );

         finish();




    }
}
