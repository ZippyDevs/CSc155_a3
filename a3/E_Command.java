package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class E_Command extends AbstractAction {
	private Camera camera;
	public E_Command(Camera camera) {
		super();
		this.camera = camera;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.setAmount(camera.getAmount() + .05f);
		camera.moveAlongV();
		System.out.println("|ECOMMAND|");

	}

}
