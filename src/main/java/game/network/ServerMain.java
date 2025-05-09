package game.network;


import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.joml.Vector3f;
import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.start();
        server.bind(54555, 54777); // TCP and UDP ports

        // Register classes for serialization
        server.getKryo().register(NetworkManager.PlayerData.class);
        server.getKryo().register(Vector3f.class);

        server.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof NetworkManager.PlayerData) {
                    NetworkManager.PlayerData data = (NetworkManager.PlayerData) object;
                    System.out.println("Received position from player " + data.id + ": " + data.position);
                    // Broadcast to all clients
                    server.sendToAllTCP(data);
                }
            }
        });

        System.out.println("Server started on port 54555");
    }
}