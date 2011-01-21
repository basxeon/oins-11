package oins.communication;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.network.Ip4;
import oins.panels.ConversationPanel;


public class TcpPacket {

	private static final int PACKET_RATE=20;
	private static  Ip4 ip = new Ip4();
	private ConversationPanel panel;
	private static MessageTcp message;
	private static Integer[] Sendip;
	private static Pcap pcap;
	
	public TcpPacket(ConversationPanel panel){
	this.setPanel(panel);	
	}
	
	public void sendPacket(Integer[] ipSender,String mess){
		
		StringBuilder str= new StringBuilder();
		str.append(mess);
		str.append("*");
		
		setSendip(ipSender);
		panel.setTxtF1("Wysy³anie...");
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
		
		PcapBpfProgram program = new PcapBpfProgram();
		String expression = "ip proto \\tcp and less 61";
		
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
		System.out.println(getSendip());
		JPacketHandler<String> sendingHandler = new JPacketHandler<String>() {

			public void nextPacket(JPacket packet, String user) {
			    try{
				if(packet.hasHeader(ip)){
					if(Conversion.equal(ip.destination(), Conversion.convert(getSendip())) &&
							Conversion.equal(ip.source(), Conversion.convert(NetInterface.getCurrIp()))){
						/*
						 * trzeba sprawdzic czy wspiera send czy inject
						 */
						
						TcpPadding.padding(packet, message.pollMesByte());
						
						if(Pcap.isSendPacketSupported()){
							
							pcap.sendPacket(packet.getByteArray(0, packet.size()));	
							}
						else if(Pcap.isInjectSupported()){
							pcap.inject(packet.getByteArray(0, packet.size()),0,packet.size());
								
							}
					}
				}}catch(Exception e){
				    e.printStackTrace();
				}
			}
		};
		JPacketHandler<String> waitingHandler = new JPacketHandler<String>() {

			public void nextPacket(JPacket packet, String user) {
			
				/*
				 * nic nie rob
				 */
			}
		};
		MessageTcp m=new MessageTcp(str.toString());
		setMessage(m);
		setSendip(getPanel().getIpSender());
		
		for (int k = 1; k>0 ;k=message.getMesByte().size()){
			pcap.loop(1, sendingHandler, null);
			pcap.loop(PACKET_RATE, waitingHandler, null);	
		}
		
		System.out.println("Wyslalem");
		pcap.close();
		panel.setTxtF1("Wysy³ano");
	}

	private void setPanel(ConversationPanel panel) {
		this.panel = panel;
	}

	public ConversationPanel getPanel() {
		return panel;
	}

	public static MessageTcp getMessage() {
		return message;
	}

	private static void setMessage(MessageTcp message) {
		TcpPacket.message = message;
	}

	public static Integer[] getSendip() {
		return Sendip;
	}

	private static void setSendip(Integer[] sendip) {
		Sendip = sendip;
	}

}
