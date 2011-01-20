package oins.tables;

import javax.swing.table.DefaultTableModel;

public class OtherConvTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 328501000808821225L;
    private static final Object[] COLUMNNAMES = { "Nadawca", "Odbiorca", "Prot_Nad.", "Data" };

    public OtherConvTableModel(Object[][] data) {
        super(data, COLUMNNAMES);
        this.isCellEditable(getRowCount(), getColumnCount());

    }

    public OtherConvTableModel() {
        super(null, COLUMNNAMES);

    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }

    public void setNewData(Object[][] data) {
        setDataVector(convertToVector(data), convertToVector(COLUMNNAMES));
    }

}
