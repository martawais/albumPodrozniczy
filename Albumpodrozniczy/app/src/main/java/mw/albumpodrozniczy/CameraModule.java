package mw.albumpodrozniczy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

/**
 * Created by mstowska on 3/17/2016.
 */
public class CameraModule extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_module);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(" Nowe zdjęcie");
        setSupportActionBar(toolbar);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        //intent.setType("image*/");
        // intent.setAction(Intent.ACTION_GET_CONTENT);
        // intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, CAMERA_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            ImageView picture = (ImageView)findViewById(R.id.picture);
            picture.setImageBitmap(photo);
        }
    }
}
