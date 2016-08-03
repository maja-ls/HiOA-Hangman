package com.example.s198515.hangman_s198515;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


/**
 * Created by s198515.
 */
// Viser et dialogfragment om bekreftelse hvis spiller vil gi opp
public class ResignConfirm extends DialogFragment  {
    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        String tittel = getResources().getString(R.string.resignConf);

        Dialog d = getDialog();
        d.setTitle(tittel);

        LinearLayout l = new LinearLayout(getActivity());
        l.setGravity(Gravity.CENTER);

        Button yes = new Button(getActivity());
        yes.setText(getResources().getString(R.string.button_yes));
        yes.setOnClickListener(yesKnappelytter);

        Button no = new Button(getActivity());
        no.setText(getResources().getString(R.string.button_no));
        no.setOnClickListener(noKnappelytter);

        l.addView(yes);
        l.addView(no);

        return l;
    }

    private OnClickListener yesKnappelytter = new OnClickListener() {
        @Override
        public void onClick(View v) {

            try{
                ((onYesClick)getActivity()).onYesClick();
            }
            catch (ClassCastException cce){
            }

            dismiss();
        }
    };

    private OnClickListener noKnappelytter = new OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    public interface onYesClick{
        public void onYesClick();
    }

}

