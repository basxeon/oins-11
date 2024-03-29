/**
 * Copyright (C) 2008 Sly Technologies, Inc. This library is free software; you
 * can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version. This
 * library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */


import java.util.ArrayList;
import java.util.List;

import org.jnetpcap.JCaptureHeader;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;

/**
 * This example is the classic libpcap example shown in nearly every tutorial on
 * libpcap. It gets a list of network devices, presents a simple ASCII based
 * menu and waits for user to select one of those interfaces. We will just
 * select the first interface in the list instead of taking input to shorten the
 * example. Then it opens that interface for live capture. Using a packet
 * handler it goes into a loop to catch a few packets, say 10. Prints some
 * simple info about the packets, and then closes the pcap handle and exits.
 * <p>
 * Here is the output generated by this example (output truncated to single
 * packet):
 * 
 * <pre>
 *  Network devices found:
 *  #0: \Device\NPF_{BC81C4FC-242F-4F1C-9DAD-EA9523CC992D} [Intel(R) PRO/100 VE Network Connection (Microsoft's Packet Scheduler) ]
 *  #1: \Device\NPF_GenericDialupAdapter [Adapter for generic dialup and VPN capture]
 * 
 *  Choosing 'Intel(R) PRO/100 VE Network Connection (Microsoft's Packet Scheduler) ' on your behalf:
 * 
 *  Packet caplen=60 wirelen=60
 * 
 *  Ethernet:  ******* Ethernet (Eth) offset=0 length=14
 *  Ethernet: 
 *  Ethernet:      destination = 00-15-F2-78-37-F3
 *  Ethernet:           source = 00-0C-41-50-4C-D0
 *  Ethernet:         protocol = 0x800 (2048)
 *  Ethernet: 
 *  ip4:  ******* ip4 (ip) offset=14 length=20
 *  ip4: 
 *  ip4:          version = 4
 *  ip4:             hlen = 5 [*4 = 20 bytes]
 *  ip4:            diffs = 0x0 (0)
 *  ip4:                    0000 00..  = [0] reserved bit: not set
 *  ip4:                    .... ..0.  = [0] ECN bit: ECN capable transport: no
 *  ip4:                    .... ...0  = [0] ECE bit: ECE-CE: no
 *  ip4:           length = 40
 *  ip4:            flags = 0x2 (2)
 *  ip4:                    0..  = [0] reserved bit: not set
 *  ip4:                    .1.  = [1] don't fragment: set
 *  ip4:                    ..0  = [0] more fragments: not set
 *  ip4:               id = 0xE7FC (59388)
 *  ip4:           offset = 0
 *  ip4:     time to live = 241 router hops
 *  ip4:         protocol = 6
 *  ip4:  header checksum = 0x5262 (21090)
 *  ip4:           source = 216.34.181.65
 *  ip4:      destination = 192.168.1.100
 *  ip4: 
 *  tcp:  ******* tcp (tcp) offset=34 length=20
 *  tcp: 
 *  tcp:      source port = 80
 *  tcp: destination port = 4193
 *  tcp:         sequence = 0xD535911F (3577057567)
 *  tcp:  acknowledgement = 0x8D944518 (2375304472)
 *  tcp:    header length = 5 [*4 = 20 bytes]
 *  tcp:         reserved = 0
 *  tcp:            flags = 0x11 (17)
 *  tcp:                    0... ....  = [0] congestion window reduces (CWR): not set
 *  tcp:                    .0.. ....  = [0] ecn-echo: not set
 *  tcp:                    ..0. ....  = [0] urgent pointer: pointer not set
 *  tcp:                    ...1 ....  = [1] acknowledgement: is present
 *  tcp:                    .... 0...  = [0] push: flag is not set
 *  tcp:                    .... .0..  = [0] reset: flag is not set
 *  tcp:                    .... ..0.  = [0] synchronize: flag is not set
 *  tcp:                    .... ...1  = [1] finish: flag is set
 *  tcp:           window = 13488 bytes [13Kb]
 *  tcp:         checksum = 0xE1FF (57855)
 *  tcp:           urgent = 0
 *  tcp: 
 *  payload:  ******* payload (data) offset=54 length=6
 *  payload: 
 *  payload: 0036: 00000000 0000                         \0 \0 \0 \0 \0 \0           
 *  payload: 
 *  Packet caplen=54 wirelen=54
 * </pre>
 * 
 * </p>
 * 
 * @author Mark Bednarczyk
 * @author Sly Technologies, Inc.
 */
public class ClassicPcapExampleUsingJPacket {

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
		 * Third we create a packet handler which will receive packets from the
		 * libpcap loop.
		 **************************************************************************/
		JPacketHandler<String> jpacketHandler = new JPacketHandler<String>() {

			public void nextPacket(JPacket packet, String user) {
				final JCaptureHeader header = packet.getCaptureHeader();

				System.out.printf("Packet caplen=%d wirelen=%d\n", header.caplen(),
				    header.wirelen());

				/*
				 * For packets, generate output using an internal TextFormatter that
				 * sends output to a StringBuilder, then calls on its toString() method
				 * to generate the final string which is returned and send to 
				 * System.out. 
				 * 
				 * Alternative would be to send output directly to System.out, bypassing 
				 * any intermediate string: 
				 * 
				 * JFormatter out = new TextFormatter(); // output to System.out 
				 * out.format(packet);   // Format and send output to System.out
				 * 
				 * Or to generate XML output:
				 * 
				 * PrintStream outputstream = // My output stream
				 * JFormatter out = next XmlFormatter(outputstream);
				 * out.format(packet); 
				 */
				System.out.println(packet.toString());

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
		pcap.loop(10, jpacketHandler, "jNetPcap rocks!");

		/***************************************************************************
		 * Last thing to do is close the pcap handle
		 **************************************************************************/
		pcap.close();
	}
}