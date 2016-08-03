package com.example.s198515.hangman_s198515;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by s198515.
 */
// Denne aktiviteten er ansvarlig for det spilleren ser av spillet. Den samhandler med Spill.java
public class SpillAktivitet extends AppCompatActivity implements ResignConfirm.onYesClick {

    private static final String MYDEBUG = "SpillAktivitet";
    private static String language;

    private Spill spill;
    private Drawable riktigknapp; // definerer bakgrunnen for knapper som var feil
    private Drawable feilknapp; // definerer bakgrunnen for knapper som var riktige

    private String[] allwords; // inneholder alle ordene
    private String[] letters; // alle bokstavene man kan gjette på
    private ArrayList<String> guessedLetters; // bokstavene spilleren har gjettet på allerede

    private boolean spilletErAvsluttet = false;
    private boolean victory;

    // Registrerer hvis spilleren trykke på ja i gi opp dialog
    @Override
    public void onYesClick()
    {
        spill.giveUp();
        oppdaterBakgrunn();
        spillSlutt(false);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spillskjerm);

        riktigknapp = ContextCompat.getDrawable(this, R.drawable.correctbutton);
        feilknapp = ContextCompat.getDrawable(this, R.drawable.wrongbutton);


        if (savedInstanceState != null) {
            // Oppdaterer språket
            language = savedInstanceState.getString("language");
            updateLanguage(this, language);


            // Henter tidligere data/status
            letters = savedInstanceState.getStringArray("letters");
            spilletErAvsluttet = savedInstanceState.getBoolean("ferdigSpill");
            guessedLetters = savedInstanceState.getStringArrayList("guessedLetters");

            spill = new Spill(savedInstanceState);

            if (spilletErAvsluttet) {
                // Sørger for at spilleren kommer rett til sluttskjerm
                victory =  savedInstanceState.getBoolean("victory");
                spill.setAntallFeil(savedInstanceState.getInt("antallFeil")); // sørger for at bakgrunnen også vises selv om spillet er avsluttet
                spillSlutt(victory);
            }
            else {
                // Lager knappene med innstillingene de hadde
                makeButtons(guessedLetters);
            }
        } else {
            // Initierer data hvis det er et helt nytt spill
            allwords = getResources().getStringArray(R.array.words);
            letters = getResources().getStringArray(R.array.chars);

            Log.d(MYDEBUG, "\n********\n\n SAVEDINSTANCESTATE ER NULL\n\n *******");
            language = getIntent().getStringExtra("languageFraHoved");
            spill = new Spill(allwords);
            guessedLetters = new ArrayList<>();
            makeButtons(null);
        }

        oppdaterScore();
        oppdaterBakgrunn();
        oppdaterHint();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // Sender bundlen til spillet for å lagre verdiene der
        outState = spill.save(outState);

