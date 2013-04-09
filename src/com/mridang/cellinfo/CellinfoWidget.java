package com.mridang.cellinfo;

import java.util.Locale;

import android.content.Context;
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

  /*
   * @see com.google.android.apps.dashclock.api.DashClockExtension#onCreate()
   */
  public void onCreate() {

    super.onCreate();
    Log.d("CellinfoWidget", "Created");
    BugSenseHandler.initAndStartSession(this, "8c25985e");

  }

  /*
   * @see
   * com.google.android.apps.dashclock.api.DashClockExtension#onUpdateData
   * (int)
   */
  @Override
  protected void onUpdateData(int arg0) {

    setUpdateWhenScreenOn(true);

    Log.d("CellinfoWidget", "Fetching cellular network information");
    ExtensionData edtInformation = new ExtensionData();
    edtInformation.visible(false);

    try {

      Log.d("CellinfoWidget", "Checking if the airplane mode is on");
      if (Settings.System.getInt(getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) == 0) {

        Log.d("CellinfoWidget", "Airplane-mode is off");
        TelephonyManager tmrTelephone = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (tmrTelephone.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {

          String strOperator = tmrTelephone.getNetworkOperatorName();
          String strCountry = new Locale("en", tmrTelephone.getNetworkCountryIso()).getDisplayCountry();
          String strRoaming = getString(R.string.roam_network);

          String strProtocol = null;
          switch (tmrTelephone.getNetworkType()) {
          case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            strProtocol = "";
          case TelephonyManager.NETWORK_TYPE_GPRS:
            strProtocol = "GPRS (2.5G)";
          case TelephonyManager.NETWORK_TYPE_EDGE:
            strProtocol = "EDGE (2.75G)";
          case TelephonyManager.NETWORK_TYPE_UMTS:
            strProtocol = "HSDPA (3G)";
          case TelephonyManager.NETWORK_TYPE_HSDPA:
            strProtocol = "HSDPA (3G)";
          case TelephonyManager.NETWORK_TYPE_HSUPA:
            strProtocol = "HSUPA (3G)";
          case TelephonyManager.NETWORK_TYPE_HSPA:
            strProtocol = "HSPA (3G)";
          case TelephonyManager.NETWORK_TYPE_CDMA:
            strProtocol = "CDMA";
          case TelephonyManager.NETWORK_TYPE_EVDO_0:
            strProtocol = "EVDO (3G)";
          case TelephonyManager.NETWORK_TYPE_EVDO_A:
            strProtocol = "EVDO (3G)";
          case TelephonyManager.NETWORK_TYPE_EVDO_B:
            strProtocol = "EVDO (3G)";
          case TelephonyManager.NETWORK_TYPE_1xRTT:
            strProtocol = "1xRTT (2G)";
          case TelephonyManager.NETWORK_TYPE_IDEN:
            strProtocol = "IDEN (2G)";
          case TelephonyManager.NETWORK_TYPE_LTE:
            strProtocol = "LTE (4G)";
          case TelephonyManager.NETWORK_TYPE_EHRPD:
            strProtocol = "EHRPD (3G)";
          case TelephonyManager.NETWORK_TYPE_HSPAP:
            strProtocol = "HSPAP (3G)";
          default:
            break;
          }

          edtInformation.visible(true);
          edtInformation.status(String.format("%s — %s", strOperator, strCountry));
          edtInformation.expandedBody(strProtocol + (tmrTelephone.isNetworkRoaming() ? "\n" + strRoaming : ""));

        }

      }

    } catch (Exception e) {
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
    Log.d("CellinfoWidget", "Destroyed");
    BugSenseHandler.closeSession(this);

  }

}