package oins.reciever.arp;

public class ByteConversion {
	
	public static byte[] convert(Integer[] tab){
		 byte[] b= new byte[tab.length];
		 for (int i=0; i< tab.length; i++){
			 b[i]= tab[i].byteValue();
		 }
		 return b;
	}
	
	public static boolean equal(byte[] tab1, byte[] tab2){
		if(tab1.length!=tab2.length)
			return false;
		for(int i=0; i<tab1.length; i++){
			if(tab1[i]!=tab2[i])
				return false;
		}
		return true;
	}
}