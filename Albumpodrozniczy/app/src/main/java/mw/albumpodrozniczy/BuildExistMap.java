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

    public boolean edycja = true;


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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }
    @Override
    protected void onResume() {
        super.onResume();
        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        databaseAdapter.open();
        nazwa_podrozy = databaseAdapter.pobranieWartosciZTabeli(databaseAdapter.DB_TABLE_MAIN, DatabaseAdapter.KEY_TITLE, pozycja);
        int[] tablica = databaseAdapter.pobranieTablicyWszystkichTras(pozycja);
        databaseAdapter.close();
        toolbar.setTitle(nazwa_podrozy);
        setSupportActionBar(toolbar);


    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle bundle = new Bundle();
        bundle.putInt(POZYCJA_PODROZY, pozycja);
        OneFragment oneFragment = new OneFragment();
        oneFragment.setArguments(bundle);


        adapter.addFragment(oneFragment, "MAPA");
        adapter.addFragment(new TwoFragment(), "ALBUM");
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