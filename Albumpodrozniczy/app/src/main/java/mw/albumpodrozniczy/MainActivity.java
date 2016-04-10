package mw.albumpodrozniczy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    Intent intent;

    public final static String POZYCJA = "pozycja";
    public final static String ILOSCPODROZY = "iloscpodrozy";

    private int permissionRequestCode = 200;

    private Context context;
    private String[] permission = {"android.permission.CAMERA","android.permission.ACCESS_FINE_LOCATION","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"};

    private String[] nazwyPodrozy;
    private DatabaseAdapter databaseAdapter;
    private ListView listView;
    public String[] krotki;
    public String[][] krotkiDwuwymiarowe;
    private boolean cameraAccepted;
    private boolean locationAccepted;
    private boolean writeExternalStorageAccepted;
    private boolean readExternalStorageAccepted;
    private  int  positionInt;


    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarbHandler = new Handler();
    private long fileSize = 1000000;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();

        listView = (ListView) findViewById(R.id.listView);
        FloatingActionButton dodajNowaPodroz = (FloatingActionButton) findViewById(R.id.fab);

        requestPermissions(permission, permissionRequestCode);


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
                if(cameraAccepted && locationAccepted && writeExternalStorageAccepted && readExternalStorageAccepted) {
                    intent = new Intent(MainActivity.this, Map.class);
                    intent.putExtra(ILOSCPODROZY, krotki.length);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(context, "Aplikacja potrzebuje przyznania uprawnień", Toast.LENGTH_SHORT).show();
                    requestPermissions(permission, permissionRequestCode);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (cameraAccepted && locationAccepted && writeExternalStorageAccepted && readExternalStorageAccepted) {
                    intent = new Intent(MainActivity.this, BuildExistMap.class);
                    intent.putExtra(POZYCJA, position);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "Aplikacja potrzebuje przyznania uprawnień", Toast.LENGTH_SHORT).show();
                    requestPermissions(permission, permissionRequestCode);
                }
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){
        switch(permsRequestCode){
            case 200:
                cameraAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                locationAccepted = grantResults[1]== PackageManager.PERMISSION_GRANTED;
                writeExternalStorageAccepted = grantResults[2]== PackageManager.PERMISSION_GRANTED;
                readExternalStorageAccepted = grantResults[3]== PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        aktualizacjaKrotekZTabeliPodroze();
        inicjalizacjaListView();
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


        String[] id = new String[krotki.length];
        String[] nazwa = new String[krotki.length];
        String[] kraj = new String[krotki.length];
        String[] miasto = new String[krotki.length];
        String[] dataP = new String[krotki.length];
        String[] dataK = new String[krotki.length];
        String[] komentarz = new String[krotki.length];

        for(int j=0; j<krotki.length; j++) {
            id[j] = krotkiDwuwymiarowe[j][0];
            nazwa[j] = krotkiDwuwymiarowe[j][1];
            kraj[j] = krotkiDwuwymiarowe[j][2];
            miasto[j] = krotkiDwuwymiarowe[j][3];
            dataP[j] = krotkiDwuwymiarowe[j][4];
            dataK[j] = krotkiDwuwymiarowe[j][5];
            komentarz[j] = krotkiDwuwymiarowe[j][6];

        }
        ListViewAdapter adapter = adapter = new ListViewAdapter(this, id, nazwa, kraj, miasto, dataP, dataK, komentarz );
        listView.setAdapter(adapter);
    }

    private void aktualizacjaKrotekZTabeliPodroze() {
        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        databaseAdapter.open();
        krotki = databaseAdapter.wypisanieWszystkichKolumnTabeliPodroze();
        krotkiDwuwymiarowe = databaseAdapter.wypisanieWszystkichKolumnDoTablicyDwuwymiarowejPodroze();
        databaseAdapter.close();
    }


    //sprawdza przyznane uprawnienia przez użytkownika
    public boolean hasPermission(String permission){
        if(canMakeSmores()){        //sprawdza czy android jest marshmallow
            return(checkSelfPermission(permission)==PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    //sprawdza czy android jest marshmallow
    private boolean canMakeSmores(){
        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
    }
}
