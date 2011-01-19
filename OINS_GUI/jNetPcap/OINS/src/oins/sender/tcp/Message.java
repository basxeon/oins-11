package oins.sender.tcp;

import java.util.Vector;

import org.jnetpcap.nio.JBuffer;

public class Message {
	Vector<byte []> mesB;
	String mesS;
	
	
	Message(String s){
		mesS=s;
		mesB= new Vector<byte[]>();
		mesB.removeAllElements();
		toByte();
	}
	public Vector<byte[]> getMesB() {
		return mesB;
	}
	public byte[] getMesB(int i) {
		return mesB.get(i);
	}
	public int size() {
		return mesB.size();
	}

	
	public void toByte(){
	
		JBuffer temp=new JBuffer(mesS.getBytes());
		int check = (temp.size() % 6);
		for(int i=0; i<temp.size() + check-6;i+=6){
			if(i <temp.size()){
				mesB.add(temp.getByteArray(i, 6));
			}
			else if(i==temp.size()){
				break;
			}
			else{
				JBuffer tmp2 = new JBuffer(6);
				tmp2.setByteArray(0, temp.getByteArray(i,temp.size()-i));
				for(int k=temp.size()-i; k<6;k++ ){
					tmp2.setByte(k, (byte)0);
				}
				mesB.add(tmp2.getByteArray(0, tmp2.size()));
			}
		}
		
	}

}
