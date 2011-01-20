package oins.communication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import org.jnetpcap.packet.JPacket;

/*
 * protokol			PID
 * TCP 				1
 * ICMP				2
 */


public class ArpPacketDecoding {
	JPacket packet;
	int PidMax = 4;
	int Pid;
	
	public int getPid() {
		return Pid;
	}


	ArpPacketDecoding(JPacket p){
		packet=p;
	}
	
	
	public int checkPID() throws NoSuchAlgorithmException{
	
		final MessageDigest messageDigest = MessageDigest
	    .getInstance("MD5");
		int i=1;
		if(packet.size()==60){
		for(;i<=PidMax;i++){		
			messageDigest.update(packet.getByteArray(6, 6),0,6);
			messageDigest.update((byte)i);
			messageDigest.update(packet.getByteArray(42, 2),0,2);
			 byte[] buff = messageDigest.digest();
			 
			 if( Conversion.equal(buff,packet.getByteArray(44,16))){
				 Pid=i;
				 return i;
			 }	 
		}
		}
		Pid=0;
		return 0;	
		
	}
		
	

}
