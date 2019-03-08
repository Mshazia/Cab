package com.msjatoi.cab;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {
    private TextView logo,createdby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        Thread thread = new Thread( )
        {
            @Override
            public void run() {
               try
               {
                   sleep( 3000 );

               }
                catch (Exception e)
                {

                    e.printStackTrace();
                }
                finally {

                   Intent welcomeIntent = new Intent( SplashActivity.this,WelcomeActivity.class );
                   startActivity( welcomeIntent );

               }
            }
        };

        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }
}
