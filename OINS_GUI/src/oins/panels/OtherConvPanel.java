package oins.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;

import oins.tables.OtherConvTable;

public class OtherConvPanel extends GenericPanel {

    private static final long serialVersionUID = 85203561313987695L;
    private static final String BUT1 = "Ok";
    private static final Integer BUTXSIZE = new Integer(120);
    private static final Integer BUTYSIZE = new Integer(25);

    private static JButton but1;
    private Dimension butDimension;

    private JPanel p1;
    private OtherConvTable otherConvTable;

    public OtherConvPanel() {
        super();
        this.setLayout(new BorderLayout());
        this.setSize(300, 300);
        this.setBackground(Color.blue);
        p1 = new JPanel();

        otherConvTable = new OtherConvTable();
        otherConvTable.insertNewDate(null);

        butDimension = new Dimension(BUTXSIZE, BUTYSIZE);
        but1 = new JButton(BUT1);
        but1.setActionCommand(BUT1);
        but1.setPreferredSize(butDimension);
        but1.setEnabled(false);

        p1.add(but1);
        p1.setBackground(Color.GRAY);
        this.add(otherConvTable, BorderLayout.CENTER);
        this.add(p1, BorderLayout.SOUTH);

        but1.addActionListener(this);

    }

    public static JButton getBut1() {
        return but1;
    }

    public void insertNewData(Object[][] data) {
        otherConvTable.insertNewDate(data);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(BUT1)) {
            // clearAllDate();
        }
    }

}
