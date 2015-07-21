package com.teamvollrausch.sh.bismorgen;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * Diese Klaase beinhaltet das Auswahlmenue, dieses zeigt nur Cocktails aus der Db an. Des Weiteren existiert ein Erstell-Button,
 * der nach Betaetigung die Aktivity Entry-Cocktail startet. Außerdem befindet sich im Optionsmenue ein Einstellungs- Button.
 * Nach dem Anklicken eines Elementes aus der Liste, wird die AnzeigeActivity gestartet.
 * @author Hanan Fakhro
 * @author Sophia Eichhorn
 * @version 1.0
 */

public class MainActivity extends Activity implements Database_Schema{

    // Strings zur Kommunikation zwischen CocktailSource und AnzeigeActivity
    public final static String EXTRA_MESSAGE = "com.example.skh.cocktail__2.";
    public final static String EXTRA_MESSAGE2 = "com.example.skh.cocktail__2";
    public final static String EXTRA_MESSAGE3 = "com.example.skh.cocktail__3";

    private boolean createAgain=true;

    private ImageButton create;

    // db_handler fuer Datenbank befehle
    private DatabaseHandler db_handler;

    // ArrayAdapter, um die Liste mit Entries in dei Listview zu setzen
    private ArrayAdapter<Cocktail> listenAdapter;

    //Beinhaltet Cocktaileintraege
    ArrayList<String> l;

    private int pos;
    // Boolean wert im Ordner speichern, um db nur einmal zu erstellen
    public SharedPreferences pref;
    private static final String VAL_KEY = "Exists";

    // Zutat Objekt mit liste
    private Zutat zutat;
    private List<Zutat> zutaten_liste;
    private List<Zutat> zutaten_listedb;

    //Cocktail Objekt mit liste
    private Cocktail cocktail;
    private List<Cocktail> cocktail_Liste;
    private List<Cocktail> cocktail_Listedb;

    // BluetoothAdapter zur Aufforderung, diesen einzuschalten
    private BluetoothAdapter bluetoothAdapter;

    private  Cocktail e;

    //fuer Aktualisierung
    // Booleanwert im Ordner speichern, um db nur einmal zu erstellen
    public SharedPreferences pref2;
    private static final String VAL_KEY2 = "Exists2";


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (bluetoothAdapter== null)
            initBluetooth();


