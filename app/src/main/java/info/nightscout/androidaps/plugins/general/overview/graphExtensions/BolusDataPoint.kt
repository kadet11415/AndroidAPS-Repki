package info.nightscout.androidaps.plugins.general.overview.graphExtensions

import android.content.Context
import info.nightscout.androidaps.core.R
import info.nightscout.androidaps.database.entities.Bolus
import info.nightscout.androidaps.interfaces.ActivePlugin
import info.nightscout.androidaps.interfaces.ResourceHelper
import info.nightscout.androidaps.utils.DecimalFormatter
import info.nightscout.androidaps.utils.DefaultValueHelper

class BolusDataPoint(
    val data: Bolus,
    private val rh: ResourceHelper,
    private val activePlugin: ActivePlugin,
    private val defaultValueHelper: DefaultValueHelper
) : DataPointWithLabelInterface {

    private var yValue = 0.0

    override fun getX(): Double = data.timestamp.toDouble()
    override fun getY(): Double = if (data.isSMBorBasal()) defaultValueHelper.determineLowLine() else yValue
    override val label
        get() = DecimalFormatter.toPumpSupportedBolus(data.amount, activePlugin.activePump, rh)
    override val duration = 0L

    override fun setY(y: Double) {
        yValue = y
    }

    override val size = 2f

    override val shape
        get() = if (data.type == Bolus.Type.SMB) PointsWithLabelGraphSeries.Shape.SMB
        else if (data.type == Bolus.Type.TBR) PointsWithLabelGraphSeries.Shape.TBR_BOLUS
        else PointsWithLabelGraphSeries.Shape.BOLUS

    override fun color(context: Context?) =
        when {
            data.type == Bolus.Type.SMB -> rh.gac(context, R.attr.smbColor)
            data.type == Bolus.Type.TBR -> rh.gac(context, R.attr.bolusDataPointColor)
            data.isValid                -> rh.gac(context,  R.attr.bolusDataPointColor)
            else                        -> rh.gac(context,  R.attr.alarmColor)
        }
}
