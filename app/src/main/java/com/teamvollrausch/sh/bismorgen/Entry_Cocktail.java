package com.teamvollrausch.sh.bismorgen;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/** In dieser Activity soll es moeglich sein, einen Cocktail zu erstellen. Dabei
 * wird ein Textfeld ( EditText ) fuer die Eingabe des Namens benoetigt. Die restlichen
 * Auswahlboxen dienen zur Eingabe der Zutaten mit dem entsprechenden Mengenverhaeltnissen.
 * AdapterView.OnItemSelectedListener wird benoetigt, um auf eine Auswahl zu reagieren.
 * @author Hanan Fakhro
 * @author Sophia Eichhorn
 * @version 1.0
 */

public class Entry_Cocktail extends Activity implements AdapterView.OnItemSelectedListener {

    // Spinner zum Auswaehlen von Optionen
    private Spinner s1,s2,s3,s4,s5,s6,s7,s8,m1,m2,m3,m4,m5,m6,m7,m8;
    // Eine TextView, um die OnClickMethode zu testen
    private TextView text;
    // Textfeld zur Eingabe des CocktailNamens
    private EditText edit;
    //Button mit Bild, der zum abspeichern des Cocktails dienen soll
    private ImageButton speichern;
    //Db-Helper zum Verwenden von Sql-Statements
    private DatabaseHandler db_handler;
    // Liste mit allen Zutaten der db, um diese in jedem Spinner anzuzeigen
    private List<Zutat> zutatenListe;
    // Cocktail, der abgespeichert werden soll
    private Cocktail cocktail = new Cocktail();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry__cocktail);

        // Setzt Actiobarhintergrund
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(0, 134, 139)));

        // Verwendung des db_handlers, um alle Zutaten aus der db in eine Liste zu schreiben
        db_handler = new DatabaseHandler(this);
        Zutat zutat = new Zutat();
        zutatenListe = zutat.ListOfAllZutaten();

        // Eine leere Zutat, damit im Spinner als erstes Itemelent ein leeres Feld aufgefuehrt werden
        // soll
        Zutat leer = new Zutat();
        leer.setNr("");
        leer.setZutatName("");
        zutatenListe.add(0, leer);

        // Initialisierung und Fuellung aller Spinners
        all_Spinners();

        // Initislisierung des Buttons
        speichern = (ImageButton) findViewById(R.id.speichern_button);

        // Aktion des SpeicherButtons festlegen
        speichern.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Sammelt alle Einträege aus dem Textfeld und den Spinnern und speichert den
                // neuen Cocktail+Rezept in die db
                get_Rezept_Spinner();
            }
        });

    }

    //Mit dieser Methode werden die Eintreage aus den einzelnen Spinner gelesen und in der Db gespeichert
    public void get_Rezept_Spinner() {

        if (edit.getText().toString() != null) {

            if(edit.getText().toString() !=null) {
                cocktail.setName(edit.getText().toString());
                cocktail.setNr(db_handler.createCocktailkey());
                db_handler.insertCocktail(cocktail);

            } else {

                Toast.makeText(Entry_Cocktail.this, "Trage einen Namen ein", Toast.LENGTH_SHORT).show();
                return;

            }

            // Falls eine Zutat mit cl-Angabe ausgewaehlt wurde, dann wird die Zustat erst gespeichert
            if (!(s1.getSelectedItem().toString().equals("")) && !(m6.getSelectedItem().toString().equals("")))
                OnClickSpeicher(edit.getText().toString(), s1.getSelectedItemPosition(), m6.getSelectedItem().toString());

            if (!(s2.getSelectedItem().toString().equals("")) && !(m1.getSelectedItem().toString().equals("")))
                OnClickSpeicher(edit.getText().toString(), s2.getSelectedItemPosition(), m1.getSelectedItem().toString());

            if (!(s3.getSelectedItem().toString().equals("")) && !(m2.getSelectedItem().toString().equals("")))
                OnClickSpeicher(edit.getText().toString(), s3.getSelectedItemPosition(), m2.getSelectedItem().toString());

            if (!(s4.getSelectedItem().toString().equals("")) && !(m3.getSelectedItem().toString().equals("")))
                OnClickSpeicher(edit.getText().toString(), s4.getSelectedItemPosition(), m3.getSelectedItem().toString());

            if (!(s5.getSelectedItem().toString().equals("")) && !(m4.getSelectedItem().toString().equals("")))
                OnClickSpeicher(edit.getText().toString(), s5.getSelectedItemPosition(), m4.getSelectedItem().toString());

            if (!(s6.getSelectedItem().toString().equals("")) && !(m5.getSelectedItem().toString().equals("")))
                OnClickSpeicher(edit.getText().toString(), s6.getSelectedItemPosition(), m5.getSelectedItem().toString());

            if (!(s7.getSelectedItem().toString().equals("")) && !(m7.getSelectedItem().toString().equals("")))
                OnClickSpeicher(edit.getText().toString(), s7.getSelectedItemPosition(), m7.getSelectedItem().toString());

            if (!(s8.getSelectedItem().toString().equals("")) && !(m8.getSelectedItem().toString().equals("")))
                OnClickSpeicher(edit.getText().toString(), s8.getSelectedItemPosition(), m8.getSelectedItem().toString());

            Toast.makeText(Entry_Cocktail.this, "gespeichert", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Entry_Cocktail.this, MainActivity.class);
            startActivity(intent);

        } else {

            Toast.makeText(Entry_Cocktail.this, "Du hast vergessen einen Namen einzutragen", Toast.LENGTH_SHORT).show();

        }
    }


    /* Speichert das Rezept in die Db
     * @param zutat id fuer die richtige Zutat
     * @param cl Mengenangabe fuer das Rezept
     */
    public void OnClickSpeicher(String name, int zutat, String cl) {

        Zutat z = zutatenListe.get(zutat);
        db_handler.insertRezept(cocktail,z,cl);

    }

    // Initialisieung aller Spinner und Fuellung mit den vorhandenen Zutaten
    public void all_Spinners() {

        // Spinner fuer Zutatenliste werden gesetzt
        s1 =(Spinner) findViewById(R.id.spinner1);
        s2 = (Spinner) findViewById(R.id.spinner2);
        s3 = (Spinner) findViewById(R.id.spinner3);
        s4 = (Spinner) findViewById(R.id.spinner4);
        s5 = (Spinner) findViewById(R.id.spinner5);
        s6 = (Spinner) findViewById(R.id.spinner6);
        s7 = (Spinner) findViewById(R.id.spinner7);
        s8 = (Spinner) findViewById(R.id.spinner8);

        // Spinner fuer Cl-Angaben wird gesetzt
        m1 = (Spinner) findViewById(R.id.menge1);
        m2 = (Spinner) findViewById(R.id.menge2);
        m3 = (Spinner) findViewById(R.id.menge3);
        m4 = (Spinner) findViewById(R.id.menge4);
        m5 = (Spinner) findViewById(R.id.menge5);
        m6 = (Spinner) findViewById(R.id.menge6);
        m7 = (Spinner) findViewById(R.id.menge7);
        m8 = (Spinner) findViewById(R.id.menge8);


        // Eine TextView zum testen der AuswahlFunktion
        text =(TextView) findViewById(R.id.textView);

        // Textfeld fuer die Eingabe des Cocktail-Namens wird gesetzt
        edit =(EditText)findViewById(R.id.cocktail_eingabe);

        // Fuellen der Spinner mit entsprechender Liste
        FillSpinnerWithList(s1, R.array.auswahl_liste);
        FillSpinnerWithList(s2, R.array.auswahl_liste);
        FillSpinnerWithList(s3, R.array.auswahl_liste);
        FillSpinnerWithList(s4, R.array.auswahl_liste);
        FillSpinnerWithList(s5, R.array.auswahl_liste);
        FillSpinnerWithList(s6, R.array.auswahl_liste);
        FillSpinnerWithList(s7, R.array.auswahl_liste);
        FillSpinnerWithList(s8, R.array.auswahl_liste);

        FillSpinner(m1, R.array.cl_liste);
        FillSpinner(m2, R.array.cl_liste);
        FillSpinner(m3, R.array.cl_liste);
        FillSpinner(m4, R.array.cl_liste);
        FillSpinner(m5, R.array.cl_liste);
        FillSpinner(m6, R.array.cl_liste);
        FillSpinner(m7, R.array.cl_liste);
        FillSpinner(m8, R.array.cl_liste);

        // Definition fuer das Anklicken eines Elements im Spinner
        s1.setOnItemSelectedListener(this);
        s2.setOnItemSelectedListener(this);
        s3.setOnItemSelectedListener(this);
        s4.setOnItemSelectedListener(this);
        s5.setOnItemSelectedListener(this);
        s6.setOnItemSelectedListener(this);
        s7.setOnItemSelectedListener(this);
        s8.setOnItemSelectedListener(this);

        m1.setOnItemSelectedListener(this);
        m2.setOnItemSelectedListener(this);
        m3.setOnItemSelectedListener(this);
        m4.setOnItemSelectedListener(this);
        m5.setOnItemSelectedListener(this);
        m6.setOnItemSelectedListener(this);
        m7.setOnItemSelectedListener(this);
        m8.setOnItemSelectedListener(this);
    }


  /* die FillSpinnerWithList Methode fuellt den Spinner mit einer Liste
   * aus dem Resourcen Ordner (string.xml)
   * @param spinner Der zu füllende Spinner
   */
    public void FillSpinnerWithList(Spinner spinner, int id) {

        /* die creatFromResource Methode erstellt einen Arrayadapter aus einem String array.
         * @param R.array.auswahl_liste greift auf das String array im Ressourcen Ordner zu
         * @param android.R.layout.simple_spinner_item Eine Layout Ressource, für die Darstellung der Elemente
         */

        ArrayAdapter<Zutat> spinnerArrayAdapter = new ArrayAdapter<Zutat>(this,   android.R.layout.simple_spinner_item, zutatenListe);

        // Zum Anzeigen der Liste
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

    }


  /* die FillSpinnerWithList Methode fuellt den Spinner mit einer Liste
   * aus dem Resourcen Ordner (string.xml)
   * @param spinner Der zu füllende Spinner
   */
    public void FillSpinner(Spinner spinner, int id) {

        /* die creatFromResource Methode erstellt einen Arrayadapter aus einem String array.
         * @param R.array.auswahl_liste greift auf das String array im Ressourcen Ordner zu
         * @param android.R.layout.simple_spinner_item Eine Layout Ressource, für die Darstellung der Elemente
         */

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                id, android.R.layout.simple_spinner_item);

        // Zum Anzeigen der Liste
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    /* Auswahlhaendler aus der Schnittstelle AdapterView für Spinner
     * @param parent die Adapterview, in der etwas angeklickt wurde
     * @param view das angeklickte Element
     * @param pos die position des angeklickten Ansicht ( also fuer Liste a,b,c erhaelt a=0,b=1,c=2
     * @param id die id des angeklickten Elements
     */
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

        // Auswahl des Items

    }

    // Musste aufgrund der Schnittstelle implementiert werden
    public void onNothingSelected(AdapterView<?> parent) {

    }


    @Override
    // Beim druecken der Back-Taste gelangt man zurueck in das Auswahlmenue
    public void onBackPressed() {

        Intent intent = new Intent(Entry_Cocktail.this, MainActivity.class);
        startActivity(intent);

    }

}
