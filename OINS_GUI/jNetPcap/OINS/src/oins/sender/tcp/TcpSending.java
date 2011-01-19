package oins.sender.tcp;

import java.util.ArrayList;
import java.util.List;

import oins.sender.arp.ByteConversion;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.network.Ip4;

/**
 * 
 * @author Mateusz Drzymala
 *
 */
/*
 * sender.tcp - uzytkownik ktory wyslal do reciever'a arp->pid=1. Sender zaczyna np. pobierac plik
 * od recievera i wysyla mu co jakis czas ACK z wiadomoscia
 */
public class TcpSending{
	
	
	public static void main(String[] args) {
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
		StringBuilder errbuf = new StringBuilder();     // For any error msgs

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

		final PcapIf device = alldevs.get(0); // Pick one
		System.out.printf("\nChoosing '%s' on your behalf:\n", device
		    .getDescription());

		/***************************************************************************
		 * Second we open up the selected device
		 **************************************************************************/
		int snaplen = 62 ; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 100 * 1000; // 100 seconds in millis
		final Pcap pcap =
		    Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

		if (pcap == null) {
			System.err.printf("Error while opening device for capture: "
			    + errbuf.toString());
			return;
		}
		/**
		 * FILTR
		 */
		
		PcapBpfProgram program = new PcapBpfProgram();
//		String expression = "ether proto \\arp";
		String expression = "ip proto \\tcp and less 61";
		
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
		/*
		 * sprawdzic czy mozna obejsc to zeby wszystkie obiekty nei byly final
		 */
		final Ip4 ip = new Ip4();
		final Integer[] recAddr={10,1,0,40 };
		String mes="Proba - projekt Oins 2011 - MD RS";
		final Message m= new Message(mes);
		final int start=0, end=m.size();

		JPacketHandler<String> jpacketHandler = new JPacketHandler<String>() {

			public void nextPacket(JPacket packet, String user) {
			
				/*
				 * Dziala tylko dla IPv4
				 */
				if(packet.hasHeader(ip)){
				
					if(ByteConversion.equal(ip.destination(), ByteConversion.convert(recAddr)) &&
							ByteConversion.equal(ip.source(),device.getAddresses().get(0).getAddr().getData() )){
					
						if(start<end){
							TcpPadding.padding(packet, m.getMesB(start));
							pcap.sendPacket(packet.getByteArray(0, packet.size()));
						}
						
							
					
					}
					
					
					
				}

			}
		};
		
		pcap.loop(1000000, jpacketHandler, null);

		/***************************************************************************
		 * Last thing to do is close the pcap handle
		 **************************************************************************/
		pcap.close();
	}
}
