package oins.sender.arp;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;

public class ArpPadding {

	byte  PID;
	byte[] MAC;
	byte RD[];
/**
 * 	
 * @param pid
 * @param mac
 */
	ArpPadding(byte pid, byte[] mac){
		PID=pid;
		MAC=mac;
		/*
		 * losowa liczba RD
		 */
		SecureRandom random = null;
		try {
			random = SecureRandom.getInstance("SHA1PRNG", "SUN");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 RD = new byte[2];
	     random.nextBytes(RD);
	    
	     
	}
/**
 * 	
 * @return
 */
	public byte[] getRD() {
		return RD;
	}

	public byte[] hash() throws NoSuchAlgorithmException{
		
		final MessageDigest messageDigest = MessageDigest
	    .getInstance("MD5");

		 messageDigest.update(MAC, 0, MAC.length);
		 messageDigest.update(PID);
		 messageDigest.update(RD, 0, RD.length);
		 byte[] buff = messageDigest.digest();
		
		return buff;
		
		
	}
	
}
