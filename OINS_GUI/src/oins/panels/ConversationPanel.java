package oins.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import oins.communication.TcpPacket;

public class ConversationPanel extends GenericPanel implements KeyListener {

    private static final long serialVersionUID = 85203561313987695L;
    private static final String BUT1 = "Wyslij";
    private static final String LABEL1 = "Status:";
    private static final Integer BUTXSIZE = new Integer(120);
    private static final Integer BUTYSIZE = new Integer(25);
    private static final Dimension TXTFILEDSIZE = new Dimension(520, 50);

    private static JButton but1;
    private Dimension butDimension;

    private JPanel p1, p2, p3, p4, p5, p6;
    private JLabel label1;
    private static JTextField txtF1, txtF2;
    private static JTextArea txtArea;
    private static JScrollPane areaScrollPane;
    private Integer[] ipSender;

    public ConversationPanel(Integer[] ipSender) {
        super();
        this.setIpSender(ipSender);
        this.setLayout(new BorderLayout());
        this.setSize(300, 300);
        p1 = new JPanel();
        p2 = new JPanel(new BorderLayout());
        p3 = new JPanel(new GridLayout(1, 2));
        p4 = new JPanel();
        p5 = new JPanel();
        p6 = new JPanel();

        txtArea = new JTextArea();
        txtArea.setFont(new Font("Serif", Font.ITALIC, 16));
        txtArea.setLineWrap(true);
        txtArea.setWrapStyleWord(true);

        areaScrollPane = new JScrollPane(txtArea);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        areaScrollPane.setPreferredSize(new Dimension(250, 250));
        areaScrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Plain Text"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)), areaScrollPane.getBorder()));

        butDimension = new Dimension(BUTXSIZE, BUTYSIZE);
        but1 = new JButton(BUT1);
        but1.setActionCommand(BUT1);
        but1.setPreferredSize(butDimension);

        label1 = new JLabel(LABEL1);

        txtF1 = new JTextField();
        txtF1.setFocusable(false);
        txtF1.setPreferredSize(butDimension);

        txtF2 = new JTextField();
        txtF2.setFocusable(true);
        txtF2.setPreferredSize(TXTFILEDSIZE);
        txtF2.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""), BorderFactory
                .createEmptyBorder(1, 1, 1, 1)), txtF2.getBorder()));
        txtF2.requestFocusInWindow();

        p6.add(but1);
        p5.add(label1);
        p5.add(txtF1);

        p3.add(p5);
        p3.add(p6);

        p4.add(txtF2);

        p2.add(p3, BorderLayout.CENTER);
        p2.add(p4, BorderLayout.NORTH);

        p1.setBackground(Color.GRAY);
        this.add(areaScrollPane, BorderLayout.CENTER);
        this.add(p2, BorderLayout.SOUTH);

        but1.addActionListener(this);
        txtF2.addKeyListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(BUT1)) {

            TcpPacket packet = new TcpPacket(this);

            ConversationPanel.setInTxtArea(txtF2.getText());
            packet.sendPacket(getIpSender(), txtF2.getText());

        }
    }

    public static JTextField getTxtF1() {
        return txtF1;
    }

    public static JTextField getTxtF2() {
        return txtF2;
    }

    public static void setTxtF1(JTextField txtF1) {
        ConversationPanel.txtF1 = txtF1;
    }

    public static void setTxtF2(JTextField txtF2) {
        ConversationPanel.txtF2 = txtF2;
    }

    public static JTextArea getTxtArea() {
        return txtArea;
    }

    public static void setInTxtArea(String mess) {
        String DATE_FORMAT = "HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        if(!txtArea.getText().equals("")){
            ConversationPanel.txtArea.append("\n\n"); 
        }
        ConversationPanel.txtArea.append((sdf.format(new java.util.Date()) + "\n"));
        ConversationPanel.txtArea.append(mess);
        ConversationPanel.txtArea.setCaretPosition(txtArea.getDocument().getLength());
        txtF2.selectAll();
    }

    @Override
    public void keyPressed(KeyEvent arg0) {
        switch (arg0.getKeyCode()) {
        case KeyEvent.VK_ENTER:
            TcpPacket packet = new TcpPacket(this);
            packet.sendPacket(getIpSender(), txtF2.getText());
            ConversationPanel.setInTxtArea(txtF2.getText());

            break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    private void setIpSender(Integer[] ipSender) {
        this.ipSender = ipSender;
    }

    public Integer[] getIpSender() {
        return ipSender;
    }

    public void setTxtF1(String text) {
        txtF1.setText(text);
    }

}
