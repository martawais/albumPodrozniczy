package mw.albumpodrozniczy;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by mstowska on 2/24/2016.
 */


//.\sqlite3.exe /data/data/mw.albumpodrozniczy/databases/album_podrozniczy.db
//run-as mw.albumpodrozniczy ls -l /data/data/mw.albumpodrozniczy/databases/album_podrozniczy.db

/* SQLite- wysoka wydajność dostępy do dużej ilości uporządkowanych danych.




*/
public class DatabaseAdapter {


    public static final String DEBUG_TAG_DB = "SqLite";     //pole wykorzystywane do wyświetlania komunikatów w LogCacie (Log.d(...))


    public static final int DB_VERSION = 2;     ////wersja bazy danych, jeśli się zmieni to będzie oznaczało, że nasz baza danych wymaga aktualizacji
    public static final String DB_NAME = "album_podrozniczy.db";        //plik w którym przechowuję bazę danych
    public static final String DB_TABLE_MAIN = "podroze";       //nazwa ierwszej tabelii
    public static final String DB_TABLE_TRASA = "trasy";
    public static final String DB_TABLE_WSPOLRZEDNE = "wspolrzedne";
    public static final String DB_TABLE_ALBUM = "albumy";
    public static final String DB_TABLE_ZDJECIA = "zdjecia";


    //  nazwa kolumny/typ danych/nr kolumny
    public static final String KEY_ID = "_id";
    public static final String ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_COLUMN = 0;

    public static final String KEY_TITLE = "title";
    public static final String TITLE_OPTIONS = "TEXT NULL";
    public static final int TITLE_COLUMN = 1;

    public static final String KEY_COUNTRY = "country";
    public static final String COUNTRY_OPTIONS = "TEXT NULL";
    public static final int COUNTRY_COLUMN = 2;

    public static final String KEY_CITY = "city";
    public static final String CITY_OPTIONS = "TEXT NULL";
    public static final int CITY_COLUMN = 3;

    public static final String KEY_DATE_START = "date_start";
    public static final String DATE_START_OPTIONS = "TEXT NULL";
    public static final int DATE_START_COLUMN = 4;

    public static final String KEY_DATE_END = "date_end";
    public static final String DATE_END_OPTIONS = "TEXT NULL";
    public static final int DATE_END_COLUMN = 5;

    //dane do drugiej tabeli TRASA:
    public static final String KEY_TRASA_ID = "_id";
    public static final String ID_TRASA_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int ID_TRASA_COLUMN = 0;

    public static final String KEY_TRASA_ID_PODROZE = "idpodroze";
    public static final String TRASA_ID_PODROZE_OPTIONS = "INTEGER NULL";
    public static final int TRASA_ID_PODROZE_COLUMN = 1;

    //dane do trzeciej tabeli WSPOLRZEDNE:
    public static final String KEY_WSPOLRZEDNE_ID = "_id";
    public static final String WSPOLRZEDNE_ID_OPTIONS = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final int WSPOLRZEDNE_ID_COLUMN = 0;

    public static final String KEY_WSPOLRZEDNE_SZEROKOSC = "szerokosc";
    public static final String WSPOLRZEDNE_SZEROKOSC_OPTIONS = "TEXT NULL";
    public static final int WSPOLRZEDNE_SZEROKOSC_COLUMN = 1;

    public static final String KEY_WSPOLRZEDNE_WYSOKOSC = "wysokosc";
    public static final String WSPOLRZEDNE_WYSOKOSC_OPTIONS = "TEXT NULL";
    public static final int WSPOLRZEDNE_WYSOKOSC_COLUMN = 1;

    public static final String KEY_WSPOLRZEDNE_ID_TRASA = "idtrasa";
    public static final String WSPOLRZEDNE_ID_TRASA_OPTIONS = "INTEGER NULL";
    public static final int WSPOLRZEDNE_ID_TRASA_COLUMN = 1;





    //stała do tworzenia pierwszej tabeli
    public static final String DB_CREATE_TABLE_MAIN =
            "CREATE TABLE " + DB_TABLE_MAIN + "( " +
                    KEY_ID + " " + ID_OPTIONS + ", " +
                    KEY_TITLE + " " + TITLE_OPTIONS + ", " +
                    KEY_COUNTRY + " " + COUNTRY_OPTIONS + ", " +
                    KEY_CITY + " " + CITY_OPTIONS + ", " +
                    KEY_DATE_START + " " + DATE_START_OPTIONS + ", " +
                    KEY_DATE_END + " " + DATE_END_OPTIONS +
                    ");";
    //stało do usuwania pierwszej tabeli
    public static final String DROP_TABLE_MAIN =
            "DROP TABLE IF EXISTS " + DB_TABLE_MAIN;

    public static final String SELECT_TABLE_MAIN =
            "SELECT * FROM " + DB_TABLE_MAIN;





    //stała do tworzenia drugiej tabeli TRASA:
    public static final String DB_CREATE_TABLE_TRASA =
            "CREATE TABLE " + DB_TABLE_TRASA + "( " +
                    KEY_TRASA_ID + " " + ID_TRASA_OPTIONS + ", " +
                    KEY_TRASA_ID_PODROZE + " " + TRASA_ID_PODROZE_OPTIONS +
                    ");";

