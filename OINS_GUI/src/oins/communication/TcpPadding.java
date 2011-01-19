package oins.communication;

import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.packet.JPacket;

public class TcpPadding {
	
	public static void padding(JPacket packet, byte[] mess){
		JBuffer buff = new JBuffer(60);
		if(packet.size()==60){
			/*
			 * TODO
			 */
			System.out.println("60");
		}
		else{
			buff.setByteArray(0, packet.getByteArray(0, packet.size()));
			buff.setByteArray(packet.size(), mess);
			packet.peer(buff);
		}
		
	}

}
