package info.nightscout.androidaps.plugins.pump.common.hw.medlink.activities;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import info.nightscout.androidaps.logging.AAPSLogger;
import info.nightscout.androidaps.logging.LTag;
import info.nightscout.androidaps.plugins.pump.medtronic.comm.PumpResponses;

/**
 * Created by Dirceu on 21/12/20.
 */
public class BolusCallback extends BaseCallback<String,Supplier<Stream<String>>> {

    private final AAPSLogger aapsLogger;
    //    private final RxBusWrapper rxBus;
    private Pattern deliveredBolusPattern = Pattern.compile(":\\s+\\d{1,2}\\.\\du\\s\\d{1,2}:\\d{1,2}\\s\\d{2}\\S", Pattern.CASE_INSENSITIVE);
    private Pattern deliveringBolusPattern = Pattern.compile(":\\s+\\d{1,2}\\.\\du", Pattern.CASE_INSENSITIVE);


    public BolusCallback(AAPSLogger aapsLogger){//RxBusWrapper rxBus) {
        super();
        this.aapsLogger = aapsLogger;
//        this.rxBus = rxBus;
    }

    @Override public MedLinkStandardReturn<String> apply(Supplier<Stream<String>> answers) {
        aapsLogger.info(LTag.PUMPBTCOMM, "BolusCallback");
        //TODO fix error response
        AtomicReference<String> pumpResponse = new AtomicReference<>();
        if (answers.get().filter(f -> f.toLowerCase().contains("pump is not delivering a bolus")).findFirst().isPresent()) {
            answers.get().filter(f -> f.toLowerCase().contains("recent bolus bl")).findFirst().map(f -> {
                Matcher matcher = deliveredBolusPattern.matcher(f);
                pumpResponse.set(matchBolus(matcher, f));
                return pumpResponse;
            });
        } else if (answers.get().filter(f -> f.toLowerCase().contains("pump is delivering a bolus")).findFirst().isPresent()) {
            answers.get().filter(f -> f.toLowerCase().contains("recent bolus bl")).findFirst().map(f -> {
                Matcher matcher = deliveringBolusPattern.matcher(f);
                pumpResponse.set(matchBolus(matcher, f));
                return pumpResponse;
            });
        }
        return  new MedLinkStandardReturn<>(answers,pumpResponse.get());
    }

    private String matchBolus(Matcher matcher, String f) {
        if (matcher.find()) {
            return PumpResponses.BolusDelivered.getAnswer();
        } else {
            return PumpResponses.UnknowAnswer.getAnswer() + f;
        }
    }

}
