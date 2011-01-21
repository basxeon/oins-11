package oins.communication;

import oins.panels.ConversationPanel;

import org.jnetpcap.Pcap;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.network.Ip4;


public class IcmpPacket {
	
	private static Pcap pcap;

	private static Ip4 ip;
	private static Integer[] ipaddr;
	private static JBuffer buff;
	public static final int PACKET_RATE=800;
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
		
		
		ConversationPanel.getTxtF1().setText("Wysylanie");
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

		
		for (int k = 1; k>0 ;k=message.getMesByte().size()){
			
			pcap.loop(PACKET_RATE, waitingHandler, null);	
			pcap.loop(1, sendingHandler, null);
		}
		System.out.println("Wyslalem");
		pcap.close();
		ConversationPanel.getTxtF1().setText("Wysylano");
		
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
