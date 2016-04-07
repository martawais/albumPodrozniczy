package mw.albumpodrozniczy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;

/**
 * Created by mstowska on 3/17/2016.
 */
public class CameraModule extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private Toolbar toolbar;
    public ImageView picture;
    private Context context;
    private DatabaseAdapter databaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_module);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(" Nowe zdjęcie");
        setSupportActionBar(toolbar);

        Intent intent = getIntent();



        //Bitmap imageBitmap = (Bitmap) intent.getParcelableExtra("Photo");

        picture = (ImageView)findViewById(R.id.picture);
        //picture.setImageBitmap(imageBitmap);
        databaseAdapter = new DatabaseAdapter(getApplicationContext());
        databaseAdapter.open();
        databaseAdapter.wypiszTabele();
        //Toast.makeText(this, "KKKKK: " + d, Toast.LENGTH_SHORT).show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera_module, menu);
        return true;
    }


}
