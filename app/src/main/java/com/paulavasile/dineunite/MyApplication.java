package com.paulavasile.dineunite;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.paulavasile.dineunite.Global.BackgroundProcessor;
import com.paulavasile.dineunite.Global.DatabaseHelper;
import com.paulavasile.dineunite.Global.GlobalHelper;

import android.app.Application;
import android.graphics.Typeface;
import android.support.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication {


	Typeface latoRegularFont;

	// for Cashing Bitmap
	protected ImageLoader imageLoader;

	@Override
	public void onCreate() {
		super.onCreate();

		// load ImageLoader
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));

		DatabaseHelper.getInstance(getApplicationContext());

		BackgroundProcessor.getBackgroundProcessor(getApplicationContext()).start();

		//Load Social Tables and Restaruant Table
	}


	@Override
	public void onTerminate() {
		super.onTerminate();
		imageLoader.clearDiskCache();
		imageLoader.clearMemoryCache();
		BackgroundProcessor.DestroybackgroundProcessor();
	}

	// return ImageLoader object
	public ImageLoader getImageLoader() {
		return imageLoader;
	}

	public Typeface getLatoRegularFont() {
		return Typeface.DEFAULT;
/*		if (latoRegularFont == null) {
			latoRegularFont = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");
		}
		return latoRegularFont;*/
	}

}
