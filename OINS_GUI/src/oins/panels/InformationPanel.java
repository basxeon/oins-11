package oins.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class InformationPanel extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3350204013232632122L;
	private static final String LABEL1 = "Twój adres MAC";
	private static final String LABEL2 = "Twoje IP";
	private static final String LABEL3 = "Twój interfejs";
	private static final Dimension BUTTONDIMENSIN = new Dimension(140,25);
	private JLabel label1, label2, label3;
	private static JTextField txtF1, txtF2, txtF3;
    private JPanel p1,p2,p3;

	public InformationPanel() {

		super();
		this.setLayout(new GridLayout(3,1));
		this.setSize(300, 300);
		this.setBackground(Color.blue);
		p1= new JPanel();
		p2= new JPanel();
		p3= new JPanel();
		
		label1= new JLabel(LABEL1);
		label2= new JLabel(LABEL2);
		label3= new JLabel(LABEL3);
		label1.setPreferredSize(BUTTONDIMENSIN);
		label2.setPreferredSize(BUTTONDIMENSIN);
		label3.setPreferredSize(BUTTONDIMENSIN);
		
		txtF1 = new JTextField();
		txtF1.setFocusable(false);
		txtF1.setPreferredSize(BUTTONDIMENSIN);
		
		txtF2 = new JTextField();
		txtF2.setFocusable(false);
		txtF2.setPreferredSize(BUTTONDIMENSIN);
		
		txtF3 = new JTextField();
		txtF3.setFocusable(false);
		txtF3.setPreferredSize(BUTTONDIMENSIN);
	
		p1.add(label1);
		p1.add(txtF1);
		

		p2.add(label2);
		p2.add(txtF2);
		

		p3.add(label3);
		p3.add(txtF3);
		this.add(p1);
		this.add(p2);
		this.add(p3);
		
	}

	public static void setTxtF1(String text) {
		txtF1.setText(text);
	}

	public static void setTxtF2(String text) {
		txtF2.setText(text);
	}

	public static void setTxtF3(String text) {
		txtF3.setText(text);
	}
	
	
	}

