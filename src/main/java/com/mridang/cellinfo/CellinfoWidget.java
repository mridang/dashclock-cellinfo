package com.mridang.cellinfo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.apps.dashclock.api.ExtensionData;

import org.acra.ACRA;

import java.util.Locale;

/*
 * This class is the main class that provides the widget
 */
public class CellinfoWidget extends ImprovedExtension {

	/*
	 * (non-Javadoc)
	 * @see com.mridang.cellinfo.ImprovedExtension#getIntents()
	 */
	@Override
	protected IntentFilter getIntents() {
		return new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.cellinfo.ImprovedExtension#getTag()
	 */
	@Override
	protected String getTag() {
		return getClass().getSimpleName();
	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.cellinfo.ImprovedExtension#getUris()
	 */
	@Override
	protected String[] getUris() {
		return null;
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

		Log.d(getTag(), "Fetching cellular network information");
		ExtensionData edtInformation = new ExtensionData();
		setUpdateWhenScreenOn(true);

		try {

			Log.d(getTag(), "Checking if the airplane mode is on");
			Boolean booAirmode;
			if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				booAirmode = Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
			} else {
				booAirmode = Settings.Global.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
			}

			if (!booAirmode) {

				Log.d(getTag(), "Airplane-mode is off");
				TelephonyManager tmrTelephone = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				if (tmrTelephone.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE
						&& tmrTelephone.getSimState() == TelephonyManager.SIM_STATE_READY) {

					edtInformation.visible(true);

					String strOperator = tmrTelephone.getNetworkOperatorName();
					String strCountry = new Locale("en", tmrTelephone.getNetworkCountryIso()).getDisplayCountry();
					String strProtocol = null;
					String strType = null;
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
						edtInformation.visible(false);
						break;
					}

					if (getBoolean("network_mode", false)) {
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
					edtInformation.clickIntent(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
				}

			}

		} catch (Exception e) {
			edtInformation.visible(false);
			Log.e(getTag(), "Encountered an error", e);
			ACRA.getErrorReporter().handleSilentException(e);
		}

		edtInformation.icon(R.drawable.ic_dashclock);
		doUpdate(edtInformation);

	}

	/*
	 * (non-Javadoc)
	 * @see com.mridang.cellinfo.ImprovedExtension#onReceiveIntent(android.content.Context, android.content.Intent)
	 */
	@Override
	protected void onReceiveIntent(Context ctxContext, Intent ittIntent) {
		onUpdateData(UPDATE_REASON_MANUAL);
	}

}