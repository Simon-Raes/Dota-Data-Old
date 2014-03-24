package be.simonraes.dotadata.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import com.nostra13.universalimageloader.cache.disc.impl.FileCountLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Simon on 3/02/14.
 */
public class DotaDataApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initImageLoader(getApplicationContext());
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);

        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "UniversalImageLoader/Cache");

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                //.threadPriority(Thread.NORM_PRIORITY - 2)
                .threadPoolSize(10)
                .discCache(new FileCountLimitedDiscCache(cacheDir, new Md5FileNameGenerator(), 1000))
                .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                        //.writeDebugLogs()
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }
}