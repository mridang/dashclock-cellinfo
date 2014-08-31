package com.mridang.cellinfo;

import java.util.Locale;
import java.util.Random;

import org.acra.ACRA;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

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
		 * @see
		 * android.content.BroadcastReceiver#onReceive(android.content.Context,
		 * android.content.Intent)
		 */
		@Override
		public void onReceive(Context ctxContext, Intent ittIntent) {

			onUpdateData(0);

		}

	}

	/*
	 * @see
	 * com.google.android.apps.dashclock.api.DashClockExtension#onInitialize
	 * (boolean)
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
		ACRA.init(new AcraApplication(getApplicationContext()));

	}

	/*
	 * @see
	 * com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData
	 * (int)
	 */
	@Override
	@SuppressWarnings("deprecation")
	@TargetApi(17)
	protected void onUpdateData(int intReason) {

		Log.d("CellinfoWidget", "Fetching cellular network information");
		ExtensionData edtInformation = new ExtensionData();
		setUpdateWhenScreenOn(true);

		try {

			Log.d("CellinfoWidget", "Checking if the airplane mode is on");
			Boolean booAirmode = false;
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				booAirmode = Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
			} else {
				booAirmode = Settings.Global.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
			}

			if (booAirmode == false) {

				Log.d("CellinfoWidget", "Airplane-mode is off");
				TelephonyManager tmrTelephone = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				if (tmrTelephone.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {

					String strOperator = tmrTelephone.getNetworkOperatorName();
					String strCountry = new Locale("en", tmrTelephone.getNetworkCountryIso()).getDisplayCountry();
					String strProtocol = null;
					String strType;
					switch (tmrTelephone.getNetworkType()) {
					case TelephonyManager.NETWORK_TYPE_GPRS:
						strProtocol = "2G";
						strType = "GPRS";
						break;
					case TelephonyManager.NETWORK_TYPE_EDGE:
						strProtocol = "2G";
						strType = "EDGE";
						break;
					case TelephonyManager.NETWORK_TYPE_CDMA:
						strProtocol = "2G";
						strType = "CDMA";
						break;
					case TelephonyManager.NETWORK_TYPE_1xRTT:
						strProtocol = "2G";
						strType = "1xRTT";
						break;
					case TelephonyManager.NETWORK_TYPE_IDEN:
						strProtocol = "2G";
						strType = "IDEN";
						break;
					case TelephonyManager.NETWORK_TYPE_UMTS:
						strProtocol = "3G";
						strType = "UMTS";
						break;
					case TelephonyManager.NETWORK_TYPE_EVDO_0:
						strProtocol = "3G";
						strType = "EVDO-0";
						break;
					case TelephonyManager.NETWORK_TYPE_EVDO_A:
						strProtocol = "3G";
						strType = "EVDO-A";
						break;
					case TelephonyManager.NETWORK_TYPE_HSDPA:
						strProtocol = "3G";
						strType = "HSDPA";
						break;
					case TelephonyManager.NETWORK_TYPE_HSUPA:
						strProtocol = "3G";
						strType = "HSUPA";
						break;
					case TelephonyManager.NETWORK_TYPE_HSPA:
						strProtocol = "3G";
						strType = "HSPA";
						break;
					case TelephonyManager.NETWORK_TYPE_EVDO_B:
						strProtocol = "3G";
						strType = "EVDO-B";
						break;
					case TelephonyManager.NETWORK_TYPE_EHRPD:
						strProtocol = "3G";
						strType = "EHRPD";
						break;
					case TelephonyManager.NETWORK_TYPE_HSPAP:
						strProtocol = "3G";
						strType = "HSPA+";
						break;
					case TelephonyManager.NETWORK_TYPE_LTE:
						strProtocol = "4G";
						strType = "LTE";
						break;
					default:
						strProtocol = "unknown";
						strType = "XX";
						break;
					}

					if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(
							"network_mode", false)) {
						edtInformation.expandedTitle(getString(R.string.message, strProtocol, strOperator, strCountry));
					} else {
						edtInformation.expandedTitle(String.format("%s, %s", strOperator, strCountry));
					}

					if (tmrTelephone.isNetworkRoaming()) {
						edtInformation.expandedBody(getString(R.string.roam_network, strType));
					} else {
						edtInformation.expandedBody(getString(R.string.home_network, strType));
					}

					edtInformation.status(strType);
					edtInformation.visible(true);

				}

			}

			if (new Random().nextInt(5) == 0 && !(0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE))) {

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
						edtInformation.clickIntent(new Intent(Intent.ACTION_VIEW).setData(Uri
								.parse("market://details?id=com.mridang.donate")));
						edtInformation.expandedTitle("Please consider a one time purchase to unlock.");
						edtInformation
								.expandedBody("Thank you for using "
										+ intExtensions
										+ " extensions of mine. Click this to make a one-time purchase or use just one extension to make this disappear.");
						setUpdateWhenScreenOn(true);

					}

				}

			}

		} catch (Exception e) {
			edtInformation.visible(false);
			Log.e("CellinfoWidget", "Encountered an error", e);
			ACRA.getErrorReporter().handleSilentException(e);
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

	}

}