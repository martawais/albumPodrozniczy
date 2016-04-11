package mw.albumpodrozniczy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * Created by mstowska on 4/11/2016.
 */
public class LadowanieZdjecGridView extends AsyncTask<String, Void, Bitmap> {
    private ImageView imageView;
    private int pozycja;

    public LadowanieZdjecGridView(int pozycja, ImageView imageView) {
        this.pozycja = pozycja;
        this.imageView = imageView;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Bitmap doInBackground(String... zdjecie) {

        String sciezkaDoZdjecia = zdjecie[0];

        Bitmap bitmap = dekodowanieBitmapy(sciezkaDoZdjecia, 200, 200);
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

            bitmap = Bitmap.createBitmap(bitmap, 0, 0,300,bitmap.getHeight(), matrix, true); // rotating bitmap

        } catch (Exception e) {

        }
        return bitmap;


    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        int obecnaPozycja = (Integer) imageView.getTag();
        if(obecnaPozycja == this.pozycja) {
            this.imageView.setImageBitmap(bitmap);
        }
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
}
