package com.mridang.cellinfo;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

/*
 * This class is the activity which contains the preferences
 */
@SuppressWarnings("deprecation")
public class WidgetSettings extends PreferenceActivity {

	/*
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getActionBar().setIcon(R.drawable.ic_dashclock);
		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	/*
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {

		@Override
		public boolean onPreferenceChange(Preference prePreference, Object objValue) {

			Integer intIndex = ((ListPreference) prePreference).findIndexOfValue(objValue.toString());
			prePreference.setSummary(((ListPreference) prePreference).getEntries()[intIndex]);
			return true;

		}

	};

	/*
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary is updated to reflect the
	 * value.
	 */
	private static void bindPreferenceSummaryToValue(Preference prePreference) {

		prePreference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		sBindPreferenceSummaryToValueListener.onPreferenceChange(prePreference, PreferenceManager
				.getDefaultSharedPreferences(prePreference.getContext()).getString(prePreference.getKey(), ""));

	}

	/*
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {

		super.onPostCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);

	}

}