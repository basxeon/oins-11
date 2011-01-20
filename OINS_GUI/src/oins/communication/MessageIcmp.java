package oins.communication;

import java.util.LinkedList;
import java.util.Queue;

import org.jnetpcap.nio.JBuffer;

public class MessageIcmp {

	Queue<byte []> mesByte;
	String mesS;
	int size;
	
	
	public String getMesS() {
		return mesS;
	}
	MessageIcmp(String s){
		mesS=s;
		mesByte=new LinkedList<byte []>();
		toByte();
	}

	public Queue<byte []> getMesByte() {
		return mesByte;
	}
	public byte[] pollMesByte() {
		return mesByte.poll();
	}

	
	public void toByte(){
		int check= mesS.getBytes().length % 18;
		if(check!=0){
			JBuffer temp=new JBuffer(mesS.getBytes().length +18-check);
			temp.setByteArray(0, mesS.getBytes());
			for(int i=18-check;i>0;i--)
				temp.setByte(mesS.getBytes().length-1 +i, (byte)0);
			for(int k=0; k<temp.size();k+=18){
				
				mesByte.offer(temp.getByteArray(k, 18));
			}
			
		}
		else{
			JBuffer temp=new JBuffer(mesS.getBytes());
			for(int i=0; i<temp.size();i+=18){
						
						mesByte.offer(temp.getByteArray(i, 18));
			}
		}
		setSize(mesByte.size());
		
	}
	
	
	public int getSize() {
		return size;
	}
	private void setSize(int size) {
		this.size = size;
	}
	
	
	

}

	

