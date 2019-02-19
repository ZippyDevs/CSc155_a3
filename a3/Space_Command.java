package a3;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
public class Space_Command extends AbstractAction{
	private World camera;
	public Space_Command(World camera) {
		super();
		this.camera = camera;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.toggleAxis();
	}

}
