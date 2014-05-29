package com.tallmatt.fogoftheworld.app.ui.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.tallmatt.fogoftheworld.app.R;

/**
 * Created by michaudm3 on 5/27/2014.
 */
public class AreYouSureDialogFragment extends DialogFragment {
    View.OnClickListener yesClickListener;
    View.OnClickListener noClickListener;

    public AreYouSureDialogFragment() {};

    /**
     * Set custom click listeners for the yes and no buttons. If null, then it will use the default listeners (both buttons dismiss the dialog).
     * @param yesClickListener
     * @param noClickListener
     */
    public AreYouSureDialogFragment(View.OnClickListener yesClickListener, View.OnClickListener noClickListener) {
        if(yesClickListener!=null) {
            this.yesClickListener = yesClickListener;
        }
        if(noClickListener!=null) {
            this.noClickListener = noClickListener;
        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View root = inflater.inflate(R.layout.dialog_fragment_are_you_sure, null);

        final Button noButton = (Button) root.findViewById(R.id.are_you_sure_no_button);
        final Button yesButton = (Button) root.findViewById(R.id.are_you_sure_yes_button);
        SeekBar seekBar = (SeekBar) root.findViewById(R.id.are_you_sure_seek);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress == 100) {
                    yesButton.setEnabled(true);
                    yesButton.setTextColor(Color.BLACK);
                } else {
                    yesButton.setEnabled(false);
                    yesButton.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if(yesClickListener==null) {
            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } else {
            yesButton.setOnClickListener(yesClickListener);
        }
        if(noClickListener==null) {
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        } else {
            noButton.setOnClickListener(noClickListener);
        }


        builder.setView(root);


        return builder.create();
    }
}
