package com.example.s198515.hangman_s198515;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.util.Log;
import java.util.Locale;


/**
 * Created by s198515.
 */
public class StartAktivitet extends AppCompatActivity {

    //private static final String MYDEBUG = "Start";
    private static String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startskjerm);

        //Log.d(MYDEBUG, "\n********\n\n Er i onCreate\n\n *******");

        if (savedInstanceState != null) {
            language = savedInstanceState.getString("language");

            updateLanguage(this, language);
            oppdaterSkjerm();

        }

    }

    // Oppdaterer språket i aktiviteten
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



    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("language", language);

        super.onSaveInstanceState(outState);
    }


    // Starter en ny spillaktivitet
    public void button_newgame(View v) {

        Intent i = new Intent(this, SpillAktivitet.class);
        i.putExtra("languageFraHoved", language);
        startActivity(i);
    }

    // Viser reglene
    public void button_rules(View v) {
        RuleDialog ruleDialog = new RuleDialog();
        ruleDialog.show(getFragmentManager(), "Dialog");
    }

    // Endrer språk til engelsk
    public void button_english(View v) {
        String newLanguage = "en";
        updateLanguage(this, newLanguage);
        oppdaterSkjerm();
    }

    // Endrer språk til norsk
    public void button_norsk(View v) {
        String newLanguage = "nb";
        updateLanguage(this, newLanguage);
        oppdaterSkjerm();
    }

    // Oppdaterer skjermbildet ved språkendring
    private void oppdaterSkjerm() {
        finish();
        startActivity(getIntent());
    }

    // Avslutter applikasjonen
    public void button_exit(View v) {
        finish();
    }


}
