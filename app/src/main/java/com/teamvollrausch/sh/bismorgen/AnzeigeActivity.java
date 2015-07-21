package com.teamvollrausch.sh.bismorgen;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Diese Klasse erscheint nach dem Auswaehlen eines Cocktails, startet die Bluetooth- Verbindung zum BT-Board 1.04v und dient zum Abschicken des formatierten
 * Strings des Rezeptes eines ausgeaehlten Cocktails.
 * @author Hanan Fakhro
 * @author Sophia Eichhorn
 * @version 1.0
 */

public class AnzeigeActivity extends Activity {

    private static final int NOTIFY_ME_ID=1337;

    //Bluetoothdevice name
    private boolean connected = false;

    // erweiteter String mit der empfangenen Nachricht
    private String test_end = "";

    // String mit der empfangenen Nachricht
    private String readMessage;

    // Actionbar Objekt, zum Setzen der Farbe
    private ActionBar actionBar;

    // Buffer zum Speichern der gelesenen Nachricht
    private byte[] buffer = new byte[512];

    private boolean auftrag_gesendet = false;

    // Dialogfenster
    private AlertDialog.Builder builder;

    // int-Konstante fuer den BT-Handler
    private static final int REQUEST_ENABLE_BT = 1;

    private boolean gesendet = false;

    private BluetoothAdapter myBluetoothAdapter;

    // Thread zum Schreiben und Empfangen von Nachrichten
    private ConnectedThread mConnectedThread;

    private BluetoothSocket btSocket = null;

    private StringBuilder recDataString = new StringBuilder();

    private Handler bluetoothIn;

    final int handlerState = 0;

    // beinhalten den Namen des ausgewaehlten Cocktails
    private String message;
    private String zutatString;

    // SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private String ende = "";

    private TextView text2;

    private String m;

    // Nachricht die spaeter an die Cmaschine geschickt wird
    private String output;

    // Db-handler
    private DatabaseHandler db_handler;

    // ImageView
    private ImageView image;
    // Textview zum Setzen des Cocktail Namens in dieser Ansicht
    private TextView text;
    // Image, welche fuer den cocktail passend ist
    private Drawable image_source;
    // Instanz der Klasse Cocktail
    private Cocktail cocktail;
    // TextView zum testen
    private TextView b;
    // Liste mit Zutaten eines Cocktails
    private ArrayList<String> list;
    // ListAdapter, zum adaptieren der Liste an Listview
    private ListAdapter adapter;
    // Button zum oeffnen der Aktivitaet auftrag bearbeiten, in der
    // der Auftrag gesendent werden soll
    private ImageButton button;
    // liste mit allen Bluetooth geraeten
    private ArrayList<String> Bluetoothlist;
    // Array der Zutatenliste mit dem zu versendenen Format
    private ArrayList<String> format;
    // Bluetooth Adapter zum Auffordern, Bluetooth enzuschalten
    private BluetoothAdapter bluetoothAdapter;
    // gekoppelte geraete
    private Set<BluetoothDevice> devicesArray;
    private String address = "20:13:05:27:13:31";

    private ImageButton img_button;

    private CommunicateAsyncTask c;

    private int cocktail_primary_key;

