package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
public class J_Command extends AbstractAction {  //  move light to the left
	private Light_Moveable light;
	
	public J_Command(Light_Moveable light) {
		super();
		this.light = light;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		light.setAmount(light.getAmount() - .05f);
		light.moveAlongJ();
		System.out.println("|JCOMMAND|");

	}

}
