package game.ui;

import org.joml.Vector3f;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.stb.STBImage.*;

import org.lwjgl.system.MemoryStack;
import java.util.Objects;

public class GameUtils {

    // Carrega uma textura a partir de um arquivo
    public static int loadTexture(String path) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true); // Opcional, dependendo da orientação da imagem
            ByteBuffer image = stbi_load(path, width, height, channels, 4); // força 4 canais (RGBA)
            if (image == null) {
                throw new RuntimeException("Falha ao carregar textura: " + path + "\n" + stbi_failure_reason());
            }

            int textureId = glGenTextures();
            glBindTexture(GL_TEXTURE_2D, textureId);

            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width.get(), height.get(), 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

            // Configuração de filtros
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            glGenerateMipmap(GL_TEXTURE_2D);

            stbi_image_free(image);
            return textureId;
        }
    }

    // Calcula a direção da câmera com base em yaw e pitch
    public static Vector3f calculateCameraDirection(float yaw, float pitch) {
        Vector3f direction = new Vector3f();
        direction.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        direction.y = (float) Math.sin(Math.toRadians(pitch));
        direction.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));

        if (direction.lengthSquared() == 0) {
            return new Vector3f(0, 0, -1); // direção padrão
        }


        return direction.normalize();
    }

    public static float toRadians(float degrees) {
        return (float) Math.toRadians(degrees);
    }
}
