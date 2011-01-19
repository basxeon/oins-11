package oins.sender.arp;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.jnetpcap.PcapAddr;
import org.jnetpcap.PcapIf;
import org.jnetpcap.nio.JBuffer;

public class ArpPacket {
	
	JBuffer buff; // bufor z danymi pakietu

public JBuffer getBuff() {
		return buff;
	}

	public void setBuff(JBuffer buff) {
		this.buff = buff;
	}

ArpPacket(PcapIf device, Integer[] adr) throws IOException, NoSuchAlgorithmException{
		
		buff = new JBuffer(60);
		List<PcapAddr> adresy = device.getAddresses(); //nie wiem dlaczego jest tutaj lista, wybieramy element 0
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
		buff.setByteArray(6, device.getHardwareAddress());
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
		buff.setByteArray(22, device.getHardwareAddress());
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
		buff.setByteArray(38, ByteConversion.convert(adr));
		/*
		 * Zaszyfrowane dane
		 */
		byte pid = 1;
		ArpPadding pad = new ArpPadding(pid, device.getHardwareAddress());
		buff.setByteArray(42, pad.getRD());
		buff.setByteArray(44, pad.hash());
		
	}
	
}
