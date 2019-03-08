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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MechanicLoginRigestrationActivity extends AppCompatActivity {
    private Button MechanicLoginbtn,MechanicRegisterbtn;
    private TextView MechanicRegisterLink,Mechanicstatus;
    private EditText MechanicEmail,Mechanicpassword;

    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_mechanic_login_rigestration );


        MechanicRegisterbtn = findViewById( R.id.mechregbtn);
        MechanicLoginbtn = findViewById( R.id.mechlogbtn );
        MechanicRegisterLink = findViewById( R.id.mechhavetv );
        Mechanicstatus = findViewById( R.id.MechanicLogtv );
        MechanicEmail = findViewById( R.id.Mechemailedt );
        Mechanicpassword = findViewById( R.id.mechpassedt );

        loadingBar = new ProgressDialog(  this);

        mAuth = FirebaseAuth.getInstance();



        MechanicRegisterbtn.setVisibility( View.INVISIBLE );
        MechanicRegisterbtn.setEnabled( false );

        MechanicRegisterLink.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MechanicLoginbtn.setVisibility( View.INVISIBLE );
                MechanicRegisterLink.setVisibility( View.INVISIBLE );

                Mechanicstatus.setText( "Register Customer" );

                MechanicRegisterbtn.setVisibility( View.VISIBLE );
                MechanicRegisterbtn.setEnabled( true );


            }
        } );

        MechanicRegisterbtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = MechanicEmail.getText().toString();
                String password = Mechanicpassword.getText().toString();

                Registermechanic(email,password);
            }
        } );
        MechanicLoginbtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = MechanicEmail.getText().toString();
                String password = Mechanicpassword.getText().toString();

                SignInMechanic(email,password);

            }
        } );

    }

    private void Registermechanic(String email, String password) {
        if (TextUtils.isEmpty( email )){
            Toast.makeText( MechanicLoginRigestrationActivity.this,"please write email....",Toast.LENGTH_SHORT ).show();

        }
        if (TextUtils.isEmpty( password )){
            Toast.makeText( MechanicLoginRigestrationActivity.this,"please write password....",Toast.LENGTH_SHORT ).show();

        }
        else {
            loadingBar.setTitle( "Mechanic registration" );
            loadingBar.setMessage( "Please wait,while we are register your data..." );
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword( email, password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        Toast.makeText( MechanicLoginRigestrationActivity.this,"mechanic register successfuly....",Toast.LENGTH_SHORT ).show();
                        loadingBar.dismiss();

                        Intent mechanicIntent = new Intent( MechanicLoginRigestrationActivity.this,MechanicMapActivity.class );
                        startActivity( mechanicIntent );

                    }
                    else {
                        Toast.makeText( MechanicLoginRigestrationActivity.this,"login unsuccessfull..please try again",Toast.LENGTH_SHORT ).show();

                        loadingBar.dismiss();
                    }
                }
            } );


        }

    }

    private void SignInMechanic(String email, String password) {

        if (TextUtils.isEmpty( email )){
            Toast.makeText( MechanicLoginRigestrationActivity.this,"please write email....",Toast.LENGTH_SHORT ).show();

        }
        if (TextUtils.isEmpty( password )){
            Toast.makeText( MechanicLoginRigestrationActivity.this,"please write password....",Toast.LENGTH_SHORT ).show();

        }
        else {
            loadingBar.setTitle( "Mechanic sign" );
            loadingBar.setMessage( "Please wait,while we are cheking your credtionals..." );
            loadingBar.show();

            mAuth.signInWithEmailAndPassword( email, password ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        Toast.makeText( MechanicLoginRigestrationActivity.this,"mechanic login successfuly....",Toast.LENGTH_SHORT ).show();
                        loadingBar.dismiss();

                        Intent mechanicIntent = new Intent( MechanicLoginRigestrationActivity.this,MechanicMapActivity.class );
                        startActivity( mechanicIntent );

                    }
                    else {
                        Toast.makeText( MechanicLoginRigestrationActivity.this,"regestration unsuccessfull..please try again",Toast.LENGTH_SHORT ).show();

                        loadingBar.dismiss();
                    }
                }
            } );

            MechanicLoginbtn.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String email = MechanicEmail.getText().toString();
                    String password = Mechanicpassword.getText().toString();

                    SignInMechanic(email,password);

                }
            } );
        }

    }
    }


