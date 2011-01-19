import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.JCaptureHeader;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;


public class ArpFilter {
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

		PcapIf device = alldevs.get(0); // Pick one
		System.out.printf("\nChoosing '%s' on your behalf:\n", device
		    .getDescription());

		/***************************************************************************
		 * Second we open up the selected device
		 **************************************************************************/
		int snaplen = 60*1024 ; // Capture all packets, no trucation
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 100 * 1000; // 100 seconds in millis
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
		
		PcapBpfProgram program = new PcapBpfProgram();
//		String expression = "ether proto \\arp";
		String expression = "ip proto \\tcp and less 64";
		
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
		
		final Tcp tcp = new Tcp();
		final Ip4 ip = new Ip4();

		JPacketHandler<String> jpacketHandler = new JPacketHandler<String>() {

			public void nextPacket(JPacket packet, String user) {
			
				//System.out.println(packet.toString());
				if(packet.hasHeader(tcp)){
					if(tcp.flags_ACK()){
						//System.out.println("dst:"+tcp.destination());
						//System.out.println("src:"+tcp.source());
						System.out.println("flag"+tcp.flags());
						if(packet.hasHeader(ip)){
							
							System.out.println("dst:"+ip.destinationToInt());
						
							System.out.println("src:"+ip.sourceToInt());
							System.out.println("checksum"+ip.checksumDescription());
						
						}
					}
					
					/*System.out.println("dst:"+tcp.destination());
					System.out.println("src:"+tcp.source());
					System.out.println("flag"+tcp.flags());*/
				}

			}
		};

		/***************************************************************************
		 * Fourth we enter the loop and tell it to capture 10 packets.
		 * 
		 * The loop method does a mapping of pcap.datalink() DLT value to JProtocol
		 * ID, which is needed by JScanner. The scanner scans the packet buffer 
		 * and decodes the headers. The mapping is done automatically, although a
		 * variation on the loop method exists that allows the programmer to
		 * sepecify exactly which protocol ID to use as the data link type for this
		 * pcap interface.
		 **************************************************************************/
		
		pcap.loop(1000, jpacketHandler, null);

		/***************************************************************************
		 * Last thing to do is close the pcap handle
		 **************************************************************************/
		pcap.close();
	}
}
