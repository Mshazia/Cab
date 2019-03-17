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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class CustomerMapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    double latitude, longitude;

    private Button LougoutBtn,SettingsBtn,mRequestBtn;
    private LatLng pickupLocation;
    private Boolean requestBol = false;
    private Marker pickupMarker;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_customer_maps);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();}
        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) { Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();        } else {            Log.d("onCreate","Google Play Services available.");        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()     .findFragmentById( R.id.map);
        mapFragment.getMapAsync(this);


        LougoutBtn = findViewById( R.id.customer_logbtn );
        mRequestBtn = findViewById( R.id.callmechbtn );
        SettingsBtn = findViewById( R.id.customer_seetgbtn);
        LougoutBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent( CustomerMapsActivity.this,WelcomeActivity.class );
                startActivity( intent );
                finish();
                return;
            }
        } );
        mRequestBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (requestBol){
                    requestBol = false;

                    geoQuery.removeAllListeners();

                    mechanicLocationRef.removeEventListener( mechanicLocationRefListener );

                    if (mechanicFoundID != null){
                        DatabaseReference mechanicRef = FirebaseDatabase.getInstance().getReference().child( "Users").child("Mechanics" ).child( mechanicFoundID );
                        mechanicRef.setValue(true);
                        mechanicFoundID = null;

                    }
                    mechanicFound = false;
                    radius = 1;
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref =FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new  GeoFire(ref);
                    geoFire.removeLocation( userId );
                    if (pickupMarker != null){
                        pickupMarker.remove();
                    }
                    mRequestBtn.setText( "Call Mechanic" );

                }else {
                    requestBol = true;

                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference ref =FirebaseDatabase.getInstance().getReference("customerRequest");
                    GeoFire geoFire = new  GeoFire(ref);
                    geoFire.setLocation( userId,new GeoLocation( mLastLocation.getLatitude(),mLastLocation.getLongitude() ),new
                            GeoFire.CompletionListener(){
                                @Override
                                public void onComplete(String key, DatabaseError error) {

                                }
                            });
                    pickupLocation = new LatLng( mLastLocation.getLatitude(),mLastLocation.getLongitude());
                    pickupMarker =  mMap.addMarker( new MarkerOptions().position( pickupLocation ).title( "Pickup here" ).icon( BitmapDescriptorFactory.fromResource( R.mipmap.ic_mechanic ) ) );

                    mRequestBtn.setText( "Getting your Mechanic....." );
                    getClosestMechanic();
                }

            }
        } );
        SettingsBtn.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( CustomerMapsActivity.this, CustomerSettingsActivity.class);
                startActivity( intent );
                return;
            }
        } );
    }
    private int radius = 1;
    private Boolean mechanicFound = false;
    private String mechanicFoundID;

    GeoQuery geoQuery;
    private void getClosestMechanic() {
        DatabaseReference mechanicLocation = FirebaseDatabase.getInstance().getReference().child( "mechanicsAvailable" );

        GeoFire geoFire = new GeoFire( mechanicLocation );

        geoQuery = geoFire.queryAtLocation( new GeoLocation( pickupLocation.latitude,pickupLocation.longitude ) ,radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener( new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if (!mechanicFound && requestBol){
                    mechanicFound = true;
                    mechanicFoundID = key;
//share information btween customer and mechanic
                    //blo for telling mechanic about customer
                    DatabaseReference mechanicRef = FirebaseDatabase.getInstance().getReference().child( "Users").child("Mechanics" ).child( mechanicFoundID );
                    String customerId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    HashMap map = new HashMap();
                    map.put("customerRideId",customerId);
                    mechanicRef.updateChildren( map );

                    //belwo for getting mechanic location for the customer
                    GetMechanicLocation();
                    mRequestBtn.setText( "Looking for Mechanic Location......." );


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
    private Marker mMechanicMarker;
    private DatabaseReference mechanicLocationRef;
    private ValueEventListener mechanicLocationRefListener;
    private void GetMechanicLocation()
    {
        mechanicLocationRef = FirebaseDatabase.getInstance().getReference().child( "MechanicWorking" ).child( mechanicFoundID ).child( "l" );
        mechanicLocationRefListener = mechanicLocationRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && requestBol) {
                    List<Object> map = (List<Object>) dataSnapshot.getValue();
                    double locationLat = 0;
                    double locationLng = 0;
                    mRequestBtn.setText( "Mechanic Found" );
                    if (map.get( 0 ) != null) {
                        locationLat = Double.parseDouble( map.get( 0 ).toString() );
                    }
                    if (map.get( 1 ) != null) {
                        locationLng = Double.parseDouble( map.get( 1 ).toString() );
                    }
                    LatLng mechanicLatLng = new LatLng( locationLat, locationLng );
                    if (mMechanicMarker != null) {
                        mMechanicMarker.remove();
                    }
                    Location loc1 = new Location( "" );
                    loc1.setLatitude( pickupLocation.latitude );
                    loc1.setLongitude( pickupLocation.longitude );

                    Location loc2 = new Location( "" );
                    loc2.setLatitude(mechanicLatLng.latitude );
                    loc2.setLongitude(mechanicLatLng.longitude );

                    float distance = loc1.distanceTo( loc2 );

                    if (distance<100){
                        mRequestBtn.setText( "Mechanic is here" );
                    }else {
                        mRequestBtn.setText( "Mechanic Found:" + String.valueOf( distance ) );
                    }


                    mRequestBtn.setText( "Mechanic Found:" + String.valueOf( distance ) );


                    mMechanicMarker = mMap.addMarker( new MarkerOptions().position( mechanicLatLng ).title( "your mechanic" ) .icon( BitmapDescriptorFactory.fromResource( R.mipmap.ic_launcherg) ));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }
//already in gmap app

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }



    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged", "entered");

        mLastLocation = location;

        latitude = location.getLatitude();
        longitude = location.getLongitude();


        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        Toast.makeText(CustomerMapsActivity.this,"Your Current Location", Toast.LENGTH_LONG).show();

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    //already in gmap app
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
    //already in gmap
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission( this, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled( true );
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText( this, "permission denied", Toast.LENGTH_LONG ).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();



    }


}



