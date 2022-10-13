package info.nightscout.androidaps.plugins.pump.common.hw.medlink.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.joda.time.LocalDateTime;

import java.util.Optional;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;
import info.nightscout.androidaps.interfaces.ActivePlugin;
import info.nightscout.androidaps.interfaces.ResourceHelper;
import info.nightscout.androidaps.plugins.pump.common.hw.medlink.defs.MedLinkError;
import info.nightscout.androidaps.plugins.pump.common.hw.medlink.defs.MedLinkPumpDevice;
import info.nightscout.androidaps.plugins.pump.common.hw.medlink.service.MedLinkServiceData;
import info.nightscout.androidaps.plugins.pump.common.hw.rileylink.R;
import info.nightscout.androidaps.plugins.pump.common.hw.rileylink.defs.RileyLinkTargetDevice;
import info.nightscout.androidaps.plugins.pump.common.utils.StringUtil;
import info.nightscout.androidaps.utils.DateUtil;
import info.nightscout.shared.sharedPreferences.SP;

/**
 * Created by andy on 5/19/18.
 */

public class MedLinkStatusGeneralFragment extends DaggerFragment {

    private static final String PLACEHOLDER = "-";

    @Inject ActivePlugin activePlugin;
    @Inject ResourceHelper resourceHelper;
    @Inject MedLinkServiceData medLinkServiceData;
    @Inject DateUtil dateUtil;
    @Inject SP sp;

    private TextView connectionStatus;
    private TextView configuredRileyLinkAddress;
    private TextView configuredRileyLinkName;
    private View batteryLevelRow;
    private TextView batteryLevel;
    private TextView connectionError;
    private View connectedDeviceDetails;
    private TextView deviceType;
    private TextView configuredDeviceModel;
    private TextView lastUsedFrequency;
    private TextView lastDeviceContact;
    private TextView firmwareVersion;

    public String rileyLinkName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rileylink_status_general, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();


        refreshData();
    }

    private void refreshData() {
        RileyLinkTargetDevice targetDevice = medLinkServiceData.targetDevice;

        if(medLinkServiceData.medLinkServiceState != null && resourceHelper.gs(medLinkServiceData.medLinkServiceState.getResourceId()) != null) {
            this.connectionStatus.setText(resourceHelper.gs(medLinkServiceData.medLinkServiceState.getResourceId()));
        }
        // BS FIXME rileyLinkServiceData is injected so I suppose it cannot be null?
        if (medLinkServiceData != null) {
            this.configuredRileyLinkAddress.setText(Optional.ofNullable(medLinkServiceData.rileylinkAddress).orElse(PLACEHOLDER));
            this.configuredRileyLinkName.setText(Optional.ofNullable(medLinkServiceData.rileylinkName).orElse(PLACEHOLDER));

            if (sp.getBoolean(resourceHelper.gs(R.string.key_riley_link_show_battery_level), false)) {
                batteryLevelRow.setVisibility(View.VISIBLE);
                Integer batteryLevel = activePlugin.getActivePump().getBatteryLevel();
                this.batteryLevel.setText( resourceHelper.gs(R.string.rileylink_battery_level_value, batteryLevel));
            } else {
                batteryLevelRow.setVisibility(View.GONE);
            }

            MedLinkError medLinkError = medLinkServiceData.medLinkError;
            this.connectionError.setText(medLinkError == null ? PLACEHOLDER : resourceHelper.gs(medLinkError.getResourceId(targetDevice)));

            this.firmwareVersion.setText(resourceHelper.gs(R.string.rileylink_firmware_version_value,
                    Optional.ofNullable(medLinkServiceData.versionBLE113).orElse(PLACEHOLDER), Optional.ofNullable(medLinkServiceData.versionCC110).orElse(PLACEHOLDER)));
        }

        MedLinkPumpDevice medLinkPumpDevice = (MedLinkPumpDevice) activePlugin.getActivePump();
//        RileyLinkPumpInfo rileyLinkPumpInfo = medLinkPumpDevice.getPumpInfo();
        this.deviceType.setText(targetDevice.getResourceId());
        if (targetDevice == RileyLinkTargetDevice.MedtronicPump) {
            this.connectedDeviceDetails.setVisibility(View.VISIBLE);
            this.configuredDeviceModel.setText(activePlugin.getActivePump().getPumpDescription().getPumpType().getDescription());
//            this.connectedDeviceModel.setText(rileyLinkPumpInfo.getConnectedDeviceModel());
        } else {
            this.connectedDeviceDetails.setVisibility(View.GONE);
        }
//        this.serialNumber.setText(rileyLinkPumpInfo.getConnectedDeviceSerialNumber());
//        this.pumpFrequency.setText(rileyLinkPumpInfo.getPumpFrequency());

        if (medLinkServiceData.lastGoodFrequency != null) {
            this.lastUsedFrequency.setText(resourceHelper.gs(R.string.rileylink_pump_frequency_value, medLinkServiceData.lastGoodFrequency));
        }

        long lastConnectionTimeMillis = medLinkPumpDevice.getLastConnectionTimeMillis();
        if (lastConnectionTimeMillis == 0) {
            this.lastDeviceContact.setText(resourceHelper.gs(R.string.riley_link_ble_config_connected_never));
        } else {
            this.lastDeviceContact.setText(StringUtil.toDateTimeString(dateUtil, new LocalDateTime(lastConnectionTimeMillis)));
        }
    }

}
