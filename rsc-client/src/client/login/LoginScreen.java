package client.login;

import java.util.Random;

import client.RuneClient;
import client.State;
import client.StateRenderer;
import client.net.Packet;

public class LoginScreen extends State {

	private LoginScreenRenderer renderer;

	public LoginScreen(RuneClient launcher) {
		super(launcher);

		renderer = new LoginScreenRenderer(this);
	}

	@Override
	public StateRenderer getRenderer() {
		return renderer;
	}

	@Override
	public void pollInput() {
		if (input.wasLeftClickReleased()) {
			// For now, just connect to the server immediately

			boolean connected = launcher.isConnected() || launcher.connect("localhost", 7780);
			if (connected) {
				sendLoginRequest("Player" + new Random().nextInt(1000), "topsecret");
			}
		}
	}

	@Override
	public void tick() {
	}

	/**
	 * Sends a login request to the server.
	 */
	private void sendLoginRequest(String name, String password) {
		// Create a packet.
		// Send the username and password.
		Packet packet = new Packet(2);

		// Send the client build.
		packet.putDouble(0.1); // TODO Add client build value to code somewhere?

		// Send name
		packet.putString(name);
		
		// Send password
		packet.putString(password); // TODO Use RSA

		// Send the packet.
		launcher.sendPacket(packet);
	}

	@Override
	public void reset() {
	}
	
}
