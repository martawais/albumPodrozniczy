package mw.albumpodrozniczy;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.CircleOptions;
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
    private MarkerOptions markerOptions, markerOptionsFolder;
    private Marker marker;
    private int counterRoutes, quantityRoute = 0;
    private LatLng previousLatLng;
    private Polyline line;
    private JSONObject jsonObject;
    private JSONArray updateObject;
    private List<Address> listAddress;
    private Geocoder geocoder;
    private FloatingActionButton buttonStart, buttonCamera;
    private Switch raportSwitch;
    private boolean requestUpdate;
    private double currentLatitude, currentLongitude;
    private BitmapDescriptor iconFolder;
    private Toolbar toolbar;
    private EditText editComment;
    private RelativeLayout layoutBelowMap;

    private int komentarz_tytul; //komentarz 0, tytul 1;

    private DatabaseAdapter databaseAdapter;


    //TABELE
    private long numerObecnejPodrozy;
    private long numerObecnejTrasy;
    private int numerObecnejPodrozyInt;



    private static final int CAMERA_REQUEST = 1888;
    private Bitmap bitmap;
    private ImageView imageView;
    private Intent intent;
    private boolean edycja;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(" Podróż");
        setSupportActionBar(toolbar);

        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        databaseAdapter.open();

        jsonObject = new JSONObject();
        //latLng = new LatLng(51.0, 20.0);
        requestUpdate = false;

        intent = getIntent();
        edycja = intent.getBooleanExtra(BuildExistMap.EDYCJA, false);

        if(edycja==true) {

            toolbar.setTitle(" Podróż");
            wlaczenieWszystkichButton();
            buttonStart.setVisibility(View.INVISIBLE);
            buttonCamera.setEnabled(true);
            raportSwitch.setEnabled(true);
            numerObecnejPodrozyInt = intent.getIntExtra(BuildExistMap.POZYCJA_PODROZY, -999999);
            toolbar.setTitle(" "+databaseAdapter.pobranieWartosciZTabeli(databaseAdapter.DB_TABLE_MAIN, DatabaseAdapter.KEY_TITLE, numerObecnejPodrozyInt));

            int[] tablica = databaseAdapter.pobranieTablicyWszystkichTras(numerObecnejPodrozyInt);
            latLng = null;
            for(int i=0; i<tablica.length;i++) {
                //Toast.makeText(this, tablica[i] + "", Toast.LENGTH_SHORT).show();
                double[] szerokosc = databaseAdapter.pobranieTablicyWszystkichWspolrzedne(tablica[i]-1, "szerokosc");
                double[] dlugosc = databaseAdapter.pobranieTablicyWszystkichWspolrzedne(tablica[i]-1, "dlugosc");
                Polyline line;

                if(szerokosc.length!=0) {
                    double latitude = szerokosc[0];
                    double longitude = dlugosc[0];

                    latLng = new LatLng(latitude, longitude);
                    if(szerokosc.length != 1) {
                        for (int j = 1; j < szerokosc.length; j++) {
                            line = map.addPolyline(new PolylineOptions().add(new LatLng(szerokosc[j - 1], dlugosc[j - 1]), new LatLng(szerokosc[j], dlugosc[j])).width(15).color(Color.parseColor("#FF4081")));
                        }
                    }
                    else {

                        map.addCircle(new CircleOptions().center(new LatLng(szerokosc[0], dlugosc[0])).radius(0.3).strokeColor(Color.parseColor("#FF4081")).fillColor(Color.parseColor("#FF4081")));
                        // line = googleMap.addPolyline(new PolylineOptions().add(new LatLng(szerokosc[0]+0.0000001, dlugosc[0]+0.0000001), new LatLng(szerokosc[0], dlugosc[0])).width(15).color(Color.parseColor("#FF4081")));

                    }
                }
            }
            if(latLng==null) {
                latLng = new LatLng(51, 16);;
            }
            layoutBelowMap.setVisibility(View.INVISIBLE);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
            currentZoom = 16;


        }
        else {
            latLng = new LatLng(51.0, 20.0);
            wlaczenieWszystkichButton();
        }

        //ustawienie parametrów zapytania o lokalizacje
        // pytamy o lokalizację, tak dokładną jak jest możliwa
        // 5 seconds, in milliseconds- odstęp czasu po jakim nastąpi update lokalizacji
        // 1 second, in milliseconds- najszybszy odstep czasu w jakim uzyskamy update lokalizacji
        mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10 * 1000).setFastestInterval(5 * 1000);

        //stworzenie inctancji Google API Client (dostarcza interfejsu do połączenia i możliwość wywołania serwisu Googla) przy pomocy buildera, zeby polączyć sie do API (asynchronicznie i przechwytując błędy) i żeby zarządzać połączeniem sieciowym miedzy urządzeniem a każdym serwisem googla
        //tworze buildera, który dostarcza metody dzięki którym mogę wyspecyfikować Google API, które chę użyć
        //specyfikowane przy asynchronicznym połączeniu, otrzymanie odpowiedzi zwrotnej przy success
        //specyfikowane przy asynchronicznym połączeniu, otrzymanie odpowiedzi zwrotnej przy success
        //specyfikowane przy asynchronicznym połączeniu, otrzymanie odpowiedzi zwrotnej przy fail
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStart.setVisibility(View.INVISIBLE);
                buttonCamera.setEnabled(true);
                raportSwitch.setChecked(true);
            }
        });


        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                intent = new Intent(Map.this, CameraModule.class);
                startActivity(intent);
               // Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.setType("image*/");
               // intent.setAction(Intent.ACTION_GET_CONTENT);
               // intent.addCategory(Intent.CATEGORY_OPENABLE);
              //  startActivityForResult(intent, CAMERA_REQUEST);
            }
        });




        //nasłuchuje
        editComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (komentarz_tytul == 1) {
                        if(edycja==true) {
                            databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozyInt+1, DatabaseAdapter.KEY_TITLE, editComment.getText().toString());
                        }
                        else {
                            databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozy, DatabaseAdapter.KEY_TITLE, editComment.getText().toString());
                        }
                        toolbar.setTitle(" " + editComment.getText().toString());
                        editComment.setText("");
                        editComment.setVisibility(View.INVISIBLE);
                    } else if (komentarz_tytul == 0) {
                        if(edycja==true) {
                            databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozyInt+1, DatabaseAdapter.KEY_COMMENT, editComment.getText().toString());
                        }else{
                            databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozy, DatabaseAdapter.KEY_COMMENT, editComment.getText().toString());
                        }
                        editComment.setText("");
                        editComment.setVisibility(View.INVISIBLE);
                    }
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

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ImageView picture = (ImageView)findViewById(R.id.picture);
            picture.setImageBitmap(photo);
        }
    }*/

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

            //pierwsze rysowanie na mapie
            if (quantityRoute == 0) {
                counterRoutes = 1;
                quantityRoute = 2;
                currentZoom = 16;
                markerOptions = new MarkerOptions().position(latLng);
                marker = map.addMarker(markerOptions);
                layoutBelowMap.setVisibility(View.INVISIBLE);
                previousLatLng = latLng;
                recognizeLocation(latLng);
                raportSwitch.setEnabled(true);
            }

            //kontynuujemy rysowanie- kolejny punkt na tej samej rasie
            else if (quantityRoute == 2) {
                currentZoom = map.getCameraPosition().zoom;
                marker.setPosition(latLng);
                line = map.addPolyline(new PolylineOptions().add(previousLatLng, latLng).width(15).color(Color.parseColor("#FF4081")));
                previousLatLng = latLng;
                databaseAdapter.wstawKrotkeDoTabeliWspolrzedne(Double.toString(latLng.latitude), Double.toString(latLng.longitude), (int) numerObecnejTrasy);
            }

            //jezeli zaczynam kolejna trase- zaczynamy nowe rysowanie
            else if (quantityRoute == 1) {
                counterRoutes++;
                currentZoom = map.getCameraPosition().zoom;
                marker.setPosition(latLng);
                previousLatLng = latLng;
                numerObecnejTrasy = databaseAdapter.wstawKrotkeDoTabeliTrasa((int) numerObecnejPodrozy);
                databaseAdapter.wstawKrotkeDoTabeliWspolrzedne(Double.toString(latLng.latitude), Double.toString(latLng.longitude), (int) numerObecnejTrasy);
                quantityRoute = 2;
            }

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, currentZoom));
            databaseAdapter.wypiszTabele();
        }
    }

    //rozpoznanie państwa, miasta, pobranie daty i zapisanie pierwszych kordynatów w JSON object
    private void recognizeLocation(LatLng latLng) throws IOException, JSONException {

        listAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        if(listAddress!=null && listAddress.size()>0) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String currentDateandTime = sdf.format(new Date());
            Address address = listAddress.get(0);

            numerObecnejPodrozy = (long) intent.getIntExtra(BuildExistMap.POZYCJA_PODROZY, -99999) + 1;
            if(edycja==false) {
                numerObecnejPodrozy = databaseAdapter.wstawKrotkeDoTabeliPodroze(DatabaseAdapter.KEY_COUNTRY, address.getCountryName());

            }
            Log.d("sadd", "" + numerObecnejPodrozy);
            databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozy, DatabaseAdapter.KEY_CITY, address.getLocality());
            databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozy, DatabaseAdapter.KEY_DATE_START, currentDateandTime);
            numerObecnejTrasy = databaseAdapter.wstawKrotkeDoTabeliTrasa((int) numerObecnejPodrozy);
            databaseAdapter.wstawKrotkeDoTabeliWspolrzedne(Double.toString(latLng.latitude), Double.toString(latLng.longitude), (int) numerObecnejTrasy);

            /*if (toolbar.getTitle() == " PODRÓŻ") {
                toolbar.setTitle(" ["+currentDateandTime+"]      "+address.getLocality().toUpperCase());
                setSupportActionBar(toolbar);
                databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozy, DatabaseAdapter.KEY_TITLE, " ["+currentDateandTime+"]      "+address.getLocality().toUpperCase());
            }*/
            databaseAdapter.wypiszTabele();
        }
    }

    private void addFolder() {
        markerOptionsFolder = new MarkerOptions().position(latLng).icon(iconFolder);
        map.addMarker(markerOptionsFolder);
        /*try {
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
        }*/
    }


    private void wlaczenieWszystkichButton() {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        buttonStart = (FloatingActionButton) findViewById(R.id.buttonStart);
        buttonCamera = (FloatingActionButton) findViewById(R.id.camera);
        geocoder= new Geocoder(getApplicationContext(), Locale.getDefault());
        raportSwitch = (Switch) findViewById(R.id.raportSwitch);
        editComment = (EditText) findViewById(R.id.editComment);
        layoutBelowMap = (RelativeLayout) findViewById(R.id.layoutBelowMap);
        raportSwitch.setEnabled(false);
        buttonCamera.setEnabled(false);
        iconFolder = BitmapDescriptorFactory.fromResource(R.drawable.folder_image);
    }




    //inflator napompuje akcje, które zostały zdefiniowane w XML i doda je do action bar
    //MenuInflator dostanie dostep przez getMenuInflator z obecnego activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_dodaj_folder:
                Toast.makeText(this, "Dodanie nowego katalogu", Toast.LENGTH_SHORT).show();
                addFolder();
                break;
            case R.id.action_dodaj_komentarz:
                Toast.makeText(this, "Dodanie komentarza", Toast.LENGTH_SHORT).show();
                komentarz_tytul = 0;
                editComment.setHint("Nowy komentarz");
                editComment.setVisibility(View.VISIBLE);
                break;
            // action with ID action_settings was selected
            case R.id.action_zmien_tytul:
                Toast.makeText(this, "Zmiana tytułu", Toast.LENGTH_SHORT).show();
                komentarz_tytul = 1;
                editComment.setHint("Nowy tytuł podróży");
                editComment.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        return true;
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