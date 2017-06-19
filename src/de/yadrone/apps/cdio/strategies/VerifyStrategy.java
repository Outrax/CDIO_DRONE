package de.yadrone.apps.cdio.strategies;

import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import application.Controller;
import application.DroneWrapper;
import de.yadrone.apps.cdio.commands.ICommander;
import de.yadrone.apps.cdio.utilities.GlobalValues;
import de.yadrone.base.ARDrone;

import com.google.zxing.*;

import java.awt.image.BufferedImage;

public class VerifyStrategy extends AbstractStrategy
{
    private int failures = 0;

    @Override
    public BufferedImage run(BufferedImage image, Controller controller, ICommander commander)
    {
        LuminanceSource source = new BufferedImageLuminanceSource(image);
        BinaryBitmap    bitmap = new BinaryBitmap(new HybridBinarizer(source));

        Result       result;
        QRCodeReader reader = new QRCodeReader();

        try
        {
            result = reader.decode(bitmap);

            if(result == null)
            {
                this.failures++;

                if(this.failures > 10)
                {
                    controller.log("Failed to scan QR code for 10+ frames!");
                }

                return image;
            }

            int number = Integer.parseInt(result.getText());

            if(number == GlobalValues.nextGateInSequence)
            {
                controller.log("Verified the current gate as next in sequence: " + number);
                controller.next(Strategies.PENETRATE);

                GlobalValues.nextGateInSequence++;
            }
        }
        catch(ReaderException exception)
        {
            controller.logException(exception);
            controller.next(Strategies.EMERGENCY_LANDING);
        }

        return image;
    }
}