package a3;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class I_Command extends AbstractAction{  // move the light up
	private Light_Moveable light;
	
	public I_Command(Light_Moveable light) {
		super();
		this.light = light;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		light.setAmount(light.getAmount() - .05f);
		light.moveAlongK();
		System.out.println("|ICOMMAND|");

	}

}
