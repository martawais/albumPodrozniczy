package mw.albumpodrozniczy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by mstowska on 3/6/2016.
 */
public class BuildExistMap extends AppCompatActivity {

    public final static String EDYCJA = "edycja";
    public final static String POZYCJA_PODROZY = "pozycjaPodrozy";


    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DatabaseAdapter databaseAdapter;

    private String nazwa_podrozy;
    public int pozycja;
    public int[] tablicaTras;

    public boolean edycja = true;

    private OneFragment oneFragment;
    private TwoFragment twoFragment;


    private Map map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_exist_map);

        Intent intent = getIntent();
        pozycja = intent.getIntExtra(MainActivity.POZYCJA, 0);

        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        databaseAdapter.open();
        nazwa_podrozy = databaseAdapter.pobranieWartosciZTabeli(databaseAdapter.DB_TABLE_MAIN, DatabaseAdapter.KEY_TITLE, pozycja);
        databaseAdapter.close();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(nazwa_podrozy);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    /*@Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.

            super.onBackPressed();
            finish();
        } else {
            // Otherwise, select the previous step.
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }
    }*/


        @Override
    protected void onResume() {
        super.onResume();
        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        databaseAdapter.open();
        nazwa_podrozy = databaseAdapter.pobranieWartosciZTabeli(databaseAdapter.DB_TABLE_MAIN, DatabaseAdapter.KEY_TITLE, pozycja);
        tablicaTras = databaseAdapter.pobranieTablicyWszystkichTras(pozycja);
        databaseAdapter.close();
        toolbar.setTitle(nazwa_podrozy);
        setSupportActionBar(toolbar);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putInt(POZYCJA_PODROZY, pozycja);

        oneFragment = new OneFragment();
        twoFragment = new TwoFragment();
        oneFragment.setArguments(bundle);
        twoFragment.setArguments(bundle);

        adapter.addFragment(oneFragment, "MAPA");
        adapter.addFragment(twoFragment, "ALBUM");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_build_exist_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_znajdz_lokalizacje:
                Toast.makeText(this, "Lokalizacja tras", Toast.LENGTH_SHORT).show();
                LatLng lokalizacja = new LatLng(oneFragment.ostatniaSzerokosc, oneFragment.ostatniaDlugosc);
                oneFragment.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokalizacja, 16));
                break;
            case R.id.action_edytuj_mape:
                Toast.makeText(this, "Edytuj mape", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(BuildExistMap.this, Map.class);
                intent.putExtra(EDYCJA, edycja);
                intent.putExtra(POZYCJA_PODROZY,  pozycja);
                startActivity(intent);
                break;
            case R.id.action_załacz_zdjecie:
                Toast.makeText(this, "Załącz zdjęcie", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }



}