package mw.albumpodrozniczy;

import android.content.Context;
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
        View elementListView = inflater.inflate(R.layout.adapter_listview, parent, false);

        TextView nazwaP = (TextView) elementListView.findViewById(R.id.nazwaPodrozy);
        TextView krajP = (TextView) elementListView.findViewById(R.id.kraj);
        TextView miastoP = (TextView) elementListView.findViewById(R.id.miasto);
        TextView dataPP = (TextView) elementListView.findViewById(R.id.dataP);
        TextView dataKP = (TextView) elementListView.findViewById(R.id.dataK);
        TextView komentarzP = (TextView) elementListView.findViewById(R.id.komentarz);
        TextView iloscZdjec = (TextView) elementListView.findViewById(R.id.iloscZdjec);
        ImageView imageView = (ImageView) elementListView.findViewById(R.id.icon);
        nazwaP.setText(nazwa[position].toUpperCase());
        krajP.setText("Kraj: " + kraj[position]);
        miastoP.setText("Miasto: " + miasto[position]);
        dataPP.setText("Początek podróży: " + dataP[position]);
        dataKP.setText("Koniec podróży: " + dataK[position]);
        if (komentarz[position] != null) {
            komentarzP.setText("Notatki:  " + komentarz[position]);
        }

        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "Album podróżniczy" + File.separator + nazwa[position]);
        if (file.isDirectory()) {
            imageView.setTag(position);
            File[] listFile = file.listFiles();
            iloscZdjec.setText("Ilość zdjęć: "+listFile.length);
            LadowanieZdjeciaListView ladowanieZdjec = new LadowanieZdjeciaListView(position, imageView);
            ladowanieZdjec.execute(listFile);
        }

        return elementListView;
    }
}

