package mw.albumpodrozniczy;

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

/**
 * Created by mstowska on 4/12/2016.
 */
public class DisplayImage extends AppCompatActivity {

    private ImageView imageView;
    private Intent intent;
    private Toolbar toolbar;

    String[] sciezka;
    int obecneZdjecie;
    String nazwaPodrozy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        intent = getIntent();
        sciezka = intent.getStringArrayExtra("sciezka");
        obecneZdjecie = intent.getIntExtra("obecneZdjecie", 0);
        nazwaPodrozy = intent.getStringExtra("nazwaPodrozy");

        toolbar = (Toolbar) findViewById(R.id.toolbarDisplay);
        toolbar.setTitle(nazwaPodrozy);
        setSupportActionBar(toolbar);

        imageView = (ImageView) findViewById(R.id.displayImage);

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

        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                if( (action == MotionEvent.ACTION_DOWN) && (action != MotionEvent.ACTION_POINTER_DOWN)) {
                    // Single touch
                    int x = (int) event.getX();
                    int width = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
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
               // Toast.makeText(getApplicationContext(), x + " Screen tapped: " + width, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

}