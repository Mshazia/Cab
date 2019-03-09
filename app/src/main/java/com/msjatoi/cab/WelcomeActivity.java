package com.msjatoi.cab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class WelcomeActivity extends AppCompatActivity {
    private Button Mechanicbtn,customerbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_welcome );

        Mechanicbtn = findViewById( R.id.wel_mechanic_btn );
        customerbtn = findViewById( R.id.wel_customer_btn );


        Mechanicbtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginRegisterMechanicIntent = new Intent( WelcomeActivity.this,MechanicLoginRigestrationActivity.class );
                startActivity( LoginRegisterMechanicIntent );

            }
        } );

        customerbtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent LoginRegisterCustomerIntent = new Intent( WelcomeActivity.this,CustomerLoginRegistrationActivity.class );
                startActivity( LoginRegisterCustomerIntent );
            }
        } );
    }
}
