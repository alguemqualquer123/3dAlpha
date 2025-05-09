package game.world;

import org.joml.Vector3f;
import static org.lwjgl.opengl.GL11.*;

public class Terrain {
    private float[][] heightMap;
    private int width = 100;
    private int depth = 100;

    public Terrain() {
        generateHeightMap();
    }

    private void generateHeightMap() {
        heightMap = new float[width][depth];

        // Piso plano
        for (int x = 0; x < width; x++) {
            for (int z = 0; z < depth; z++) {
                heightMap[x][z] = 0;
            }
        }

        // Plataforma elevada no centro
        int startX = width / 2 - 5;
        int endX = width / 2 + 5;
        int startZ = depth / 2 - 5;
        int endZ = depth / 2 + 5;

        for (int x = startX; x < endX; x++) {
            for (int z = startZ; z < endZ; z++) {
                heightMap[x][z] = 5.0f; // Plataforma de 5 unidades de altura
            }
        }
    }

    public void render() {
        glColor3f(0.0f, 1.0f, 0.0f); // Verde

        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE); // Modo wireframe para ver o relevo (opcional)

        for (int x = 0; x < width - 1; x++) {
            glBegin(GL_TRIANGLE_STRIP);
            for (int z = 0; z < depth; z++) {
                Vector3f v1 = new Vector3f(x - width / 2, heightMap[x][z], z - depth / 2);
                Vector3f v2 = new Vector3f(x + 1 - width / 2, heightMap[x + 1][z], z - depth / 2);
                glVertex3f(v1.x, v1.y, v1.z);
                glVertex3f(v2.x, v2.y, v2.z);
            }
            glEnd();
        }

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL); // Volta ao normal
    }

    public float getHeight(float x, float z) {
        int ix = (int) x + width / 2;
        int iz = (int) z + depth / 2;

        if (ix >= 0 && ix < width && iz >= 0 && iz < depth) {
            return heightMap[ix][iz];
        }
        return 0;
    }
}
