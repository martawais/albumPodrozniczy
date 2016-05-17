package mw.albumpodrozniczy;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


/**
 * Created by mstowska on 3/6/2016.
 */
public class OneFragment extends Fragment {


    private int pozycja;

    private SupportMapFragment mapaSupported;

    private DatabaseAdapter databaseAdapter;
    private Context context;
    private LatLng latLng;


    private MapView mMapView;
    public GoogleMap googleMap;

    private View mCustomMarkerView;
    private ImageView mMarkerImageView;

    public double ostatniaSzerokosc;
    public double ostatniaDlugosc;

    public String aktualnyFolder = "";

    public OneFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        //your code which you want to refresh
        pobranieWszystkichTrasOrazWspolrzednychZBazyDanych();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_one, container, false);
        mapaSupported = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        googleMap = mapaSupported.getMap();
        context = container.getContext();
        pozycja = getArguments().getInt(BuildExistMap.POZYCJA_PODROZY);

        pobranieWszystkichTrasOrazWspolrzednychZBazyDanych();

        if(latLng==null) {
            latLng = new LatLng(51, 16);;
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));


        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                arg0.showInfoWindow();
                aktualnyFolder = arg0.getId();

                //Toast.makeText(context, "nacisnieto marker: " + arg0.getId(), Toast.LENGTH_SHORT).show();
                TwoFragment.setAktualnyAlbum(aktualnyFolder + "_");

                return true;
            }

        });
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                aktualnyFolder = "";
                TwoFragment.setAktualnyAlbum(aktualnyFolder);
            }
        });

        return view;



    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser == true) {
            //onResume();
        }
    }





    private void pobranieWszystkichTrasOrazWspolrzednychZBazyDanych() {

        databaseAdapter = new DatabaseAdapter(context);
        databaseAdapter.open();

        int[] tablica = databaseAdapter.pobranieTablicyWszystkichTras(pozycja);
        latLng = null;
        if(tablica.length!=0) {
            for (int i = 0; i < tablica.length; i++) {
                //Toast.makeText(context, tablica[i]-1 + "", Toast.LENGTH_SHORT).show();
                double[] szerokosc = databaseAdapter.pobranieTablicyWszystkichWspolrzedne(tablica[i], "szerokosc");
                double[] dlugosc = databaseAdapter.pobranieTablicyWszystkichWspolrzedne(tablica[i], "dlugosc");

                Polyline line;
                //Toast.makeText(context, szerokosc.length + "", Toast.LENGTH_SHORT).show();
                if (szerokosc.length != 0) {
                    latLng = new LatLng(szerokosc[0], dlugosc[0]);
                    if(szerokosc.length != 1) {
                        for (int j = 1; j < szerokosc.length; j++) {
                            line = googleMap.addPolyline(new PolylineOptions().add(new LatLng(szerokosc[j - 1], dlugosc[j - 1]), new LatLng(szerokosc[j], dlugosc[j])).width(15).color(R.color.colorPath));
                            if(j==(szerokosc.length-1)) {
                                ostatniaSzerokosc = szerokosc[j];
                                ostatniaDlugosc = dlugosc[j];
                            }
                        }
                    }else {
                        googleMap.addCircle(new CircleOptions().center(new LatLng(szerokosc[0], dlugosc[0])).radius(0.3).strokeColor(R.color.colorPath).fillColor(R.color.colorPath));
                        ostatniaSzerokosc = szerokosc[0];
                        ostatniaDlugosc = dlugosc[0];

                    }
                }

                /*if(i==(szerokosc.length-1)) {
                    ostatniaSzerokosc = szerokosc[szerokosc.length-1];
                    ostatniaDlugosc = dlugosc[szerokosc.length-1];
                }*/
            }
        }

        String[] tablicaNazwAlbumow = databaseAdapter.pobranieTablicyWszystkichNazwAlbumu(pozycja);
        if(tablicaNazwAlbumow.length!=0) {

            mCustomMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.element_marker, null);
            mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.miniaturka_marker);
            LadowanieZdjecDoMarkera ladowanieZdjec = new LadowanieZdjecDoMarkera(context, pozycja, mCustomMarkerView,mMarkerImageView,googleMap, false);
            ladowanieZdjec.execute(tablicaNazwAlbumow);
        }

        databaseAdapter.close();
    }




}