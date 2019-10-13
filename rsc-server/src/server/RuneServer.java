package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import server.entityhandling.defs.DoorDef;
import server.entityhandling.defs.GameObjectDef;
import server.entityhandling.defs.ItemDef;
import server.entityhandling.defs.NpcDef;
import server.entityhandling.defs.PrayerDef;
import server.entityhandling.defs.SpellDef;
import server.entityhandling.defs.TileDef;
import server.game.world.World;
import server.game.world.WorldLoader;
import server.net.AcceptThread;
import server.net.Client;
import server.net.SendPacketTask;
import server.net.SocketUtils;
import server.packets.Packet;
import server.packets.builders.LoginSuccessPacketBuilder;
import server.packets.from_server.KickPacket;
import server.res.ResourceLoader;
import server.res.Resources;

public class RuneServer {

    public static final int SERVER_PORT = 7780;

    // Kick reasons
    public static final byte SKIP_REASON = 0;
    public static final byte SERVER_CLOSED = 1;

    /**
     * Time to wait for all clients to get kicked before shutting down.
     */
    private static final long SHUTDOWN_TIME = 0;

    private ExecutorService executor;

    private ServerSocket serverSocket;

    private boolean exiting;
    private volatile boolean dead;

    private List<Client> clients = new ArrayList<>();

    public RuneServer() throws IOException {
        serverSocket = new ServerSocket(SERVER_PORT);
        executor = Executors.newCachedThreadPool();
    }

    public void load() {

        // Load resources
        Resources.doors       = (DoorDef[])       ResourceLoader.loadGzipData("defs/DoorDef.xml.gz");
        Resources.gameObjects = (GameObjectDef[]) ResourceLoader.loadGzipData("defs/GameObjectDef.xml.gz");
        Resources.items       = (ItemDef[])       ResourceLoader.loadGzipData("defs/ItemDef.xml.gz");
        Resources.npcs        = (NpcDef[])        ResourceLoader.loadGzipData("defs/NPCDef.xml.gz");
        Resources.prayers     = (PrayerDef[])     ResourceLoader.loadGzipData("defs/PrayerDef.xml.gz");
        Resources.spells      = (SpellDef[])      ResourceLoader.loadGzipData("defs/SpellDef.xml.gz");
        Resources.tiles       = (TileDef[])       ResourceLoader.loadGzipData("defs/TileDef.xml.gz");

        // Load world
        World world = new World();
        WorldLoader worldLoader = new WorldLoader();
        worldLoader.loadWorld(world);
    }

    public void requestExit() {
        exiting = true;
    }

    public boolean isExiting() {
        return exiting;
    }

    public void kill() {

        // Ensure this method is only called once
        if (dead) {
            return;
        }

        dead = true;
        exiting = true;

        // Stop accepting new clients
        SocketUtils.close(serverSocket);

        // Kick all clients
        for (Client client : clients) {
            Packet kickPacket = new KickPacket(SERVER_CLOSED);
            executor.execute(new SendPacketTask(client, kickPacket));
        }

        // Wait for clients to get kicked
        executor.shutdown();
        try {
            executor.awaitTermination(SHUTDOWN_TIME, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Kill all client connections
        for (Client client : clients) {
            client.kill();
        }
    }

    public void addClient(Client client) {
        synchronized (clients) {
            System.out.println("Client connected from " + client.getAddress());
            Packet packet = new LoginSuccessPacketBuilder().build();
            executor.execute(new SendPacketTask(client, packet));
            clients.add(client);
        }
    }

    public void run() throws IOException {

        // Listen for new clients in a separate thread
        AcceptThread acceptThread = new AcceptThread(this, serverSocket);
        executor.execute(acceptThread);

        // Server main thread
        while (!exiting) {

            removeDeadClients();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeDeadClients() {
         synchronized (clients) {
             clients = clients
                     .stream()
                     .filter(c -> !c.isDead())
                     .collect(Collectors.toList());
         }
    }

}
