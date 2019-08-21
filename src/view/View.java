package view;

import main.Main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class View extends JFrame {	
	static JTextArea regAddress = new JTextArea("Addrs\n");
	static JTextArea regNames = new JTextArea("Nmes\n");
	static JTextArea regData = new JTextArea("Data\n");
	static JTextArea opCodeView = new JTextArea("opCd\n");
	static JTextArea instructionsView = new JTextArea("Instructions\n\n");

	final int width = 370, height = 350;
	final int east_view_width = 35, east_view_height = 290;

	public View() {	

		setTitle("Anber|Uranus");
		setResizable(false);
		setSize(width, height);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JPanel frame = new JPanel();
		Color color = new Color(0x207C6C);
		frame.setBackground(color);
		frame.setLayout(new BorderLayout(1,5));
		this.add(frame);

		//East Side 
		JPanel middleEastPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 1, 0));
		middleEastPanel.setPreferredSize(new Dimension(200, east_view_height));	
		middleEastPanel.setOpaque(false);
		frame.add(middleEastPanel, BorderLayout.EAST); 

		//Op. Codes Panel
		JPanel opCodeViewPanel = new JPanel();
		opCodeViewPanel.setOpaque(false);
		opCodeView.setBackground(color);
		opCodeView.setForeground(Color.white);
		opCodeView.setEditable(false);
		opCodeView.setWrapStyleWord(true);
		opCodeView.setPreferredSize(new Dimension(east_view_width, east_view_height));
		opCodeViewPanel.add(opCodeView);	
		middleEastPanel.add(opCodeViewPanel);
		//East Side part left: (Registers View)
		JPanel regNamesPanel = new JPanel();
		regNamesPanel.setOpaque(false);
		regNames.setBackground(color);
		regNames.setForeground(Color.cyan);
		regNames.setEditable(false);
		regNames.setWrapStyleWord(true);
		regNames.setPreferredSize(new Dimension(east_view_width, east_view_height));
		regNamesPanel.add(regNames, BorderLayout.WEST);
		middleEastPanel.add(regNamesPanel);
		//East Side part Middle: (Registers View)
		JPanel regAddressPanel = new JPanel();
		regAddressPanel.setOpaque(false);
		regAddress.setBackground(color);
		regAddress.setForeground(Color.white);
		regAddress.setEditable(false);
		regAddress.setWrapStyleWord(true);
		regAddress.setPreferredSize(new Dimension(east_view_width, east_view_height));
		regAddressPanel.add(regAddress);		
		middleEastPanel.add(regAddressPanel);
		//East Side part right: (Registers View)
		JPanel regViewPanel = new JPanel();
		regViewPanel.setOpaque(false);
		regData.setBackground(color);
		regData.setForeground(Color.cyan);
		regData.setEditable(false);
		regData.setWrapStyleWord(true);
		regData.setPreferredSize(new Dimension(east_view_width, east_view_height));
		regViewPanel.add(regData);
		middleEastPanel.add(regViewPanel);
		//-----------------------
		//Middle Panel
		JPanel middlePanel = new JPanel();
		//middlePanel.setOpaque(false);
		middlePanel.setBackground(color);
		//Instructions Panel 
		instructionsView.setBackground(color);
		instructionsView.setForeground(Color.LIGHT_GRAY);
		instructionsView.setEditable(false);
		instructionsView.setWrapStyleWord(true);		
		JScrollPane scroll = new JScrollPane(instructionsView);
		scroll.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		scroll.setPreferredSize(new Dimension(140, east_view_height));
		middlePanel.add(scroll);
		frame.add(middlePanel, BorderLayout.WEST);
		//-------------
		update();
		this.setVisible(true);
	}

	public static void main(String[] args) {
		Main.init();
		new View();
	}

	public static void update() {
		for(String s :  Main.regNames) {
			regNames.setText(regNames.getText() + "\n" + s);
			regAddress.setText(regAddress.getText()+"\n"+  Main.regConvertToBinary(s));
		}
		for(String s : Main.registers)
			regData.setText(regData.getText() + "\n" + s);

		for(int i=0; i<Main.opCodes.length; i++)
			opCodeView.setText(opCodeView.getText()+"\n"+ Main.opCodes[i]);
		
		for(String s :  Main.instructions)		
			instructionsView.setText(instructionsView.getText() + s + "\n");
	}
}
