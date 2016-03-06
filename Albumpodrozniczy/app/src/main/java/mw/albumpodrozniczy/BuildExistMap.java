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

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DatabaseAdapter databaseAdapter;

    private String nazwa_podrozy;
    private int pozycja;

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

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OneFragment(), "MAPA");
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

                break;
            case R.id.action_załacz_zdjecie:
                Toast.makeText(this, "Załącz zdjęcie", Toast.LENGTH_SHORT).show();
                //editComment.setVisibility(View.VISIBLE);
                break;

            default:
                break;
        }

        return true;
    }
}