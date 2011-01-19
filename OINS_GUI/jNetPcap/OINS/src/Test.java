import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.jnetpcap.nio.JBuffer;


public class Test {
	public static void main(String[] args) throws NoSuchAlgorithmException{
		final MessageDigest messageDigest = MessageDigest
        .getInstance("MD5");
		
		byte [] rd = {(byte)1, (byte)2 };
		byte []dupa = {(byte)4, (byte) 3 };
		byte []cyc={(byte)5, (byte) 6, (byte) 12}; 
		 messageDigest.update(rd, 0, rd.length);
		 messageDigest.update(dupa, 0, dupa.length);
		 messageDigest.update(cyc, 0, cyc.length);
		 
		 JBuffer buff = new JBuffer(messageDigest.digest());
		 System.out.println(buff.toHexdump());
		 messageDigest.reset();
		 
		 byte [] tak = {(byte)1, (byte)2,(byte)4, (byte) 3,(byte)5, (byte) 6, (byte) 12}; 
		 
		 messageDigest.update(tak, 0, tak.length);
		 JBuffer buff2 = new JBuffer(messageDigest.digest());
		 System.out.println(buff2.toHexdump());
	}
    
	
}
