package mw.albumpodrozniczy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by mstowska on 2/24/2016.
 */


/* Klasa pomocna przy tworzeniu i aktualizowaniu bazy danych
   dziedziczy po SQLiteOpenHelper- jest to mechanizm tworzacy i aktualizujący strukturę bazy danyc
   SQLiteOpenerHalper posiada dwie klasy onCreate, onUpgrade


*/
public class DatabaseHelper extends SQLiteOpenHelper {

    private Cursor cursor;
    private EditText text;

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //metoda wywoływana w momencie, gdy odwołuje się do bazy danych, której fizycznie nie ma
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseAdapter.DB_CREATE_TABLE_MAIN);
        db.execSQL(DatabaseAdapter.DB_CREATE_TABLE_TRASA);
        db.execSQL(DatabaseAdapter.DB_CREATE_TABLE_WSPOLRZEDNE);
        Log.d(DatabaseAdapter.DEBUG_TAG_DB, "Database creating...");
        Log.d(DatabaseAdapter.DEBUG_TAG_DB, "Table " + DatabaseAdapter.DB_TABLE_MAIN + " ver." + DatabaseAdapter.DB_VERSION + " created");
        Log.d(DatabaseAdapter.DEBUG_TAG_DB, "Table " + DatabaseAdapter.DB_TABLE_TRASA + " ver." + DatabaseAdapter.DB_VERSION + " created");
        Log.d(DatabaseAdapter.DEBUG_TAG_DB, "Table " + DatabaseAdapter.DB_TABLE_WSPOLRZEDNE + " ver." + DatabaseAdapter.DB_VERSION + " created");
    }

    //służy do aktualizacji bazy danych, jeśli okaże się ze na urządzeniu istnieje starsza wersja(na podstawie DB_VERSION)- aktualizuje jej struktury
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseAdapter.DROP_TABLE_MAIN);
        db.execSQL(DatabaseAdapter.DROP_TABLE_TRASA);
        db.execSQL(DatabaseAdapter.DROP_TABLE_WSPOLRZEDNE);
        Log.d(DatabaseAdapter.DEBUG_TAG_DB, "Database updating...");
        Log.d(DatabaseAdapter.DEBUG_TAG_DB, "Table " + DatabaseAdapter.DB_TABLE_MAIN + " updated from ver." + oldVersion + " to ver." + newVersion);
        Log.d(DatabaseAdapter.DEBUG_TAG_DB, "Table " + DatabaseAdapter.DB_TABLE_TRASA + " updated from ver." + oldVersion + " to ver." + newVersion);
        Log.d(DatabaseAdapter.DEBUG_TAG_DB, "Table " + DatabaseAdapter.DB_TABLE_WSPOLRZEDNE + " updated from ver." + oldVersion + " to ver." + newVersion);
        Log.d(DatabaseAdapter.DEBUG_TAG_DB, "All data is lost.");

        onCreate(db);
    }

    //wypisanie calej tabel
    public void selectTable(SQLiteDatabase db) {
        cursor = db.rawQuery(DatabaseAdapter.SELECT_TABLE_MAIN, null);
        if(cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                String string1 = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_ID));
                String string2 = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TITLE));
                String string3 = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_COUNTRY));
                String string4 = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_CITY));
                String string5 = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_START));
                String string6 = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_END));
                String string7 = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_COMMENT));
                Log.d(DatabaseAdapter.DEBUG_TAG_DB, string1+","+string2+","+string3+","+string4+","+string5+","+string6+","+string7); // Only assign string value if we moved to first record
                cursor.moveToNext();
            }
        }
        cursor = db.rawQuery(DatabaseAdapter.SELECT_TABLE_TRASA, null);
        if(cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                String string1 = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TRASA_ID));
                String string2 = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TRASA_ID_PODROZE));
                Log.d(DatabaseAdapter.DEBUG_TAG_DB, string1+","+string2); // Only assign string value if we moved to first record
                cursor.moveToNext();
            }
        }
        cursor = db.rawQuery(DatabaseAdapter.SELECT_TABLE_WSPOLRZEDNE, null);
        if(cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                String string1 = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_WSPOLRZEDNE_ID));
                String string2 = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_WSPOLRZEDNE_SZEROKOSC));
                String string3 = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_WSPOLRZEDNE_WYSOKOSC));
                String string4 = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_WSPOLRZEDNE_ID_TRASA));
                Log.d(DatabaseAdapter.DEBUG_TAG_DB, string1+","+string2+","+string3+","+string4); // Only assign string value if we moved to first record
                cursor.moveToNext();
            }
        }

    }

    public String[] dostanieWszystkichKolumnTabeliPodroze(SQLiteDatabase db) {
        String[] columns = {DatabaseAdapter.KEY_ID, DatabaseAdapter.KEY_TITLE, DatabaseAdapter.KEY_COUNTRY,DatabaseAdapter.KEY_CITY,DatabaseAdapter.KEY_DATE_START,DatabaseAdapter.KEY_COMMENT};
        cursor = db.query(DatabaseAdapter.DB_TABLE_MAIN, columns, null, null, null, null, null);
        int iloscKrotekPodroze = cursor.getCount();
        String[] krotki = new String[iloscKrotekPodroze];
        if(cursor.moveToFirst()) {
            int i = 0;
            while (cursor.isAfterLast() == false) {
                krotki[i] = cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_ID)) +"    "+ cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_TITLE)) +"  "+ cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_COUNTRY)) +"  "+ cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_CITY)) +"  "+ cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_DATE_START)) +"  "+ cursor.getString(cursor.getColumnIndex(DatabaseAdapter.KEY_COMMENT));
                cursor.moveToNext();
                i++;
            }
        }
        return krotki;
    }


    public String dostanieWartosciZTabeli(SQLiteDatabase db, String nazwa_tabeli, String nazwa_kolumny, Integer pozycja) {
        String wartosc = null;
        String[] columns = {nazwa_kolumny};
        pozycja++;
        String where = DatabaseAdapter.KEY_ID + "=" + pozycja;
        cursor = db.query(nazwa_tabeli, columns,where, null, null, null, null, null);
        if(cursor.moveToFirst()) {
            wartosc = cursor.getString(cursor.getColumnIndex(nazwa_kolumny));
            Log.d(DatabaseAdapter.DEBUG_TAG_DB, ""+wartosc);
        }
        return wartosc;
    }

    //funkcja wywołująca zapytanie SQL do usuniecia tabeli
    public void removeTable(SQLiteDatabase db) {
        db.execSQL(DatabaseAdapter.DROP_TABLE_MAIN);
        db.execSQL(DatabaseAdapter.DROP_TABLE_TRASA);
        db.execSQL(DatabaseAdapter.DROP_TABLE_WSPOLRZEDNE);
        Log.d(DatabaseAdapter.DEBUG_TAG_DB, "Tabele usunieta");
    }

    //usuniecie bazy danych
    public void removeDB(Context context, String string) {
        context.deleteDatabase(string);
        Log.d(DatabaseAdapter.DEBUG_TAG_DB, "DB usunieta");
    }


}
