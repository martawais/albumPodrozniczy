package mw.albumpodrozniczy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Created by mstowska on 4/8/2016.
 */
public class GridViewAdapter extends BaseAdapter {
    // Declare variables
    private Context context;
    private String[] filepath;
    private String[] filename;


    private static LayoutInflater inflater = null;

    public GridViewAdapter(Context context, String[] fpath, String[] fname) {
        this.context = context;
        filepath = fpath;
        filename = fname;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    public int getCount() {
        return filepath.length;

    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
       if (convertView == null)
            vi = inflater.inflate(R.layout.element_gridview, null);
        // Locate the TextView in gridview_item.xml
       // TextView text = (TextView) vi.findViewById(R.id.textGridView);
        // Locate the ImageView in gridview_item.xml
        ImageView image = (ImageView) vi.findViewById(R.id.imageGridView);

        if(image.getTag() != null) {
            ((ImageGetter) image.getTag()).cancel(true);
        }
       // image.setImageBitmap(null);
        ImageGetter task = new ImageGetter(image) ;
        task.execute(filepath[position]);

        image.setTag(task);


        return vi;
    }



    public class ImageGetter extends AsyncTask<String, Void, Bitmap> {
        private ImageView iv;
        public ImageGetter(ImageView v) {
            iv = v;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(params[0],options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            Log.d("imageHeight", imageHeight+"");
            Log.d("imageWidth", imageWidth+"");
            imageWidth = 500;
            imageHeight = imageHeight/(options.outWidth/500);
            Log.d("111imageHeight", imageHeight+"");
            Log.d("1111imageWidth", imageWidth+"");
            options.inJustDecodeBounds = false;
            Bitmap bmp = BitmapFactory.decodeFile(params[0],options);

            try {
                ExifInterface exif = new ExifInterface(params[0]);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                }
                else if (orientation == 3) {
                    matrix.postRotate(180);
                }
                else if (orientation == 8) {
                    matrix.postRotate(270);
                }
                //Toast.makeText(context, bmp.getWidth()+"    "+ bmp.getHeight()+ "", Toast.LENGTH_SHORT).show();
                //bmp = Bitmap.createScaledBitmap(bmp, 500, 500, false);
                //bmp = Bitmap.createScaledBitmap(bmp, imageWidth, imageHeight, false);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true); // rotating bitmap
                //bmp = Bitmap.createScaledBitmap(bmp,  imageHeight, imageWidth, false);
            }
            catch (Exception e) {

            }

//Bitmap.createScaledBitmap(bmp, 500, 500, false)
            //return Bitmap.createScaledBitmap(bmp,  imageHeight, imageWidth, false);
            return Bitmap.createScaledBitmap(bmp,  500, 500, false);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            iv.setImageBitmap(result);
        }
    }
}
