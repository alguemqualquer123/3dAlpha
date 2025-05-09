package game;

import game.datamanager.DataManager;
import game.player.Player;
import game.network.NetworkManager;
import game.player.Camera;
import game.ui.UIManager;
import game.world.Terrain;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.UUID;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;


public class MainGame {
    private long window;
    private final int WIDTH = 1280;
    private final int HEIGHT = 720;
    private Player player;
    private Camera camera;
    private Terrain terrain;
    private UIManager ui;
    private NetworkManager network;
    private DataManager data;
    private long lastTime;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        System.out.println("Inicializando MainGame...");
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Não foi possível inicializar o GLFW.");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "Jogo 3D Mundo Aberto", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Falha ao criar a janela GLFW.");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1);
            IntBuffer pHeight = stack.mallocInt(1);

            glfwGetWindowSize(window, pWidth, pHeight);

            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            glfwSetWindowPos(window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2);
        }

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();
        System.out.println("OpenGL Version: " + glGetString(GL_VERSION));
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.3f, 0.1f, 0.1f, 1.0f);
        //glClearColor(0.2f, 0.3f, 0.6f, 1.0f); // Cor de fundo azul clara para depuração
        // glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        // Inicializar componentes
        System.out.println("Inicializando componentes...");
        data = new DataManager();
        terrain = new Terrain(); // inicializa primeiro
        player = data.loadPlayer();
        player.setPlayerId(UUID.fromString(UUID.randomUUID().toString()));
        player.setTerrain(terrain);
        // System.out.println("Player position: " + player.getPosition());
        camera = new Camera(player, false);
        ui = new UIManager(WIDTH, HEIGHT); // Passa dimensões da janela
        lastTime = System.nanoTime();

        try {
            network = new NetworkManager(false, "localhost", 54555); // Cliente
            if (network.isConnected()) {
                network.addListener(new com.esotericsoftware.kryonet.Listener() {
                    @Override
                    public void received(com.esotericsoftware.kryonet.Connection connection, Object object) {
                        if (object instanceof NetworkManager.PlayerData) {
                            NetworkManager.PlayerData data = (NetworkManager.PlayerData) object;
                            System.out.println("Jogador " + data.id + " em: " + data.position);
                        }
                    }
                });
            } else {
                System.out.println("Running in single-player mode");
            }
        } catch (Exception e) {
            System.err.println("Network initialization failed: " + e.getMessage());
            network = null;
        }

        // Configurar input do mouse
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            double[] x = new double[1];
            double[] y = new double[1];
            glfwGetCursorPos(window, x, y);
            player.setMouseInput(x[0] - WIDTH / 2, y[0] - HEIGHT / 2);
            glfwSetCursorPos(window, WIDTH / 2, HEIGHT / 2);
        });

        System.out.println("Inicialização concluída.");
    }

    private void loop() {
        //System.out.println("Iniciando loop de renderização...");
        while (!glfwWindowShouldClose(window)) {
            float deltaTime = (System.nanoTime() - lastTime) / 1_000_000_000.0f;
            lastTime = System.nanoTime();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            if (!ui.isPaused()) {
                player.update(window, deltaTime);
                camera.update();
                // System.out.println("Camera position: " + camera.getPosition());
                if (network != null && network.isConnected()) {
                    network.sendPlayerPosition(1, player.getPosition());
                }
            }

            ui.handleInput(window);

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            float aspectRatio = (float) WIDTH / HEIGHT;
            float fov = (float) Math.toRadians(70.0f);
            float near = 0.1f;
            float far = 1000.0f;
            float yScale = (float) (1.0 / Math.tan(fov / 2.0));
            float xScale = yScale / aspectRatio;
            float frustumLength = far - near;
            float[] projectionMatrix = new float[] {
                    xScale, 0, 0, 0,
                    0, yScale, 0, 0,
                    0, 0, -((far + near) / frustumLength), -1,
                    0, 0, -((2 * near * far) / frustumLength), 0
            };
            glLoadMatrixf(projectionMatrix);

            // Configurar matriz de visualização
            glMatrixMode(GL_MODELVIEW);
            glLoadMatrixf(camera.getViewMatrix().get(new float[16]));
            // Renderização
            // System.out.println("Renderizando frame... Player pos: " + player.getPosition() + ", Camera pos: " + camera.getPosition());
            terrain.render();
            ui.render();

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup() {
        if (network != null) {
            network.close();
        }
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public static void main(String[] args) {
        new MainGame().run();
    }
}