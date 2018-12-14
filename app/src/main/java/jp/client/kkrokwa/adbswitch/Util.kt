package jp.client.kkrokwa.adbswitch

import android.content.Context
import android.provider.Settings

var TOGGLE = "jp.kkrokwa.client.adbswitch.TOGGLE"

internal fun isPermitted(context: Context): Boolean {
    val current = Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0)
    var result = false
    try {
        result = Settings.Global.putInt(context.contentResolver, Settings.Global.ADB_ENABLED, current)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}