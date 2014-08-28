package be.simonraes.dotadata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import be.simonraes.dotadata.R;
import be.simonraes.dotadata.util.ImageLoadListener;
import be.simonraes.dotadata.util.Conversions;
import be.simonraes.dotadata.util.HeroList;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Simon on 23/03/14.
 */
public class HeroSpinnerAdapter extends BaseAdapter implements android.widget.SpinnerAdapter {
    private Context context;

    private ArrayList<String> names;
    private ArrayList<String> keys;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private ImageLoadingListener animateFirstListener;

    private HashMap<String, String> spinnerContent;

    public HeroSpinnerAdapter(Context ctx, HashMap<String, String> content) {
        context = ctx;

        spinnerContent = Conversions.sortHashMapByValues(content);

        names = new ArrayList<String>(spinnerContent.values());
        names.add(0, "Any hero");
        keys = new ArrayList<String>(spinnerContent.keySet());
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

    public int getPositionForId(String id) {
        return keys.indexOf(id);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.hero_picker_row_selected, null);

            imageLoader = ImageLoader.getInstance();
            animateFirstListener = new ImageLoadListener();
            options = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true)
                    .cacheInMemory(true)
                    .showImageOnLoading(R.drawable.hero_sb_loading)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();
        }

        ImageView imgHero = (ImageView) convertView.findViewById(R.id.imgPickerHero);

        imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(keys.get(position)) + "_sb.png", imgHero, options, animateFirstListener);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.hero_picker_row, null);

            imageLoader = ImageLoader.getInstance();
            animateFirstListener = new ImageLoadListener();
            options = new DisplayImageOptions.Builder()
                    .resetViewBeforeLoading(true)
                    .cacheInMemory(true)
                    .showImageOnLoading(R.drawable.hero_sb_loading)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();
        }

        ImageView imgHero = (ImageView) convertView.findViewById(R.id.imgPickerHero);
        TextView txtHero = (TextView) convertView.findViewById(R.id.txtPickerHero);
        txtHero.setText(names.get(position));

        imageLoader.displayImage("http://cdn.dota2.com/apps/dota2/images/heroes/" + HeroList.getHeroImageName(keys.get(position)) + "_sb.png", imgHero, options, animateFirstListener);

        return convertView;
    }


}