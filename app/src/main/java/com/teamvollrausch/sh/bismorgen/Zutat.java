package com.teamvollrausch.sh.bismorgen;
import java.util.ArrayList;
import java.util.List;

/**
 * Objekt wird benoetig, um Zutaten persistent in der datenbank zu speichern.
 * Jede Zutat besitzt sowohl einen Namen, als auch einen Primary Key.(Nr.)
 * @author Hanan Fakhro
 * @author Sophia Eichhorn
 * @version 1.0
 */

public class Zutat {

    // Name und Key einer Zutat
    private String zutat_name;
    private String nr;


    // Get- und Setter
    public String getNr() {
        return this.nr;
    }
    public void setNr(String nr) {
        this.nr = nr;
    }

    public String getZutatName() {
        return this.zutat_name;
    }
    public void setZutatName(String name) {
        this.zutat_name = name;
    }


    /* Erstellung einer Zutat
     * @param name Name der Zutat
     * @param nr Primary Key einer Zutat
     */
    public Zutat createZutat(String name, String nr) {
        Zutat zutat = new  Zutat();
        zutat.setZutatName(name);
        zutat.setNr(nr);

        return zutat;
    }


    /* Einfuegen aller verfuegbaren Zutaten in eine Liste
     * @return liste Liste mit allen Zutaten
     */
    public List< Zutat> ListOfAllZutaten() {

        List<Zutat> liste = new ArrayList<Zutat>();
        liste.add(createZutat("Brauner Rum","0"));
        liste.add(createZutat("Weißer Rum","1"));
        liste.add(createZutat("Gin","2"));
        liste.add(createZutat("Tequila","3"));
        liste.add(createZutat("Pitu","4"));
        liste.add(createZutat("Ananassaft","5"));
        liste.add(createZutat("Orangensaft","6"));
        liste.add(createZutat("Kirschsaft","7"));
        liste.add(createZutat("Cola","8"));
        liste.add(createZutat("Wodka","9"));
        liste.add(createZutat("Zitronensaft","10"));
        liste.add(createZutat("Soda","11"));
        liste.add(createZutat("Pfirsichlikör","12"));
        liste.add(createZutat("Blue Curaçau","13"));
        liste.add(createZutat("Orangenlikör","14"));
        liste.add(createZutat("Cream of Coconut","15"));
        liste.add(createZutat("Grenadine","16"));

        return liste;
    }


    // toString methode liefert Namen einer Zutat
    public String toString() {
        return ""+ zutat_name +  "\n";
    }

}
