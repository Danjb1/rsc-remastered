package client;

import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import client.game.Game;
import client.loading.LoadingScreen;
import client.login.LoginScreen;
import client.net.Connection;
import client.net.Packet;

/**
 * Class responsible for setting up and running the game.
 *
 * <p><i>Based on <code>GameShell.java</code> from other RSC sources.</i>
 *
 * @author Dan Bryce
 */
public class RuneClient {
	
	private final Logger logger = Logger.getLogger(getClass().getName());

	private static final int WINDOW_WIDTH = 512;
	private static final int WINDOW_HEIGHT = 346;
	private static final String WINDOW_TITLE = "OpenRSC";

	private static final int MS_PER_FRAME = 16; // 60fps

	/**
	 * Flag used to tell the game to exit.
	 *
	 * The original RSC used an exit timer instead, to give the game time to
	 * finish any outstanding operations before exiting.
	 */
	private boolean exiting;

	private JFrame frame;
	private JPanel gamePanel;
	private Canvas canvas;
	private BufferedImage screenBuffer;

	// Client states
	private State state;
	private LoadingScreen loadingScreen;
	private LoginScreen loginScreen;
	private Game game;

	// Network
	private Connection connection;
	private BlockingQueue<Packet> packetQueue = new LinkedBlockingQueue<>();
	private long pingLastTime = 0L;

	// Packet constants
	private final int OPCODE_RSA_HANDSHAKE = 0;
	private final int OPCODE_PING = 1;
	private final int OPCODE_LOGIN_RESPONSE = 2;

	public RuneClient() {
		createFrame(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_TITLE);

		screenBuffer = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
		canvas = new Canvas(screenBuffer);

		this.loadingScreen = new LoadingScreen(this);
		this.loginScreen = new LoginScreen(this);
		this.game = new Game(this);
	}

	private void createFrame(int width, int height, String title) {

		// Create the content pane
		gamePanel = new JPanel();
		gamePanel.setPreferredSize(new Dimension(width, height));

		// Create the frame itself
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		// Pseudo-fullscreen if window fills the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setUndecorated(width == screenSize.width && height == screenSize.height);
		frame.setContentPane(gamePanel);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.toFront();
	}

	public void load() {

		//LoadingScreen loadingScreen = new LoadingScreen(this);
		state = loadingScreen;

		while (!loadingScreen.isLoaded()){
			loadingScreen.continueLoading();
			render();
			try {
				// Don't hog the thread
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void run() {
		Thread.currentThread().setName(WINDOW_TITLE);
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		
		while (!exiting) {
			long before = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());

			pollInput();
			tick();
			render();

			int elapsed = (int) (TimeUnit.NANOSECONDS.toMillis(System.nanoTime()) - before);
			int sleepTime = MS_PER_FRAME - elapsed;

			if (sleepTime < 1) {
				sleepTime = 1;
			}

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void pollInput() {
		Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mouseLoc, gamePanel);
		Input input = state.getInput();
		input.setMousePos(mouseLoc.x, mouseLoc.y);

		synchronized (input) {
			state.pollInput();
			input.consume();
		}
	}

	private void tick() {
		pollNetwork();
		state.tick();
	}

	private void render() {

		// Clear the Canvas
		canvas.clear();

		// Render the state onto our Canvas
		state.getRenderer().render(canvas);

		// Render this Canvas to the screen
		gamePanel.getGraphics().drawImage(canvas.getImage(), 0, 0, null);
	}

	private void changeState(State newState) {

		// Remove listeners from previous state
		Input input = state.getInput();
		gamePanel.removeMouseListener(input);
		frame.removeKeyListener(input);

		// Reset previous state
		state.reset();

		// Set new state
		state = newState;
		state.start();

		// Add listeners to new state
		input = state.getInput();
		gamePanel.addMouseListener(input);
		frame.addKeyListener(input);
	}

	/**
	 * Handles network logic.
	 */
	private void pollNetwork() {
		final long currentTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());

		// Send ping.
		if (isConnected() && currentTime - pingLastTime > 15000) {
			pingLastTime = currentTime;
			sendPacket(new Packet(1));
			//System.out.println("PING");
		}

		// Execute the incoming packets.
		List<Packet> toProcess = new ArrayList<Packet>();
		packetQueue.drainTo(toProcess);
		for (Packet packet : toProcess) {

			// Opcode 0 reserved for future pre-login handshake.
			if (packet.getOpcode() == OPCODE_RSA_HANDSHAKE) {
				continue;
			}

			// Handle pong
			if (packet.getOpcode() == OPCODE_PING) {
				//System.out.println("PONG");
				continue;
			}

			// Login response
			if (packet.getOpcode() == OPCODE_LOGIN_RESPONSE) {
				handleLoginResponse(packet);
				continue;
			}

			// The game state is enabled.
			// Pass the incoming packet to the game state.
			if (state instanceof Game) {
				game.executePacket(packet);
				continue;
			}

		}

	}

	private void handleLoginResponse(Packet packet) {
		// The login response code.
		boolean loginAccepted = packet.getBoolean();

		// Login request accepted.
		if (loginAccepted) {
			String displayName = packet.getBase37();
			int sessionId = packet.getInt();
			int privilege = packet.getByte();
			game.loggedIn(displayName, sessionId, privilege);
			changeState(new Game(this));
			return;
		}

		// Login request denied.
		// Read the error message.
		String errorMessage = packet.getString();
		
		// TODO pass the errorMessage to the login screen
		System.out.println("Login Rejected, Reason: " + errorMessage);
	}

	/**
	 * Tries to connect to the server.
	 */
	public boolean connect(String address, int port) {
		if (isConnected()) {
			disconnect();
			return true;
		}
		logger.info("Connecting to server " + address + ":" + port);
		this.connection = new Connection(this);

		if (connection.connect(address, port)) {
			logger.info("Connection established");
		}
		return isConnected();
	}

	/**
	 * Disconnects from the server.
	 */
	public void disconnect() {
		connection.disconnect();
		connection = null;
		packetQueue.clear();

		// Go to login screen.
		if (state instanceof Game) {
			changeState(loginScreen);
		}
	}

	public boolean isConnected() {
		return connection != null && connection.isConnected();
	}

	/**
	 * Sends a packet to the server.
	 */
	public void sendPacket(Packet packet) {
		connection.sendPacket(packet);
	}

	/**
	 * Adds an incoming packet to the queue. Incoming packets have to be queued and
	 * executed in the main thread to prevent concurrency issues.
	 */
	public void queuePacket(Packet packet) {
		packetQueue.add(packet);
	}

	// Called by LoadingScreen when loading is complete.
	public void onLoaded() {
		changeState(loginScreen);
	}

	public State getState() {
		return state;
	}

	public int getWidth() {
		return WINDOW_WIDTH;
	}

	public int getHeight() {
		return WINDOW_HEIGHT;
	}

}
