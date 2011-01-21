package oins.communication;

import org.jnetpcap.nio.JBuffer;


public class IcmpPadding {

	public static JBuffer padding( byte[] mess){
		JBuffer buff = new JBuffer(60);
		buff.setByteArray(0, IcmpPrimListen.getBuff().getByteArray(0, IcmpPrimListen.getBuff().size()));
		buff.setByteArray(42, mess);
		
		return buff;
		
		
		
	}
}
