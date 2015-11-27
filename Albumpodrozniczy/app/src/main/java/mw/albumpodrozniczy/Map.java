package mw.albumpodrozniczy;

import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


/**
 * Created by mstowska on 11/22/2015.
 */

//Getting a user’s location is an asynchronous process because it might take a little time to get the location data. We don’t want the app to be unresponsive while it’s waiting, so we do that work in the background. When that work is done in the background, it needs to get back to this main thread somehow.

public class Map extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = Map.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private GoogleMap map;
    private LatLng latLng;
    private float currentZoom;
    private boolean start=false;
    boolean mResolvingError;
    private MarkerOptions options = new MarkerOptions();
    private Marker marker;
    private boolean firstMarker = true;
    private LatLng previousLatLng;
    private Polyline line;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        currentZoom = 5;
        latLng = new LatLng(50.0, 20.0);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        setUpMap();

        final FloatingActionButton buttonStart = (FloatingActionButton) findViewById(R.id.buttonStart);




        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)// pytamy o lokalizację, tak dokładną jak jest możliwa
                .setInterval(10 * 1000)       // 10 seconds, in milliseconds- odstęp czasu po jakim nastąpi update lokalizacji
                .setFastestInterval(5 * 1000); // 1 second, in milliseconds- najszybszy odstep czasu w jakim uzyskamy update lokalizacji


        //    buttonStart.setVisibility(View.INVISIBLE);
            mGoogleApiClient = new GoogleApiClient.Builder(this)//create GoogleApiClient object using the Builder pattern
                    // The next two lines tell the new client that “this” current class will handle connection stuff
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                            //fourth line adds the LocationServices API endpoint from GooglePlayServices
                    .addApi(LocationServices.API)//adds the LocationServices API endpoint from GooglePlayServices, and then finally the client is built for us
                    .build();


        buttonStart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Lokalizowanie obecnego położenia...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                start = true;
                buttonStart.setVisibility(View.INVISIBLE);
               if(mGoogleApiClient.isConnected()) {
                   Log.d(TAG, "jest polaczony");
               }
                //currentZoom = 10;
            }
        });



    }


    @Override
    protected void onStart() {
        super.onStart();

        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }

        setUpMap();

    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMap();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
        Log.d(TAG, "stop aplikacja");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
       // Log.i(TAG, "Location services connected.");
        //Toast.makeText(this, "onConnected", Toast.LENGTH_LONG).show();
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(TAG, "pobiera location");
            if (location == null) {
                Toast.makeText(this, "location==null", Toast.LENGTH_LONG).show();
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                Log.d(TAG, "location == 0");
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                Log.d(TAG, "location != 0");
                // handleNewLocation(location);
            }

    }

    private void handleNewLocation(Location location) {
        if (start) {


           // Log.d(TAG, location.toString());
          //  Toast.makeText(this, "handleNewLocation", Toast.LENGTH_LONG).show();



            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            Log.d(TAG, Double.toString(currentLatitude));
            Log.d(TAG, Double.toString(currentLongitude));
            latLng = new LatLng(currentLatitude, currentLongitude);


            options = options.position(latLng).title("I am here!");
            setUpMap();
            if(firstMarker == true) {
                marker = map.addMarker(options);
                firstMarker = false;
                previousLatLng = latLng;

            }
            else {
                marker.setPosition(latLng);
                line = map.addPolyline(new PolylineOptions().add(previousLatLng,latLng).width(3).color(Color.RED));
                previousLatLng = latLng;
            }
            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));


        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
        Toast.makeText(this, "Suspended", Toast.LENGTH_LONG).show();
    }

    //poczytać w dokumentacji
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if(firstMarker == true) {
            currentZoom = 17;
        }
        else{
            currentZoom = map.getCameraPosition().zoom;
        }
        handleNewLocation(location);

    }


    private void setUpMap() {


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, currentZoom));

    }



}
