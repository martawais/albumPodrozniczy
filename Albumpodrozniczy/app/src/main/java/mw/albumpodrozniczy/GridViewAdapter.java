package mw.albumpodrozniczy;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        TextView text = (TextView) vi.findViewById(R.id.textGridView);
        // Locate the ImageView in gridview_item.xml
        ImageView image = (ImageView) vi.findViewById(R.id.imageGridView);

        // Set file name to the TextView followed by the position
        //text.setText(filename[position]);

        // Decode the filepath with BitmapFactory followed by the position
        Bitmap bmp = BitmapFactory.decodeFile(filepath[position]);
        Bitmap bmp1 = Bitmap.createScaledBitmap(bmp, 500, 500, false);
        // Set the decoded bitmap into ImageView
        image.setImageBitmap(bmp1);
        return vi;
    }
}
