package game.datamanager;

import com.sun.source.tree.IdentifierTree;
import game.player.Player;
import game.world.Terrain;
import org.joml.Vector3f;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DataManager {
    private static final String SAVE_FILE = "player_data.json";
    private static Terrain terrain;;
    public void savePlayer(Player player) {
        JSONObject obj = new JSONObject();
        Vector3f pos = player.getPosition();
        obj.put("x", (double) pos.x); // Armazena como Double para compatibilidade JSON
        obj.put("y", (double) pos.y);
        obj.put("z", (double) pos.z);

        try (FileWriter file = new FileWriter(SAVE_FILE)) {
            file.write(obj.toJSONString());
        } catch (IOException e) {
            System.err.println("Erro ao salvar player_data.json: " + e.getMessage());
        }
    }

    public Player loadPlayer() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            // Cria um arquivo padrão se não existir
            Player defaultPlayer = new Player(0, 5, 0); // Posição inicial y=5 para evitar colisão com terreno
            savePlayer(defaultPlayer);
            System.out.println("Posição inicial do jogador: " + defaultPlayer.getPosition());
            return defaultPlayer;
        }

        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(file)) {
            JSONObject obj = (JSONObject) parser.parse(reader);
            Number x = (Number) obj.get("x");
            Number y = (Number) obj.get("y");
            Number z = (Number) obj.get("z");
            if (x == null || y == null || z == null) {
                System.err.println("Dados inválidos em player_data.json. Usando posição padrão.");
                return new Player(0f, terrain.getHeight(0f, 0f), 0f);
            }
            return new Player(x.floatValue(), y.floatValue(), z.floatValue());
        } catch (Exception e) {
            System.err.println("Erro ao carregar player_data.json: " + e.getMessage());
            return new Player(0, 5, 0); // Posição padrão
        }
    }
}