package oins.reciever.arp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.network.Arp;

/**
 * @author Mateusz Drzymala
 *
 */
public class PacketListener {
	public static void main(String[] args) {
		
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
		StringBuilder errbuf = new StringBuilder();     

		/***************************************************************************
		 * First get a list of devices on this system
		 **************************************************************************/
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

		PcapIf device = alldevs.get(0); // Pick one
		System.out.printf("\nChoosing '%s' on your behalf:\n", device
		    .getDescription());

		/***************************************************************************
		 * Second we open up the selected device
		 **************************************************************************/
		int snaplen = 60;
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 60 * 10000; // 600 seconds= 10 minutes
		Pcap pcap =
		    Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

		if (pcap == null) {
			System.err.printf("Error while opening device for capture: "
			    + errbuf.toString());
			return;
		}
				
		/**
		 * FILTR
		 */
		/**
		 * TODO
		 * maske filtru sparametryzowac
		 */
		PcapBpfProgram program = new PcapBpfProgram();
		String expression = "ether proto \\arp";
		
		int optimize = 0;         // 0 = false
		int netmask = 0xFFFFFF00; // 255.255.255.0
		
		if (pcap.compile(program, expression, optimize, netmask) != Pcap.OK) {
			System.err.println(pcap.getErr());
			return;
		}
		
		if (pcap.setFilter(program) != Pcap.OK) {
			System.err.println(pcap.getErr());
			return;		
		}
		
		System.out.println("Filter set !!! : " + expression);
		
		final Arp arp = new Arp();


		final Integer[] dzimAdr= {10,1,0,50};
		JPacketHandler<String> jpacketHandler = new JPacketHandler<String>() {

			public void nextPacket(JPacket packet, String user) {
				PacketDecoding dec = new PacketDecoding(packet);
				try {
					if(packet.hasHeader(arp)){
						if(ByteConversion.equal(arp.spa(), ByteConversion.convert(dzimAdr))){
							
							System.out.println("Odebralem pakiet o Dzimiego");
							System.out.println(packet.toHexdump());
							System.out.println("Teraz dekodujemy:");

							final MessageDigest messageDigest = MessageDigest
						    .getInstance("MD5");
							messageDigest.update(packet.getByteArray(6, 6),0,6);
							messageDigest.update((byte)1);
							messageDigest.update(packet.getByteArray(42, 2),0,2);
							JBuffer baf = new JBuffer( messageDigest.digest());
							 System.out.println(baf.toHexdump());
							
						}
					}
					if(dec.checkPID()!=0){
					
						/*
						 * TODO obsluga pakietu + odeslanie pakietu
						 */
						System.out.println("PID: " + dec.getPid());
					//	System.out.println(packet.toString());
						
						
					}
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				

				

			}
		};
		/**
		 * TODO
		 * sprawdzic czy ilosc 10000 wystarczy ;)
		 */
	
		pcap.loop(10000, jpacketHandler,"OINS");


		pcap.close();
	}
	
	
}
