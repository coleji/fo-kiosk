package org.sailcbi.FoKiosk;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.starmicronics.starioextension.ICommandBuilder;
import com.starmicronics.starioextension.StarIoExt;
import com.starmicronics.starprntsdk.Communication;
import com.starmicronics.starprntsdk.ModelCapability;
import com.starmicronics.starprntsdk.PrinterSettingManager;
import com.starmicronics.starprntsdk.PrinterSettings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        data = getCommands(context, emulation, "Charlie Zechel", "1234567");

        Communication.sendCommands(context, data, settings.getPortName(), settings.getPortSettings(), 10000, context, mCallback);     // 10000mS!!!
        System.out.println("Done printing");
    }

    private static byte[] getCommands(Context context, Emulation emulation, String name, String cardNum) {
        ICommandBuilder builder = StarIoExt.createCommandBuilder(emulation);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        String dateString = dateFormat.format(date);

        Bitmap logoImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.cbi_logo_horiz);

        builder.beginDocument();
        builder.append(("Community Boating Guest Ticket\n\n\n").getBytes());
        builder.appendBitmapWithAlignment(logoImage, true, 380, true, ICommandBuilder.AlignmentPosition.Center);
        builder.append(("\n").getBytes());
        //TODO: figure out why it's not respecting the alignment.  The demo app doesn't work either (although I swear it used to)
        builder.appendBarcodeWithAlignment(
                cardNum.getBytes(),
                ICommandBuilder.BarcodeSymbology.NW7,
                ICommandBuilder.BarcodeWidth.Mode1,
                40,
                true,
                ICommandBuilder.AlignmentPosition.Center
        );
        builder.append(("\n\nGuest: " + name + "\n\n").getBytes());
        builder.append(("Take this ticket\nto the Dockhouse\nto complete your rental!\n\n\n").getBytes());
        builder.append(("Printed: " + dateString + "\n\n").getBytes());
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

