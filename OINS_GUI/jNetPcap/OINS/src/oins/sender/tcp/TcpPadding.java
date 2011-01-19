package oins.sender.tcp;

import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.JPacket;

public class TcpPadding {
	
	public static void padding(JPacket packet, byte[] mess){
		JBuffer buff = new JBuffer(60);
		if(packet.size()==60){
			/*
			 * TODO
			 */
		}
		else{
			buff.setByteArray(0, packet.getByteArray(0, packet.size()));
			int i=60-packet.size();
			buff.setByteArray(i, mess);
		
			packet.peer(buff);
		}
		
	}

}
