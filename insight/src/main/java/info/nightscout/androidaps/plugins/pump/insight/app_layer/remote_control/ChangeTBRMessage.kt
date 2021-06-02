package info.nightscout.androidaps.plugins.pump.insight.app_layer.remote_control

import info.nightscout.androidaps.plugins.pump.insight.app_layer.AppLayerMessage
import info.nightscout.androidaps.plugins.pump.insight.app_layer.Service
import info.nightscout.androidaps.plugins.pump.insight.descriptors.MessagePriority
import info.nightscout.androidaps.plugins.pump.insight.utils.ByteBuf

class ChangeTBRMessage : AppLayerMessage(MessagePriority.NORMAL, false, true, Service.REMOTE_CONTROL) {

    internal var percentage = 0
    internal var duration = 0
    override val data: ByteBuf
        get() {
            val byteBuf = ByteBuf(6)
            byteBuf.putUInt16LE(percentage)
            byteBuf.putUInt16LE(duration)
            byteBuf.putUInt16LE(31)
            return byteBuf
        }

}