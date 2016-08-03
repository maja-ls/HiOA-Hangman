package com.example.s198515.hangman_s198515;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


/**
 * Created by s198515.
 */
// Viser dialogfragment med reglene
public class RuleDialog extends DialogFragment  {
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        String tittel = getResources().getString(R.string.button_rules);
        String regler = getResources().getString(R.string.the_rules);
        Dialog d = getDialog();
        d.setTitle(tittel);

        ScrollView s = new ScrollView(getActivity());
        LinearLayout l = new LinearLayout(getActivity());
        l.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(getActivity());
        tv.setText(regler);


        Button b = new Button(getActivity());
        b.setText("Ok");
        b.setOnClickListener(knappelytter);

        l.addView(tv);
        l.addView(b);
        s.addView(l);
        return s;
    }

    private OnClickListener knappelytter = new OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };


}

