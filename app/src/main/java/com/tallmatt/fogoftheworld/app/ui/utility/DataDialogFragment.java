package com.tallmatt.fogoftheworld.app.ui.utility;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.tallmatt.fogoftheworld.app.MaskTileProvider;
import com.tallmatt.fogoftheworld.app.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by michaudm3 on 5/28/2014.
 */
public class DataDialogFragment extends DialogFragment {

    ArrayList<LatLng> points;

    public DataDialogFragment(ArrayList<LatLng> points) {
        this.points = points;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View root = inflater.inflate(R.layout.dialog_fragment_data, null);

        LinearLayout values = (LinearLayout) root.findViewById(R.id.data_values);

        addTextLine("Total number of points: "+(points.size()), "Total number of points:", values  );
        addTextLine("Very much estimated percentage of the world revealed: "+(
                MaskTileProvider.radiusConstant/196900000.0*points.size()*100.0+
                        "%"
        ), "Very much estimated percentage of the world revealed:", values);

        final Button cancelButton = (Button) root.findViewById(R.id.data_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        builder.setView(root);

        return builder.create();
    }

    private void addTextLine(String text, String title, ViewGroup root) {
        TextView view = new TextView(getActivity());

        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(0, 0, 0));
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);

        sb.setSpan(fcs, 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(bss, 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        view.setText(sb);
        root.addView(view);
    }
}
