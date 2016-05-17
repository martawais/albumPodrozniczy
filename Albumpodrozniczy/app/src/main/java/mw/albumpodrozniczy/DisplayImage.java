package mw.albumpodrozniczy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mstowska on 4/12/2016.
 */
public class DisplayImage extends AppCompatActivity {

    private ImageView imageView;
    private ImageView krzyzyk;
    private Intent intent;
    private Toolbar toolbar;
    private TextView tytulPodrozy;
    private TextView komentarz;
    private Context context;
    private View decorView;
    private int uiOptions;

    private String[] sciezka;
    private int obecneZdjecie;
    private String nazwaPodrozy;
    private int iloscZdjec;
    private String komentarzString;
    private String[] nazwyZdjec;
    private String aktualnyAlbum;
    private DatabaseAdapter databaseAdapter;


    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();

        uiOptions = View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_display_image);

        context = getApplicationContext();

        intent = getIntent();
        sciezka = intent.getStringArrayExtra("sciezka");
        obecneZdjecie = intent.getIntExtra("obecneZdjecie", 0);
        nazwaPodrozy = intent.getStringExtra("nazwaPodrozy");
        iloscZdjec = intent.getIntExtra("iloscZdjec", -1);
        nazwyZdjec = intent.getStringArrayExtra("nazwyZdjec");
        aktualnyAlbum = intent.getStringExtra("aktualnyAlbum");

       // toolbar = (Toolbar) findViewById(R.id.toolbarDisplay);
       // toolbar.setTitle(nazwaPodrozy);
     //   setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.displayImage);
        tytulPodrozy = (TextView) findViewById(R.id.tytul_display_image);
        tytulPodrozy.setText("[" + (obecneZdjecie+1) + "|"+ iloscZdjec +"]  "+nazwaPodrozy);
        komentarz = (TextView) findViewById(R.id.komentarz);
        databaseAdapter = new DatabaseAdapter(context);
        databaseAdapter.open();
        String przygotowywanyString = nazwyZdjec[obecneZdjecie].replaceFirst(".jpg", "");
        String regex = "m\\d{1,}_";
        String kom = przygotowywanyString.replaceAll(regex, "");

        komentarzString = databaseAdapter.pobranieKomentarzaDoZdjecia(kom);
        databaseAdapter.close();
        //Toast.makeText(getApplicationContext(), "komentarz    "+komentarzString, Toast.LENGTH_SHORT).show();
        if(komentarzString!=null) {
            komentarz.setText(komentarzString);
        }
        else {
            komentarz.setText("");
        }
        krzyzyk = (ImageView) findViewById(R.id.krzyzyk_display_imge);

        Bitmap bitmap = BitmapFactory.decodeFile(sciezka[obecneZdjecie]);
        try {
            ExifInterface exif = new ExifInterface(sciezka[obecneZdjecie]);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(),bitmap.getHeight(), matrix, true); // rotating bitmap
        } catch (Exception e) {
        }
        imageView.setImageBitmap(bitmap);

        krzyzyk.setOnTouchListener(new View.OnTouchListener() {



            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                //Toast.makeText(getApplicationContext(), "naciśniety krzyzyk!!!!!!!!!!", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if ((action == MotionEvent.ACTION_DOWN) && (action != MotionEvent.ACTION_POINTER_DOWN)) {
                    // Single touch
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    int width = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
                    int high = getApplicationContext().getResources().getDisplayMetrics().heightPixels;
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    // Toast.makeText(getApplicationContext(), "wysokosc  " + y, Toast.LENGTH_SHORT).show();
                    if (y > 120) {
                        if (x > width / 2) {

                            if (obecneZdjecie == sciezka.length - 1) {
                                intent.putExtra("obecneZdjecie", 0);
                            } else {
                                intent.putExtra("obecneZdjecie", obecneZdjecie + 1);
                            }
                            finish();
                            startActivity(intent);

                        } else {

                            if (obecneZdjecie == 0) {
                                intent.putExtra("obecneZdjecie", sciezka.length - 1);
                            } else {
                                intent.putExtra("obecneZdjecie", obecneZdjecie - 1);
                            }

                            finish();
                            startActivity(intent);
                            // Toast.makeText(getApplicationContext(), "po lewo", Toast.LENGTH_SHORT).show();
                            // obecneZdjecie--;
                        }
                    }
                }
                // Toast.makeText(getApplicationContext(), x + " Screen tapped: " + width, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

}