package mw.albumpodrozniczy;

import android.content.Context;
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
    private String[] sciezka;
    private String[] nazwa;
    private static LayoutInflater inflater = null;

    public GridViewAdapter(Context context, String[] sciezka, String[] nazwa) {

        this.context = context;
        this.sciezka = sciezka;
        this.nazwa = nazwa;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return sciezka.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ImageView picture;


        if (convertView == null) {
            view = inflater.inflate(R.layout.element_gridview, parent, false);
            view.setTag(R.id.imageGridView, view.findViewById(R.id.imageGridView));
        }

        picture = (ImageView) view.findViewById(R.id.imageGridView);
        //ImageView imageView = (ImageView) view.findViewById(R.id.imageGridView);
        //imageView.setLayoutParams(new GridView.LayoutParams(504, 504));
       // imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
       // imageView.setPadding(8, 8, 8, 8);

        picture.setTag(position);
        LadowanieZdjecGridView ladowanieZdjec = new LadowanieZdjecGridView(position, picture);
        ladowanieZdjec.execute(sciezka[position]);

        return view;
    }




}
