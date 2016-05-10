package mw.albumpodrozniczy;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by mstowska on 11/22/2015.
 */

public class Map extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int REQUEST_IMAGE_CAPTURE = 1;

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
    private List<Address> listAddress;
    private Geocoder geocoder;
    private FloatingActionButton buttonStart, buttonCamera;
    private Switch raportSwitch;
    private boolean requestUpdate; //jeżeli na true, to jest żądanie aktualizacji położenia- dyktowane przez użytkownika switchem
    private double currentLatitude, currentLongitude;
    private BitmapDescriptor iconFolder;
    private Toolbar toolbar;
    private EditText editComment;
    private RelativeLayout layoutBelowMap;
    private PackageManager packageManager;
    private TextView markeryZeZdjeciami;

    private int komentarz_tytul; //komentarz 0, tytul 1;

    private DatabaseAdapter databaseAdapter;


    //TABELE
    private long numerObecnejPodrozy;
    private long numerObecnejTrasy;
    private long numerTrasyWdanejPodrozy;
    private int[] tablicaWszystkichTras;

    private Context context;
    private Intent intent;
    private boolean trybEdycja;
    private String timeStamp;
    private String nazwa_folderu;

    private MenuItem tytul;
    private MenuItem folder;
    private MenuItem komentarz;

    private boolean enableMenu;
    private int iloscPodrozyZActivityMap;



    private View mCustomMarkerView;
    private ImageView mMarkerImageView;

    private String aktualnyFolder = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //invalidateOptionsMenu();

        pobranieElementowWidoku();


        context = getApplicationContext();

        dostepDoBazyDanych();

        requestUpdate = false;

        intent = getIntent();
        trybEdycja = intent.getBooleanExtra(BuildExistMap.EDYCJA, false);
        iloscPodrozyZActivityMap = intent.getIntExtra(MainActivity.ILOSCPODROZY, -1)+1;

        toolbar.setTitle(" Podróż nr "+iloscPodrozyZActivityMap);
        setSupportActionBar(toolbar);


        if(trybEdycja==true) {
            enableMenu = true;
            invalidateOptionsMenu();
            buttonStart.setVisibility(View.INVISIBLE);
            buttonCamera.setEnabled(true);
            buttonCamera.setVisibility(View.VISIBLE);
            raportSwitch.setEnabled(true);
            numerObecnejPodrozy = (long) intent.getIntExtra(BuildExistMap.POZYCJA_PODROZY, -999999);

            toolbar.setTitle(" "+databaseAdapter.pobranieWartosciZTabeli(databaseAdapter.DB_TABLE_MAIN, DatabaseAdapter.KEY_TITLE, (int) numerObecnejPodrozy));

            tablicaWszystkichTras = databaseAdapter.pobranieTablicyWszystkichTras((int)numerObecnejPodrozy );

            latLng = null;
            for(int i=0; i<tablicaWszystkichTras.length;i++) {
                double[] szerokosc = databaseAdapter.pobranieTablicyWszystkichWspolrzedne(tablicaWszystkichTras[i], "szerokosc");
                double[] dlugosc = databaseAdapter.pobranieTablicyWszystkichWspolrzedne(tablicaWszystkichTras[i], "dlugosc");
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
                    }
                }
            }

            String[] tablicaNazwAlbumow = databaseAdapter.pobranieTablicyWszystkichNazwAlbumu((int)numerObecnejPodrozy);
            double[] tablicaSzerokosciAlbumow = databaseAdapter.pobranieTablicyWszystkichWspolrzedneAlbumu((int) numerObecnejPodrozy, "szerokosc");
            double[] tablicaDlugosciAlbumow = databaseAdapter.pobranieTablicyWszystkichWspolrzedneAlbumu((int) numerObecnejPodrozy, "dlugosc");
            if(tablicaNazwAlbumow.length!=0) {

                mCustomMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.element_marker, null);
                mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.miniaturka_marker);

                for (int i = 0; i < tablicaNazwAlbumow.length; i++) {
                    LatLng wspolrzedne = new LatLng(tablicaSzerokosciAlbumow[i], tablicaDlugosciAlbumow[i]);
                    MarkerOptions markerOptionsFolder = new MarkerOptions().alpha(70).position(wspolrzedne).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, R.drawable.pionowe))).title("Tutaj trafią zdjęcia");
                    map.addMarker(markerOptionsFolder);
                }

            }
            if(latLng==null) {
                latLng = new LatLng(51, 16);

            }
            layoutBelowMap.setVisibility(View.INVISIBLE);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

            currentZoom = 16;


        }
        else {
            enableMenu = false;
            invalidateOptionsMenu();


            latLng = new LatLng(51.0, 20.0);

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
                buttonCamera.setVisibility(View.VISIBLE);
                buttonCamera.setEnabled(true);
                raportSwitch.setChecked(true);
                tytul.setEnabled(true);
                folder.setEnabled(true);
                komentarz.setEnabled(true);
            }
        });


        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "kkkkkk:" + Environment.getExternalStorageState().toString(), Toast.LENGTH_SHORT).show();
                int iloscModulowCamera = Camera.getNumberOfCameras();
                packageManager = context.getPackageManager();
                boolean urzadzeniaPosiadaModulCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);

                if (iloscModulowCamera == 0 || !urzadzeniaPosiadaModulCamera) {
                    Toast.makeText(context, "Urzedzenie nie posiada modułu 'Camera'", Toast.LENGTH_SHORT).show();
                } else {
                    nazwa_folderu = null;
                    timeStamp = null;
                    if (trybEdycja == true) {
                        nazwa_folderu = "Album podróżniczy/" + databaseAdapter.pobranieWartosciZTabeli(databaseAdapter.DB_TABLE_MAIN, DatabaseAdapter.KEY_TITLE, (int) numerObecnejPodrozy);
                        timeStamp = new SimpleDateFormat(tablicaWszystkichTras[tablicaWszystkichTras.length-1] + "_yyyyMMdd_HHmmss").format(new Date());
                        databaseAdapter.wstawKrotkeDoTabeliZdjecia(timeStamp, tablicaWszystkichTras[tablicaWszystkichTras.length-1], null);
                    } else {
                        nazwa_folderu = "Album podróżniczy/" + databaseAdapter.pobranieWartosciZTabeli(databaseAdapter.DB_TABLE_MAIN, DatabaseAdapter.KEY_TITLE, (int) numerObecnejPodrozy);
                        timeStamp = new SimpleDateFormat(numerObecnejTrasy + "_yyyyMMdd_HHmmss").format(new Date());
                        databaseAdapter.wstawKrotkeDoTabeliZdjecia(timeStamp, (int) numerObecnejTrasy, null);
                    }
                    Intent intentZdjecie = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


                    File imagesFolder = new File(Environment.getExternalStorageDirectory(), nazwa_folderu);
                    imagesFolder.mkdirs();

                    File image = new File(imagesFolder, aktualnyFolder + timeStamp + ".jpg");
                    Uri uriSavedImage = Uri.fromFile(image);

                    intentZdjecie.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

                    if (intentZdjecie.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intentZdjecie, REQUEST_IMAGE_CAPTURE);
                    }
                }

            }
        });


        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker arg0) {
                markeryZeZdjeciami.setText("Raportowanie zdjęć do folderu " + arg0.getId());
                arg0.showInfoWindow();
                aktualnyFolder = arg0.getId()+"_";
                //Toast.makeText(context, "nacisnieto marker: " + arg0.getId(), Toast.LENGTH_SHORT).show();
                return true;
            }

        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                markeryZeZdjeciami.setText("");
                aktualnyFolder = "";
            }
        });


        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker arg0) {
                // TODO Auto-generated method stub
               // Log.d("System out", "onMarkerDragStart..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onMarkerDragEnd(Marker arg0) {
                // TODO Auto-generated method stub
              //  Log.d("System out", "onMarkerDragEnd..." + arg0.getPosition().latitude + "..." + arg0.getPosition().longitude);
                databaseAdapter.aktualizacjaKrotkiTabeliAlbum(arg0.getId(), (int) numerObecnejPodrozy, Double.toString(arg0.getPosition().latitude), Double.toString(arg0.getPosition().longitude));
                //map.animateCamera(CameraUpdateFactory.newLatLng(arg0.getPosition()));
            }

            @Override
            public void onMarkerDrag(Marker arg0) {
                // TODO Auto-generated method stub
              //  Log.i("System out", "onMarkerDrag...");
            }
        });



        //nasłuchuje
        editComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (komentarz_tytul == 1) {
                        if (trybEdycja == true) {


                            /*nazwa_folderu = "Album podróżniczy/" + databaseAdapter.pobranieWartosciZTabeli(databaseAdapter.DB_TABLE_MAIN, DatabaseAdapter.KEY_TITLE, (int) numerObecnejPodrozy - 1);
                            timeStamp = new SimpleDateFormat(numerObecnejTrasy + "_yyyyMMdd_HHmmss").format(new Date());
                            databaseAdapter.wstawKrotkeDoTabeliZdjecia(timeStamp, (int) numerObecnejTrasy, null);
                        }
                        Intent intentZdjecie = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);


                        File imagesFolder = new File(Environment.getExternalStorageDirectory(), nazwa_folderu);*/
                        String nazwa_starego_folderu = "Album podróżniczy/" + databaseAdapter.pobranieWartosciZTabeli(databaseAdapter.DB_TABLE_MAIN, DatabaseAdapter.KEY_TITLE, (int) numerObecnejPodrozy);
                        File starszyFile = new File(Environment.getExternalStorageDirectory(), nazwa_starego_folderu);
                        String nowa_nazwa = editComment.getText().toString();
                        File nowyFile = new File(starszyFile.getParent(),nowa_nazwa);
                        starszyFile.renameTo(nowyFile);
                        /*    File file = new File(databaseAdapter.pobranieWartosciZTabeli(databaseAdapter.DB_TABLE_MAIN, DatabaseAdapter.KEY_TITLE, (int) numerObecnejPodrozy));
                        File file2 = new File(editComment.getText().toString());
                        file.renameTo(file2);*/
                        databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozy, DatabaseAdapter.KEY_TITLE, editComment.getText().toString());



                        } else {
                        databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozy, DatabaseAdapter.KEY_TITLE, editComment.getText().toString());
                    }
                    toolbar.setTitle(" " + editComment.getText().toString());
                    editComment.setText("");
                    editComment.setVisibility(View.INVISIBLE);
                } else if (komentarz_tytul == 0) {
                    if (trybEdycja == true) {
                        databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozy, DatabaseAdapter.KEY_COMMENT, editComment.getText().toString());
                    } else {
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
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String currentDateandTime = sdf.format(new Date());
            databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozy, DatabaseAdapter.KEY_DATE_END, currentDateandTime);
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {


        //zamkniecieDostepuDOBazyDanych();
        mGoogleApiClient.disconnect();
        super.onStop();
        Log.d(TAG, "stop activity");
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            //Intent intentPhoto = new Intent(Map.this, CameraModule.class);
           // startActivity(intentPhoto);
        }
        else {
            File imagesFolder = new File(Environment.getExternalStorageDirectory(), nazwa_folderu+"/"+timeStamp);
            imagesFolder.delete();
            Toast.makeText(this, "Zrezygnowanie z robienia zdjęcia", Toast.LENGTH_SHORT).show();
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
                //markerOptions = new MarkerOptions().position(latLng);
                //marker = map.addMarker(markerOptions);
                layoutBelowMap.setVisibility(View.INVISIBLE);
                previousLatLng = latLng;
                recognizeLocation(latLng);
                raportSwitch.setEnabled(true);
            }

            //kontynuujemy rysowanie- kolejny punkt na tej samej rasie
            else if (quantityRoute == 2) {
                currentZoom = map.getCameraPosition().zoom;
                //marker.setPosition(latLng);
                line = map.addPolyline(new PolylineOptions().add(previousLatLng, latLng).width(15).color(Color.parseColor("#FF4081")));
                previousLatLng = latLng;
                databaseAdapter.wstawKrotkeDoTabeliWspolrzedne(Double.toString(latLng.latitude), Double.toString(latLng.longitude), (int) numerObecnejTrasy);
            }

            //jezeli zaczynam kolejna trase- zaczynamy nowe rysowanie
            else if (quantityRoute == 1) {
                counterRoutes++;
                currentZoom = map.getCameraPosition().zoom;
                //marker.setPosition(latLng);
                previousLatLng = latLng;
                numerObecnejTrasy = databaseAdapter.wstawKrotkeDoTabeliTrasa((int) numerObecnejPodrozy);
                databaseAdapter.wstawKrotkeDoTabeliWspolrzedne(Double.toString(latLng.latitude), Double.toString(latLng.longitude), (int) numerObecnejTrasy);
                quantityRoute = 2;
            }

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, currentZoom));
           // databaseAdapter.wypiszTabele();
        }
    }

    //rozpoznanie państwa, miasta, pobranie daty i zapisanie pierwszych kordynatów w JSON object
    private void recognizeLocation(LatLng latLng) throws IOException, JSONException {

        listAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        if(listAddress!=null && listAddress.size()>0) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            String currentDateandTime = sdf.format(new Date());

            Address address = listAddress.get(0);

            numerObecnejPodrozy = (long) intent.getIntExtra(BuildExistMap.POZYCJA_PODROZY, -99999);

            if(trybEdycja==false) {
                numerObecnejPodrozy = databaseAdapter.wstawKrotkeDoTabeliPodroze(DatabaseAdapter.KEY_COUNTRY, address.getCountryName());
                databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozy, DatabaseAdapter.KEY_TITLE, "Podróż nr " + iloscPodrozyZActivityMap);
                databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozy, DatabaseAdapter.KEY_CITY, address.getLocality());
                databaseAdapter.aktualizacjaKrotkiTabeliPodroze(numerObecnejPodrozy, DatabaseAdapter.KEY_DATE_START, currentDateandTime);
            }
            numerObecnejTrasy = databaseAdapter.wstawKrotkeDoTabeliTrasa((int) numerObecnejPodrozy);
            databaseAdapter.wstawKrotkeDoTabeliWspolrzedne(Double.toString(latLng.latitude), Double.toString(latLng.longitude), (int) numerObecnejTrasy);
        }
    }

    private void addFolder() {
        mCustomMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.element_marker, null);
        mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.miniaturka_marker);

        markerOptionsFolder = new MarkerOptions().alpha(70).draggable(true).position(latLng).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, R.drawable.pionowe))).title("Tutaj trafią zdjęcia");
        String idMarker = map.addMarker(markerOptionsFolder).getId();

        databaseAdapter.wstawKrotkeDoTabeliAlbum(idMarker,(int) numerObecnejPodrozy, Double.toString(latLng.latitude), Double.toString(latLng.longitude));
        databaseAdapter.wypiszTabele();
    }


    private void pobranieElementowWidoku() {
        markeryZeZdjeciami = (TextView) findViewById(R.id.informacjaMarker);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        buttonStart = (FloatingActionButton) findViewById(R.id.buttonStart);
        buttonCamera = (FloatingActionButton) findViewById(R.id.camera);

        geocoder= new Geocoder(getApplicationContext(), Locale.getDefault());
        raportSwitch = (Switch) findViewById(R.id.raportSwitch);
        editComment = (EditText) findViewById(R.id.editComment);
        layoutBelowMap = (RelativeLayout) findViewById(R.id.layoutBelowMap);
        raportSwitch.setEnabled(false);
        buttonCamera.setVisibility(View.INVISIBLE);
        buttonCamera.setEnabled(false);
        //iconFolder = BitmapDescriptorFactory.fromResource(R.drawable.pionowe);

    }

    private Bitmap getMarkerBitmapFromView(View view, @DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.element_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.miniaturka_marker);
        markerImageView.setImageResource(resId);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }




    //inflator napompuje akcje, które zostały zdefiniowane w XML i doda je do action bar
    //MenuInflator dostanie dostep przez getMenuInflator z obecnego activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_map, menu);
        tytul = (MenuItem) menu.findItem(R.id.action_zmien_tytul);
        folder = (MenuItem) menu.findItem(R.id.action_dodaj_folder);
        komentarz =  (MenuItem) menu.findItem(R.id.action_dodaj_komentarz);

        tytul.setEnabled(enableMenu);
        folder.setEnabled(enableMenu);
        komentarz.setEnabled(enableMenu);
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

    private void dostepDoBazyDanych() {
        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        databaseAdapter.open();
    }
    private void zamkniecieDostepuDOBazyDanych() {
        databaseAdapter.close();
    }


}