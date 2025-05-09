package game.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.joml.Vector3f;
import java.io.IOException;

public class NetworkManager {
    private Server server;
    private Client client;
    private boolean isServer;
    private boolean isConnected;

    public static class PlayerData {
        public int id;
        public Vector3f position;
    }

    public NetworkManager(boolean isServer, String host, int port) {
        this.isServer = isServer;
        if (isServer) {
            try {
                server = new Server();
                server.start();
                server.bind(port);
                server.getKryo().register(PlayerData.class);
                server.getKryo().register(Vector3f.class);
                isConnected = true;
                System.out.println("Server started on port " + port);
            } catch (IOException e) {
                System.err.println("Failed to start server: " + e.getMessage());
                isConnected = false;
            }
        } else {
            try {
                client = new Client();
                client.start();
                client.connect(5000, host, port);
                client.getKryo().register(PlayerData.class);
                client.getKryo().register(Vector3f.class);
                isConnected = true;
                System.out.println("Connected to server at " + host + ":" + port);
            } catch (IOException e) {
                System.err.println("Failed to connect to server: " + e.getMessage());
                isConnected = false;
            }
        }
    }

    public void sendPlayerPosition(int id, Vector3f position) {
        if (!isConnected) {
            return;
        }
        PlayerData data = new PlayerData();
        data.id = id;
        data.position = position;
        if (isServer) {
            server.sendToAllTCP(data);
        } else {
            client.sendTCP(data);
        }
    }

    public void addListener(Listener listener) {
        if (!isConnected) {
            return;
        }
        if (isServer) {
            server.addListener(listener);
        } else {
            client.addListener(listener);
        }
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void close() {
        if (isServer && server != null) {
            server.close();
            server.stop();
        }
        if (!isServer && client != null) {
            client.close();
            client.stop();
        }
        isConnected = false;
    }
}