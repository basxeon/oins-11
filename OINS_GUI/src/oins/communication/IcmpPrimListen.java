package oins.communication;

import javax.swing.JOptionPane;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;

public class IcmpPrimListen extends Thread {

	
	private static Pcap pcap;
	private static Icmp icmp;
	private static Ip4 ip;
	private static Integer[] ipaddr;
	private static JBuffer buff;
	//private static JPacket packet;
	private static JPacketHandler<String> listenHandler;
	private static boolean recieved=false;
	
	public IcmpPrimListen(Integer [] address){
		
		setIpaddr(address);
		StringBuilder errbuf = new StringBuilder();
		int snaplen = 64; 
		int flags = Pcap.MODE_PROMISCUOUS; 
		int timeout = 2 * 1000; // 2 minutes
		 pcap =
		    Pcap.openLive(NetInterface.getDevice().getName(), snaplen, flags, timeout, errbuf);

		/**
		 * obs³uga nie otwarcia pcap
		 */
		if (pcap == null) {
			//TODO obsluga wyjatku
			System.err.printf("Error while opening device for capture: "
			    + errbuf.toString());
			return;
		}
		/**
		 * FILTR
		 */
		
		PcapBpfProgram program = new PcapBpfProgram();
		String expression = "ip proto \\icmp";
		
		int optimize = 0;         // 0 = false
		int netmask = Conversion.netmask(NetInterface.getDevice().getAddresses().get(0).getNetmask().getData());
		
		if (pcap.compile(program, expression, optimize, netmask) != Pcap.OK) {
			//TODO osbluga wyjatku
			System.err.println(pcap.getErr());
			return;
		}
		if (pcap.setFilter(program) != Pcap.OK) {
			//TODO obsluga wyjatku
			System.err.println(pcap.getErr());
			return;		
		}
		
		/**
		 * Handlers
		 */
		ip= new Ip4();
		icmp= new Icmp();
		buff= new JBuffer(60);
		listenHandler = new JPacketHandler<String>() {

			public void nextPacket(JPacket packet, String user) {
			    try{
				
					if(packet.hasHeader(ip)){
						if(Conversion.equal(ip.destination(), Conversion.convert(getIpaddr())) &&
								Conversion.equal(ip.source(), Conversion.convert(NetInterface.getCurrIp()))){
							
							if(packet.hasHeader(icmp)){
									
								if(icmp.type()==Icmp.IcmpType.ECHO_REQUEST_ID){
									System.out.println("jestem tu-icmp echo request");
									if(!isRecieved()){
										//setPacket(packet);
										setBuff(new JBuffer(packet.getByteArray(0, 60)));
										setRecieved(true);
										JOptionPane.showMessageDialog(null, "Mozesz skorzystac z protokolu ICMP do komunikacji", "Information", JOptionPane.INFORMATION_MESSAGE);
									}
									
								}	
								
							}
						
					}
					
						
					}
				}catch(Exception e){
				    System.out.println("w catch");
				}
		}
		};

		
	}

	public void run()
	{
		pcap.loop(8, listenHandler, null);
		System.out.println("skonczylem nasluchiwac pingow");
		System.out.println(getBuff().toHexdump());
		pcap.close();
		setRecieved(false);
		
	}

	
	private static void setIpaddr(Integer[] ipaddr) {
		IcmpPrimListen.ipaddr = ipaddr;
	}



	public static Integer[] getIpaddr() {
		return ipaddr;
	}


	public static void setBuff(JBuffer buff) {
		IcmpPrimListen.buff = buff;
	}


	public static JBuffer getBuff() {
		return buff;
	}

	public static void setRecieved(boolean recieved) {
		IcmpPrimListen.recieved = recieved;
	}

	public static boolean isRecieved() {
		return recieved;
	}

	/*private static void setPacket(JPacket packet) {
		IcmpPrimListen.packet = packet;
	}

	public static JPacket getPacket() {
		return packet;
	}*/
	
}
