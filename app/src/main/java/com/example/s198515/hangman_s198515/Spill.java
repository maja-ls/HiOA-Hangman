package com.example.s198515.hangman_s198515;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by s198515.
 */
// Denne klassen har dataene om det nåværende spillet, og er ansvarlig for spill-logikken
public class Spill {

    //lagres ikke
    private static final String MYDEBUG = "Spill.JAVA";
    private static int numberOfTries = 11;

    //lagres
    private ArrayList<String> wordsToDo;
    private ArrayList<String> finishedWords;

    private String currentWordString;
    private char[] currentWord;
    private char[] currentDisplayNoSpace; // Hva som vises for spilleren nå
    private int antallBokstaver;
    private int antallRiktige;
    private int antallFeil;

    private int wins;
    private int losses;


    // Konstruktør for nytt spill
    public Spill (String[] ordliste) {
        wordsToDo = new ArrayList<>(Arrays.asList(ordliste));
        finishedWords = new ArrayList<>();

        wins = 0;
        losses = 0;

        getNewWord();
    }


    // Konstruktør for lastet spill
    public Spill (Bundle currentState) {

        wordsToDo = currentState.getStringArrayList("wordsToDo");
        finishedWords = currentState.getStringArrayList("finishedWords");

        currentWordString = currentState.getString("currentWordString");

        currentWord = currentState.getCharArray("currentWord");
        currentDisplayNoSpace = currentState.getCharArray("currentDisplayNoSpace");

        antallBokstaver = currentState.getInt("antallBokstaver");
        wins = currentState.getInt("wins");
        losses= currentState.getInt("losses");

        antallFeil = 0;
        antallRiktige = 0;
    }

    // Tar imot bundle fra Spillaktivitet og lagrer nåværende innstillinger der.
    // Sender så bundlen tilbake
    public Bundle save(Bundle b) {

        b.putStringArrayList("wordsToDo", wordsToDo);
        b.putStringArrayList("finishedWords", finishedWords);


        b.putString("currentWordString", currentWordString);

        b.putCharArray("currentWord", currentWord);
        b.putCharArray("currentDisplayNoSpace", currentDisplayNoSpace);

        b.putInt("antallBokstaver", antallBokstaver);
        b.putInt("wins", wins);
        b.putInt("losses", losses);

        b.putInt("antallFeil", antallFeil);

        return b;
    }


    // Henter et nytt ubrukt ord og nullstiller innstillingene
    private void getNewWord() {
        Random rand = new Random();
        int  n = rand.nextInt(wordsToDo.size());

        String w = wordsToDo.get(n);

        currentWordString = w;
        currentWord = w.toCharArray();

        //Lager hintet som spilleren vil se
        String ny = "";
        for (int i = 0; i < currentWord.length; i++) {
            ny += "_";
        }

        currentDisplayNoSpace = ny.toCharArray();

        antallFeil = 0;
        antallRiktige = 0;
        antallBokstaver = currentWord.length;

        finishedWords.add(w); // legger til ordet i lista over ord som er gjort
        wordsToDo.remove(n); // fjerner ordet fra liste over ord som ikke er gjort slik at det ikke kommer igjen


        if (wordsToDo.isEmpty()) { // nullstiller hvilke ord som er gjort dersom man har gått gjennom alle sammen
            wordsToDo = finishedWords;
            finishedWords = new ArrayList<>();
        }


        // DEBUG
        Log.d(MYDEBUG, " \n********\n\n TALL TILFELDIG GENERERT = " + n + " \n\n *******");
        Log.d(MYDEBUG, " \n********\n\n ORD VALGT = " + w + " \n\n *******");

        Log.d(MYDEBUG, " \n********\n\n ORD VISES = " + ny + " \n\n *******");
        Log.d(MYDEBUG, " \n********\n\n antallbokstaer = " + antallBokstaver + " \n\n *******");
    }



    // Tar imot en bokstav og sjekker om den er riktig eller feil
    public boolean try_letter(String l) {

        for (int i = 0; i < currentWord.length; i++) {
            // Sjekker om bokstaven det er gjettet på finnes i ordet
            if (String.valueOf(currentWord[i]).equalsIgnoreCase(l)) {

                for (int j = 0; j < currentWord.length; j++) {
                    // Synliggjør alle forekomstene av bokstaven i ordet
                    if (String.valueOf(currentWord[j]).equalsIgnoreCase(l)) {

                        antallRiktige++;
                        currentDisplayNoSpace[j] = currentWord[j];
                    }
                }

                return true;
            }
        }

        antallFeil++;
        return false;
    }


    // Padder hintet som skal vises for spilleren og returnerer det til spillaktiviteten
    public String displayOrdForSpiller() {

        String s = "";

        for (int i = 0; i < currentDisplayNoSpace.length; i++) {
            s += String.valueOf(currentDisplayNoSpace[i]);
            if (i < (currentDisplayNoSpace.length-1))
                s += " ";
        }

        s = s.toUpperCase();
        return s;
    }


    // Sjekker om spilleren har vunnet eller tapt
    public boolean sjekkFerdig() {

        if (antallRiktige == antallBokstaver) {
            wins++;
            return true;
        }

        else if (antallFeil == numberOfTries) {
            losses++;
            return true;
        }

        return false;
    }

    // Metode for å starte et nytt spill
    public void nyttSpill() {
        getNewWord();
    }

    // Brukes når spilleren gir opp for å vise siste bakgrunnsbilde
    public void giveUp() {
        antallFeil = numberOfTries;
        sjekkFerdig();
    }

    public int getAntallFeil() {
        return antallFeil;
    }

    public void setAntallFeil(int n) {
        antallFeil = n;
    }


    public int getLosses() {
        return losses;
    }

    public int getWins() {
        return wins;
    }

    public String getCurrentWordString() {
        return currentWordString;
    }

}