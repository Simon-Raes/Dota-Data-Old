package be.simonraes.dotadata.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Simon Raes on 8/07/2014.
 */
public class FillWidthImageView extends ImageView {
    public FillWidthImageView(Context context) {
        super(context);
        setScaleType(ScaleType.MATRIX);
    }

    public FillWidthImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FillWidthImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    public FillWidthImageView(Context context) {

//    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        super.setFrame(l, t, r, b);
        Matrix matrix = getImageMatrix();
        float scaleFactor = getWidth() / (float) getDrawable().getIntrinsicWidth();
        matrix.setScale(scaleFactor, scaleFactor, 0, 0);
        setImageMatrix(matrix);
        return super.setFrame(l, t, r, b);
    }
}
