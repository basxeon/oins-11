package oins.tables;

import javax.swing.table.DefaultTableModel;

public class ContactTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 328501000808821225L;
    private static final Object[] COLUMNNAMES = { "Nazwa Kontaktu", "Addres IP","DostÍpnosc" };

    public ContactTableModel(Object[][] data) {
        super(data, COLUMNNAMES);
        this.isCellEditable(getRowCount(), getColumnCount());

    }

    public ContactTableModel() {
        super(null, COLUMNNAMES);

    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void setNewData(Object[][] data) {
        setDataVector(convertToVector(data), convertToVector(COLUMNNAMES));
    }

    public void updateRow(Object value,int row){
        setValueAt(value, row, 2);
    }
}
