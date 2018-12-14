package jp.client.kkrokwa.adbswitch

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.RemoteViews
import android.widget.Toast


class AdbSwitch : AppWidgetProvider() {

    override fun onEnabled(context: Context?) {
        if (isPermitted(context!!)) {
            super.onEnabled(context)
        } else {
            Toast.makeText(context, R.string.msg_error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        if (isPermitted(context)) {
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
            super.onUpdate(context, appWidgetManager, appWidgetIds)
        } else {
            Toast.makeText(context, R.string.msg_error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val appContext = context!!.applicationContext
        if (isPermitted(appContext) && intent!!.action == TOGGLE) {
            val newVal = if (Settings.Global.getInt(appContext.contentResolver, Settings.Global.ADB_ENABLED, 0) == 0) {
                1
            } else {
                0
            }
            try {
                Settings.Global.putInt(appContext.contentResolver, Settings.Global.ADB_ENABLED, newVal)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val appWidgetManager = AppWidgetManager.getInstance(appContext)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(intent.component)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
        super.onReceive(context, intent)
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val views = RemoteViews(context.packageName, R.layout.app_widget)
        val intent = Intent(context, AdbSwitch::class.java)
        intent.action = TOGGLE
        val pendingIntent =
            PendingIntent.getBroadcast(context, appWidgetId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        views.setOnClickPendingIntent(R.id.btn_widget, pendingIntent)
        val status = if (Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1) {
            context.getString(R.string.btn_on)
        } else {
            context.getString(R.string.btn_off)
        }
        views.setTextViewText(R.id.btn_widget, status)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
