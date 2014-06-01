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
import com.tallmatt.fogoftheworld.app.PointLatLng;
import com.tallmatt.fogoftheworld.app.R;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by michaudm3 on 5/28/2014.
 */
public class DataDialogFragment extends DialogFragment {
    private static final String POINTS_KEY = "POINTS";

    ArrayList<PointLatLng> points;

    public static DataDialogFragment newInstance(ArrayList<PointLatLng> points) {
        DataDialogFragment fragment = new DataDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(POINTS_KEY, points);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if(args!=null && args.containsKey(POINTS_KEY)) {
            points = (ArrayList<PointLatLng>) args.getSerializable(POINTS_KEY);
        } else {
            points = new ArrayList<PointLatLng>();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.dialog_fragment_data, null);
        LinearLayout values = (LinearLayout) root.findViewById(R.id.data_values);

        addTextLine("Total number of points: "+(points.size()), "Total number of points:", values  );
        /*
         * Area of each point, times the number of points, all over the total area of the planet times 100%
         * This is super estimated and I would love to look at doing percentages based on countries and cities.
         */
        addTextLine("Very much estimated percentage of the world revealed: "+(
                (Math.PI*Math.pow(MaskTileProvider.radiusConstant, 2))/196900000.0*points.size()*100.0+
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
        ForegroundColorSpan fcs = new ForegroundColorSpan(Color.BLACK);
        StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);

        sb.setSpan(fcs, 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        sb.setSpan(bss, 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

        view.setText(sb);
        view.setTextColor(Color.BLACK);
        root.addView(view);
    }
}
