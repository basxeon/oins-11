package oins.communication;

public class Conversion {
	
	public static Integer[] toInt( byte[] tab){
		
		Integer[] tabInt = new Integer[tab.length];
		for ( int i=0; i<tab.length;i++){
			tabInt[i]=(int)tab[i] & 0xff;
		}
		return tabInt;
	}
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
	
	public static Integer netmask(byte[] tab1){
		Integer[] tabInt = toInt(tab1);
		Integer nmask=0;
		if(tabInt[3]==0){
			if(tabInt[2]==0){
				if(tabInt[1]==0){
					if(tabInt[0]==0){
						nmask=0x00000000;
					}
					else{
						nmask=0xFF000000;	
					}
				}
				else{
					nmask=0xFFFF0000;
				}
			}
			else{
				nmask=0xFFFFFF00;
			}
			
		}
		else{
			nmask=0xFFFFFFFF;
		}
		return nmask;
	}
	public static String toString(Integer[] tab){
		String s="";
		for(Integer temp: tab){
			s+=temp.toString();
			s+="/";
		}
		return s;
	}
	

}
