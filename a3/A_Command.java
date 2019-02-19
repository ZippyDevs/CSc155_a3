package a3;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

public class A_Command extends AbstractAction {
	private Camera camera;
	public A_Command(Camera camera) {
		super();
		this.camera = camera;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.setAmount(camera.getAmount() - .05f);
		camera.moveAlongU();
		System.out.println("|ACOMMAND|");

		
	}

}
