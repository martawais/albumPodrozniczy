package mw.albumpodrozniczy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    Intent intent;

    public final static String POZYCJA = "pozycja";

    final private int REQUEST_FINE_LOCATION = 0;
    private String[] nazwyPodrozy;
    private DatabaseAdapter databaseAdapter;
    private ListView listView;
    String[] krotki;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        listView = (ListView) findViewById(R.id.listView);
        FloatingActionButton dodajNowaPodroz = (FloatingActionButton) findViewById(R.id.fab);



        //if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_FINE_LOCATION);
        }


       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
                Toast.makeText(getApplicationContext(),
                        "Podroz nr: " + pos,
                        Toast.LENGTH_SHORT).show();
            }
        });*/

        dodajNowaPodroz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Znajdz swoja lokalizacje", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


                intent = new Intent(MainActivity.this, Map.class);
                startActivity(intent);


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        aktualizacjaKrotekZTabeliPodroze();
        inicjalizacjaListView();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                intent = new Intent(MainActivity.this, BuildExistMap.class);
                intent.putExtra(POZYCJA, position);
                startActivity(intent);
            }
        });


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
        if (id == R.id.action_usun) {
            databaseAdapter.open();
            Toast.makeText(this, "Czyszczenie bazy danych", Toast.LENGTH_SHORT).show();
            databaseAdapter.usuwanieBazyDanych();
            databaseAdapter.close();
            onResume();
            //String nazwa_podrozy = databaseAdapter.pobranieWartosciZTabeli(databaseAdapter.DB_TABLE_MAIN, DatabaseAdapter.KEY_TITLE, 2);
            //Toast.makeText(this, nazwa_podrozy, Toast.LENGTH_LONG).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void inicjalizacjaListView() {
        listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, krotki));
    }

    private void aktualizacjaKrotekZTabeliPodroze() {
        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        databaseAdapter.open();
        krotki = databaseAdapter.wypisanieWszystkichKolumnTabeliPodroze();
        databaseAdapter.close();
    }
}
