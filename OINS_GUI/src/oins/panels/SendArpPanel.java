package oins.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import oins.core.ConvFrame;
import oins.tables.ContactTable;
import oins.communication.ArpListener;
import oins.communication.ArpPacket;
import oins.communication.TcpListener;

public class SendArpPanel extends GenericPanel {

    private static final long serialVersionUID = 85203561313987695L;
    private static final String BUT1 = "Rozmawiaj";
    private static final String BUT2 = "Wyslij ARP ";
    private static final String LABEL1 = "Status:";
    private static final String LABEL2 = "Wybór odbiorcy:";
    private static final String CHB1 = "TCP ";
    private static final String CHB2 = "ICMP";
    private static final Integer BUTXSIZE = new Integer(120);
    private static final Integer BUTYSIZE = new Integer(25);

    private static JButton but1, but2;
    private Dimension butDimension;

    private JRadioButton radioBut1;
    private JRadioButton radioBut2;
    private ButtonGroup radioGroup;
    private JPanel p1, p2, p3, p4, p5, p6, p7, p8;
    private JLabel label1, label2;
	private Integer[] ipAddress;
    private static JTextField txtF1, txtF2;

    public SendArpPanel() {
        super();
        this.setLayout(new GridLayout(4, 1));
        this.setSize(300, 300);
        p1 = new JPanel(new GridLayout(2, 1));
        p2 = new JPanel(new GridLayout(2, 1));
        p3 = new JPanel();
        p4 = new JPanel();
        p5 = new JPanel();
        p6 = new JPanel();
        p7 = new JPanel();
        p8 = new JPanel();

        butDimension = new Dimension(BUTXSIZE, BUTYSIZE);
        but1 = new JButton(BUT1);
        but1.setActionCommand(BUT1);
        but1.setPreferredSize(butDimension);
        but1.setEnabled(false);

        butDimension = new Dimension(BUTXSIZE, BUTYSIZE);
        but2 = new JButton(BUT2);
        but2.setActionCommand(BUT2);
        but2.setPreferredSize(butDimension);

        label1 = new JLabel(LABEL1);
        label2 = new JLabel(LABEL2);
        label1.setPreferredSize(butDimension);
        label2.setPreferredSize(butDimension);

        txtF1 = new JTextField();
        txtF1.setFocusable(false);
        txtF1.setPreferredSize(butDimension);
        

        txtF2 = new JTextField();
        txtF2.setFocusable(false);
        txtF2.setPreferredSize(butDimension);
        
        if(ArpListener.isRecieving()==true && ArpListener.isSending()==false){
        	txtF1.setText("Odebrano Arp od:" + ArpListener.getCurrIp());
        	if(ArpListener.getPid()==1){
        		txtF2.setText("TCP");
        	}
        	else if (ArpListener.getPid()==2){
        		txtF2.setText("ICMP");
        		
        	}      	
        }
        radioBut2 = new JRadioButton(CHB2);
        radioBut2.setActionCommand(CHB2);
        radioBut2.addActionListener(this);

        radioBut1 = new JRadioButton(CHB1);
        radioBut1.setActionCommand(CHB1);
        radioBut1.setSelected(true);
        radioBut1.addActionListener(this);

        radioGroup = new ButtonGroup();
        radioGroup.add(radioBut1);
        radioGroup.add(radioBut2);

        p5.add(radioBut1);
        p6.add(radioBut2);
    
        p3.add(label1);
        p3.add(txtF1);

        p4.add(label2);
        p4.add(txtF2);

        p7.add(but1);
        p8.add(but2);

        p1.add(p5);
        p1.add(p6);
        p2.add(p3);
        p2.add(p4);

        p1.setBackground(Color.GRAY);
        this.add(p1);
        this.add(p8);
        this.add(p2);
        this.add(p7);

        if(ArpListener.isRecieving()==false){
        	setIpAddress(ContactPanel.getAddressIpAsInteger(ContactTable.getIpAddress()));
        }
        else{
        	setIpAddress(ArpListener.getCurrIpInt());
        }
        
        but1.addActionListener(this);
        but2.addActionListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(BUT1)) {
        	if(ArpListener.isRecieving()==false){
        		TcpListener tcp=new TcpListener(ContactPanel.getAddressIpAsInteger(ContactTable.getIpAddress()));
            	tcp.start();
        	}
        	else{
        		TcpListener tcp=new TcpListener(ArpListener.getCurrIpInt());
            	tcp.start();
        	}
        	ArpListener.setRecieving(false);
			ArpListener.setSending(false);
        	
            ConvFrame.changeCard();
        }
        else if(e.getActionCommand().equals(BUT2)){
        	try {
        		//Narazie domyœlnie ustawiam pid=1
				
				ArpListener.setSending(true);
				if(ArpListener.isRecieving()==true && ArpListener.isSending()==true){
					ArpPacket arpPacket= new ArpPacket(ArpListener.getCurrIpInt(),1);
					arpPacket.sendArp();
					but1.setEnabled(true);
					setTxtF1("Wyslano pakiet Arp");
					if(arpPacket.getPid()==1){
						setTxtF2("TCP");
		        	}
		        	else if (arpPacket.getPid()==2){
		        		setTxtF2("ICMP");
		        		
		        	}
				
				}
				else {
					ArpPacket arpPacket= new ArpPacket(getIpAddress(),1);
					arpPacket.sendArp();
					setTxtF1("Wysy³anie...");
				}
			} catch (NoSuchAlgorithmException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        }
    }
    public static void setTxtF1(String text) {
        txtF1.setText(text);
    }

    public static void setTxtF2(String text) {
        txtF2.setText(text);
    }
    public static JTextField getTxtF1() {
        return txtF1;
    }

    public static JTextField getTxtF2() {
        return txtF2;
    }

    public static void setTxtF1(JTextField txtF1) {
        SendArpPanel.txtF1 = txtF1;
    }

    public static void setTxtF2(JTextField txtF2) {
        SendArpPanel.txtF2 = txtF2;
    }

	public void setIpAddress(Integer[] ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Integer[] getIpAddress() {
		return ipAddress;
	}

	public static JButton getBut1() {
		return but1;
	}

	public static JButton getBut2() {
		return but2;
	}
	
	

}
