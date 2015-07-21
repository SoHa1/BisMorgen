package com.teamvollrausch.sh.bismorgen;

/**
 * Das Database_Schmema-Interface beinhaltet Konstanten für die einzelnen
 * SQL Tabellen.
 * @author Hanan Fakhro
 * @author Sophia Eichhorn
 * @version 1.0
 */

public interface Database_Schema {

    //Elemente der Tabelle Cocktail
    public static final String TABLE_COCKTAIL = "Cocktail";
    public static final String COCKTAIL_NAME  = "Name";
    public static final String COCKTAIL_NR  = "C_id";
    public static final String COCKTAIL_IMAGE  = "C_image";

    //Elemente der Tabelle Zutat
    public static final String TABLE_ZUTAT = "Zutat";
    public static final String ZUTAT_NR = "Z_id";
    public static final String ZUTAT_NAME = "Name";

    //Elemente der Tabelle Zutat_in_Cocktail
    public static final String TABLE_ZUTAT_IN_COKTAIL = "CocktailRezept";
    public static final String REZEPT_MENGE_IN_COKTAIL = "Menge";

    //Elemente der Tabelle Getraenke
    public static final String TABLE_GETRAENKE = "Getränk";

    //Elemente der Tabelle alkoholische Getraenke
    public static final String TABLE_ALKOHOL = "Spirituose";
    public static final String ALKOHOL_GEHALT = "AlkoholGehalt";

    //Elemente der Tabelle nicht alkoholische Getraenke
    public static final String TABLE_KEIN_ALKOHOL = "Nicht_alkoholisches_Getränk";

    //Elemente der Tabelle Sirup
    public static final String TABLE_SIRUP = "Sirup";

    //Elemente der Tabelle Saft
    public static final String TABLE_SAFT = "Saft";

    //Elemente der Tabelle keine Getraenke
    public static final String TABLE_KEIN_GETRAENK = "Kein_Getraenk";

    //Elemente der Tabelle Bilder
    public static final String TABLE_BILD= "Bild";

    /* Erstellung der einzelnen Tabellen in Form von Strings, die in der onCreate Methode
     * als SQL Statement verarbeitet werden
     */

    // Tabelle 1 = Cocktail
    public static final String CREATE_COCKTAIL = "CREATE TABLE "
            + TABLE_COCKTAIL + "(" + COCKTAIL_NR
            + " INTEGER PRIMARY KEY , " + COCKTAIL_NAME
            + " TEXT NOT NULL)";


    // Tabelle 2 = Zutat
    public static final String CREATE_ZUTAT = "CREATE TABLE "
            + TABLE_ZUTAT + "(" + ZUTAT_NR
            + " INTEGER PRIMARY KEY , " + ZUTAT_NAME
            + " TEXT NOT NULL)";

    // Tabelle 3 = CocktailRezept
    public static final String CREATE_COCKTAIL_REZEPT = "CREATE TABLE "
            + TABLE_ZUTAT_IN_COKTAIL + "(" + COCKTAIL_NR
            + " INTEGER NOT NULL, " + ZUTAT_NR
            + " INTEGER NOT NULL,"+ REZEPT_MENGE_IN_COKTAIL + " TEXT, "
            + "FOREIGN KEY("+ ZUTAT_NR +") REFERENCES " + TABLE_ZUTAT + "("+ ZUTAT_NR + ") );";

    // Tabelle 4 = Getränk
    public static final String CREATE_GETRAENKE = "CREATE TABLE "
            + TABLE_GETRAENKE + "(" + ZUTAT_NR
            + " INTEGER PRIMARY KEY, FOREIGN KEY("+ ZUTAT_NR +") REFERENCES " + TABLE_ZUTAT + "("+ ZUTAT_NR + ") );";

    // Tabelle 5 = Spirituose
    public static final String CREATE_SPIRITUOSE = "CREATE TABLE "
            + TABLE_ALKOHOL + "(" + ZUTAT_NR
            + " INTEGER PRIMARY KEY,"
            +  ALKOHOL_GEHALT + "TEXT,"
            + " FOREIGN KEY("+ ZUTAT_NR +") REFERENCES " + TABLE_GETRAENKE+ "("+ ZUTAT_NR + "));";

    // Tabelle 6 = Nicht alkoholische Getränke
    public static final String CREATE_KEIN_ALKOHOL = "CREATE TABLE "
            + TABLE_KEIN_ALKOHOL + "(" + ZUTAT_NR
            + " INTEGER PRIMARY KEY, FOREIGN KEY("+ ZUTAT_NR +") REFERENCES " + TABLE_GETRAENKE+ "("+ ZUTAT_NR + "));";

    // Tabelle 7 = Sirup
    public static final String CREATE_SIRUP = "CREATE TABLE "
            + TABLE_SIRUP + "(" + ZUTAT_NR
            + " INTEGER PRIMARY KEY, FOREIGN KEY("+ ZUTAT_NR +") REFERENCES " + TABLE_KEIN_ALKOHOL+ "("+ ZUTAT_NR + "));";

    // Tabelle 8 = Saft
    public static final String CREATE_SAFT = "CREATE TABLE "
            + TABLE_SAFT + "(" + ZUTAT_NR
            + " INTEGER PRIMARY KEY, FOREIGN KEY("+ ZUTAT_NR +") REFERENCES " + TABLE_KEIN_ALKOHOL+ "("+ ZUTAT_NR + "));";

    // Tabelle 9 = Keine Getränke
    public static final String CREATE_KEIN_GETRAENK = "CREATE TABLE "
            + TABLE_KEIN_GETRAENK + "("+ ZUTAT_NR
            + " INTEGER PRIMARY KEY, FOREIGN KEY("+ ZUTAT_NR +") REFERENCES " + TABLE_ZUTAT+ "("+ ZUTAT_NR + "));";

    // Tabelle 10 = BILD
    public static final String CREATE_BILD = "CREATE TABLE "
            + TABLE_BILD + "(" + COCKTAIL_IMAGE + " BLOB, " + COCKTAIL_NR + " INTEGER"
            + " , FOREIGN KEY("+ COCKTAIL_NR +") REFERENCES " + TABLE_COCKTAIL + "("+ COCKTAIL_NR + "),  PRIMARY KEY( " +
            COCKTAIL_NR +"," + COCKTAIL_IMAGE + "));";

}