    //stało do usuwania drugiej tabeli
    public static final String DROP_TABLE_TRASA =
            "DROP TABLE IF EXISTS " + DB_TABLE_TRASA;

    public static final String SELECT_TABLE_TRASA =
            "SELECT * FROM " + DB_TABLE_TRASA;




    //stała do tworzenia trzeciej tabeli WSPOLRZEDNE:
    public static final String DB_CREATE_TABLE_WSPOLRZEDNE =
            "CREATE TABLE " + DB_TABLE_WSPOLRZEDNE + "( " +
                    KEY_WSPOLRZEDNE_ID + " " + WSPOLRZEDNE_ID_OPTIONS + ", " +
                    KEY_WSPOLRZEDNE_SZEROKOSC + " " + WSPOLRZEDNE_SZEROKOSC_OPTIONS + ", " +
                    KEY_WSPOLRZEDNE_WYSOKOSC + " " + WSPOLRZEDNE_WYSOKOSC_OPTIONS + ", " +
                    KEY_WSPOLRZEDNE_ID_TRASA + " " + WSPOLRZEDNE_ID_TRASA_OPTIONS +
                    ");";

    //stało do usuwania trzeciej tabeli
    public static final String DROP_TABLE_WSPOLRZEDNE =
            "DROP TABLE IF EXISTS " + DB_TABLE_WSPOLRZEDNE;

    public static final String SELECT_TABLE_WSPOLRZEDNE =
            "SELECT * FROM " + DB_TABLE_WSPOLRZEDNE;


    //pola niezbędne do funkcjonowania klasy DatabaseHelper
    private SQLiteDatabase database;
    private Context context;
    private DatabaseHelper dbHelper;


    //konstruktor
    public DatabaseAdapter(Context context) {
        this.context = context;
    }

    //metoda otworzy połączenie z bazą danych- tutaj przekazuję wersje bazy danych- system podejmuje decyzje o aktualizacji
    public DatabaseAdapter open(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        database = dbHelper.getWritableDatabase() ;
        //blok zabezpiecza mnie, gdyby sie okazało ze nie mam pełnego dostępu do bazy danych(odczyt/zapis), w takim przypadku mogę przynajmniej czytać dane
        try {
            database = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            database = dbHelper.getReadableDatabase();
        }
        return this;
    }

    //metoda zamykająca połączenie z bazą danych
    public void close() {
        dbHelper.close();
    }

    //dodanie nowej krotki do tabeli podroze
    public long wstawKrotkeDoTabeliPodroze(String klucz, String opis) {
        ContentValues nowaKrotka = new ContentValues();    //stworzenie obiektu do przekazania danych do zapytania
        nowaKrotka.putNull(KEY_TITLE);
        nowaKrotka.put(klucz, opis);       //metoda put- umieszcza się pare klucz-opis, gdzie klucz to nazwa kolumny
        nowaKrotka.putNull(KEY_CITY);
        nowaKrotka.putNull(KEY_DATE_START);
        nowaKrotka.putNull(KEY_DATE_END);
        return database.insert(DB_TABLE_MAIN, null, nowaKrotka);
    }


    //dodanie nowej krotki do tabeli trasa
    public long wstawKrotkeDoTabeliTrasa(Integer IDpodroz) {
        ContentValues nowaKrotka = new ContentValues();    //stworzenie obiektu do przekazania danych do zapytania
        nowaKrotka.put(KEY_TRASA_ID_PODROZE, IDpodroz);
        return database.insert(DB_TABLE_TRASA, null, nowaKrotka);
    }


    //dodanie nowej krotki do tabeli wspolrzedne
    public long wstawKrotkeDoTabeliWspolrzedne(String szerokosc, String wysokosc, Integer IDtrasy) {
        ContentValues nowaKrotka = new ContentValues();    //stworzenie obiektu do przekazania danych do zapytania
        nowaKrotka.put(KEY_WSPOLRZEDNE_SZEROKOSC, szerokosc);
        nowaKrotka.put(KEY_WSPOLRZEDNE_WYSOKOSC, wysokosc);
        nowaKrotka.put(KEY_WSPOLRZEDNE_ID_TRASA, IDtrasy);
        return database.insert(DB_TABLE_WSPOLRZEDNE, null, nowaKrotka);
    }


    public String[] wypisanieWszystkichKolumnTabeliPodroze() {
        String[] krotki = dbHelper.dostanieWszystkichKolumnTabeliPodroze(database);
        return krotki;
    }




    public boolean aktualizacjaKrotkiTabeliPodroze(long id, String klucz, String opis) {
        String where = KEY_ID + "=" + id;
        ContentValues nowaWartosc = new ContentValues();
        nowaWartosc.put(klucz, opis);
        return database.update(DB_TABLE_MAIN, nowaWartosc, where, null) > 0;
    }

    public void wypiszTabele() {
        dbHelper.selectTable(database);

    }

    public boolean usuwanieKrotkiZTabeliPodroze(long id){
        String where = KEY_ID + "=" + id;
        return database.delete(DB_TABLE_MAIN, where, null) > 0;
    }

    public void usuwanieTabeli() {
        dbHelper.removeTable(database);

    }

    public void usuwanieBazyDanych() {
        dbHelper.removeDB(context, DB_NAME);

    }

}
