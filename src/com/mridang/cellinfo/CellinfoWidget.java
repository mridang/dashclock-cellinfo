package com.mridang.cellinfo;

import java.util.Locale;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.bugsense.trace.BugSenseHandler;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

/*
 * This class is the main class that provides the widget
 */
public class CellinfoWidget extends DashClockExtension {

	/* This is the instance of the receiver that deals with cellular status */
	private ToggleReceiver objAeroplaneReceiver;

	/*
	 * This class is the receiver for getting aeroplane mode toggle events
	 */
	private class ToggleReceiver extends BroadcastReceiver {

		/*
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive(Context ctxContext, Intent ittIntent) {

			onUpdateData(0);

		}

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onInitialize(boolean)
	 */
	@Override
	protected void onInitialize(boolean booReconnect) {

		super.onInitialize(booReconnect);

		if (objAeroplaneReceiver != null) {

			try {

				Log.d("CellinfoWidget", "Unregistered any existing status receivers");
				unregisterReceiver(objAeroplaneReceiver);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		IntentFilter itfIntent = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		objAeroplaneReceiver = new ToggleReceiver();
		registerReceiver(objAeroplaneReceiver, itfIntent);
		Log.d("CellinfoWidget", "Registered the status receiver");

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onCreate()
	 */
	public void onCreate() {

		super.onCreate();
		Log.d("CellinfoWidget", "Created");
		BugSenseHandler.initAndStartSession(this, getString(R.string.bugsense));

	}

	/*
	 * @see
	 * com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData
	 * (int)
	 */
	@Override
	protected void onUpdateData(int intReason) {

		Log.d("CellinfoWidget", "Fetching cellular network information");
		ExtensionData edtInformation = new ExtensionData();
		setUpdateWhenScreenOn(true);

		try {

			Log.d("CellinfoWidget", "Checking if the airplane mode is on");
			if (Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 0) {

				Log.d("CellinfoWidget", "Airplane-mode is off");
				TelephonyManager tmrTelephone = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

				if (tmrTelephone.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {

					String strOperator = tmrTelephone.getNetworkOperatorName();
					String strCountry = new Locale("en", tmrTelephone.getNetworkCountryIso()).getDisplayCountry();

					edtInformation.visible(true);
					edtInformation.status(String.format("%s, %s", strOperator, strCountry));
					edtInformation.expandedBody(tmrTelephone.isNetworkRoaming() ? getString(R.string.roam_network) : getString(R.string.home_network));

					if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("network_mode", false)) {

						String strProtocol = null;
						switch (tmrTelephone.getNetworkType()) {
						case TelephonyManager.NETWORK_TYPE_GPRS:
						case TelephonyManager.NETWORK_TYPE_EDGE:
						case TelephonyManager.NETWORK_TYPE_CDMA:
						case TelephonyManager.NETWORK_TYPE_1xRTT:
						case TelephonyManager.NETWORK_TYPE_IDEN:
							strProtocol = "2G";
						case TelephonyManager.NETWORK_TYPE_UMTS:
						case TelephonyManager.NETWORK_TYPE_EVDO_0:
						case TelephonyManager.NETWORK_TYPE_EVDO_A:
						case TelephonyManager.NETWORK_TYPE_HSDPA:
						case TelephonyManager.NETWORK_TYPE_HSUPA:
						case TelephonyManager.NETWORK_TYPE_HSPA:
						case TelephonyManager.NETWORK_TYPE_EVDO_B:
						case TelephonyManager.NETWORK_TYPE_EHRPD:
						case TelephonyManager.NETWORK_TYPE_HSPAP:
							strProtocol = "3G";
						case TelephonyManager.NETWORK_TYPE_LTE:
							strProtocol = "4G";
						default:
							strProtocol = "Unknown";
						}

						edtInformation.expandedBody(edtInformation.expandedBody() + "\n" + strProtocol);
					}

				}

			}

			if (new Random().nextInt(5) == 0) {

				PackageManager mgrPackages = getApplicationContext().getPackageManager();

				try {

					mgrPackages.getPackageInfo("com.mridang.donate", PackageManager.GET_META_DATA);

				} catch (NameNotFoundException e) {

					Integer intExtensions = 0;
				    Intent ittFilter = new Intent("com.google.android.apps.dashclock.Extension");
				    String strPackage;

				    for (ResolveInfo info : mgrPackages.queryIntentServices(ittFilter, 0)) {

				    	strPackage = info.serviceInfo.applicationInfo.packageName;
						intExtensions = intExtensions + (strPackage.startsWith("com.mridang.") ? 1 : 0); 

					}

					if (intExtensions > 1) {

						edtInformation.visible(true);
						edtInformation.clickIntent(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://details?id=com.mridang.donate")));
						edtInformation.expandedTitle("Please consider a one time purchase to unlock.");
						edtInformation.expandedBody("Thank you for using " + intExtensions + " extensions of mine. Click this to make a one-time purchase or use just one extension to make this disappear.");
						setUpdateWhenScreenOn(true);

					}

				}

			} else {
				setUpdateWhenScreenOn(true);
			}

		} catch (Exception e) {
			edtInformation.visible(false);
			Log.e("CellinfoWidget", "Encountered an error", e);
			BugSenseHandler.sendException(e);
		}

		edtInformation.icon(R.drawable.ic_dashclock);
		publishUpdate(edtInformation);
		Log.d("CellinfoWidget", "Done");

	}

	/*
	 * @see com.google.android.apps.dashclock.api.DashClockExtension#onDestroy()
	 */
	public void onDestroy() {

		super.onDestroy();

		if (objAeroplaneReceiver != null) {

			try {

				Log.d("CellinfoWidget", "Unregistered the status receiver");
				unregisterReceiver(objAeroplaneReceiver);

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		Log.d("CellinfoWidget", "Destroyed");
		BugSenseHandler.closeSession(this);

	}

}