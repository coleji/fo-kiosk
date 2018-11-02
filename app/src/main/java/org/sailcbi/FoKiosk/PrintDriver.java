package org.sailcbi.FoKiosk;

import android.app.Activity;

import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starprntsdk.Communication;
import com.starmicronics.starprntsdk.ModelCapability;
import com.starmicronics.starprntsdk.PrinterSettingManager;
import com.starmicronics.starprntsdk.PrinterSettings;
import static com.starmicronics.starioextension.StarIoExt.Emulation;

public class PrintDriver {
    private static PrinterSettings settings = new PrinterSettings(
            9,
            "BT:00:15:0E:E6:BD:0D",
            "mini",
            "00:15:0E:E6:BD:0D",
            "Star Micronics",
            true,
            384
    );
    public static void print(Activity context) {
        System.out.println("Inside print()");
        byte[] data;

        PrinterSettingManager settingManager = new PrinterSettingManager(context);
        //PrinterSettings settings       = settingManager.getPrinterSettings();

        Emulation emulation = ModelCapability.getEmulation(settings.getModelIndex());
        int paperSize = settings.getPaperSize();

        data = getCommands(emulation);

        Communication.sendCommands(context, data, settings.getPortName(), settings.getPortSettings(), 10000, context, mCallback);     // 10000mS!!!
        System.out.println("Done printing");
    }

    private static byte[] getCommands(Emulation emulation) {
        byte[] data = "1234567".getBytes();

        ICommandBuilder builder = StarIoExt.createCommandBuilder(emulation);

        builder.beginDocument();

        builder.appendBarcode(data, ICommandBuilder.BarcodeSymbology.NW7, ICommandBuilder.BarcodeWidth.Mode1, 40, true);
        builder.appendUnitFeed(32);


        builder.appendCutPaper(ICommandBuilder.CutPaperAction.PartialCutWithFeed);

        builder.endDocument();

        return builder.getCommands();
    }

    private static final Communication.SendCallback mCallback = new Communication.SendCallback() {
        @Override
        public void onStatus(boolean result, Communication.Result communicateResult) {
            String msg;

            switch (communicateResult) {
                case Success :
                    msg = "Success!";
                    break;
                case ErrorOpenPort:
                    msg = "Fail to openPort";
                    break;
                case ErrorBeginCheckedBlock:
                    msg = "Printer is offline (beginCheckedBlock)";
                    break;
                case ErrorEndCheckedBlock:
                    msg = "Printer is offline (endCheckedBlock)";
                    break;
                case ErrorReadPort:
                    msg = "Read port error (readPort)";
                    break;
                case ErrorWritePort:
                    msg = "Write port error (writePort)";
                    break;
                default:
                    msg = "Unknown error";
                    break;
            }

            System.out.println("PRINT RESULT: ");
            System.out.println(msg);
        }
    };

}

