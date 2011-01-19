package oins.communication;

import org.jnetpcap.packet.JPacket;

public class MessDecoding {
	static String ms;
	

	
	public static final String getMs() {
		return ms;
	}

	public static  void decode(JPacket packet){
		byte[] txt=packet.getByteArray(54, 6);
		ms= new String(txt);
	}
}