        // Actionbarfarbe setzen
        ActionBar actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(0,134,139)));

        // Erstell Button
        create = (ImageButton) findViewById(R.id.create);

        // DB Handler erstellen
        db_handler = new DatabaseHandler(this);

        // Variable, um abzufrgaen, ob die Db bereits existiert
        pref =getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        //falls es VAL_KEY nicht gibt, fuege diesen in den Ordner ein und setze den Wert auf true
        // geschieht nur beim ersten Starten der App
        if (!pref.contains(VAL_KEY)) {

            editor.putBoolean(VAL_KEY, true);
            editor.commit();

        }


        // Falls der Key bereits existeirt, dann ueberpruefe ob die app das erste mal benutzt wird,
        // indem der Wert von VAL_KEY auf true (Default Wert ) ueberprueft wird und setze diesen
        // nach der Datanbank Auffüllung auf false
        if (pref.getBoolean(VAL_KEY,true)) {


            zutat = new Zutat();
            cocktail = new Cocktail();

            zutaten_liste = zutat.ListOfAllZutaten();
            cocktail_Liste = cocktail.ListOfAllCocktails();

            //Zutaten in die Db schreiben
            for (int i = 0; i < zutaten_liste.size(); i++) {
                db_handler.insertZutat(zutaten_liste.get(i));
            }

            //Cocktails in die Db schreiben
            for (int i = 0; i < cocktail_Liste.size(); i++) {
                db_handler.insertCocktail(cocktail_Liste.get(i));
            }

            insertRezepte();
            insertGetraenk();
            insertalkohol();
            insertKeinAlkohol();
            insertSaft();
            insertSirup();

            editor.putBoolean(VAL_KEY, false);
            editor.commit();

        }


        //Alle in der Db vorhandenen Zutaten + Key werden in z_e gespeichert
        if(createAgain) {

            zutaten_listedb = db_handler.getAllZutaten();

            //Alle in der Db vorhandenen Cocktails + Key werden in c_e gespeichert
            cocktail_Listedb = db_handler.getAllCocktails();

        }

        // Init den Listenadapter, um Arralyst an die Listview zu heften
        listenAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, cocktail_Listedb);
        final ListView meineListView = (ListView) findViewById(R.id.listView);
        // Adapter setzen
        meineListView.setAdapter(listenAdapter);


        // Auf anklicken reagieren, dabei sollen die Zutaten des angeklickten Cocktail
        // durch eine Query im Db_handler in eine ArrayList gespeichert werden
        // und dann an die AnzeigeActivity geschickt werden
        meineListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                                                     public boolean onItemLongClick(    AdapterView<?> arg0,    View v,    final int position,    long id){
                                                         // Es koennen nur selbsterstelle Cocktails geloescht werden
                                                         if (position > 11) {

                                                             AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                                             builder.setTitle("Löschen");
                                                             e = cocktail_Listedb.get(position);
                                                             builder.setMessage("Möchtest du den Cocktail " + e.getName() + " wirklich löschen?");

                                                             pos = position;

                                                             builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

                                                                 @Override
                                                                 public void onClick(DialogInterface dialog, int which) {

                                                                     cocktail_Listedb.remove(pos);
                                                                     String p = ""+e.getNr();
                                                                     db_handler.delete(p);
                                                                     listenAdapter.notifyDataSetChanged();
                                                                     dialog.dismiss();
                                                                 }

                                                             });

                                                             builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {

                                                                 @Override
                                                                 public void onClick(DialogInterface dialog, int which) {

                                                                     dialog.dismiss();
                                                                 }

                                                             });
                                                             // starten des Dialogfensters
                                                             AlertDialog alert = builder.create();
                                                             alert.show();

                                                             return true;

                                                         }
                                                         return true;
                                                     }


                                                 }
        );

        // Auf anklicken reagieren, dabei sollen die Zutaten des angeklickten Cocktail
        // durch eine Query im Db_handler in eine ArrayList gespeichert werden
        // und dann an die AnzeigeActivity geschickt werden
        meineListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
            {
                // Aktueller Context
                Context context = getApplicationContext();
                // Text im popup fenster, nach dem Anklicken eines Elements
                CharSequence text = "wurde ausgewählt.";
                // Der jeweilige Cocktail aus der Liste wird ausgewaehlt, wobei pos
                // das angeklickte feld in der Listview darstellt und auch aus der lIste selbst
                Cocktail e = cocktail_Listedb.get(pos);
                // Query zum Greifen der Zutatenliste des ausgewaehlten Cocktails
                l = db_handler.getAllRezept(e);

                // Fenster, das den Namen des Cocktails nach auswahl anzeigt
                Toast toast = Toast.makeText(context,  " " + e.toString() + text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 2,2);
                toast.show();

                // Starte intent fuer AnzeigeActivity
                Intent intent = new Intent(MainActivity.this, AnzeigeActivity.class);

                String message = e.toString();
                String p_id = ""+ pos;

                //Senden des Cocktailnamens und der zutatenliste an AnzeigeActivity
                intent.putExtra(EXTRA_MESSAGE,message); // name
                intent.putStringArrayListExtra(EXTRA_MESSAGE2,l); //zuatenliste ausgewaehlten cocktails
                intent.putExtra(EXTRA_MESSAGE3, p_id);
                startActivity(intent);
            }
        });


        create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Entry_Cocktail.class);
                startActivity(intent);

            }
        });

    }


    //BT-Initialisierung und aktivierung
    public void initBluetooth() {

        // Zugriff auf dem vom Gerat zugehoerigen Bluetoothadapter

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Falls kein Bluetooth vorhanden ist, wird die Methode beendet
        if ( bluetoothAdapter==null) {
            Toast toast = Toast.makeText(this,  "Kein Bluetooth gefunden!" , Toast.LENGTH_LONG);
            toast.show();
            finish();
        } else {
            //Falls ein Adapter gefunden wurde, wird ueberprueft, ob dieser eingeschaltet ist bzw.
            //aufgefordert einzuschalten
            if (!bluetoothAdapter.isEnabled()) {
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1);
            }
        }
    }


    //DB MEhtoden zum inserten von Daten

    //Einfuegen von Rezepten
    public void insertRezepte() {

        // Sex on t.b
        db_handler.insertRezept(cocktail_Liste.get(0), zutaten_liste.get(12),"2"); //Pfirsichlikör
        db_handler.insertRezept(cocktail_Liste.get(0), zutaten_liste.get(9),"4"); //Wodka
        db_handler.insertRezept(cocktail_Liste.get(0), zutaten_liste.get(6),"8"); //Orangensaft
        db_handler.insertRezept(cocktail_Liste.get(0), zutaten_liste.get(5),"12"); //Ananassaft
        db_handler.insertRezept(cocktail_Liste.get(0), zutaten_liste.get(16),"2"); //Grenadine

        //Tequila s
        db_handler.insertRezept(cocktail_Liste.get(1), zutaten_liste.get(3),"6"); // Tequila
        db_handler.insertRezept(cocktail_Liste.get(1), zutaten_liste.get(6),"18"); // Orangesaft
        db_handler.insertRezept(cocktail_Liste.get(1), zutaten_liste.get(10),"2"); // Zitronensaft
        db_handler.insertRezept(cocktail_Liste.get(1), zutaten_liste.get(16),"2"); // Grenadine

        //Long I.I
        db_handler.insertRezept(cocktail_Liste.get(2), zutaten_liste.get(1),"2"); // weißer rum havana
        db_handler.insertRezept(cocktail_Liste.get(2), zutaten_liste.get(9),"2"); // Wodka
        db_handler.insertRezept(cocktail_Liste.get(2), zutaten_liste.get(3),"2"); // Tequila
        db_handler.insertRezept(cocktail_Liste.get(2), zutaten_liste.get(2),"2"); // Gin
        db_handler.insertRezept(cocktail_Liste.get(2), zutaten_liste.get(6),"2"); // Orangensaft
        db_handler.insertRezept(cocktail_Liste.get(2), zutaten_liste.get(8),"15"); // Cola
        db_handler.insertRezept(cocktail_Liste.get(2), zutaten_liste.get(12),"2"); // pfirsichliklr

        //Swimminhg Pool
        db_handler.insertRezept(cocktail_Liste.get(3), zutaten_liste.get(1),"3"); //weißer rum
        db_handler.insertRezept(cocktail_Liste.get(3), zutaten_liste.get(9),"3"); // Wodka
        db_handler.insertRezept(cocktail_Liste.get(3), zutaten_liste.get(5),"13"); // Ananmassaft
        db_handler.insertRezept(cocktail_Liste.get(3), zutaten_liste.get(15),"2"); //cream o coco
        db_handler.insertRezept(cocktail_Liste.get(3), zutaten_liste.get(13),"3"); //blue curacau

        // Zombie
        db_handler.insertRezept(cocktail_Liste.get(4), zutaten_liste.get(1),"3"); //weißer rum
        db_handler.insertRezept(cocktail_Liste.get(4), zutaten_liste.get(0),"4"); //brauner rum
        db_handler.insertRezept(cocktail_Liste.get(4), zutaten_liste.get(6),"7"); //Orangensaft
        db_handler.insertRezept(cocktail_Liste.get(4), zutaten_liste.get(5),"10"); //Ananassaft
        db_handler.insertRezept(cocktail_Liste.get(4), zutaten_liste.get(10),"2"); //Zitroonensaft
        db_handler.insertRezept(cocktail_Liste.get(4), zutaten_liste.get(16),"2"); // grendaine

        //P. Punch
        db_handler.insertRezept(cocktail_Liste.get(5), zutaten_liste.get(1),"3"); //weißer rum
        db_handler.insertRezept(cocktail_Liste.get(5), zutaten_liste.get(0),"3"); //beauner rum
        db_handler.insertRezept(cocktail_Liste.get(5), zutaten_liste.get(16),"1"); //grenadine
        db_handler.insertRezept(cocktail_Liste.get(5), zutaten_liste.get(10),"2"); ///zitronensaft
        db_handler.insertRezept(cocktail_Liste.get(5), zutaten_liste.get(6),"13"); //Orangensaft
        db_handler.insertRezept(cocktail_Liste.get(5), zutaten_liste.get(5),"6"); ///Anannassaft

        //Cuba Libre
        db_handler.insertRezept(cocktail_Liste.get(6), zutaten_liste.get(1),"6");  //Havanna
        db_handler.insertRezept(cocktail_Liste.get(6), zutaten_liste.get(8),"19");  //Cola
        db_handler.insertRezept(cocktail_Liste.get(6), zutaten_liste.get(10),"3");  //Cola

        // Pina C.
        db_handler.insertRezept(cocktail_Liste.get(7), zutaten_liste.get(5),"16");  //Ananassaft
        db_handler.insertRezept(cocktail_Liste.get(7), zutaten_liste.get(1),"6");  //Weißer rum
        db_handler.insertRezept(cocktail_Liste.get(7), zutaten_liste.get(15),"2");  //Coco
        db_handler.insertRezept(cocktail_Liste.get(7), zutaten_liste.get(10),"2");  //Zitro

        //Caipi.
        db_handler.insertRezept(cocktail_Liste.get(8), zutaten_liste.get(10),"3");  //Zitro
        db_handler.insertRezept(cocktail_Liste.get(8), zutaten_liste.get(4),"5");  //Pitu
        db_handler.insertRezept(cocktail_Liste.get(8), zutaten_liste.get(11),"20");  //Soda

        //Virgin C.
        db_handler.insertRezept(cocktail_Liste.get(9), zutaten_liste.get(5),"22");  //ananas
        db_handler.insertRezept(cocktail_Liste.get(9), zutaten_liste.get(15),"2");  //Coco
        db_handler.insertRezept(cocktail_Liste.get(9), zutaten_liste.get(10),"2");  //Zitronensaft

        //Safer sex
        db_handler.insertRezept(cocktail_Liste.get(10), zutaten_liste.get(6),"14");  //osaft
        db_handler.insertRezept(cocktail_Liste.get(10), zutaten_liste.get(5),"12");  //ananas
        db_handler.insertRezept(cocktail_Liste.get(10), zutaten_liste.get(16),"2");  //grenadine

        //Cinderella
        db_handler.insertRezept(cocktail_Liste.get(11), zutaten_liste.get(6),"12");  //o-saft
        db_handler.insertRezept(cocktail_Liste.get(11), zutaten_liste.get(5),"10");  //ananas
        db_handler.insertRezept(cocktail_Liste.get(11), zutaten_liste.get(15),"2");  //Coco
        db_handler.insertRezept(cocktail_Liste.get(11), zutaten_liste.get(16),"2");  //Grenadine

    }

    public void insertGetraenk() {

        db_handler.insertZutatKinder(zutaten_liste.get(3), TABLE_GETRAENKE);  //Tequila
        db_handler.insertZutatKinder(zutaten_liste.get(13), TABLE_GETRAENKE);  //Blue Curacau
        db_handler.insertZutatKinder(zutaten_liste.get(10), TABLE_GETRAENKE);  //Zitronensaft
        db_handler.insertZutatKinder(zutaten_liste.get(5), TABLE_GETRAENKE);  //Ananassaft
        db_handler.insertZutatKinder(zutaten_liste.get(0), TABLE_GETRAENKE);  //brauner rum
        db_handler.insertZutatKinder(zutaten_liste.get(14), TABLE_GETRAENKE);  //Orangenlikoer
        db_handler.insertZutatKinder(zutaten_liste.get(9), TABLE_GETRAENKE);  //Wodka
        db_handler.insertZutatKinder(zutaten_liste.get(2), TABLE_GETRAENKE);  //gin
        db_handler.insertZutatKinder(zutaten_liste.get(12), TABLE_GETRAENKE);  //pfirsichlikör
        db_handler.insertZutatKinder(zutaten_liste.get(6), TABLE_GETRAENKE);  //orenagensaft
        db_handler.insertZutatKinder(zutaten_liste.get(15), TABLE_GETRAENKE);  //cream coco
        db_handler.insertZutatKinder(zutaten_liste.get(11), TABLE_GETRAENKE);  //soda
        db_handler.insertZutatKinder(zutaten_liste.get(16), TABLE_GETRAENKE);  //grenadine
        db_handler.insertZutatKinder(zutaten_liste.get(8), TABLE_GETRAENKE);  //cola
        db_handler.insertZutatKinder(zutaten_liste.get(1), TABLE_GETRAENKE);  //havanna
    }

    public void insertalkohol() {

        db_handler.insertZutatKinder(zutaten_liste.get(3), TABLE_ALKOHOL);  //Tequila
        db_handler.insertZutatKinder(zutaten_liste.get(10), TABLE_ALKOHOL);  //Zitronensaft
        db_handler.insertZutatKinder(zutaten_liste.get(0), TABLE_ALKOHOL);  //brauner rum
        db_handler.insertZutatKinder(zutaten_liste.get(9), TABLE_ALKOHOL);  //Wodka
        db_handler.insertZutatKinder(zutaten_liste.get(2), TABLE_ALKOHOL);  //gin
        db_handler.insertZutatKinder(zutaten_liste.get(1), TABLE_ALKOHOL);  //havanna
    }


    public void insertKeinAlkohol() {

        db_handler.insertZutatKinder(zutaten_liste.get(10), TABLE_KEIN_ALKOHOL);  //Zitronensaft
        db_handler.insertZutatKinder(zutaten_liste.get(5), TABLE_KEIN_ALKOHOL);  //Ananassaft
        db_handler.insertZutatKinder(zutaten_liste.get(6), TABLE_KEIN_ALKOHOL);  //orenagensaft
        db_handler.insertZutatKinder(zutaten_liste.get(11), TABLE_KEIN_ALKOHOL);  //soda
        db_handler.insertZutatKinder(zutaten_liste.get(16), TABLE_KEIN_ALKOHOL);  //grenadine
        db_handler.insertZutatKinder(zutaten_liste.get(8), TABLE_KEIN_ALKOHOL);  //cola
    }


    public void insertSaft() {

        db_handler.insertZutatKinder(zutaten_liste.get(5), TABLE_SAFT);  //Ananassaft
        db_handler.insertZutatKinder(zutaten_liste.get(6), TABLE_SAFT);  //orenagensaft
        db_handler.insertZutatKinder(zutaten_liste.get(11), TABLE_SAFT);  //soda
        db_handler.insertZutatKinder(zutaten_liste.get(8), TABLE_SAFT);  //cola

    }

    public void insertSirup() {

        db_handler.insertZutatKinder(zutaten_liste.get(16), TABLE_SIRUP);  //grenadine
        db_handler.insertZutatKinder(zutaten_liste.get(13), TABLE_SIRUP);  //Blue Curacau
        db_handler.insertZutatKinder(zutaten_liste.get(12), TABLE_SIRUP);  //pfirsichlikör
        db_handler.insertZutatKinder(zutaten_liste.get(15), TABLE_SIRUP);  //cream coco
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Erstellung des Optiones-Menues
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Eventmanager des Options-Menues
        int id = item.getItemId();

        // Einstellungsbutton ruft Bluetooth-Einstellungsmenue des vorliegenden Android-systems auf
        if ( id == R.id.action_settings) {

            initBluetooth();
            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    protected void onStop() {

        super.onStop();

    }

    // Mit einem leeren Methodenrumpf aufgerufen, um die Back-Taste zu deaktivieren
    @Override
    public void onBackPressed() {

    }

}