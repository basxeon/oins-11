package oins.tables;

import java.awt.Dimension;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import oins.panels.ContactPanel;

public class ContactTable extends JPanel {
    /**
	 * 
	 */
    private static final long serialVersionUID = 3350204013232632122L;
    private static JTable table;
    private ContactTableModel tableModel;
    private static String contactName;
    private static String ipAddress;

    public ContactTable() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        tableModel = new ContactTableModel();
        table = new JTable() {

            private static final long serialVersionUID = -7186239388477240658L;

            public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                int realColumnIndex = convertColumnIndexToModel(colIndex);
                if (realColumnIndex == 0 || realColumnIndex == 1) {
                    try {
                        tip = "" + getValueAt(rowIndex, colIndex);
                    } catch (Exception e2) {
                    }
                    return tip;
                } else {
                    return null;
                }
            }
        };

        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setModel(tableModel);

        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                int viewRow = table.getSelectedRow();
                if (viewRow >= 0) {
                    ContactPanel.getBut1().setEnabled(true);
                    ContactTable.setContactName(table.getValueAt(viewRow, 0).toString());
                    ContactTable.setIpAddress(table.getValueAt(viewRow, 1).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);
    }

    public void insertNewDate(Object[][] data) {
        tableModel.setNewData(data);
    }

    public static String getContactName() {
        return contactName;
    }
    
    public static String searchUserName(Integer[] temp){
        for(int i = 0; i < table.getRowCount(); i++){
            if(temp.equals(ContactPanel.getAddressIpAsInteger((String)table.getValueAt(i, 1)))){
                return (String)table.getValueAt(i, 0);
            }
        }
        return "";
        
    }

    public static void setContactName(String contactName) {
        ContactTable.contactName = contactName;
    }

    public static String getIpAddress() {
        return ipAddress;
    }

    public static void setIpAddress(String ipAddress) {
        ContactTable.ipAddress = ipAddress;
    }

}
