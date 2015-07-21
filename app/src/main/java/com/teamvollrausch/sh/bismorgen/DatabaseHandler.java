package com.teamvollrausch.sh.bismorgen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;



/**
 * Der DatabaseHandler dient zum Erstellen und Verwalten der Datenbank.
 * Database_Schema: Schnittstelle, die einzelne String Konstanten beinhaltet, mit den
 * CREATE TABLE Befehlen, die im DatabaseHandler in SQL umgesetzt werden.
 * @author Hanan Fakhro
 * @author Sophia Eichhorn
 * @version 1.0
 */

public class DatabaseHandler extends SQLiteOpenHelper implements Database_Schema {


    //Versions Nummer der DB
    private static final int DATABASE_VERSION = 1;
    //Bennenung der Datenbank
    private static final String DATABASE_NAME = "Cocktail_Bar";


    /* Konstruktor für die Datenbank
     * @param context zum Oeffnen erstellen der DB in einem geeigneten Kontext
     */
    public DatabaseHandler(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    // Oeffnen der Datenbank
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    /* Diese Methode dient zum Löschen bereits erstellter Cocktails
     * @param id Primary-Key des zu loeschenden Cocktails
     */
    public void delete(String id) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("Delete FROM  " + TABLE_COCKTAIL + " where C_id = '" + id + "';");
        db.execSQL("Delete FROM  " + TABLE_BILD + " where C_id = '" + id + "';");
        db.execSQL("Delete FROM  " + TABLE_ZUTAT_IN_COKTAIL + " where C_id = '" + id + "';");

    }

    /* Diese Methode dient zum Löschen von Bilddateien aus der Db
     * @param id Primary-Key eines Cocktails, um die dazugehoerige Bilddatei zu loeschen
     */
    public void deleteImage(String id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Delete FROM  " + TABLE_BILD + " where C_id = '" + id + "';");

    }

