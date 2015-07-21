package com.teamvollrausch.sh.bismorgen;

import java.util.ArrayList;
import java.util.List;


/**
 * Objekt wird benoetig, um einen Cocktail persistent in der datenbank zu speichern.
 * Jeder Cocktail besitzt sowohl einen Namen, eine Bilddatei, als auch einen Primary Key.(Nr.)
 * @author Hanan Fakhro
 * @author Sophia Eichhorn
 * @version 1.0
 */

public class Cocktail {

    // Name eines Cocktails
    private String name;
    // Key eines Cocktails
    private String nr;
    // Bilddatei eines Cocktails
    private byte[] image;

    // Get- und Setter
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getNr() {
        return nr;
    }
    public void setNr(String nr) {
        this.nr = nr;
    }


    public void setImage(byte[] image) {
        this.image = image;
    }
    public byte[] getImage() {
        return this.image;
    }


    /*  Cocktail erstellen
     *  @param name Cocktailname
     *  @param nr Cocktail_id
     *  @return cocktail Cocktail ohne Bilddatei
     */
    public Cocktail createCocktail(String name, String nr) {
        Cocktail cocktail = new Cocktail();
        cocktail.setName(name);
        cocktail.setNr(nr);

        return cocktail;
    }

    /*  Einfügen aller vorhandenen Cocktails in eine Liste
     *  @return liste Eine Liste, die alle Cocktails beinhaltet
     */
    public List<Cocktail> ListOfAllCocktails() {

        // Erstellung einer Liste, die Objekte des Typs Cocktails beinhaltet
        List<Cocktail> liste = new ArrayList<Cocktail>();

        // Einfügen verschiedener Cocktails mit zugehoeriger id
        liste.add(createCocktail("Sex on the Beach","1"));
        liste.add(createCocktail("Tequila Sunrise","2"));
        liste.add(createCocktail("Long Island Ice Tea","3"));
        liste.add(createCocktail("Swimming Pool","4"));
        liste.add(createCocktail("Zombie","5"));
        liste.add(createCocktail("Planter's Punch","6"));
        liste.add(createCocktail("Cuba Libre","7"));
        liste.add(createCocktail("Pina Colada","8"));
        liste.add(createCocktail("Caipirinha","9"));
        liste.add(createCocktail("Virgin Colada","10"));
        liste.add(createCocktail("Safer Sex on the Beach","11"));
        liste.add(createCocktail("Cinderella","12"));

        return liste;
    }

    //toString MEthode liefert den Namen eines Cocktails
    public String toString() {
        return ""+ name + "\n";
    }
}