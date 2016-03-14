package mw.albumpodrozniczy;

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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


/**
 * Created by mstowska on 3/6/2016.
 */
public class OneFragment extends Fragment {

    private static GoogleMap mMap;
    private SupportMapFragment mapaSupported;

    private DatabaseAdapter databaseAdapter;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_one, container, false);

        mapaSupported = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        googleMap = mapaSupported.getMap();



       // Toast.makeText(this, databaseAdapter.pobranieTablicyWszystkichTras(2)+"", Toast.LENGTH_SHORT).show();

        double latitude = 17.385044;
        double longitude = 78.486671;

        LatLng previousLatLng = new LatLng(latitude, longitude);

        double latitude1 = 17.395044;
        double longitude1 = 78.496671;

        LatLng latLng = new LatLng(latitude1, longitude1);


        Polyline line = googleMap.addPolyline(new PolylineOptions().add(previousLatLng, latLng).width(15).color(Color.parseColor("#FF4081")));
        // create marker
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Hello Maps");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(17.385044, 78.486671)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        return view;

    }



}