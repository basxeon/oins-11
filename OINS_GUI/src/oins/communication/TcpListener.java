package oins.communication;

import javax.swing.JOptionPane;

import oins.panels.ConversationPanel;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.protocol.network.Ip4;

public class TcpListener extends Thread {
	
	private Pcap pcap; 
	JPacketHandler<String> jpacketHandler;
	private static  Ip4 ip = new Ip4();
	private static Integer[] ipSender;


	
	
	public TcpListener(Integer[] ipSender){
		
		TcpListener.ipSender=ipSender;
		StringBuilder errbuf = new StringBuilder();
		
		int snaplen = 61; // capture packet less than 61B
		int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
		int timeout = 60 * 10000; // 600 seconds= 10 minutes
		//int timeout = 1; 
		setPcap(Pcap.openLive(NetInterface.getDevice().getName(), snaplen, flags, timeout, errbuf));
		

		if (pcap == null) {
			System.err.printf("Error while opening device for capture: "
			    + errbuf.toString());
			return;
		}
		
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
		JPacketHandler<String> listeningHandler = new JPacketHandler<String>() {

			public void nextPacket(JPacket packet, String user) {
				
				try{	
				if(packet.hasHeader(ip)){
				if(Conversion.equal(ip.destination(), Conversion.convert(NetInterface.getCurrIp()) ) &&
						Conversion.equal(ip.source(),Conversion.convert(getIpSender()) )){
				
				if(packet.size()>54){
					if(packet.getByte(54)!=0){
						MessDecoding.decodeTcp(packet);
						System.out.println("dekoduje");
						//setText(MessDecoding.getMs());
						
						ConversationPanel.setInTxtArea(MessDecoding.getMsTcp());
				}
				}
				}
                    }
                } catch(Exception e){
                   System.out.println("w catch");
                }
			}
			
		};
		setJpacketHandler(listeningHandler);
		
	}

	public void run() {
		// ilosc pakietow
		
			getPcap().loop(400000000, getJpacketHandler(),"OINS");
			System.out.println("Koniec");
			JOptionPane.showMessageDialog(null, "Zacznij jeszcze raz rozmowe", "Information", JOptionPane.INFORMATION_MESSAGE);
			pcap.close();
		
	}

	public Pcap getPcap() {
		return pcap;
	}

	private void setPcap(Pcap pcap) {
		this.pcap = pcap;
	}

	public JPacketHandler<String> getJpacketHandler() {
		return jpacketHandler;
	}

	private void setJpacketHandler(JPacketHandler<String> jpacketHandler) {
		this.jpacketHandler = jpacketHandler;
	}

	public static Integer[] getIpSender() {
		return ipSender;
	}

	public static void setIpSender(Integer[] ipSender) {
		TcpListener.ipSender = ipSender;
	}


	

}
