package mw.albumpodrozniczy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
                TwoFragment.setAktualnyAlbum(aktualnyFolder+"_");

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

                if(i==(szerokosc.length-1)) {
                    ostatniaSzerokosc = szerokosc[szerokosc.length-1];
                    ostatniaDlugosc = dlugosc[szerokosc.length-1];
                }
            }
        }

        String[] tablicaNazwAlbumow = databaseAdapter.pobranieTablicyWszystkichNazwAlbumu(pozycja);
        double[] tablicaSzerokosciAlbumow = databaseAdapter.pobranieTablicyWszystkichWspolrzedneAlbumu(pozycja, "szerokosc");
        double[] tablicaDlugosciAlbumow = databaseAdapter.pobranieTablicyWszystkichWspolrzedneAlbumu(pozycja, "dlugosc");
        if(tablicaNazwAlbumow.length!=0) {

            mCustomMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.element_marker, null);
            mMarkerImageView = (ImageView) mCustomMarkerView.findViewById(R.id.miniaturka_marker);

            for (int i = 0; i < tablicaNazwAlbumow.length; i++) {
                LatLng wspolrzedne = new LatLng(tablicaSzerokosciAlbumow[i],tablicaDlugosciAlbumow[i]);
                MarkerOptions markerOptionsFolder = new MarkerOptions().alpha(70).position(wspolrzedne).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, R.drawable.pionowe))).title("Zobacz tylko te zdjęcia");
                googleMap.addMarker(markerOptionsFolder);

            }

        }

        databaseAdapter.close();
    }



    private Bitmap getMarkerBitmapFromView(View view, @DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.element_marker, null);
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


}