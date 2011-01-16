package oins.panels;

import javax.swing.JTabbedPane;

public class TabPanel extends JTabbedPane{
	
	private static final long serialVersionUID = -8609417737687214276L;
	
	private static final String CONTACTPANEL = "Kontakty";
	private static final String OTHERCONVERSATION = "Inne rozmowy";
	
	private OtherConvPanel otherConvPanel;
	private ContactPanel contactPanel;
	public TabPanel(){
		
		this.setSize(300, 300);
		otherConvPanel = new OtherConvPanel();
		contactPanel = new ContactPanel();
		
		this.add(CONTACTPANEL,contactPanel);		
		this.add(OTHERCONVERSATION,otherConvPanel);			
	}

}
