package oins.communication;

import org.jnetpcap.packet.JPacket;

public class MessDecoding {
	static String msTcp;
	static String msIcmp;
	

	
	public static final String getMsTcp() {
		return msTcp;
	}
	public static final String getMsIcmp() {
		return msIcmp;
	}

	public static  void decodeTcp(JPacket packet){
		byte[] txt=packet.getByteArray(54, 6);
		msTcp= new String(txt);
	}
	
	public static  void decodeIcmp(JPacket packet){
		byte[] txt=packet.getByteArray(42, 18);
		msIcmp= new String(txt);
	}
}