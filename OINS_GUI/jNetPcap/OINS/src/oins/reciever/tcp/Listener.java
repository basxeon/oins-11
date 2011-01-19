package oins.reciever.tcp;

import java.util.ArrayList;
import java.util.List;

import oins.sender.arp.ByteConversion;
import oins.sender.tcp.Message;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.network.Ip4;

public class Listener {
	public static void main(String[] args) {
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

		final PcapIf device = alldevs.get(1); // Pick one
		System.out.printf("\nChoosing '%s' on your behalf:\n", device
		    .getDescription());

		
		int snaplen = 62 ; // Capture all packets, no trucation
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
		
		final Ip4 ip = new Ip4();
		final Integer[] sendAddr={10,1,0,50 };

		JPacketHandler<String> jpacketHandler = new JPacketHandler<String>() {

			public void nextPacket(JPacket packet, String user) {
				
				/*
				 * Dziala tylko dla IPv4
				 */
				if(packet.hasHeader(ip)){
				
					
					if(ByteConversion.equal(ip.destination(), device.getAddresses().get(0).getAddr().getData() ) &&
							ByteConversion.equal(ip.source(),ByteConversion.convert(sendAddr) )){
	
					
					if(packet.size()>54){
						
						if(packet.getByte(54)!=0){
							//System.out.println(packet.toHexdump());
							System.out.println("Odbieram cos, sprawdzmy co..");
							MessDecoding.decode(packet);
							System.out.println(MessDecoding.getMs());
							
					}
					
					}
						
				
					
					
					}
					
					
					
				}

			}
		};
		
		pcap.loop(100000, jpacketHandler, null);

		/***************************************************************************
		 * Last thing to do is close the pcap handle
		 **************************************************************************/
		pcap.close();
	}
}
