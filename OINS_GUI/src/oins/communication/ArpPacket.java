package oins.communication;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapAddr;
import org.jnetpcap.nio.JBuffer;

public class ArpPacket {
	
	JBuffer buff; // bufor z danymi pakietu
	int pid;

public JBuffer getBuff() {
		return buff;
	}

	public ArpPacket( Integer[] adr, int p) throws IOException, NoSuchAlgorithmException{
		pid=p;
		buff = new JBuffer(60);
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
		 * Target Ip address
		 */
		buff.setByteArray(38, Conversion.convert(adr));
		/*
		 * Zaszyfrowane dane
		 */
		
		ArpPadding pad = new ArpPadding((byte)pid, NetInterface.getDevice().getHardwareAddress());
		buff.setByteArray(42, pad.getRD());
		buff.setByteArray(44, pad.hash());
		
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
	if(pcap.isSendPacketSupported()){
		pcap.sendPacket(getBuff());	
	}
	else if(pcap.isInjectSupported()){
		pcap.inject(getBuff(),0,getBuff().size());
	}
	pcap.close();
	
	
}

public int getPid() {
	return pid;
}
	
}