    // Erstellung der Tabellen durch Aufruf von execSQL
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_COCKTAIL);
        db.execSQL(CREATE_BILD);
        db.execSQL(CREATE_ZUTAT);
        db.execSQL(CREATE_COCKTAIL_REZEPT);
        db.execSQL(CREATE_GETRAENKE);
        db.execSQL(CREATE_SPIRITUOSE);
        db.execSQL(CREATE_KEIN_ALKOHOL);
        db.execSQL(CREATE_SIRUP);
        db.execSQL(CREATE_SAFT);
        db.execSQL(CREATE_KEIN_GETRAENK);
    }


    /* Einfuegen eines Cocktails in db
     * @param cocktail Ist ein Objekt der Klasse Cocktail
     * @return Wahrheitswert zum Ueberpruefen, ob der Cocktail erfolgreich gespeichert wurde
     */
    public boolean insertCocktail(Cocktail cocktail) {

        try {

            // Zugriff zum Schreiben auf Db setzen
            SQLiteDatabase db = getWritableDatabase();
            // dient zum Speichern einer Menge von Werten
            ContentValues cv = new ContentValues();
            // fuegt in die Menge die Cocktail-Nr ein
            cv.put(COCKTAIL_NR, cocktail.getNr());
            // fuegt den Cocktail Namen ein
            cv.put(COCKTAIL_NAME, cocktail.getName());
            // insert Methode zum Einfuegen der in cv gespeicherten Werte in die Tabelle
            // Cocktail
            db.insert(TABLE_COCKTAIL, null, cv);
            // Db schließen
            db.close();

            // Bei erfolgeicher Speicherung soll true zurückgegeben werden
            return true;

        } catch (Exception exp) {

            exp.printStackTrace();
            // Falls ein Fehler aufgetreten ist, wird false zurueckgegeben
            return false;
        }
    }



    /* Einfuegen einer Zutat in die Db
     * @param zutat ist ein Objekt der Klasse Zutat
     * @return Wahrheitswert zum Ueberpruefen, ob eine Zutat erfolgreich gespeichert wurde
     */
    public boolean insertZutat(Zutat zutat) {

        try {

            SQLiteDatabase d_b = getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put(ZUTAT_NR, zutat.getNr());

            cv.put(ZUTAT_NAME, zutat.getZutatName());

            d_b.insert(TABLE_ZUTAT, null, cv);

            d_b.close();

            return true;

        } catch (Exception exp) {

            exp.printStackTrace();

            return false;
        }
    }



    /* Einfuegen in die Subklassen von Zutat
     * @param zutat Ist ein Objekt der Klasse Zutat
     * @param tablename ist der Tabellen name
     * @return Wahrheitswert zum Ueberpruefen, ob der Speichervorgang erfolgreich abgeschlossen wurde
     */
    public boolean insertZutatKinder(Zutat zutat, String tablename) {

        try {

            SQLiteDatabase d_b = getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put(ZUTAT_NR, zutat.getNr());

            d_b.insert(tablename, null, cv);

            d_b.close();

            return true;

        } catch (Exception exp) {

            exp.printStackTrace();

            return false;
        }
    }

    /* Fuegt Rezepte in die Db ein
     * @param cocktail Cocktail
     * @param zutat Zutat
     * @param menge in cl
     * @return Wahrheitswert zum Ueberpruefen, ob der Speichervorgang erfolgreich abgeschlossen wurde
     */
    public boolean insertRezept(Cocktail cocktail, Zutat zutat, String menge) {

        if (cocktail==null || zutat==null || menge==null) {
            throw new NullPointerException("Ein Null-Objekt kann nicht eingefuegt werden.");
        }

        try {

            SQLiteDatabase d_b = getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put(COCKTAIL_NR, cocktail.getNr());

            cv.put(ZUTAT_NR, zutat.getNr());

            cv.put(REZEPT_MENGE_IN_COKTAIL, menge);

            d_b.insert(TABLE_ZUTAT_IN_COKTAIL, null, cv);

            d_b.close();

            return true;

        } catch (Exception exp) {

            exp.printStackTrace();

            return false;
        }
    }


    /* Fuegt eine Bilddatei in die Tabelle Bild mit zugehoeriger Cocktailid ein
     * @param cocktail die Cocktailid
     * @return cocktail Cocktailobjekt
     */
     public Cocktail insertImage(Cocktail cocktail) {


        if (cocktail == null) {

            throw new NullPointerException("Ein Null-Objekt kann nicht eingefuegt werden.");
        }

        try {

            SQLiteDatabase d_b = getWritableDatabase();

            ContentValues cv = new ContentValues();

            cv.put(COCKTAIL_NR, cocktail.getNr());

            cv.put(COCKTAIL_IMAGE, cocktail.getImage());

            d_b.insert(TABLE_BILD, null, cv);

            d_b.close();

            return cocktail;

        } catch (Exception exp) {

            exp.printStackTrace();

        }

        return cocktail;
    }

    /*
     * Query die eine Bilddatei entsprechend der id zurueck gibt
     * @param id Primary Key des Cocktails
     * @return cursor.getBlob(0) Bilddatei
     */
    public byte[] getCocktailImage(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BILD, new String[] {COCKTAIL_IMAGE},
                COCKTAIL_NR + "=?", new String[] {String.valueOf(id)},null,null,null,null);

        if (cursor != null)
            cursor.moveToFirst();


        return cursor.getBlob(0);

     }


    /*
     * Laenge des cursors der Tabelle Bild zum Abfragen, ob Cocktail einer Bilddatei zugeordnet wurde
     * @param c Cocktailid
     * @return cursor.getCount() Laenge der Einträge eines Cocktails der Tabelle Bild
     * @return 0 falls ein Cocktail kein Bild hat
     */
    public int cursorLengthofCocktail(Cocktail c) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select C_id From "+ TABLE_BILD + " Where C_id =" + c.getNr() , null);

        if (cursor!=null) {

            return cursor.getCount();
        }

        else return 0;

    }


    /* Query die den neu erstellten Cocktails eine neue eindeutige id berechnet
     * @param id Primary Key des Cocktails
     * @return neuer Primary Key
     */
    public String createCocktailkey() {

        String key="";

        SQLiteDatabase db = this.getReadableDatabase();

        List<String> key_list = new ArrayList<String>();
        // Zugriff zum lesen der db

        // einen Cursor setzen der als zeiger die Ergebnismenge Tupelweise durchlaeuft
        Cursor cursor = db.rawQuery("SELECT C_id FROM Cocktail ",null);

        if (cursor == null) {
            return null;
        }

        // Setze den Cursor zum Anfang der Ergebnismenge
        if (cursor.moveToFirst()) {

            do {
                key_list.add(cursor.getString(0));
            // Zum naechsten Tupel bewegen, bricht ab, wenn keins mehr kommt
            } while (cursor.moveToNext());
        }


        int primary = key_list.size()+1;
        key = ""+primary;

        return key;

    }


    /*
     * Query die eine zutat entsprechend der id zurueckgibt (wird zur Zeit nicht benötigt)
     * @param id Primary Key der Zutat
     * @return zutat Zutat entsprechend der gesetzten id
     */
    public Zutat getZutat(String nr) {

        SQLiteDatabase db = this.getReadableDatabase();

        // Anfrage, die eine Zutat mit der uebergebenen id zurueckliefert
        Cursor cursor = db.query(TABLE_ZUTAT, new String[] {ZUTAT_NR,ZUTAT_NAME},
                ZUTAT_NR + "=?", new String[] {nr},null,null,null,null);

        if (cursor != null)
            cursor.moveToFirst();

        Zutat zutat = new Zutat();
        zutat.setNr(cursor.getString(0));
        zutat.setZutatName(cursor.getString(1));

        return zutat;
    }


    /* Query die alle Cocktails aus der db in eine Liste einfuegt
     * und zurueckgibt
     * @return cocktailList Liste die alle in der db vorhandenen Cocktails beinhaltet
     */
    public List<Cocktail> getAllCocktails() {

        List<Cocktail> cocktailList = new ArrayList<Cocktail>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COCKTAIL,null);

        if (cursor.moveToFirst()) {

            do {
                Cocktail cocktail = new Cocktail();
                cocktail.setNr(cursor.getString(0));
                cocktail.setName(cursor.getString(1));

                cocktailList.add(cocktail);
            } while (cursor.moveToNext());
        }

        return cocktailList;

    }


    /* Query die alle Zutaten aus der db in eine Liste einfuegt
     * und zurueckgibt.
     * @return zutatList Liste mit allen Zutaten
     */
    public List<Zutat> getAllZutaten() {

        List<Zutat> zutatList = new ArrayList<Zutat>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ZUTAT,null);

        if (cursor.moveToFirst()) {

            do {
                Zutat zutat = new Zutat();
                zutat.setNr(cursor.getString(0));
                zutat.setZutatName(cursor.getString(1));

                zutatList.add(zutat);
            } while (cursor.moveToNext());
        }

        return zutatList;

    }


    /* Query die alle Zutaten eines Cocktails zurueckgibt
     * @param cocktail Cocktail
     * @return Liste mit allen Zutaten eunes Cocktails
     */
    public ArrayList<String> getAllRezept(Cocktail cocktail) {

        ArrayList<String> zutatList = new ArrayList<String>();
        SQLiteDatabase db = this.getWritableDatabase();


        Cursor cursor = db.rawQuery("select Zutat.Name , CocktailRezept.Menge From Zutat inner join CocktailRezept on CocktailRezept.Z_id = Zutat.Z_id Where CocktailRezept.C_id = ?",new String[] {cocktail.getNr()});


        if (cursor.moveToFirst()) {

            do {
                String zutat = cursor.getString(0) ;
                String menge = "" +  cursor.getString(1) +"" ;

                zutatList.add(zutat);
                zutatList.add(menge);

            } while (cursor.moveToNext());
        }

        return zutatList;

    }


    /* Query die die id's aller Zutaten eines Rezepts zurueckgibt, und
     * das Format des Strings zum spaeteren versenden vorbereitet
     * @param zutat_name Liste mit Zutaten
     * @return abschluss String der alle ids eines Rezepts (Zutaten) in einem bestimmten Format beinhaltet
     */
    public String getIds(ArrayList<String> zutat_name) {

        String abschluss = "c;";
        String zutat ="";
        SQLiteDatabase db = this.getWritableDatabase();

        for (int i=0;i<zutat_name.size()/2;i++) {

            Cursor cursor = db.rawQuery("select Zutat.Z_id From Zutat  Where Zutat.Name = ? EXCEPT Select Sirup.Z_id From Sirup" , new String[]{zutat_name.get(i*2)});

            if (cursor.moveToFirst()) {

                do {
                    zutat += cursor.getString(0) + ":";
                    zutat += zutat_name.get(2*i+1) + ";";


                } while (cursor.moveToNext());
            }

        }

        String bytes = Integer.toString(zutat.length());

        abschluss +=bytes+ ";";
        abschluss +=zutat;
        return abschluss;

    }


    @Override
    /* Löscht alle Daten der Datenbank, nach einem Update der Versionsnummer
     * @param db Datenbankilfsklasse
     * @param oldversion alte Versionsnummer der db
     * @param newVersion neue Versionsnummer der db
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DatabaseHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_COCKTAIL);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_ZUTAT);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_COCKTAIL_REZEPT);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_GETRAENKE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_SPIRITUOSE);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_KEIN_ALKOHOL);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_SIRUP);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_SAFT);
        db.execSQL("DROP TABLE IF EXISTS " + CREATE_KEIN_GETRAENK);
        onCreate(db);
    }


}