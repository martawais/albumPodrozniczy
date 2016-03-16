package mw.albumpodrozniczy;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
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


    MapView mMapView;
    private GoogleMap googleMap;

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

        return view;
    }


    private void pobranieWszystkichTrasOrazWspolrzednychZBazyDanych() {

        databaseAdapter = new DatabaseAdapter(context);
        databaseAdapter.open();

        int[] tablica = databaseAdapter.pobranieTablicyWszystkichTras(pozycja);
        latLng = null;
        if(tablica.length!=0) {
            for (int i = 0; i < tablica.length; i++) {
                //Toast.makeText(context, tablica[i]-1 + "", Toast.LENGTH_SHORT).show();
                double[] szerokosc = databaseAdapter.pobranieTablicyWszystkichWspolrzedne(tablica[i]-1, "szerokosc");
                double[] dlugosc = databaseAdapter.pobranieTablicyWszystkichWspolrzedne(tablica[i]-1, "dlugosc");
                Polyline line;
                //Toast.makeText(context, szerokosc.length + "", Toast.LENGTH_SHORT).show();
                if (szerokosc.length != 0) {
                    latLng = new LatLng(szerokosc[0], dlugosc[0]);
                    if(szerokosc.length != 1) {
                        for (int j = 1; j < szerokosc.length; j++) {
                            line = googleMap.addPolyline(new PolylineOptions().add(new LatLng(szerokosc[j - 1], dlugosc[j - 1]), new LatLng(szerokosc[j], dlugosc[j])).width(15).color(Color.parseColor("#FF4081")));
                        }
                    }else {
                        googleMap.addCircle(new CircleOptions().center(new LatLng(szerokosc[0], dlugosc[0])).radius(0.3).strokeColor(Color.parseColor("#FF4081")).fillColor(Color.parseColor("#FF4081")));
                    }
                }
            }
        }
        databaseAdapter.close();
    }

}