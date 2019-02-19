package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
public class S_Command extends AbstractAction{
	private Camera camera;
	public S_Command(Camera camera) {
		super();
		this.camera = camera;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.setAmount(camera.getAmount() + .05f);
		camera.moveAlongN();
		System.out.println("|SCOMMAND|");

	}

}
