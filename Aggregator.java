package org.sunspotworld;

import com.sun.spot.resources.Resources;
import com.sun.spot.resources.transducers.ISwitch;
import com.sun.spot.resources.transducers.ISwitchListener;
import com.sun.spot.resources.transducers.LEDColor;
import com.sun.spot.resources.transducers.ITriColorLEDArray;
import com.sun.spot.resources.transducers.SwitchEvent;
import com.sun.spot.io.j2me.radiogram.*;

import java.io.*;
import javax.microedition.io.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

/**
 * A simple MIDlet that uses the left switch (SW1) to broadcast a message
 * to set the color of the LEDs of any receiving SPOTs and the right
 * switch (SW2) to count in binary in its LEDs.
 *
 * Messages received from the other SPOTs control the LEDs of this SPOT.
 */
public class Aggregator extends MIDlet implements ISwitchListener {

    private static final int CHANGE_COLOR = 1;
    private static final int CHANGE_COUNT = 2;

    private ITriColorLEDArray leds = (ITriColorLEDArray)Resources.lookup(ITriColorLEDArray.class);
    private ISwitch sw1 = (ISwitch)Resources.lookup(ISwitch.class, "SW1");
    private ISwitch sw2 = (ISwitch)Resources.lookup(ISwitch.class, "SW2");
    private int count = -1;
    private int color = 0;
    private LEDColor[] colors = { LEDColor.RED, LEDColor.GREEN, LEDColor.BLUE };
    private RadiogramConnection tx = null;
    private Radiogram xdg;;
    private Datagram dg;
    private Datagram dgrx;
    private String tempNow = new String();
    private String lightNow = new String();
    int LightYes;
    int TempYes;

    private void showCount(int count, int color) {
        for (int i = 7, bit = 1; i >= 0; i--, bit <<= 1) {
            if ((count & bit) != 0) {
                leds.getLED(i).setColor(colors[color]);
                leds.getLED(i).setOn();
            } else {
                leds.getLED(i).setOff();
            }
        }
    }

    private void showColor(int color) {
        leds.setColor(colors[color]);
        leds.setOn();
    }

    protected void startApp() throws MIDletStateChangeException {
        System.out.println("Broadcast Counter MIDlet");
        showColor(color);
        sw1.addISwitchListener(this);
        sw2.addISwitchListener(this);
        sw1.addISwitchListener(this);
        try {
            //tx = (RadiogramConnection)Connector.open("radiogram://7f00.0101.0000.1004:100");  //Sink Port 100
            //dg = (Datagram) tx.newDatagram(tx.getMaximumLength());

            RadiogramConnection rx = (RadiogramConnection) Connector.open("radiogram://:67");  //Connection to nodes
            dgrx = (Datagram) rx.newDatagram(rx.getMaximumLength());

           while (true) {
               try {
                if (rx.packetsAvailable()) {
                    rx.receive(dgrx);
                    String value = dgrx.readUTF();
                    dgrx.reset();
                    if (value.startsWith("T")) {

                        TempYes=1;
                        tempNow=value;
                    }
                    else if (value.startsWith("L")) {
                        LightYes = 1;
                        lightNow = value;
                    }
                }
               }
               catch (IOException ex) {
                   System.out.println("Error receiving packet: " + ex);
                   ex.printStackTrace();
               }

           }
        }
        catch (Exception e) {
            System.out.println("Error opening connection: " + e);
            e.printStackTrace();
        }
    }

    public void switchPressed(SwitchEvent evt) {
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
    }

    public void switchReleased(SwitchEvent evt) {
    }
}
