package oins.core;

import java.awt.Container;

import javax.swing.JFrame;

import oins.panels.SendArpPanel;


/**
 * @author Robert
 * Main class which is responsible for GUI
 */
public class ConvFrame{
	
	private static final String FRAMENAME = "Komunikator PADSTEG";;
	private static final long serialVersionUID = 7264967178880598959L;
	private static final Integer SIZEX = new Integer(440);
	private static final Integer SIZEY = new Integer(280);
	

	private SendArpPanel sendArpPanel;
	private void addComponentToPane(Container pane){
		
		sendArpPanel = new SendArpPanel();
				
		pane.add(sendArpPanel);
		
	}
	
	/**
	 * Methods which create main Frame of application.
	 */
	private static void createAndShowGUI(){
		JFrame appFrame = new JFrame(FRAMENAME);
		appFrame.setResizable(false);
		ConvFrame app = new ConvFrame();
		app.addComponentToPane(appFrame.getContentPane());
		appFrame.setLocation(200, 100);
		appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		appFrame.setSize(SIZEX, SIZEY);
		appFrame.setVisible(true);
	}
	
	public static void create(){
		
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}
	

}
