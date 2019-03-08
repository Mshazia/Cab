package com.msjatoi.cab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CustomerLoginRegistrationActivity extends AppCompatActivity {

    private Button CustomerLoginbtn, customerRegisterbtn;
    private TextView CustomerRegisterLink, Customerstatus;
    private EditText CustomerEmail, CustomerPassword;

    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_customer_login_registration );


        customerRegisterbtn = findViewById( R.id.cusregbtn );
        CustomerLoginbtn = findViewById( R.id.cuslogbtn );
        CustomerRegisterLink = findViewById( R.id.dhavetv );
        Customerstatus = findViewById( R.id.cuslogintv );
        CustomerEmail = findViewById( R.id.cusemail );
        CustomerPassword = findViewById( R.id.cuspassedt );

        loadingBar = new ProgressDialog( this );

        mAuth = FirebaseAuth.getInstance();


        customerRegisterbtn.setVisibility( View.INVISIBLE );
        customerRegisterbtn.setEnabled( false );

        CustomerRegisterLink.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomerLoginbtn.setVisibility( View.INVISIBLE );
                CustomerRegisterLink.setVisibility( View.INVISIBLE );

                Customerstatus.setText( "Register Customer" );

                customerRegisterbtn.setVisibility( View.VISIBLE );
                customerRegisterbtn.setEnabled( true );


            }
        } );

        customerRegisterbtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = CustomerEmail.getText().toString();
                String password = CustomerPassword.getText().toString();

                RegisterCustomer( email, password );
            }
        } );
        CustomerLoginbtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = CustomerEmail.getText().toString();
                String password = CustomerPassword.getText().toString();

                SignInCustomer( email, password );
            }
        } );
    }

    private void RegisterCustomer(String email, String password) {
        if (TextUtils.isEmpty( email )) {
            Toast.makeText( CustomerLoginRegistrationActivity.this, "please write email....", Toast.LENGTH_SHORT ).show();

        }
        if (TextUtils.isEmpty( password )) {
            Toast.makeText( CustomerLoginRegistrationActivity.this, "please write password....", Toast.LENGTH_SHORT ).show();

        } else {
            loadingBar.setTitle( "Customer registration" );
            loadingBar.setMessage( "Please wait,while we are register your data..." );
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword( email, password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText( CustomerLoginRegistrationActivity.this, "customer login successfuly....", Toast.LENGTH_SHORT ).show();
                        loadingBar.dismiss();

                    } else {
                        Toast.makeText( CustomerLoginRegistrationActivity.this, "regestration unsuccessfull..please try again", Toast.LENGTH_SHORT ).show();

                        loadingBar.dismiss();
                    }
                }
            } );


        }

    }

    private void SignInCustomer(String email, String password) {

        if (TextUtils.isEmpty( email )) {
            Toast.makeText( CustomerLoginRegistrationActivity.this, "please write email....", Toast.LENGTH_SHORT ).show();

        }
        if (TextUtils.isEmpty( password )) {
            Toast.makeText( CustomerLoginRegistrationActivity.this, "please write password....", Toast.LENGTH_SHORT ).show();

        } else {
            loadingBar.setTitle( "Customer login" );
            loadingBar.setMessage( "Please wait,while we are cheaking yor creditionals..." );
            loadingBar.show();

            mAuth.signInWithEmailAndPassword( email, password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Toast.makeText( CustomerLoginRegistrationActivity.this, "customer login successfuly....", Toast.LENGTH_SHORT ).show();
                        loadingBar.dismiss();

                        Intent customerIntent = new Intent( CustomerLoginRegistrationActivity.this,CustomerMapsActivity.class );
                        startActivity( customerIntent );


                    } else {
                        Toast.makeText( CustomerLoginRegistrationActivity.this, "login unsuccessfull..please try again", Toast.LENGTH_SHORT ).show();

                        loadingBar.dismiss();
                    }
                }
            } );

        }
    }
}