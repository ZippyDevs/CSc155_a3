package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
public class O_Command extends AbstractAction {  // move light forward
	private Light_Moveable light;
	
	public O_Command(Light_Moveable light) {
		super();
		this.light = light;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		light.setAmount(light.getAmount() - .05f);
		light.moveAlongL();
		System.out.println("|OCOMMAND|");

	}

}
