package oins.sender.arp;


import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;


public class ArpSending {
	
	
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
		
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); 
		StringBuilder errbuf = new StringBuilder();     

		
		int r = Pcap.findAllDevs(alldevs, errbuf);
		if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
			System.err.printf("Can't read list of devices, error is %s", errbuf
			    .toString());
			return;
		}

		System.out.println("Network devices found:");
		int i = 0;
		for (PcapIf device : alldevs) {
			System.out.printf("#%d: %s [%s]\n", i++, device.getName(), device
			    .getDescription());
		}

		PcapIf device = alldevs.get(1); // Pick one
		System.out.printf("\nChoosing '%s' on your behalf:\n", device
		    .getDescription());

		/***************************************************************************
		 * Second we open up the selected device
		 **************************************************************************/
		int snaplen = 64 * 1024; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 10 * 1000; // 10 seconds in millis
		Pcap pcap =
		    Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

		if (pcap == null) {
			System.err.printf("Error while opening device for capture: "
			    + errbuf.toString());
			return;
		}
		/***************************************************************************
		 * Now we send Special Arp address on 'adres'
		 **************************************************************************/		
		
		Integer[] adres={10,1,0,40};

		/*
		 * TODO odebranie pakietu i sprawdzenie jakim protokolem bede sie komunikowal z drugim uzytkownikiem
		 */
			ArpPacket arp =new ArpPacket(device, adres);
			System.out.println(arp.getBuff().toHexdump());
			pcap.sendPacket(arp.getBuff());
			
			final MessageDigest messageDigest = MessageDigest
		    .getInstance("MD5");
			messageDigest.update(arp.getBuff().getByteArray(6, 6),0,6);
			messageDigest.update((byte)1);
			messageDigest.update(arp.getBuff().getByteArray(42, 2),0,2);
			 JBuffer baf = new JBuffer( messageDigest.digest());
			 System.out.println(baf.toHexdump());
			
		
		
		pcap.close();
	}
}
