package oins.utils;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import oins.core.ConvFrame;

public class ConvFrameAdapter extends WindowAdapter {

    ConvFrame app;

    
    // adapter
    public ConvFrameAdapter(ConvFrame temp) {
        this.app = temp;
    } 

    
    
    public void windowDeactivated(WindowEvent e) {

    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowLostFocus(WindowEvent e) {
    }

    public void windowStateChanged(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent we) {
    }

    public void windowClosing(WindowEvent we) {
        // TODO operacje zwiazane z zamykaniem okna
        we.getWindow().dispose();
    }
}
