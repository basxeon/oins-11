package oins.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import oins.core.ConvFrame;
import oins.tables.ContactTable;

public class ContactPanel extends GenericPanel {

    private static final long serialVersionUID = 85203561313987695L;
    private static final String BUT1 = "Rozmawiaj";
    private static final Integer BUTXSIZE = new Integer(120);
    private static final Integer BUTYSIZE = new Integer(25);

    private static JButton but1;
    private Dimension butDimension;

    private JPanel p1;
    private ContactTable contactTable;
    
    private static Integer[][] ipAdressesInt;

    public ContactPanel() {
        super();
        this.setLayout(new BorderLayout());
        this.setSize(300, 300);
        this.setBackground(Color.blue);
        p1 = new JPanel();

        contactTable = new ContactTable();
        contactTable.insertNewDate(this.loadConfiguration());

        butDimension = new Dimension(BUTXSIZE, BUTYSIZE);
        but1 = new JButton(BUT1);
        but1.setActionCommand(BUT1);
        but1.setPreferredSize(butDimension);
        but1.setEnabled(false);

        p1.add(but1);
        p1.setBackground(Color.GRAY);
        this.add(contactTable, BorderLayout.CENTER);
        this.add(p1, BorderLayout.SOUTH);
        but1.addActionListener(this);

    }

    public static JButton getBut1() {
        return but1;
    }

    public Object[][] loadConfiguration() {
        String contacts = new String();
        String ipAddress = new String();
        try {
            Properties configFile = new Properties();
            configFile.load(new FileInputStream("configuration.txt"));
            contacts = configFile.getProperty("contacts");
            ipAddress = configFile.getProperty("ip_address");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "BRAK PLIKU KONFIGURACYJNEGO!", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
        String[] contacTable = contacts.split(" ");
        String[] ipAddressTable = ipAddress.split(" ");
        Integer[][] ipadrInt=new Integer[contacTable.length][4];
        Object[][] tableObj = new Object[contacTable.length][2];
        for (int i = 0; i < contacTable.length; i++) {
            tableObj[i][0] = contacTable[i];
            tableObj[i][1] = ipAddressTable[i];
            ipadrInt[i]=getAddressIpAsInteger(ipAddressTable[i]);
        }
        setIpAdressesInt(ipadrInt);
        return tableObj;
    }

    public void insertNewData(Object[][] data) {
        contactTable.insertNewDate(data);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(BUT1)) {
            int response = JOptionPane.showConfirmDialog(this, "Wybra³eœ u¿ytkownika: " + ContactTable.getContactName() + ". \n Czy chcesz kontynuowaæ?",
                    "Question", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                
                ConvFrame.create();
            } else
                return;
        }
    }

    public static Integer[] getAddressIpAsInteger(String ip) {
        String[] table = new String[ip.split("/").length];
        table = ip.split("/");
        Integer[] tableInt = new Integer[table.length];

        for (int i = 0; i < table.length; i++) {
            tableInt[i] = Integer.parseInt(table[i]);
        }
        
        return tableInt;
    }

	public static Integer[][] getIpAdressesInt() {
		return ipAdressesInt;
	}

	private void setIpAdressesInt(Integer[][] ipAdressesInt) {
		ContactPanel.ipAdressesInt = ipAdressesInt;
	}

}
