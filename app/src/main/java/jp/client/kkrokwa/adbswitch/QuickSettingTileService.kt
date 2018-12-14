package jp.client.kkrokwa.adbswitch

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.provider.Settings
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.widget.Toast
import androidx.annotation.RequiresApi

@RequiresApi(api = Build.VERSION_CODES.N)
class QuickSettingTileService : TileService() {

    override fun onStartListening() {
        val context = applicationContext
        if (isPermitted(context)) {
            super.onStartListening()

            val isOn = Settings.Global.getInt(context.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1
            val title =
                if (isOn) {
                    getString(R.string.tile_on)
                } else {
                    getString(R.string.tile_off)
                }
            qsTile.label = title
            if (isOn) {
                qsTile.state = Tile.STATE_ACTIVE
                qsTile.icon = Icon.createWithResource(context, R.drawable.ic_on)
            } else {
                qsTile.state = Tile.STATE_INACTIVE
                qsTile.icon = Icon.createWithResource(context, R.drawable.ic_off)
            }
            qsTile.updateTile()
        } else {
            Toast.makeText(context, R.string.msg_error, Toast.LENGTH_LONG).show()
        }
    }

    override fun onClick() {
        super.onClick()
        when (qsTile.state) {
            Tile.STATE_ACTIVE -> {
                qsTile.label = getString(R.string.tile_off)
                qsTile.state = Tile.STATE_INACTIVE
                qsTile.icon = Icon.createWithResource(applicationContext, R.drawable.ic_off)
            }
            Tile.STATE_INACTIVE -> {
                qsTile.label = getString(R.string.tile_on)
                qsTile.state = Tile.STATE_ACTIVE
                qsTile.icon = Icon.createWithResource(applicationContext, R.drawable.ic_on)
            }
        }
        qsTile.updateTile()
        val toggleIntent = Intent(applicationContext, AdbSwitch::class.java)
        toggleIntent.action = TOGGLE
        sendBroadcast(toggleIntent)
    }

}
