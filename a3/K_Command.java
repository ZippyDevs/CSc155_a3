package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
public class K_Command extends AbstractAction{  // move light backwards
	private Light_Moveable light;
	
	public K_Command(Light_Moveable light) {
		super();
		this.light = light;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		light.setAmount(light.getAmount() + .05f);
		light.moveAlongL();
		System.out.println("|KCOMMAND|");

		
	}

}
