package oins.communication;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Vector;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.nio.JBuffer;

public class ArpAvail {

	
	int pid;
	Vector<JBuffer > buff2;
	
	public Vector <JBuffer > getBuff() {
		return buff2;
	}
	
	
	public ArpAvail( Integer[][] adr, int p) throws IOException, NoSuchAlgorithmException{
		
		pid=p;
		buff2=new Vector<JBuffer >();
		JBuffer buff = new JBuffer(60);
		
		List<PcapAddr> adresy = NetInterface.getDevice().getAddresses(); 
		/**
		 * nag³ówek ETHERNET
		 */
		/*
		 * adres docelowy : BroadCast
		 */
		buff.setByteArray(0, adresy.get(0).getBroadaddr().getData());
		/*
		 * adres Ÿród³owy
		 */
		buff.setByteArray(6, NetInterface.getDevice().getHardwareAddress());
		/*
		 * ty protokolu: ARP 0x0806
		 */
		buff.setByte(12, (byte)8);
		buff.setByte(13, (byte)6);
		/**
		 * Nag³ówek ARP
		 */
		/*
		 * hardware type : Ethernet 0x0001
		 */
		buff.setByte(14, (byte)0);
		buff.setByte(15, (byte)1);
		/*
		 * protocol type IP 0x0800
		 */
		buff.setByte(16, (byte)8);
		buff.setByte(17, (byte)0);
		/*
		 * hardw size = 6
		 */
		buff.setByte(18, (byte)6);
		/*
		 * protocol size = 4
		 */
		buff.setByte(19, (byte)4);
		/*
		 * operation - request 0x0001
		 */
		buff.setByte(20, (byte)0);
		buff.setByte(21, (byte)1);
		/*
		 * sender Mac address
		 */
		buff.setByteArray(22, NetInterface.getDevice().getHardwareAddress());
		/*
		 * Sender Ip address 
		 */
		buff.setByteArray(28, adresy.get(0).getAddr().getData());
		/*
		 * Target Mac address
		 */
		byte[] mac1= {0,0,0,0,0,0};
		buff.setByteArray(32, mac1);
		
		/*
		 * Zaszyfrowane dane
		 */
		ArpPadding pad ;
		pad= new ArpPadding((byte)pid, NetInterface.getDevice().getHardwareAddress());
		
			
		
		for (int i=0;i<adr.length; i++){
			
			JBuffer tmp= new JBuffer(60);
			tmp.setByteArray(0, buff.getByteArray(0, 32));
			tmp.setByteArray(38, Conversion.convert(adr[i]));
			tmp.setByteArray(42, pad.getRD());
			tmp.setByteArray(44, pad.hash());
			getBuff().add(tmp);
			
		}
	}
	@SuppressWarnings("static-access")
	public void sendArp(){
		
		StringBuilder errbuf = new StringBuilder();
		/**
		 * we open PCAP
		 */
		int snaplen = 64; 
		int flags = Pcap.MODE_NON_PROMISCUOUS; 
		int timeout = 2 * 1000; // 2 minutes
		Pcap pcap =
		    Pcap.openLive(NetInterface.getDevice().getName(), snaplen, flags, timeout, errbuf);

		/**
		 * obs³uga nie otwarcia pcap
		 */
		if (pcap == null) {
			System.err.printf("Error while opening device for capture: "
			    + errbuf.toString());
			return;
		}
		for(int i=0; i<getBuff().size();i++){
			
			if(pcap.isSendPacketSupported()){
				System.out.println(getBuff().get(i).toHexdump());
				pcap.sendPacket(getBuff().get(i));	
			}
			else if(pcap.isInjectSupported()){
				pcap.inject(getBuff().get(i),0,getBuff().size());
			}
		}	
		System.out.println("Wysylam do wszystkich");
		ArpListener.setSend_avail(true);
		pcap.close();
		
	}
}