        outState.putString("language", language);
        outState.putBoolean("ferdigSpill", spilletErAvsluttet);
        outState.putBoolean("victory", victory);
        outState.putStringArray("letters", letters);
        outState.putStringArrayList("guessedLetters", guessedLetters);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_spillaktivitet, menu);

        //Skrur av tilbake-pila
        ActionBar actionBar = getActionBar();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(false);      // Disable the button
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); // Remove the left caret
            getSupportActionBar().setDisplayShowHomeEnabled(false); // Remove the icon
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            
            case R.id.option_resign:
                // Viser dialog om å gi opp
                if (!spilletErAvsluttet) {
                    ResignConfirm conf = new ResignConfirm();
                    conf.show(getFragmentManager(),"Dialog");
                }
                break;
            
            case R.id.option_rules:
                // Viser reglene
                RuleDialog ruleDialog = new RuleDialog();
                ruleDialog.show(getFragmentManager(),"Dialog");
                break;
            
        }
        return super.onOptionsItemSelected(item);
    }


    // Skrur av funksjonen til tilbakeknappen mens man er i spillet
    @Override
    public void onBackPressed() {

    }

    // Endrer språket i aktiviteten
    private static void updateLanguage(Context ctx, String l)
    {
        language = l;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String lang = prefs.getString("locale_override", l);


        Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang))
            cfg.locale = new Locale(lang);
        else
            cfg.locale = Locale.getDefault();

        ctx.getResources().updateConfiguration(cfg, null);
    }



    // Oppdaterer hintet som vises for spilleren
    private void oppdaterHint() {
        TextView tv = (TextView) findViewById(R.id.hint);
        tv.setText(spill.displayOrdForSpiller());
    }

    // Oppdaterer scoren som vises på skjermen
    private void oppdaterScore() {
        int wins = spill.getWins();
        int losses = spill.getLosses();

        TextView tv = (TextView) findViewById(R.id.score);
        tv.setText("W:" + wins + " / L:" + losses);
    }

    // Denne metoden oppdaterer bakgrunnen utfra hvor mange feil spilleren har gjort
    private void oppdaterBakgrunn() {
        RelativeLayout bak = (RelativeLayout)findViewById(R.id.bakgrunn);
        int feil = spill.getAntallFeil();

        switch (feil) {
            case 0:
                bak.setBackgroundResource(R.drawable.back0);
                return;
            case 1:
                bak.setBackgroundResource(R.drawable.back1);
                return;
            case 2:
                bak.setBackgroundResource(R.drawable.back2);
                return;
            case 3:
                bak.setBackgroundResource(R.drawable.back3);
                return;
            case 4:
                bak.setBackgroundResource(R.drawable.back4);
                return;
            case 5:
                bak.setBackgroundResource(R.drawable.back5);
                return;
            case 6:
                bak.setBackgroundResource(R.drawable.back6);
                return;
            case 7:
                bak.setBackgroundResource(R.drawable.back7);
                return;
            case 8:
                bak.setBackgroundResource(R.drawable.back8);
                return;
            case 9:
                bak.setBackgroundResource(R.drawable.back9);
                return;
            case 10:
                bak.setBackgroundResource(R.drawable.back10);
                return;
            case 11:
                bak.setBackgroundResource(R.drawable.back11);
                return;
        }
    }


    // Lager knappene og knytter de til bokstavKnappelytter
    // Parameter er ArrayList med tidligere trykte knapper, dersom det er noen
    private void makeButtons(ArrayList<String> buttons) {

        // Setter layout for knappene
        FrameLayout layout = new FrameLayout(this);
        layout.setLayoutParams(new FrameLayout.LayoutParams(59, 65, 1));
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) layout.getLayoutParams();
        params.setMargins(20, 0, 20, 0);


        // Henter layoutene som knappene skal settes inn i
        LinearLayout l1 = (LinearLayout) findViewById(R.id.bL1);
        LinearLayout l2 = (LinearLayout) findViewById(R.id.bL2);
        LinearLayout l3 = (LinearLayout) findViewById(R.id.bL3);
        LinearLayout l4 = (LinearLayout) findViewById(R.id.bL4);
        LinearLayout l5 = (LinearLayout) findViewById(R.id.bL5);

        // Henter bakgrunn for knappene
        Drawable d = ContextCompat.getDrawable(this, R.drawable.roundedbutton);

        // Oppretter knapper
        for (int i=0; i < letters.length; i++) {
            Button knapp = new Button(this);

            // Sjekker om knappen er trykket inn tidligere dersom knapper er trykket tidligere
            if (buttons != null && !buttons.isEmpty()) {

                boolean brukt = false;

                for (String s : buttons) {
                    if(s.equalsIgnoreCase(letters[i])){
                        brukt = true;
                        break;
                    }
                }
                if (brukt) {
                    if (spill.try_letter(letters[i])) {
                        knapp.setBackground(riktigknapp);
                    }
                    else {
                        knapp.setBackground(feilknapp);
                    }
                    knapp.setEnabled(false);
                }
                else {
                    knapp.setBackground(d);
                }
            }
            else {
                knapp.setBackground(d);
            }

            knapp.setLayoutParams(params);
            knapp.setText(letters[i]);
            knapp.setOnClickListener(bokstavKnappelytter);

            // Legger knappene til passende layout
            if (i <= 6) {
                l1.addView(knapp);
                //Log.d(MYDEBUG, "\n********\n\n lager knapp "+i+"\n\n *******");
            }
            else if (i <= 13 ) {
                l2.addView(knapp);
                //Log.d(MYDEBUG, "\n********\n\n lager knapp " + i + "\n\n *******");
            }
            else if (i <= 20 ) {
                l3.addView(knapp);
                //Log.d(MYDEBUG, "\n********\n\n lager knapp " + i + "\n\n *******");
            }
            else if (i <= 25){
                l4.addView(knapp);
                //Log.d(MYDEBUG, "\n********\n\n lager knapp " + i + "\n\n *******");
            }
            else {
                l5.addView(knapp);
            }
        }
    }


    // Denne metoden kalles når spilleren har gitt opp, fått alle riktige, eller er hengt
    // Parameter bestemmer om spilleren har vunnet eller tapt
    private void spillSlutt(boolean vunnet) {
        spilletErAvsluttet = true;

        oppdaterScore();

        // Fjerner spillknapper
        LinearLayout l1 = (LinearLayout) findViewById(R.id.bL1);
        LinearLayout l2 = (LinearLayout) findViewById(R.id.bL2);
        LinearLayout l3 = (LinearLayout) findViewById(R.id.bL3);
        LinearLayout l4 = (LinearLayout) findViewById(R.id.bL4);
        LinearLayout l5 = (LinearLayout) findViewById(R.id.bL5);
        l1.removeAllViews();
        l2.removeAllViews();
        l3.removeAllViews();
        l4.removeAllViews();
        l5.removeAllViews();

        TextView tv = new TextView(this);

        String t;
        if (vunnet) {
            victory = true;
            t = getResources().getString(R.string.win);
        }
        else {
            victory = false;
            t = getResources().getString(R.string.loss);

            TextView tvShowWord = new TextView(this);
            tvShowWord.setText(getResources().getString(R.string.solution_text) + " '" + spill.getCurrentWordString() + "'");
            l4.addView(tvShowWord);
        }

        tv.setText(t);
        l1.addView(tv);

        Drawable d = ContextCompat.getDrawable(this, R.drawable.roundedbutton);

        Button yes = new Button(this);
        Button no = new Button(this);

        yes.setBackground(d);
        no.setBackground(d);

        yes.setText(getResources().getString(R.string.button_yes));
        no.setText(getResources().getString(R.string.button_no));


        yes.setOnClickListener(newgameKnappelytter);
        no.setOnClickListener(avsluttKnappelytter);
        l2.addView(yes);
        l3.addView(no);

    }

    ///// KNAPPELYTTERE ///////////

    //Knappelytter som lytter på bokstavknappene
    private OnClickListener bokstavKnappelytter = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //Log.d(MYDEBUG, " \n********\n\n Knapp med id = " + v.getId() + " er trykket\n\n *******");

            Button b = (Button) v;
            b.setEnabled(false);

            String bokstav = b.getText().toString();

            guessedLetters.add(bokstav);

            boolean riktig = spill.try_letter(bokstav);

            if (riktig) {
                b.setBackground(riktigknapp);

                oppdaterHint();

                boolean ferdig = spill.sjekkFerdig();
                if (ferdig) {
                    spillSlutt(true);
                }

            }
            else {
                b.setBackground(feilknapp);
                oppdaterBakgrunn();

                boolean ferdig = spill.sjekkFerdig();
                if (ferdig) {
                    spillSlutt(false);
                }

            }
        }
    };

    //Knappelytter som lytter på knappen for nytt spill
    private OnClickListener newgameKnappelytter = new OnClickListener() {
        @Override
        public void onClick(View v) {

            LinearLayout l1 = (LinearLayout) findViewById(R.id.bL1);
            LinearLayout l2 = (LinearLayout) findViewById(R.id.bL2);
            LinearLayout l3 = (LinearLayout) findViewById(R.id.bL3);
            LinearLayout l4 = (LinearLayout) findViewById(R.id.bL4);
            LinearLayout l5 = (LinearLayout) findViewById(R.id.bL5);
            l1.removeAllViews();
            l2.removeAllViews();
            l3.removeAllViews();
            l4.removeAllViews();
            l5.removeAllViews();

            spill.nyttSpill();
            spilletErAvsluttet = false;
            guessedLetters = new ArrayList<>();
            oppdaterBakgrunn();
            oppdaterHint();
            makeButtons(null);
        }
    };

    //Knappelytter som lytter på knappen for avslutt
    private OnClickListener avsluttKnappelytter = new OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };


    /* Metode som har blitt brukt for testing sammen med log
    public void vis_slett(String t) {
        TextView tekst = (TextView)findViewById(R.id.score);

        tekst.setText(t);
    }*/
}
