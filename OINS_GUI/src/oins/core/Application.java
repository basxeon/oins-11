package oins.core;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import oins.panels.InformationPanel;
import oins.panels.InterfacesPanel;
import oins.panels.TabPanel;


/**
 * @author Robert
 * Main class which is responsible for GUI
 */
public class Application{
	
	private static final String FRAMENAME = "Komunikator PADSTEG";
	private static final String INTERFACESPANEL = "interfacesPanel";
	private static final String COMUNICATORPANEL = "comunicatorPanel";
	private static final long serialVersionUID = 7264967178880598959L;
	private static final Integer SIZEX = new Integer(640);
	private static final Integer SIZEY = new Integer(480);
	private static final Dimension MINMUMSIZE = new Dimension(520,200);
	
	
	
	private TabPanel tabPanel;
	private static JPanel switchPanel;
	private InterfacesPanel interfacesPanel;
	private static CardLayout cardlo;
	private static String cardState;
	private InformationPanel infoPanel;
	private JPanel p1;
	private void addComponentToPane(Container pane){
		
		cardState = INTERFACESPANEL;
		tabPanel = new TabPanel();
		interfacesPanel = new InterfacesPanel();
		infoPanel = new InformationPanel();
		
		p1 = new JPanel(new BorderLayout());
		p1.add(infoPanel,BorderLayout.NORTH);
		p1.add(tabPanel,BorderLayout.CENTER);
		
		switchPanel = new JPanel();
		cardlo = new CardLayout();
		switchPanel.setLayout(cardlo);
		
		switchPanel.add(p1,COMUNICATORPANEL);
		switchPanel.add(interfacesPanel,INTERFACESPANEL);
		
		cardlo.show(switchPanel,cardState );
		
		pane.add(switchPanel);
		
	}
	
	/**
	 * Method which change the card which are currently displayed.
	 */
	public static void changeCard(){
		if(cardState ==INTERFACESPANEL ){
			cardlo.show(switchPanel,COMUNICATORPANEL);
			cardState = COMUNICATORPANEL;
		}
		else if(cardState ==COMUNICATORPANEL){
			cardlo.show(switchPanel,INTERFACESPANEL);
			cardState = INTERFACESPANEL;
		}
		
	}
	
	/**
	 * Methods which create main Frame of application.
	 */
	private static void createAndShowGUI(){
		JFrame appFrame = new JFrame(FRAMENAME);
		appFrame.setResizable(false);
		Application app = new Application();
		app.addComponentToPane(appFrame.getContentPane());
		appFrame.setLocation(200, 100);
		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		appFrame.setSize(SIZEX, SIZEY);
		appFrame.setVisible(true);
		appFrame.setMinimumSize(MINMUMSIZE);
	}
	
	public static void main(String[] args){
		
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
	

}
