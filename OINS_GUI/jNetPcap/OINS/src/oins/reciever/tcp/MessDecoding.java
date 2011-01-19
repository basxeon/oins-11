package oins.reciever.tcp;

import org.jnetpcap.packet.JPacket;

public class MessDecoding {
	static String ms;
	

	
	public static final String getMs() {
		return ms;
	}

	public static final void decode(JPacket packet){
		byte[] txt=packet.getByteArray(54, 6);
		ms= new String(txt);
	}
}
