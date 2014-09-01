package be.simonraes.dotadata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import be.simonraes.dotadata.R;

/**
 * Created by Simon on 6/02/14.
 */
public class DrawerAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] titles;

    public DrawerAdapter(Context context, String[] titles) {
        super(context, R.layout.drawer_list_item, titles);
        this.context = context;
        this.titles = titles;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //return list item or divider
        if (titles[position].startsWith("divider ")) {
            View dividerView = inflater.inflate(R.layout.drawer_list_divider, parent, false);
            TextView dividerTitle = (TextView) dividerView.findViewById(R.id.txtDividerTitle);
            dividerTitle.setText(titles[position].replace("divider ", ""));
            return dividerView;
        } else {
            View view = inflater.inflate(R.layout.drawer_list_item, parent, false);
            TextView dividerTitle = (TextView) view.findViewById(R.id.txtDrawerItem);
            dividerTitle.setText(titles[position]);
            return view;
        }
    }

    //make headers non-clickable
    @Override
    public boolean isEnabled(int position) {
        return !titles[position].startsWith("divider");
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }


}
