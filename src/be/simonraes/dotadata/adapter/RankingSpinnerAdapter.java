package be.simonraes.dotadata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.util.AnimateFirstDisplayListenerToo;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.HeroList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Simon Raes on 19/04/2014.
 */
public class RankingSpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {

    private Context context;
    private String[] content;

    public RankingSpinnerAdapter(Context ctx, String[] content) {
        context = ctx;
        this.content = content;
    }

    @Override
    public int getCount() {
        return content.length;
    }

    @Override
    public Object getItem(int i) {
        return content[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spinner_text_item, null);
        }

        TextView txtDropdown = (TextView) convertView.findViewById(R.id.txtSpinnerItem);

        txtDropdown.setPadding(10, 10, 10, 10);
        txtDropdown.setText(content[i]);

        return convertView;
    }
}
