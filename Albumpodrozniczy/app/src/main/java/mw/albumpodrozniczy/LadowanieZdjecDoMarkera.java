package mw.albumpodrozniczy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * Created by mstowska on 4/11/2016.
 */
public class LadowanieZdjecDoMarkera extends AsyncTask<String, Void, MarkerOptions[]> {
    private ImageView mMarkerImageView;
    private int pozycja;
    private Context context;
    private View mCustomMarkerView;
    private GoogleMap googleMap;
    private double[] tablicaSzerokosciAlbumow;
    private double[] tablicaDlugosciAlbumow;
    private boolean trybaEdycja;

    private String sciezkaDoZdjecia;
    private DatabaseAdapter databaseAdapter;


    public LadowanieZdjecDoMarkera(Context context, int pozycja, View mCustomMarkerView, ImageView  mMarkerImageView, GoogleMap googleMap, boolean trybEdycja) {
        this.pozycja = pozycja;
        this.mMarkerImageView = mMarkerImageView;
        this.context = context;
        this.mCustomMarkerView = mCustomMarkerView;
        this.googleMap = googleMap;
        this.trybaEdycja = trybEdycja;

    }

    @Override
    protected void onPreExecute() {
        databaseAdapter = new DatabaseAdapter(context);
        databaseAdapter.open();
        tablicaSzerokosciAlbumow = databaseAdapter.pobranieTablicyWszystkichWspolrzedneAlbumu(pozycja, "szerokosc");
        tablicaDlugosciAlbumow = databaseAdapter.pobranieTablicyWszystkichWspolrzedneAlbumu(pozycja, "dlugosc");
    }

    @Override
    protected MarkerOptions[] doInBackground(String... nazwaMarkera) {


        MarkerOptions[] opcjeMarkerow = new MarkerOptions[nazwaMarkera.length];
        for(int i=0; i<nazwaMarkera.length; i++) {
            LatLng wspolrzedne = new LatLng(tablicaSzerokosciAlbumow[i], tablicaDlugosciAlbumow[i]);
            sciezkaDoZdjecia = getFirstPhotoInAlbum(nazwaMarkera[i]);
            if(trybaEdycja == true) {
                opcjeMarkerow[i] = new MarkerOptions().alpha(70).position(wspolrzedne).draggable(true).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, mMarkerImageView, R.drawable.folder_multiple_image, sciezkaDoZdjecia))).title("Zobacz tylko te zdjęcia");
            }
            else {
                opcjeMarkerow[i] = new MarkerOptions().alpha(70).position(wspolrzedne).draggable(false).icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(mCustomMarkerView, mMarkerImageView, R.drawable.folder_multiple_image, sciezkaDoZdjecia))).title("Zobacz tylko te zdjęcia");
            }
        }

        return opcjeMarkerow;

    }

    @Override
    protected void onPostExecute(MarkerOptions[] opcjeMarkerow) {
        for(int i=0; i<opcjeMarkerow.length; i++){
            googleMap.addMarker(opcjeMarkerow[i]);
        }

        databaseAdapter.close();

    }

    private int obliczanieOptymalnegoRozmiaruZdjecia(BitmapFactory.Options opcje, int maxSzerokosc,int maxWysokosc ) {
        int wysokosc = opcje.outHeight;
        int szerokosc = opcje.outWidth;
        int wspolczynnik = 1;

        if (wysokosc > maxWysokosc || szerokosc > maxSzerokosc) {
            int polowaWysokosci = wysokosc / 2;
            int polowaSzerokosci = szerokosc / 2;

            while ((polowaWysokosci / wspolczynnik) > maxWysokosc
                    && (polowaSzerokosci / wspolczynnik) > maxSzerokosc) {
                wspolczynnik *= 2;
            }
        }
        return wspolczynnik;
    }

    private Bitmap dekodowanieBitmapy(String sciezkaDoZdjecia, int maxSzerokosc,int maxWysokosc) {
        BitmapFactory.Options opcje = new BitmapFactory.Options();
        opcje.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(sciezkaDoZdjecia, opcje);

        opcje.inSampleSize = obliczanieOptymalnegoRozmiaruZdjecia(opcje, maxSzerokosc,maxWysokosc);

        opcje.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(sciezkaDoZdjecia, opcje);
    }



    public String getFirstPhotoInAlbum(String marker) {

        String sciezkaDoZdjecia = "";
        String nazwaPodrozy = databaseAdapter.pobranieWartosciZTabeli(databaseAdapter.DB_TABLE_MAIN, DatabaseAdapter.KEY_TITLE, (int) pozycja);
        File file = new File(Environment.getExternalStorageDirectory()+ File.separator + "Album podróżniczy" + File.separator+ nazwaPodrozy);

        if (file.isDirectory()) {
            // listFile = file.listFiles();
            final Pattern regex = Pattern.compile(marker + ".*");
            File[] flists = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return regex.matcher(file.getName()).matches();
                }
            });

            if(flists.length>0) {
                sciezkaDoZdjecia = flists[0].getAbsolutePath();
            }
        }
        return sciezkaDoZdjecia;
    }

    private Bitmap getMarkerBitmapFromView(View view, ImageView mMarkerImageView, @DrawableRes int resId, String nazwaAlbumu) {

        //View customMarkerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.element_marker, null);
       // ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.miniaturka_marker);

        if(nazwaAlbumu!="") {
            Bitmap bitmap = dekodowanieBitmapy(sciezkaDoZdjecia, 40, 40);
            //Bitmap bitmap = BitmapFactory.decodeFile(sciezkaDoZdjecia);
            //
            try {
                ExifInterface exif = new ExifInterface(sciezkaDoZdjecia);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                //Bitmap bitmap = BitmapFactory.decodeFile(sciezka[obecneZdjecie]);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap

                mMarkerImageView.setImageBitmap(bitmap);
                mMarkerImageView.setMaxWidth(60);
                mMarkerImageView.setMaxHeight(60);
                mMarkerImageView.setMinimumWidth(57);
                mMarkerImageView.setMinimumHeight(57);
                //    markerImageView.setImageBitmap(bitmap);
                //   Toast.makeText(context, "bitmapa wgrana", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e) {}

        }

       // markerImageView.setImageResource(R.drawable.folder_multiple_image);
       // markerImageView.setImageResource(context.getResources().getIdentifier("folder_multiple_image", "drawable", context.getPackageName()));
        //markerImageView.setImageResource(context.getResources().getIdentifier("folder_multiple_image", "drawable", context.getPackageName()));

        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = view.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        view.draw(canvas);
        return returnedBitmap;
    }

}
