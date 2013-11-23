package com.burstingbrains.rapmap.util;

import android.app.Activity;
import android.content.Intent;

import com.burstingbrains.rapmap.R;

public class ThemeUtils {

	public enum Theme{ 
		THEME_SADCROW,
		THEME_DREAMMAGNET,
		THEME_CIRCLINGSUN,
		THEME_ANTIQUEBIRD,
		THEME_MIDNIGHT,
		THEME_ROMANTIC;

		public Theme getNext() {
			if(this.ordinal() < Theme.values().length - 1){
				return Theme.values()[this.ordinal() + 1];   
			}
			else{
				return Theme.values()[0];
			}
		}
	};
	private static Theme savedTheme = Theme.THEME_SADCROW;

	public static void changeToTheme(Activity activity, Theme theme) {
		savedTheme = theme;
		activity.finish();

		activity.startActivity(new Intent(activity, activity.getClass()));
	}

	public static void onActivityCreateSetTheme(Activity activity) {        
		switch (savedTheme) {
		case THEME_SADCROW:
			activity.setTheme(R.style.theme_sadcrow);
			break;
		case THEME_DREAMMAGNET:
			activity.setTheme(R.style.theme_dreammagnet);
			break;
		case THEME_CIRCLINGSUN:
			activity.setTheme(R.style.theme_circlingsun);
			break;
		case THEME_ANTIQUEBIRD:
			activity.setTheme(R.style.theme_antiquebird);
			break;
		case THEME_MIDNIGHT:
			activity.setTheme(R.style.theme_midnight);
			break;
		case THEME_ROMANTIC:
			activity.setTheme(R.style.theme_romantic);
			break;
		default:
			break;
		}
	}

	public static void toggleTheme(Activity activity) {
		changeToTheme(activity, savedTheme.getNext());

	}
}
