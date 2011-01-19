package oins.core;

import java.awt.CardLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JPanel;

import oins.communication.ArpListener;
import oins.panels.ContactPanel;
import oins.panels.ConversationPanel;
import oins.panels.SendArpPanel;
import oins.tables.ContactTable;

/**
 * @author Robert Main class which is responsible for GUI
 */
public class ConvFrame {

    private static final String FRAMENAME = "Komunikator PADSTEG";;
    private static final long serialVersionUID = 7264967178880598959L;
    private static final String SENDARPPANEL = "sendArpPanel";
    private static final String CONVPANEL = "conversationPanel";
    private static final Integer SIZEX = new Integer(540);
    private static final Integer SIZEY = new Integer(380);

    private static JPanel switchPanel;
    private static CardLayout cardlo;
    private static String cardState;
    private SendArpPanel sendArpPanel;
    private ConversationPanel conversationPanel;
    


    private void addComponentToPane(Container pane) {

        switchPanel = new JPanel();
        cardlo = new CardLayout();
        switchPanel.setLayout(cardlo);
        cardState = SENDARPPANEL;

        sendArpPanel = new SendArpPanel();
        System.out.println(ArpListener.isRecieving());
        System.out.println(ArpListener.isSending());
        if(ArpListener.isSending()==false && ArpListener.isRecieving()==false){
        	conversationPanel = new ConversationPanel(ContactPanel.getAddressIpAsInteger(ContactTable.getIpAddress()));
        	System.out.println(ContactTable.getIpAddress());
        }
        
        else if (ArpListener.isRecieving()==true){
        	conversationPanel = new ConversationPanel(ArpListener.getCurrIpInt());
        	
        }
       
        switchPanel.add(sendArpPanel, SENDARPPANEL);
        switchPanel.add(conversationPanel, CONVPANEL);

        cardlo.show(switchPanel, cardState);

        pane.add(switchPanel);

        // pane.add(sendArpPanel);

    }

    public static void changeCard() {
        if (cardState == SENDARPPANEL) {
            cardlo.show(switchPanel, CONVPANEL);
            cardState = CONVPANEL;
        } else if (cardState == CONVPANEL) {
            cardlo.show(switchPanel, SENDARPPANEL);
            cardState = SENDARPPANEL;
        }
    }

    /**
     * Methods which create main Frame of application.
     */
    private static void createAndShowGUI() {
        JFrame appFrame = new JFrame(FRAMENAME);
        appFrame.setResizable(false);
        ConvFrame app = new ConvFrame();
        app.addComponentToPane(appFrame.getContentPane());
        appFrame.setLocation(200, 100);
        appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        appFrame.setSize(SIZEX, SIZEY);
        appFrame.setVisible(true);
        
        
    }

    public static void create() {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    /*
     * public static void main(String[] args) { ConvFrame.create(); }
     */





}
