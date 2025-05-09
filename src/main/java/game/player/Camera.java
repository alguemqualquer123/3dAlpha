package game.player;

import game.ui.GameUtils;
import game.player.Player;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class Camera {
    private Vector3f position;
    private float pitch;
    private float yaw;
    private Player player;
    private boolean isFirstPerson;

    public Camera(Player player, boolean isFirstPerson) {
        this.player = player;
        this.isFirstPerson = isFirstPerson;
        this.position = new Vector3f();
        this.pitch = 0;
        this.yaw = 0;
        update();
    }

    public void update() {
        Vector3f playerPos = player.getPosition();
        if (isFirstPerson) {
            position.set(playerPos.x, playerPos.y + 1.6f, playerPos.z); // Olhos do jogador
        } else {
            // Câmera em terceira pessoa
            float distance = 5.0f; // Distância do jogador
            float angle = (float) Math.toRadians(yaw);
            float offsetX = (float) (Math.sin(angle) * distance);
            float offsetZ = (float) (Math.cos(angle) * distance);
            position.set(playerPos.x - offsetX, playerPos.y + 2.0f, playerPos.z - offsetZ);
            pitch = 20.0f; // Inclinação para ver o jogador
        }
    }

    public void applyProjection(float aspectRatio) {
        // Configuração da matriz de projeção movida para MainGame
    }

    public Matrix4f getViewMatrix() {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotate((float) Math.toRadians(pitch), new Vector3f(1, 0, 0));
        viewMatrix.rotate((float) Math.toRadians(yaw), new Vector3f(0, 1, 0));
        viewMatrix.translate(-position.x, -position.y, -position.z);
        return viewMatrix;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}