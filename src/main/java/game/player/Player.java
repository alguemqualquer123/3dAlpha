package game.player;

import game.ui.GameUtils;
import game.world.Terrain;
import org.joml.Vector3f;

import java.util.UUID;

import static org.lwjgl.glfw.GLFW.*;

public class Player {
    UUID playerId;
    private Vector3f position;
    private Vector3f velocity;
    private float yaw;
    private float pitch;
    private float speed = 5.0f;
    private Terrain terrain;

    public Player(float x, float y, float z) {
        position = new Vector3f(x, y, z);
        velocity = new Vector3f(0, 0, 0);
        yaw = 0;
        pitch = 0;
    }

    public void setPlayerId(UUID playerId) {
        this.playerId = playerId;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public void update(long window, float deltaTime) {
        pitch = clampPitch(pitch); // garante pitch válido

        Vector3f direction = GameUtils.calculateCameraDirection(yaw, pitch);

        if (containsNaN(direction)) {
            System.out.println("Direção inválida: " + direction);
            direction.set(0, 0, -1); // padrão para frente
        }

        Vector3f moveDir = new Vector3f();

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            moveDir.add(direction);
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            moveDir.sub(direction);
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            Vector3f left = new Vector3f(direction).cross(new Vector3f(0, 1, 0)).normalize();
            moveDir.sub(left);
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            Vector3f right = new Vector3f(direction).cross(new Vector3f(0, 1, 0)).normalize();
            moveDir.add(right);
        }

        if (moveDir.lengthSquared() > 0) {
            moveDir.normalize();
            velocity.set(moveDir.mul(speed * deltaTime));
            position.add(velocity);

            System.out.println("Passo do jogador em: " + position);
        }

        if (terrain != null) {
            float terrainHeight = terrain.getHeight(position.x, position.z);
            position.y = terrainHeight;
        }
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }

    public void setMouseInput(double xoffset, double yoffset) {
        yaw += xoffset * 0.1f;
        pitch -= yoffset * 0.1f;
        pitch = clampPitch(pitch);
    }

    private float clampPitch(float value) {
        return Math.max(-89.0f, Math.min(89.0f, value));
    }

    private boolean containsNaN(Vector3f vec) {
        return Float.isNaN(vec.x) || Float.isNaN(vec.y) || Float.isNaN(vec.z);
    }
}
