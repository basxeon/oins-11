package oins.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import oins.communication.ArpAvail;
import oins.communication.ArpListener;
import oins.communication.NetInterface;
import oins.core.Application;
import oins.tables.ContactTable;
import oins.tables.InterfacesTable;

public class InterfacesPanel extends GenericPanel {

    private static final long serialVersionUID = 85203561313987695L;
    private static final String BUT1 = "Ok";
    private static final Integer BUTXSIZE = new Integer(120);
    private static final Integer BUTYSIZE = new Integer(25);
    private static final String INFO = "                           Komunikator PADSTEG";
    private static final String INFO2 = "Poni¿ej znajduje siê lista interfejsów. \n " + "Wybierz jeden z nich:";

    private static JButton but1;
    private Dimension butDimension;

    private JPanel p1, p2;
    private InterfacesTable interfacesTable;
    private Font f2 = new Font("Calibri", 30, 30);
    private JLabel label1, label2;

    public InterfacesPanel() {
        super();
        this.loadConfiguration();

        this.setLayout(new BorderLayout());
        this.setSize(300, 300);
        this.setBackground(Color.blue);
        p1 = new JPanel();
        p2 = new JPanel(new GridLayout(2, 1));

        Font f = this.getFont();

        label1 = new JLabel(INFO);
        label1.setFont(f2);

        label2 = new JLabel(INFO2);
        label2.setFont(f);
        p2.add(label1);
        p2.add(label2);

        interfacesTable = new InterfacesTable();

        // Ponizej jest ladowanie danych do tabeli - zrobilem przyklad jak to
        // sie robi z pliku
        // konfiguracyjnego u Ciebie powinno byc analogicznie. Pamietaj ze
        // insertNewDate dostaje tablice
        // Object[][]

        interfacesTable.insertNewDate(this.loadConfiguration());

        butDimension = new Dimension(BUTXSIZE, BUTYSIZE);
        but1 = new JButton(BUT1);
        but1.setActionCommand(BUT1);
        but1.setPreferredSize(butDimension);
        but1.setEnabled(false);
        but1.addActionListener(this);

        p1.add(but1);
        p1.setBackground(Color.GRAY);
        this.add(p2,BorderLayout.NORTH);
        this.add(interfacesTable,BorderLayout.CENTER);
        this.add(p1,BorderLayout.SOUTH);

    }

    public Object[][] loadConfiguration() {
        
		NetInterface interfaces = new NetInterface();
    	String[] table =interfaces.getInterfaces();
    	Object[][] tableObj = new Object[table.length][2];
        for (int i = 0; i < table.length; i++) {
            tableObj[i][0] = i;
            tableObj[i][1] = table[i];
        }
        return tableObj;
    }

    public static JButton getBut1() {
        return but1;
    }

    public void insertNewData(Object[][] data) {
        interfacesTable.insertNewDate(data);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(BUT1)) {
        	
        	InformationPanel.setTxtF2(NetInterface.getIpAddress(InterfacesTable.getChoosenInterfaceId()));
        	InformationPanel.setTxtF1(NetInterface.getMAC(InterfacesTable.getChoosenInterfaceId()));
        	NetInterface.setDeviceID(InterfacesTable.getChoosenInterfaceId());
        	
            int response = JOptionPane.showConfirmDialog(this, "Wybra³eœ interfejs: " + InterfacesTable.getChoosenInterface() + ". \n Czy chcesz kontynuowaæ?",
                    "Question", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                InformationPanel.setTxtF3(InterfacesTable.getChoosenInterface());
                Timer timer1 = new Timer();
                ArpListener arplist= new ArpListener();
                
         timer1.schedule(arplist, 10, ArpListener.ARP_CLEAN*60*1000);
         ContactTable.updateRowNotAvailAll();
         ArpAvail avail;
		try {
			avail = new ArpAvail(ContactPanel.getIpAdressesInt(),3);
			avail.sendArp();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			
                Application.changeCard();
            } else
                return;
        }
    }

}
