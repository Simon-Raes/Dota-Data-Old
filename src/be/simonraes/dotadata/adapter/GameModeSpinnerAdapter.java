package be.simonraes.dotadata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.GameModes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Simon on 23/03/14.
 */
public class GameModeSpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {

    Context context;
    ArrayList<String> names;
    ArrayList<String> keys;

    HashMap<String, String> thisContent;

    public GameModeSpinnerAdapter(Context ctx, HashMap<String, String> content) {
        context = ctx;
        //sort on values
        thisContent = Conversions.sortHashMapByValues(content);
        //remove unneeded gamemodes

        content.remove("0"); //unknown
        content.remove("6"); //intro
        content.remove("10"); //tutorial
        //content.remove("14"); //Compendium
        content.remove("15"); //custom gamemode
        content.remove("17"); //balanced draft todo: remove this line when this gamemode gets released

        names = new ArrayList<String>(thisContent.values());
        names.add(0, "Any gamemode");
        keys = new ArrayList<String>(thisContent.keySet());
        keys.add(0, "-1");
    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public String getItem(int index) {
        return names.get(index);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    public String getIDForPosition(int position) {
        return keys.get(position);
    }

    @Override
    /**Returns row for selected item in gamemode spinner (with an abbreviated name)*/
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.gamemode_picker_row_selected, null);
        }

        TextView txtGameMode = (TextView) convertView.findViewById(R.id.txtPickerGameMode);
        txtGameMode.setText(GameModes.getGameModeAbbreviation(keys.get(position)));
        if (names.get(position).equals("Any gamemode")) {
            txtGameMode.setText("Any");
        }


        return convertView;
    }

    @Override
    /**Returns row for gamemode spinner*/
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.gamemode_picker_row, null);
        }

        TextView txtGameMode = (TextView) convertView.findViewById(R.id.txtPickerGameMode);
        txtGameMode.setText(names.get(position));

        return convertView;
    }
}