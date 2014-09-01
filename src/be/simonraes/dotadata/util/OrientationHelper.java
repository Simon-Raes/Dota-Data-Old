package be.simonraes.dotadata.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Created by Simon on 25/02/14.
 */
public class OrientationHelper {

    public static int getScreenOrientation(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if (width == height) {
            orientation = Configuration.ORIENTATION_SQUARE;
        } else {
            if (width < height) {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            } else {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }
        return orientation;
    }

    public static void lockOrientation(Activity activity) {
        int orientation = activity.getRequestedOrientation();
        int rotation = ((WindowManager) activity.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();


        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            default:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
        }

        activity.setRequestedOrientation(orientation);


        //code that might work in case nexus 10 gives problems (might just be an emulator issue)

//        switch (activity.getResources().getConfiguration().orientation){
//            case Configuration.ORIENTATION_PORTRAIT:
//                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
//                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                } else {
//                    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//                    if(rotation == android.view.Surface.ROTATION_90|| rotation == android.view.Surface.ROTATION_180){
//                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
//                    } else {
//                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                    }
//                }
//                break;
//
//            case Configuration.ORIENTATION_LANDSCAPE:
//                if(android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.FROYO){
//                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                } else {
//                    int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//                    if(rotation == android.view.Surface.ROTATION_0 || rotation == android.view.Surface.ROTATION_90){
//                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                    } else {
//                        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
//                    }
//                }
//                break;
//        }


    }

    public static void unlockOrientation(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

}
