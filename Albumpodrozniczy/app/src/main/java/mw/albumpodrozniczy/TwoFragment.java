package mw.albumpodrozniczy;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;

/**
 * Created by mstowska on 3/6/2016.
 */
public class TwoFragment extends Fragment {


    private int pozycja;
    private String nazwaPodrozy;
    private DatabaseAdapter databaseAdapter;
    private Context context;
    private GridView grid;
    private GridViewAdapter adapter;

    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;


    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_two, container, false);
        context = container.getContext();
        pozycja = getArguments().getInt(BuildExistMap.POZYCJA_PODROZY);
        nazwaPodrozy = getArguments().getString(BuildExistMap.NAZWA_PODROZY);

        databaseAdapter = new DatabaseAdapter(context);
        databaseAdapter.open();

        int[] trasy = databaseAdapter.pobranieTablicyWszystkichTras(pozycja++);
        String[][] tablicWszystkichZdjec = new String[trasy.length][];
        String[][] tablicWszystkichSciezekDoZdjec = new String[trasy.length][];

        for(int i=0; i<trasy.length; i++) {
            String[] tablica = databaseAdapter.pobranieTablicyWszystkichZdjecDoTrasy(trasy[i]);
            tablicWszystkichZdjec[i] = tablica;
            if(tablica.length!=0) {
                Toast.makeText(context, "aa" + tablica[0], Toast.LENGTH_SHORT).show();
            }
        }

        File file = new File(Environment.getExternalStorageDirectory()+ File.separator + "Album podróżniczy" + File.separator+ nazwaPodrozy);



        if (file.isDirectory()) {
            listFile = file.listFiles();
            // Create a String array for FilePathStrings
            FilePathStrings = new String[listFile.length];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[listFile.length];

            for (int i = 0; i < listFile.length; i++) {
                // Get the path of the image file
                FilePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name image file
                FileNameStrings[i] = listFile[i].getName();
            }
        }
        grid = (GridView) view.findViewById(R.id.gridView);
        adapter = new GridViewAdapter(context, FilePathStrings, FileNameStrings);
        grid.setAdapter(adapter);

        return view;
    }


}