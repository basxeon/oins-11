package oins.communication;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import oins.panels.ContactPanel;

public class ArpCleaner extends TimerTask{

	public static final int ARP_CLEAN=10;	
	
	@Override
	public void run() {
		try {
			ArpAvail avail=new ArpAvail(ContactPanel.getIpAdressesInt(),3);
			avail.sendArp();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JOptionPane.showMessageDialog(null, "Wyslano Arp z informacja o Dostepnosci", "Information", JOptionPane.INFORMATION_MESSAGE);
		
	}

}
