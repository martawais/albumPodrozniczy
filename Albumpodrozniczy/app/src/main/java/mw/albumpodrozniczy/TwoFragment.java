package mw.albumpodrozniczy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

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
    private View view;

    private String[] FilePathStrings;
    private String[] FileNameStrings;
    private File[] listFile;
    private File file;

    public static String aktualnyAlbum = "";


    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_two, container, false);
        context = container.getContext();
        pozycja = getArguments().getInt(BuildExistMap.POZYCJA_PODROZY);


        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser == true) {
            onResume();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        //your code which you want to refresh
        databaseAdapter = new DatabaseAdapter(context);
        databaseAdapter.open();

        int pozycja1 = pozycja;

        int[] trasy = databaseAdapter.pobranieTablicyWszystkichTras(pozycja1);
        String[][] tablicWszystkichZdjec = new String[trasy.length][];
        String[][] tablicWszystkichSciezekDoZdjec = new String[trasy.length][];

        for(int i=0; i<trasy.length; i++) {
            String[] tablica = databaseAdapter.pobranieTablicyWszystkichZdjecDoTrasy(trasy[i]);
            tablicWszystkichZdjec[i] = tablica;
            if(tablica.length!=0) {
                //Toast.makeText(context, "aa" + tablica[0], Toast.LENGTH_SHORT).show();
            }
        }

        nazwaPodrozy = databaseAdapter.pobranieWartosciZTabeli(databaseAdapter.DB_TABLE_MAIN, DatabaseAdapter.KEY_TITLE, (int) pozycja);
        file = new File(Environment.getExternalStorageDirectory()+ File.separator + "Album podróżniczy" + File.separator+ nazwaPodrozy);



        if (file.isDirectory()) {
           // listFile = file.listFiles();

            final Pattern regex = Pattern.compile(aktualnyAlbum+".*");
            File[] flists = file.listFiles(new FileFilter() {
                @Override
                public boolean accept(File file) {
                    return regex.matcher(file.getName()).matches();
                }
            });

            FilePathStrings = new String[flists.length];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[flists.length];
            for (int i = 0; i < flists.length; i++) {
                // Get the path of the image file
                FilePathStrings[i] = flists[i].getAbsolutePath();
                // Get the name image file
                FileNameStrings[i] = flists[i].getName();
            }

            grid = (GridView) view.findViewById(R.id.gridView);
            if(FilePathStrings != null) {
                adapter = new GridViewAdapter(context, FilePathStrings, FileNameStrings);
                grid.setAdapter(adapter);
                databaseAdapter.close();

/*
            // Create a String array for FilePathStrings
            FilePathStrings = new String[listFile.length];
            // Create a String array for FileNameStrings
            FileNameStrings = new String[listFile.length];
            for (int i = 0; i < listFile.length; i++) {
                // Get the path of the image file
                FilePathStrings[i] = listFile[i].getAbsolutePath();
                // Get the name image file
                FileNameStrings[i] = listFile[i].getName();
            }*/
        }

        }


        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                //Toast.makeText(context.getApplicationContext(), position + "", Toast.LENGTH_SHORT).show();
                Intent intentDisplayImage = new Intent(getActivity(), DisplayImage.class);
                intentDisplayImage.putExtra("sciezka", FilePathStrings);
                intentDisplayImage.putExtra("obecneZdjecie", position);
                intentDisplayImage.putExtra("nazwaPodrozy", nazwaPodrozy);
                startActivity(intentDisplayImage);
            }
        });
    }


    public static void setAktualnyAlbum(final String string) {
        aktualnyAlbum = string;
    }




}