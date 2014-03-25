package be.simonraes.dotadata.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import be.simonraes.dotadata.util.Conversions;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Simon on 23/03/14.
 */
public class GameModeSpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {

    Context context;
    ArrayList<String> names;
    ArrayList<String> keys;

    public GameModeSpinnerAdapter(Context ctx, HashMap<String, String> content) {
        context = ctx;
        //sort on values
        content = Conversions.sortHashMapByValues(content);
        //remove unneeded gamemodes

        content.remove("0"); //unknown
        content.remove("6"); //intro
        content.remove("10"); //tutorial
        //content.remove("14"); //Compendium
        content.remove("15"); //custom gamemode
        content.remove("17"); //balanced draft todo: remove this line when this gamemode gets released

        names = new ArrayList<String>(content.values());
        names.add(0, "Any gamemode");
        keys = new ArrayList<String>(content.keySet());
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
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView text = new TextView(context);
        text.setTextColor(Color.BLACK);
        text.setText(names.get(position));
        return text;
    }
}