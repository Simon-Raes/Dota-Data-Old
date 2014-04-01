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
        View view = convertView;
        final ViewHolder viewholder;

        View dividerView;
        TextView dividerTitle;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //create viewholder if needed
        if (convertView == null) {
            view = inflater.inflate(R.layout.drawer_list_item, parent, false);
            viewholder = new ViewHolder();
            viewholder.txtTitle = (TextView) view.findViewById(R.id.txtDrawerItem);
            view.setTag(viewholder);
        } else {
            viewholder = (ViewHolder) view.getTag();
        }

        //return list item or divider
        if (titles[position].startsWith("divider ")) {
            dividerView = inflater.inflate(R.layout.drawer_list_divider, parent, false);
            dividerTitle = (TextView) dividerView.findViewById(R.id.txtDividerTitle);
            dividerTitle.setText(titles[position].replace("divider ", ""));
            return dividerView;
        } else {
            viewholder.txtTitle.setText(titles[position]);
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

    private class ViewHolder {
        public TextView txtTitle;
    }
}
