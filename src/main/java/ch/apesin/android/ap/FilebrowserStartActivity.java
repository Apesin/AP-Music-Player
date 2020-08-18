/*
 * Copyright (C) 2017 Adrian Ulrich <adrian@apesin.ch>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>. 
 */

package ch.apesin.android.ap;

import android.os.Bundle;
import android.content.SharedPreferences;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.ArrayList;

public class FilebrowserStartActivity extends FolderPickerActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.filebrowser_start);

		// Make sure that we display the current selection
		File startPath = FileUtils.getFilesystemBrowseStart(this);
		setCurrentDir(startPath);

		RelativeLayout layout = new RelativeLayout(this);
		layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
		// Create the adView
		AdView adView = new AdView(this);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

		// Add the adView to it
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
			RelativeLayout.LayoutParams.WRAP_CONTENT,
			RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		adView.setLayoutParams(params);

		layout.addView(adView);

		setContentView(layout);
		MobileAds.initialize(this, "ca-app-pub-5253623051644719/2988596005");
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}


	@Override
	public void onFolderPicked(File directory, ArrayList<String> a, ArrayList<String> b) {
		SharedPreferences.Editor editor = SharedPrefHelper.getSettings(this).edit();
		editor.putString(PrefKeys.FILESYSTEM_BROWSE_START, directory.getAbsolutePath());
		editor.apply();
		finish();
	}

}
