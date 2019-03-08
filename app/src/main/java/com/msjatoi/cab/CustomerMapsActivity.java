package com.msjatoi.cab;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CustomerMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {


    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mlastlocation;
    LocationRequest mLocationRequest;
    private String shaziaTesting;

    private Button LougoutBtn,SettingsBtn,mRequest;

    private LatLng pickupLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_customer_maps );
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map );
        mapFragment.getMapAsync( this );


        LougoutBtn = findViewById( R.id.customer_logbtn );
        SettingsBtn = findViewById( R.id.customer_seetgbtn );
        mRequest = findViewById( R.id.callmechbtn );


        LougoutBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CustomerMapsActivity .this,WelcomeActivity.class );
                startActivity( intent );
                finish();
                return;
            }
        } );

        mRequest.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customer Request");
                GeoFire geoFire = new GeoFire( ref );
                geoFire.setLocation( userID,new GeoLocation( mlastlocation.getLatitude(),mlastlocation.getLongitude() ) );

                pickupLocation = new LatLng( mlastlocation.getLatitude(),mlastlocation.getLongitude() );
                mMap.addMarker( new MarkerOptions().position( pickupLocation ).title( "pickup here" ) );

                mRequest.setText( "Getting your Mechanic....." );


                getClosestMechanic();

            }
        } );
    }
//find mechnic for request
    //send requset to mechanic

    private int radius = 1;
    private Boolean mechanicFound = false;
    private String mechanicFoundID;
    private void getClosestMechanic(){
        DatabaseReference MechanicLocation = FirebaseDatabase.getInstance().getReference().child( "Mechanic Available" );

        GeoFire geoFire = new GeoFire( MechanicLocation );

        GeoQuery geoQuery = geoFire.queryAtLocation( new GeoLocation( pickupLocation.latitude,pickupLocation.longitude ),radius );
        geoQuery.removeAllListeners();

    geoQuery.addGeoQueryEventListener( new GeoQueryEventListener() {
        @Override
        public void onKeyEntered(String key, GeoLocation location) {
            if (!mechanicFound) {
                mechanicFound = true;
                mechanicFoundID = key;

            }
        }
        @Override
        public void onKeyExited(String key) {

        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {

        }

        @Override
        public void onGeoQueryReady() {
          if (!mechanicFound)
          {
              radius++;
              getClosestMechanic();
          }

        }

        @Override
        public void onGeoQueryError(DatabaseError error) {

        }
    } );

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled( true );
    }
    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder( this )
                .addConnectionCallbacks( this )
                .addOnConnectionFailedListener( this )
                .addApi( LocationServices.API )
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mlastlocation = location;

        LatLng latLng = new LatLng( location.getLatitude(),location.getLongitude() );

        mMap.moveCamera( CameraUpdateFactory.newLatLng( latLng ) );
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 11 ) );


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval( 1000 );
        mLocationRequest.setFastestInterval( 1000 );
        mLocationRequest.setPriority( LocationRequest.PRIORITY_HIGH_ACCURACY );

        if (ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission( this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates( mGoogleApiClient, mLocationRequest, this );


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
