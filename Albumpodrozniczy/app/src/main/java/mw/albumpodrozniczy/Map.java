package mw.albumpodrozniczy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by mstowska on 11/22/2015.
 */

public class Map extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = Map.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private GoogleMap map;
    private LatLng latLng;
    private float currentZoom = 5;
    boolean mResolvingError;
    private MarkerOptions markerOptions;
    private MarkerOptions markerOptionsFolder;
    private Marker marker;
    private int counterRoutes, counterComments;
    private int quantityRoute = 0;
    private LatLng previousLatLng;
    private Polyline line;
    private JSONObject jsonObject;
    private JSONArray updateObject;
    private List<Address> listAddress;
    private Geocoder geocoder;
    private FloatingActionButton buttonStart, buttonAddFolder, buttonAddComment, buttonJSON;
    private Switch raportSwitch;
    private boolean requestUpdate;
    private double currentLatitude;
    private double currentLongitude;
    private BitmapDescriptor iconFolder;
    private BitmapDescriptor iconMarker;
    private Toolbar toolbar;
    private EditText editComment;
    private RelativeLayout layoutBelowMap;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(" NOWA PODRÓŻ");
        setSupportActionBar(toolbar);

        jsonObject = new JSONObject();
        latLng = new LatLng(51.0, 20.0);
        requestUpdate = false;

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        buttonStart = (FloatingActionButton) findViewById(R.id.buttonStart);
        buttonAddFolder = (FloatingActionButton) findViewById(R.id.buttonAddFolder);
        buttonAddComment = (FloatingActionButton) findViewById(R.id.buttonAddComment);
        buttonJSON = (FloatingActionButton) findViewById(R.id.buttonJSON);
        geocoder= new Geocoder(getApplicationContext(), Locale.getDefault());
        raportSwitch = (Switch) findViewById(R.id.raportSwitch);
        editComment = (EditText) findViewById(R.id.editComment);
        layoutBelowMap = (RelativeLayout) findViewById(R.id.layoutBelowMap);
        raportSwitch.setEnabled(false);
        buttonAddFolder.setEnabled(false);
        buttonAddComment.setEnabled(false);
        buttonJSON.setEnabled(false);


        iconFolder = BitmapDescriptorFactory.fromResource(R.drawable.folder_image);
       // iconMarker = BitmapDescriptorFactory.fromResource(R.drawable.map_marker_black);



        //ustawienie parametrów zapytania o lokalizacje
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)// pytamy o lokalizację, tak dokładną jak jest możliwa
                .setInterval(10 * 1000)       // 5 seconds, in milliseconds- odstęp czasu po jakim nastąpi update lokalizacji
                .setFastestInterval(5 * 1000); // 1 second, in milliseconds- najszybszy odstep czasu w jakim uzyskamy update lokalizacji

        //stworzenie inctancji Google API Client (dostarcza interfejsu do połączenia i możliwość wywołania serwisu Googla) przy pomocy buildera, zeby polączyć sie do API (asynchronicznie i przechwytując błędy) i żeby zarządzać połączeniem sieciowym miedzy urządzeniem a każdym serwisem googla
        //tworze buildera, który dostarcza metody dzięki którym mogę wyspecyfikować Google API, które chę użyć
        mGoogleApiClient = new GoogleApiClient.Builder(this)//create GoogleApiClient object using the Builder pattern
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)	//specyfikowane przy asynchronicznym połączeniu, otrzymanie odpowiedzi zwrotnej przy success
                .addOnConnectionFailedListener(this)	//specyfikowane przy asynchronicznym połączeniu, otrzymanie odpowiedzi zwrotnej przy fail
                        //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)//adds the LocationServices API endpoint from GooglePlayServices, and then finally the client is built for us
                .build();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStart.setVisibility(View.INVISIBLE);
                raportSwitch.setChecked(true);
            }
        });

        buttonAddFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Dodanie nowego katalogu", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                addFolder();


            }
        });

        buttonAddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Dodanie nowego komentarza", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                editComment.setVisibility(View.VISIBLE);

            }
        });

        buttonJSON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Map.this)
                        .setTitle("JSON Object")
                        .setMessage(jsonObject.toString())
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // whatever...
                            }
                        }).create().show();

            }
        });

        editComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    try {
                        if (!jsonObject.has("comments")) {
                            updateObject = new JSONArray();
                            updateObject.put(editComment.getText());
                            jsonObject.put("comments", updateObject);
                        } else {
                            updateObject = jsonObject.getJSONArray("comments");
                            updateObject.put(editComment.getText());
                            jsonObject.put("comments", updateObject);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    editComment.setText("");
                    editComment.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        raportSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Snackbar.make(buttonView, "Lokalizowanie obecnego położenia...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    requestUpdate = true;
                } else {
                    Snackbar.make(buttonView, "Wstrzymanie lokalizowania położenia", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    requestUpdate = false;
                    quantityRoute = 1;
                }
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, currentZoom));
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






    //po połączenie z Google Play Service, prosze o zaktualizowanie lokalizacji, jeżeli użytkownik chce zostać zlokalizowany
    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            handleNewLocation(location);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void handleNewLocation(Location location) throws IOException, JSONException {

        if (requestUpdate) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
            latLng = new LatLng(currentLatitude, currentLongitude);


            if (quantityRoute == 0) {
                counterRoutes = 1;
                quantityRoute = 2;
                currentZoom = 16;
                markerOptions = new MarkerOptions().position(latLng); //.icon(iconMarker)
                //markerOptions = markerOptions.position(latLng).title("I am here!");
                marker = map.addMarker(markerOptions);
                layoutBelowMap.setVisibility(View.INVISIBLE);
                previousLatLng = latLng;
                recognizeLocation(latLng);
                raportSwitch.setEnabled(true);
                buttonAddFolder.setEnabled(true);
                buttonAddComment.setEnabled(true);
                buttonJSON.setEnabled(true);
            }
            else if (quantityRoute == 2) {
                currentZoom = map.getCameraPosition().zoom;
                marker.setPosition(latLng);
                line = map.addPolyline(new PolylineOptions().add(previousLatLng, latLng).width(15).color(Color.parseColor("#FF4081")));
                previousLatLng = latLng;
                updateObject = jsonObject.getJSONArray("route"+Integer.toString(counterRoutes));
                updateObject.put(Double.toString(latLng.latitude) + "," + Double.toString(latLng.longitude));
            }
            else if (quantityRoute == 1) {

                counterRoutes++;
                currentZoom = map.getCameraPosition().zoom;
                marker.setPosition(latLng);
                previousLatLng = latLng;
                updateObject = new JSONArray();
                updateObject.put(Double.toString(latLng.latitude) + "," + Double.toString(latLng.longitude));
                jsonObject.put("route"+Integer.toString(counterRoutes), updateObject);
                quantityRoute = 2;
            }
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, currentZoom));
            Log.d(TAG, jsonObject.toString());
        }
    }

    //rozpoznanie państwa, miasta, pobranie daty i zapisanie pierwszych kordynatów w JSON object
    private void recognizeLocation(LatLng latLng) throws IOException, JSONException {

        listAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        if(listAddress!=null && listAddress.size()>0) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String currentDateandTime = sdf.format(new Date());

            Address address = listAddress.get(0);
            jsonObject.put("country", address.getCountryName());
            jsonObject.put("city", address.getLocality());
            jsonObject.put("dateStart", currentDateandTime);
            updateObject = new JSONArray();
            updateObject.put(Double.toString(latLng.latitude) + "," + Double.toString(latLng.longitude));
            jsonObject.put("route" + Integer.toString(counterRoutes), updateObject);
            if (toolbar.getTitle() == " NOWA PODRÓŻ") {
                toolbar.setTitle(" ["+currentDateandTime+"]      "+address.getLocality().toUpperCase());
                setSupportActionBar(toolbar);
            }
        }
    }

    private void addFolder() {

        markerOptionsFolder = new MarkerOptions().position(latLng).icon(iconFolder);
        map.addMarker(markerOptionsFolder);

        try {
            if (!jsonObject.has("folders")) {
                updateObject = new JSONArray();
                updateObject.put(Double.toString(latLng.latitude) + "," + Double.toString(latLng.longitude));
                jsonObject.put("folders", updateObject);
            } else {
                updateObject = jsonObject.getJSONArray("folders");
                updateObject.put(Double.toString(latLng.latitude) + "," + Double.toString(latLng.longitude));
                jsonObject.put("folders", updateObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

}