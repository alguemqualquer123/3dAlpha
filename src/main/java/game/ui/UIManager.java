package game.ui;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.glfw.GLFW.*;

public class UIManager {
    private boolean isPaused;
    private final int width;
    private final int height;

    public UIManager(int width, int height) {
        this.isPaused = false;
        this.width = width;
        this.height = height;
    }

    public void render() {
        if (isPaused) {
            //System.out.println("Renderizando menu de pausa...");
            glDisable(GL_DEPTH_TEST);
            glMatrixMode(GL_PROJECTION);
            glPushMatrix();
            glLoadIdentity();
            glOrtho(0, width, height, 0, -1, 1);
            glMatrixMode(GL_MODELVIEW);
            glPushMatrix();
            glLoadIdentity();

            // Botão de pausa (quadrado cinza centralizado)
            glBegin(GL_QUADS);
            glColor3f(0.5f, 0.5f, 0.5f);
            float buttonWidth = 200;
            float buttonHeight = 100;
            float x = (width - buttonWidth) / 2;
            float y = (height - buttonHeight) / 2;
            glVertex2f(x, y);
            glVertex2f(x + buttonWidth, y);
            glVertex2f(x + buttonWidth, y + buttonHeight);
            glVertex2f(x, y + buttonHeight);
            glEnd();

            glPopMatrix();
            glMatrixMode(GL_PROJECTION);
            glPopMatrix();
            glMatrixMode(GL_MODELVIEW);
            glEnable(GL_DEPTH_TEST);
            glColor3f(1.0f, 1.0f, 1.0f); // Restaura a cor padrão
        }
    }

    public void handleInput(long window) {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            isPaused = !isPaused;
            System.out.println("Jogo pausado: " + isPaused);
            try {
                Thread.sleep(200); // Evita múltiplas trocas rápidas
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isPaused() {
        return isPaused;
    }
}