    private BluetoothDevice device;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anzeige);

        // Initialisierung Des BT-Adapters
        initBluetooth();

        // Actionbar-Farben setzen
        setActionBar();

       // Einige Buttons initialisieren und den Buffer neu setzen
        init_Var();

        // Initialisierung des Alter-Dialog Fensters
        builder = new AlertDialog.Builder(AnzeigeActivity.this);
        // Bluetooth-Adapter
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // MAC-Adresse des BT Boards 1.04v
        device = bluetoothAdapter.getRemoteDevice("20:13:05:27:13:31");
        Bluetoothlist = new ArrayList<String>();

        // init db_handler
        db_handler = new DatabaseHandler(this);
        // Intent erzeugen, um den Cocktailnamen und die Zutatliste aus Cocktail_source zu erhalten
        Intent intent = getIntent();
        // Alle Cocktails die es gibt
        List<Cocktail> c = db_handler.getAllCocktails();
        // Cocktail wird erstellt, um auf die vordefinierten Cocktails zugreifen zu koennen
        cocktail = new Cocktail();

        // init ImageView
        image = (ImageView) findViewById(R.id.Cocktail_image);

        // Zeigt den Namen des Cocktails an
        text = (TextView) findViewById(R.id.Cocktail_name);

        // Cocktailname der gewaehlt wurde
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        // Id des ausgeweahlten Cocktails
        final String primaryKey = intent.getStringExtra(MainActivity.EXTRA_MESSAGE3);

        // Parsen der String-Id in einen Int-Wert
        cocktail_primary_key = Integer.parseInt(primaryKey);

        // Erstellung eines Cocktails
        cocktail.setName(message);
        // Setzung des Primary- Keys des erstellten Cockttailobjekts
        cocktail.setNr(primaryKey);
        // Der des Cocktails wird gesetzt
        text.setText(message);
        // Name des Cocktails wird auf der Action-Bar angeszeit
        actionBar.setTitle(message);

        // cocktail_with_Image = 1, falls ein Cocktail bereits ein Bild hat
        // cocktail_with_Image = 0, falls ein Cocktail kein Bild hat
        int cocktail_with_Image =  db_handler.cursorLengthofCocktail(cocktail);

      // Vordefinierte Cocktails erhalten Bilddateien aus dem Ordner drawable
      if (cocktail_primary_key >=0 && cocktail_primary_key <=11) {
            SetImage(message);

       } else {

          // Falls ein selbsterstellter Cocktai kein Bild besitzt, wird diesem eins zugewiesen
          if (cocktail_with_Image  > 0) {

              byte[] image_dc = db_handler.getCocktailImage(cocktail_primary_key);
              Bitmap bitmap = BitmapFactory.decodeByteArray(image_dc, 0, image_dc.length);
              image.setImageBitmap(bitmap);
          }

      }

        // Liste mit zuatten aus Cocktailsource
        list = intent.getStringArrayListExtra(MainActivity.EXTRA_MESSAGE2);
        // Liste wird veraendert, so dass cl mit drauf steht
        format = GetFormat(list);
        // Nachricht, die an aufrtrag abschicken gesendent wird, aus query
        output = db_handler.getIds(list);
        //Zutatstring
        m = getZutatString(list);
        //Testbutton
        b = (TextView) findViewById(R.id.testbluetooth);
        //init Imagebutton zum senden
        button = (ImageButton) findViewById(R.id.send);

        //Listadapter setzen
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, format);
        ListView meineListView = (ListView) findViewById(R.id.text_coktail);
        meineListView.setAdapter(adapter);

        // BT-Handler initialisieren
        setting_bt_Handler();

        // Vordefinierte Cocktailbilder sollen nicht geloescht werden
        if ( cocktail_primary_key <= 11) {

            img_button.setVisibility(View.INVISIBLE);

        }

        // Bluetooth Verbindung ueber Asynchtask starten
        BluetoothAsyncTask btasync = new BluetoothAsyncTask();
        btasync.execute("");

        // Eventmanager des Foto-Buttons
        img_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            // Auswahl eines Bildes
            choose_pic();

            }

        });

        // Eventmanager des Sende-Buttons, zum Verschicken des Strings
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            send_Auftrag();

            }
        });

        /* Eventmanager der Imageview, wenn man lange das Bild gedrueckt haelt, erscheint
         * ein Dialogfenster
         */
        image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                delete_Bild();

                return true;
            }

        });

    }


    // Bluetooth-Handler wird erstellt und gestartet
    public void setting_bt_Handler() {

        bluetoothIn = new Handler() {

            public void handleMessage(android.os.Message msg) {

                if (msg.what == handlerState) {

                    // msg.arg1 = bytes vom Thread
                    String readMessage = (String) msg.obj;
                    //fuege solange ein, bi Ablschlusssymbol kommt
                    recDataString.append(readMessage);
                    // determine the end-of-line
                    int endOfLineIndex = recDataString.indexOf("\0");

                    if (endOfLineIndex > 0) {

                        // String extrahieren
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);
                        text.setText("Data Received = " + dataInPrint);
                        //Laenge der Daten nehmen
                        int dataLength = dataInPrint.length();

                    }
                }
            }
        };
    }


    // Startet einen Dialog, mit zwei Auswahlmoeglichkeiten (Gallerie, Kamera )
    public void choose_pic() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AnzeigeActivity.this);
        builder.setTitle("Wie hättest du es gern ;-)");

        builder.setPositiveButton("Gallerie", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                Gallery();
                dialog.dismiss();

            }

        });

        builder.setNegativeButton("Kamera", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                callCamera();
                dialog.dismiss();

            }

        });

        AlertDialog alert = builder.create();
        alert.show();

    }


    // Methode zum Versenden der Rezeptliste an das BT-Board 1.04v
    public void send_Auftrag() {

        init();

        try {

            ConnectAsyncTask   connectAsyncTask = new ConnectAsyncTask();
            connectAsyncTask.execute(output);

        } catch (Exception e) {

            e.printStackTrace();

       }

        auftrag_gesendet = true;

    }


    public void delete_Bild() {

        AlertDialog.Builder builder = new AlertDialog.Builder(AnzeigeActivity.this);
        builder.setTitle("Löschen");
        builder.setMessage("Möchtest du dieses Bild wirklich löschen??");

        builder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

             @Override
             public void onClick(DialogInterface dialog, int which) {

                 String key =""+ cocktail_primary_key;
                 db_handler.deleteImage(key);
                 image.setImageDrawable(null);

                 dialog.dismiss();

             }

         });

         builder.setNegativeButton("Nein", new DialogInterface.OnClickListener() {

             @Override
             public void onClick(DialogInterface dialog, int which) {

                 dialog.dismiss();
             }

          });

         AlertDialog alert = builder.create();
         alert.show();

    }


    // Initialisierung einiger Variablen
    public void init_Var() {

        img_button = (ImageButton)findViewById(R.id.Cocktail_image_button);
        img_button.setVisibility(View.VISIBLE);
        buffer = new byte[buffer.length];
        text2 = (TextView) findViewById(R.id.textView2);

    }


    // Farben der Actionbar setzen
    public void setActionBar() {

        actionBar = getActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(0, 134, 139)));

    }

    // Starten des Gallerie-Intents
    public void Gallery() {

        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1);

    }


    // Startend der Kamera
    public void callCamera() {

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 0);

    }

    // Bluetooth initialisieren
    public void initBluetooth() {

        // Zugriff auf dem vom Gerat zugehoerigen Bluetooth adapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Falls kein bluetooth vorhanden ist, wird die Methode beendet
        if (bluetoothAdapter == null) {

            Toast toast = Toast.makeText(this, "Kein Bluetooth gefunden!", Toast.LENGTH_LONG);
            toast.show();
            finish();

        } else {

            //Falls ein adapter gefunden wurde, wird ueberprueft, ob dieser eingeschaltet ist bzw.
            //aufgefordert einzuschaltent
            if (!bluetoothAdapter.isEnabled()) {

                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1);
            }
        }
    }


    @Override
    protected void onRestart() {

        super.onRestart();

    }


    @Override
    protected void onStart() {

        super.onStart();

    }


    @Override
    public synchronized void onResume() {

        super.onResume();

    }

    // Bluetoothverbindung nicht schließen
    @Override
    public synchronized void onPause() {

        super.onPause();

        try {

            btSocket.close();

        } catch (IOException e2) {

            e2.printStackTrace();

        }
    }


    @Override
    protected void onStop() {

        super.onStop();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT) {

            if (myBluetoothAdapter.isEnabled()) {

            } else {

              Toast.makeText(this, "Kein Bluetoothadapter gefunden", Toast.LENGTH_LONG).show();
              finish();

              }
        }

        // Ueberpruefung, ob ein Foto aus der Gallerie oder Kamera ausgewaehlt wurde
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {

            case 0:

                     if ( image != null ) {

                        db_handler.deleteImage(cocktail.getNr());
                        image.setImageDrawable(null);

                     }

                     // ausgewaehlte Fotodatei
                     Bitmap thumb = (Bitmap) data.getExtras().get("data");
                     // Bild anzeigen
                     image.setImageBitmap(thumb);

                     /* Konvertierung der ausgewaehlten Bilddatei in ein Array vom
                      * Typen byte[] und speichern in die Db
                      */
                     ByteArrayOutputStream b_stream = new ByteArrayOutputStream();
                     thumb.compress(Bitmap.CompressFormat.PNG, 100, b_stream);
                     byte imageInByte[] = b_stream.toByteArray();
                     cocktail.setImage(imageInByte);
                     db_handler.insertImage(cocktail);

                     break;

             case 1:

                    // Buendeln Bilddatei aus der Gallerie
                    Bundle extras2 = data.getExtras();

                    if ( image != null && cocktail_primary_key > 11 ) {

                        db_handler.deleteImage(cocktail.getNr());
                        image.setImageDrawable(null);

                    }


                     if (extras2 != null) {

                         Bitmap yourImage = extras2.getParcelable("data");

                         ByteArrayOutputStream bg_stream = new ByteArrayOutputStream();
                         yourImage.compress(Bitmap.CompressFormat.PNG, 100, bg_stream);
                         byte imageInByteg[] = bg_stream.toByteArray();
                         cocktail.setImage(imageInByteg);
                         db_handler.insertImage(cocktail);

                     }

                      // Dateipfad
                      Uri selectedImage = data.getData();
                      String[] filePathColumn = { MediaStore.Images.Media.DATA };

                      Cursor cursor = getContentResolver().query(selectedImage,
                      filePathColumn, null, null, null);
                      cursor.moveToFirst();

                      int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                      String picturePath = cursor.getString(columnIndex);
                      cursor.close();

                     /* Konvertierung der ausgewaehlten Bilddatei in ein Array vom
                      * Typen byte[] und speichern in die Db
                      */
                      byte[] h = convertImageToByte(selectedImage);

                      cocktail.setImage(h);
                      db_handler.insertImage(cocktail);
                      image.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                      break;

        }
    }


    /* Konvertierung der ausgewaehten Imagedatei aus der Gallerie
     * @param uri Uri-Objekt
     * @return data Bilddatei vom Typen byte[]
     */
    public byte[] convertImageToByte(Uri uri) {

        byte[] data = null;

        try {

            ContentResolver cr = getBaseContext().getContentResolver();
            InputStream inputStream = cr.openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();

        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }

        return data;
    }


    // Erstellung des BT-Sockets fuer den Verbindungsaufbau zum BT-Board
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        // UUID des BT-Boards
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);

    }


    // Liste als String verarbeiten
    public String preparedTest(ArrayList<String> list) {

        String plist = "";

        for (int i = 0; i < list.size(); i = i + 2) {

            plist += list.get(i);

        }

        return plist;

    }


    //Zutatenmenge mit cl bearbeiten
    public ArrayList<String> GetFormat(ArrayList<String> list) {

        ArrayList<String> list1 = new ArrayList<String>();
        String cl = "cl ";

        for (int i = 0; i < list.size(); i = i + 2) {

            list1.add(list.get(i + 1) + cl + list.get(i));

        }

        return list1;

    }


    /* Die Rezeotliste als String verarbeiten
     * @param l Liste mit dem Rezeot des ausgewaehlten Cocktails
     * @return a String mit den Zutaten aus der Rezeptliste
     */
    public String getZutatString(ArrayList<String> l) {

        String a = "";

        for (int i = 0; i < l.size(); i++) {

            a += l.get(i);

        }

        return a;

    }


    /* Auswahl der richtigen Imagedatei fuer die vordefinierten Cocktails
     * @param message Cocktailname
     */
    public void SetImage(String message) {

        if (message.contains("Sex on the Beach") ||message.contains("Safer Sex on the Beach") ) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.sotbgross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Tequila Sunrise")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.tequilasunrisegross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Long Island Ice Tea")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.longislandiceteakleingross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Swimming Pool")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.swimminggross);
            image.setImageDrawable(image_source);

        }  else if (message.contains("Yellow Runner")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.yellowrunnergross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Pussy Foot")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.pussyfootgross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Hurricane")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.hurricanegross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Planter's Punch")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.plantersgross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Blue Lady")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.blueladygross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Screwdriver")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.screwdrivergross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Aftersex")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.aftergross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Flying Kangaroo")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.flyinggross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Mandarine Martini")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.manmartinigross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Bubble Gum")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.bubblegross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Electric Margarita")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.elecmargaritagross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Cuban Special")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.cubanspecialgross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Red Russian")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.redrussiangross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Vesper")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.vespergross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Fuzzy Navel")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.fuzzynavelgross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Bellini Martini")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.bellinigross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Cuba Libre")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.cubalibregross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Coconut Kiss")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.cocokissgross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Caipirinha")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.caipigross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Zombie")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.zgross);
            image.setImageDrawable(image_source);

        }else if (message.contains("Pina Colada") || message.contains("Virgin Colada") ) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.pinagross);
            image.setImageDrawable(image_source);

        } else if (message.contains("Cinderella")) {

            image_source = (Drawable) getResources().getDrawable(R.drawable.cgross);
            image.setImageDrawable(image_source);

        }

    }


    // Initialisierung des BT-Adapters
    public void init() {

        // Zugriff auf dem vom Geraet zugehoerigen Bluetoothadapter
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Falls kein Bluetooth vorhanden ist, wird die Methode beendet
        if (bluetoothAdapter == null) {

            Toast toast = Toast.makeText(this, "Gerät ist nicht bluetoothfähig!!", Toast.LENGTH_LONG);
            toast.show();
            finish();

        } else {

            //Falls ein adapter gefunden wurde, wird ueberprueft, ob dieser eingeschaltet ist bzw.
            //aufgefordert einzuschaltent
            if (!bluetoothAdapter.isEnabled()) {

                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, 1);

            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_anzeige, menu);
        return true;

    }


    // Durch Betaetigung von Erstellen wechselt man zu einer Uebersicht, in der
    // Cocktails erstellt und gespeichert werden können.
    public void GoToCreate(View view) {

        Intent intent = new Intent(this, Entry_Cocktail.class);
        startActivity(intent);

    }


    /* Diese innere Klasse startet einen Thread, der zum Empfangen
     * und Senden von Nachrichten via Bluetooth dient
     */
    private class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        // Kostruktor mit BT Socket
        public ConnectedThread(BluetoothSocket socket) {

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                    // I/O Streams fuer die Verbindung
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();

            } catch (IOException e) {

                e.printStackTrace();

            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;

        }


        // Run-Methode
        public void run() {

            Looper.prepare();

            AnzeigeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }

            });

            int bytes;

            CharSequence c = "";
            readMessage = "";

            while (true) {

                try {

                    bytes = mmInStream.read(buffer);

                    //lesen aus input buffer
                    String readCharacter = new String(buffer, 0, bytes);
                    // senden an dern Ui handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();

                    // empfangene Nachricht
                    readMessage += readCharacter;

                    // angehengt an test_end, zum spaeteren Ueberpruefen, ob die aktuelle Kommunikation
                    // beendet wurde
                    test_end += readMessage + "";
                    btSocket.getInputStream();

                    // Falls der String "Der Adler ist gelandet" empfangen wurde, soll die aktuelle
                    // Activity beendet werden
                    if (test_end.endsWith("Der Adler ist gelandet.")) {

                        try {

                            Thread.sleep(5500);

                        } catch (Exception e) {

                            e.printStackTrace();

                        }

                        test_end = new String();
                        test_end = "";
                        finish();

                    }

                } catch (IOException e) {

                    e.printStackTrace();
                    break;

                }

            }

        }


        // Methode zum Senden
        public  void write(String input) {

            // String in bytes umwandeln
            byte[] msgBuffer = input.getBytes();

            try {

                for (int i = 0; i < msgBuffer.length; i++) {

                    // Schreiben via Bluetooth
                    mmOutStream.write(msgBuffer[i]);
                    sleep(500);
                    gesendet = true;

                }

            } catch (IOException e) {

                try {

                    for (int i = 0; i < 6; i++) {

                        Toast.makeText(AnzeigeActivity.this, "Zur Zeit kann keine Verbindung hergestellt werden, da das Modul entweder gebraucht wird oder die Entfernung zu groß ist. Versuche es in einigen Minuten nochmal", Toast.LENGTH_LONG).show();

                    }

                    finish();

                } catch (Exception ex) {

                    ex.printStackTrace();

                  }

                finish();
                gesendet = false;

            } catch (InterruptedException e) {

                e.printStackTrace();

              }

        }

    }


    /* Random-Auswahl eines lustigen Spruchs
     * @param min Minimum
     * @param max Maximum
     * @return zufaellig ausgewaehlter Spruch
     */
    public String  witzigerTeil(int min, int max) {

        Random rand = new Random();

         message = "";

        int output = rand.nextInt((max-min) + 1) + min;

        switch(output) {

            case 0: message += "Ich bin Informatiker, da hat man bei den Frauen schon per default verschissen...";
                break;
            case 1: message +="Broadcast Prost!";
                break;
            case 2: message +="No place like localhost.";
                break;
            case 3: message +="Mein Kopf ist noch nicht gebootet.";
                break;
            case 4: message +="Realität ist eine Illusion, die durch Mangel von Alkohol hervorgerufen wird.";
                break;
            case 5: message +="Ich trinke um meine Probleme zu ertränken! Aber diese Bastarde können schwimmen!";
                break;
            case 6: message +="Saufen erhöht das Risiko flachgelegt zu werden!";
                break;
            case 7: message +="Ich kann auch ohne Alkohol lustig sein. Aber sicher ist sicher.";
                break;
            case 8: message +="Nüchtern betrachtet war es besoffen besser!";
                break;
            case 9: message +="Scheiß Fete, wenn ich meine Hose finde, gehe ich!";
                break;
            case 10: message +="Gestern habe ich aufgehört zu trinken. Heute feiere ich mein Comeback.";
                break;
            case 11: message +="Alle Tage sind gleich lang..aber unterschiedlich breit!";
                break;
            case 12: message +="Spiel mir das Lied vom Dope.";
                break;
            case 13: message +="Saufet, saufet fallet nieder, stehet auf und saufet wieder!";
                break;
            case 14: message +="Wenn meine Zunge deinen Hals berührt und deine feuchte Öffnung spürt, dann weiß ich du gehörst zu mir, mein geliebtes Dosenbier";
                break;
            case 15: message +="Ihre Argumente sind so schwammig wie Ihr Busen.";
                break;
            case 16: message +="Ein Ingenieur der nicht säuft, ist wie ein Motor der nicht läuft!";
                break;
            case 17: message +="Betrunken flirten ist wie hungrig einkaufen.";
                break;
            case 18: message +="Natürlich wollen alle Hochschulen nur die besten Studenten... aber wir haben nur SIE...";
                break;
            case 19: message +="Fehler: Tastatur nicht angeschlossen! Bitte Taste F1 drücken.";
                break;
            case 20: message +="Software is like sex! It's best if it is free.";
                break;
            case 21: message +="Programming is similar to sex. If you make a mistake, you have to support it for the rest of your life.";
                break;

        }

        return message;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if ( id == R.id.action_settings) {

            initBluetooth();
            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));

        }

        return super.onOptionsItemSelected(item);

    }


    /* Diese innnere Klasse startet einen AsyncTask, damit die UI
     * der App beim Anzeigen des Dialogs nicht erfriert
     */
    private class ConnectAsyncTask extends AsyncTask<String, String, String> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {

            dialog = new ProgressDialog(AnzeigeActivity.this);
            dialog.setTitle("");
            dialog.setMessage("Final Countdown :-D");
            dialog.show();

        }

        @Override
        protected String doInBackground(String... message) {

            String out = message[0];

            try {

                 mConnectedThread.write(out);

            } catch (Exception e) {

                e.printStackTrace();

            }

            String witzig = witzigerTeil(0,21);

            while (!test_end.endsWith("Der Adler ist gelandet.") )
            publishProgress("Lass es krachen ;-) \n" + "Aye Aye Captain Sandwich: \n "+ witzig );

            // Sobald die Kommunikation beendet wurden, erscheint ein Sound
            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.auf_den_alkohol);
            mp.start();


            // Vibrator initialisiert
            Vibrator v = (Vibrator) AnzeigeActivity.this.getSystemService(Context.VIBRATOR_SERVICE);

            // Vibration fuer eine halbe Sekunde starten
            if (v.hasVibrator()) {

                Log.v("Can Vibrate", "YES");
                v.vibrate(1000);

            } else {

                Log.v("Can Vibrate", "NO");

            }

            // Sobald die Kommunikation beendet wurden, erscheint eine Notification
            final NotificationManager mgr=
                    (NotificationManager)AnzeigeActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification note=new Notification(R.drawable.vesperklein,
                    "Cocktail fertig!",
                    System.currentTimeMillis());

            // Aktion die nach dem Klicken auf das Notification durchgefuehrt werden soll
            PendingIntent i = PendingIntent.getActivity(AnzeigeActivity.this, 0,
                    new Intent(AnzeigeActivity.this, MainActivity.class),
                    0);


            note.setLatestEventInfo(AnzeigeActivity.this, "Bis Morgen",
                    "Ready...Steady...Go!!!", i);

            // Nach dem Anklicken verschwindet das Icon
            note.flags = Notification.FLAG_AUTO_CANCEL;

            mgr.notify(NOTIFY_ME_ID, note);

            try {

                Thread.sleep(2000);

            } catch (Exception ex) {

                ex.printStackTrace();

            }

            return message[0];

        }


        @Override
        protected void onProgressUpdate(String... values) {

            dialog.setMessage(values[0]);

        }


        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            // Warten
            for (int i=0;i<6;i++) {

            }

            dialog.dismiss();

        }

    }


    /* Diese innere Klasse startet einen AsyncTask zum Aufbau
     * der Bluetoothverbindung mit dem BT-Board 1.04v
     */
    private class BluetoothAsyncTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            BluetoothDevice B_device = myBluetoothAdapter.getRemoteDevice("20:13:05:27:13:31");

            try {

                btSocket = createBluetoothSocket(B_device);

            } catch (IOException e) {

                Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
                e.printStackTrace();

            }

            // Verbindung zum Socket herstellen
            try {

                btSocket.connect();
                connected = true;

            } catch (IOException e) {

                try {

                    btSocket.close();

                } catch (IOException e2) {

                    e2.printStackTrace();

                }
            }
        }


        @Override
        protected String doInBackground(String... message) {

            mConnectedThread = new ConnectedThread(btSocket);
            mConnectedThread.start();

            return message[0];

        }


        @Override
        protected void onProgressUpdate(String... values) {

        }


        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

        }

    }


    /* Diese innere Klasse startet ein Dialogfenster über einen AsyncTask,
     * damit die UI nicht einfriert
     */
    private class CommunicateAsyncTask extends AsyncTask<String, String, String> {

        private ProgressDialog dialog1;


        @Override
        protected void onPreExecute() {

            dialog1 = new ProgressDialog(AnzeigeActivity.this);
            dialog1.setTitle("");
            dialog1.setMessage("Verbindung wird aufgebaut...");
            dialog1.show();

        }


        @Override
        protected String doInBackground(String... message) {

            String out = readMessage;

            for (int i=0;i<10;i++) {

                dialog1.show();

            }

            return out;

        }


        @Override
        protected void onProgressUpdate(String... values) {

        }


        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if (connected==true)
                dialog1.dismiss();

         }

    }


    @Override
    // Beim Druecken der Backtaste wird das Auswahlmenue geoeffnet
    public void onBackPressed() {

        Intent intent = new Intent(AnzeigeActivity.this, MainActivity.class);
        startActivity(intent);

    }

}
