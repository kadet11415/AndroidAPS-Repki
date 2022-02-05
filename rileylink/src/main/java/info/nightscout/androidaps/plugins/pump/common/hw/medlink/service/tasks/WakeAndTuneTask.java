package info.nightscout.androidaps.plugins.pump.common.hw.medlink.service.tasks;

import javax.inject.Inject;

import dagger.android.HasAndroidInjector;
import info.nightscout.androidaps.interfaces.ActivePlugin;
import info.nightscout.androidaps.plugins.bus.RxBus;
import info.nightscout.androidaps.plugins.pump.common.events.EventRefreshButtonState;
import info.nightscout.androidaps.plugins.pump.common.hw.medlink.defs.MedLinkPumpDevice;
import info.nightscout.androidaps.plugins.pump.common.hw.rileylink.defs.RileyLinkPumpDevice;
import info.nightscout.androidaps.plugins.pump.common.hw.rileylink.service.tasks.PumpTask;

/**
 * Created by geoff on 7/16/16.
 */
public class WakeAndTuneTask extends PumpTask {

    @Inject ActivePlugin activePlugin;
    @Inject RxBus rxBus;

    private static final String TAG = "WakeAndTuneTask";

    public WakeAndTuneTask(HasAndroidInjector injector) {
        super(injector);
    }

    @Override
    public void run() {
        MedLinkPumpDevice pumpDevice = (MedLinkPumpDevice) activePlugin.getActivePump();
        rxBus.send(new EventRefreshButtonState(false));
        pumpDevice.setBusy(true);
        pumpDevice.getRileyLinkService().doTuneUpDevice();
        pumpDevice.setBusy(false);
        rxBus.send(new EventRefreshButtonState(true));
    }
}
