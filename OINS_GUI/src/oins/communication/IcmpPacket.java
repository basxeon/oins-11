package oins.communication;

import oins.panels.ContactPanel;
import oins.panels.ConversationPanel;
import oins.tables.ContactTable;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.JProtocol;
import org.jnetpcap.protocol.network.Icmp;
import org.jnetpcap.protocol.network.Ip4;


public class IcmpPacket {
	
	private static Pcap pcap;
	private static Icmp icmp;
	private static Ip4 ip;
	private static Integer[] ipaddr;
	private static JBuffer buff;
	public static final int PACKET_RATE=2;
	private static MessageIcmp message;
	
	public static MessageIcmp getMessage() {
		return message;
	}



	public static void setMessage(MessageIcmp message) {
		IcmpPacket.message = message;
	}



	public IcmpPacket(){

}
	


	public static void sendPacket(String mess){
		
		StringBuilder str= new StringBuilder();
		str.append(mess);
		str.append("*");
		
		
		ConversationPanel.getTxtF1().setText("Wysylanie...");
		StringBuilder errbuf = new StringBuilder();
		/**
		 * we open PCAP
		 */
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
		
		/*PcapBpfProgram program = new PcapBpfProgram();
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
		}*/
		
		/**
		 * Handlers
		 */
		ip=new Ip4();
		JPacketHandler<String> sendingHandler = new JPacketHandler<String>() {

			public void nextPacket(JPacket packet, String user) {
			   //TODO wysylanie
				if(packet.hasHeader(ip)){
					
						/*
						 * trzeba sprawdzic czy wspiera send czy inject
						 */
						
						JBuffer baf=IcmpPadding.padding( message.pollMesByte());
						
						if(Pcap.isSendPacketSupported()){
							
							pcap.sendPacket(baf);	
							}
						else if(Pcap.isInjectSupported()){
							pcap.inject(baf,0,baf.size());
								
							}
				}
			
		}};
		JPacketHandler<String> waitingHandler = new JPacketHandler<String>() {

			public void nextPacket(JPacket packet, String user) {
			
				/*
				 * nic nie rob
				 */
			}
		};
		MessageIcmp m=new MessageIcmp(str.toString());
		setMessage(m);
	//	setSendip(getPanel().getIpSender());
		
		for (int k = 1; k>0 ;k=message.getMesByte().size()){
			pcap.loop(1, sendingHandler, null);
			pcap.loop(PACKET_RATE, waitingHandler, null);	
		}
		System.out.println("Wyslalem");
		pcap.close();
		ConversationPanel.getTxtF1().setText("Wysylano");
		
	}
	private static void setIpaddr(Integer[] ipaddr) {
		IcmpPacket.ipaddr = ipaddr;
	}



	public static Integer[] getIpaddr() {
		return ipaddr;
	}


	public static void setBuff(JBuffer buff) {
		IcmpPacket.buff = buff;
	}


	public static JBuffer getBuff() {
		return buff;
	}



	
}
