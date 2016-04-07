package mw.albumpodrozniczy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


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

    public ListViewAdapter(Context context, String[] id_podrozy, String[] nazwa, String[] kraj, String[] miasto, String[] dataP, String[] dataK, String[] komentarz) {
        super(context, -1, id_podrozy);
        this.context = context;
        this.id_podrozy = id_podrozy;
        this.nazwa = nazwa;
        this.kraj = kraj;
        this.miasto = miasto;
        this.dataP = dataP;
        this.dataK = dataK;
        this.komentarz = komentarz;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.adapter_listview, parent, false);
        TextView nazwaP = (TextView) rowView.findViewById(R.id.nazwaPodrozy);
        TextView krajP = (TextView) rowView.findViewById(R.id.kraj);
        //TextView miastoP = (TextView) rowView.findViewById(R.id.miasto);
        TextView dataPP = (TextView) rowView.findViewById(R.id.dataP);
       // TextView dataKP = (TextView) rowView.findViewById(R.id.dataK);
        TextView komentarzP = (TextView) rowView.findViewById(R.id.komentarz);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        nazwaP.setText("Nazwa podróży:  "+nazwa[position]);
        krajP.setText("Kraj: "+kraj[position]+"               Miasto: "+miasto[position]);
        //miastoP.setText("Miasto: " +miasto[position]);
        dataPP.setText("Początek podróży: "+dataP[position]+"         Koniec podróży: "+dataK[position]);
        //dataKP.setText("Koniec podróży: "+dataK[position]);
        if(komentarz[position]!=null) {
            komentarzP.setText("Komentarz:  " + komentarz[position]);
        }

        // change the icon for Windows and iPhone

        imageView.setImageResource(R.drawable.pionowe);


        return rowView;
    }
}

