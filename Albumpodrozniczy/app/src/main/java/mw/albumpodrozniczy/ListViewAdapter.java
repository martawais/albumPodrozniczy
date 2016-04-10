package mw.albumpodrozniczy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;


/**
 * Created by mstowska on 4/7/2016.
 */
public class ListViewAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] id_podrozy;
    private final String[] nazwa;
    private final String[] kraj;
    private final String[] miasto;
    private final String[] dataP;
    private final String[] dataK;
    private final String[] komentarz;
    private final String[] zdjecie;

    public ListViewAdapter(Context context, String[] id_podrozy, String[] nazwa, String[] kraj, String[] miasto, String[] dataP, String[] dataK, String[] komentarz, String[] zdjecie) {
        super(context, -1, id_podrozy);
        this.context = context;
        this.id_podrozy = id_podrozy;
        this.nazwa = nazwa;
        this.kraj = kraj;
        this.miasto = miasto;
        this.dataP = dataP;
        this.dataK = dataK;
        this.komentarz = komentarz;
        this.zdjecie = zdjecie;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.adapter_listview, parent, false);
        TextView nazwaP = (TextView) rowView.findViewById(R.id.nazwaPodrozy);
        TextView krajP = (TextView) rowView.findViewById(R.id.kraj);
        TextView miastoP = (TextView) rowView.findViewById(R.id.miasto);
        TextView dataPP = (TextView) rowView.findViewById(R.id.dataP);
        TextView dataKP = (TextView) rowView.findViewById(R.id.dataK);
        TextView komentarzP = (TextView) rowView.findViewById(R.id.komentarz);
        TextView iloscZdjec = (TextView) rowView.findViewById(R.id.iloscZdjec);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        nazwaP.setText(nazwa[position].toUpperCase());
        krajP.setText("Kraj: " + kraj[position]);//+"               Miasto: "+miasto[position]);
        miastoP.setText("Miasto: " + miasto[position]);
        dataPP.setText("Początek podróży: " + dataP[position]);//+"         Koniec podróży: "+dataK[position]);
        dataKP.setText("Koniec podróży: " + dataK[position]);
        if (komentarz[position] != null) {
            komentarzP.setText("Notatki:  " + komentarz[position]);
        }

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Album podróżniczy" + File.separator + nazwa[position]);
        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            if (file.listFiles().length != 0) {
                iloscZdjec.setText("Ilość zdjęć: " + listFile.length);

                String FilePathStrings = listFile[0].getAbsolutePath();
                ImageGetter2 task = new ImageGetter2(imageView);
                task.execute(FilePathStrings);

                imageView.setTag(task);
                //imageView.setImageBitmap(bitmap);
            }

        }
        return rowView;
    }

    public class ImageGetter2 extends AsyncTask<String, Void, Bitmap> {
        private ImageView iv;

        public ImageGetter2(ImageView v) {
            iv = v;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeFile(params[0]), 500, 500, false);
            try {
                ExifInterface exif = new ExifInterface(params[0]);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                //Toast.makeText(context, bmp.getWidth()+"    "+ bmp.getHeight()+ "", Toast.LENGTH_SHORT).show();
                //bmp = Bitmap.createScaledBitmap(bmp, 500, 500, false);
                //bmp = Bitmap.createScaledBitmap(bmp, imageWidth, imageHeight, false);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
                //bmp = Bitmap.createScaledBitmap(bmp,  imageHeight, imageWidth, false);

            } catch (Exception e) {

            }
            return Bitmap.createScaledBitmap(bitmap, 500, 500, false);
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            iv.setImageBitmap(result);
        }

    }
}

