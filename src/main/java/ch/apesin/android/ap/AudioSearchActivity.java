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

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AudioSearchActivity extends PlaybackActivity {
	/**
	 * The AsyncTask used to perform the query.
	 */
	private AudioSearchWorker mWorker;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

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

		Intent intent = getIntent();
		String action = (intent == null ? null : intent.getAction());
		if (action == null || !action.equals(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)) {
			finish();
			return;
		}

		if (PermissionRequestActivity.requestPermissions(this, intent)) {
			finish();
			return;
		}

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Fixme: add a progress dialog.

		String query = intent.getExtras().getString(SearchManager.QUERY);
		mWorker = new AudioSearchWorker();
		mWorker.execute(query);
	}

	/**
	 * Called by AudioSearchWorker after the search completed.
	 */
	private void onSearchCompleted() {
		finish();
	}

	/**
	 * Async worker class used to perform the search
	 */
	private class AudioSearchWorker extends AsyncTask<String, Void, Void> {
		@Override
		protected Void doInBackground(String... search) {
			Context ctx = getApplicationContext();
			MediaAdapter adapter = new MediaAdapter(ctx, MediaUtils.TYPE_SONG, null, null);
			adapter.setFilter(search[0]);

			QueryTask query = adapter.buildSongQuery(Song.FILLED_PROJECTION);
			query.mode = SongTimeline.MODE_PLAY;

			PlaybackService service = PlaybackService.get(ctx);
			service.pause();
			service.setShuffleMode(SongTimeline.SHUFFLE_ALBUMS);
			service.emptyQueue();
			service.addSongs(query);
			if (service.getTimelineLength() > 0) {
				service.play();
			}
			return null;
		}
		@Override
		protected void onPostExecute(Void nil) {
			onSearchCompleted();
		}
	}

}
