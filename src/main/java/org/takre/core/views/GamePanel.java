package org.takre.core.views;

import org.takre.core.controllers.KeyControllers.KeyHandler;
import org.takre.core.controllers.ThreadReadController;
import org.takre.core.models.entity.Player;
import org.takre.core.models.entity.RemotePlayer;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel implements Runnable {
    // Configuración de pantalla
    final int originalTileSize = 32;
    final int scale = 2;
    public int tileSize = originalTileSize * scale;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public final int screenWidth = tileSize * maxScreenCol;
    public final int screenHeight = tileSize * maxScreenRow;
    public int scaleX = screenWidth / (originalTileSize * maxScreenCol);
    public int scaleY = screenHeight / (originalTileSize * maxScreenRow);
    public int tileSizeX;
    public int tileSizeY;

    // FPS
    int FPS = 60;

    // Entrada de teclado y jugador
    KeyHandler keyH = new KeyHandler();
    Player player = new Player(this, keyH);

    // Red
    private String userName;
    private DataOutputStream out;

    // Hilo de juego
    Thread gameThread;

    // Lista de usuarios conectados
    private java.util.List<String> usuariosConectados = new ArrayList<>();
    private Map<String, RemotePlayer> jugadoresRemotos = new HashMap<>();


    // Constructor
    public GamePanel(String ip, int port, String userName) {
        this.userName = userName;
        connectToServer(ip, port);

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        tileSizeX = screenWidth / maxScreenCol;
        tileSizeY = screenHeight / maxScreenRow;
    }

    private void connectToServer(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            this.out = new DataOutputStream(socket.getOutputStream());

            // Enviar nombre de usuario al servidor
            this.out.writeUTF("USERNAME:" + this.userName);

            // Iniciar hilo de lectura
            ThreadReadController read = new ThreadReadController(socket, this);
            read.start();
        } catch (Exception e) {
            System.err.println("Error al conectar al servidor: " + e.getMessage());
        }
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1000000000.0 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
                drawCount++;
            }

            if (timer >= 1000000000) {
                drawCount = 0;
                timer = 0;
            }
        }
    }

    // Actualización del juego
    public void update() {
        player.update();

        // Enviar posición del jugador al servidor
        if (out != null) {
            try {
                int x = player.x;
                int y = player.y;

                String mensaje = "PLAYER:" + userName + "," + x + "," + y;
                out.writeUTF(mensaje);
            } catch (Exception e) {
                System.out.println("Error al enviar datos del jugador: " + e.getMessage());
            }
        }
    }

    // Dibujo del juego + usuarios conectados
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        player.draw(g2);

        for (RemotePlayer rp : jugadoresRemotos.values()) {
            rp.draw(g2);
        }


        // Dibuja la lista de usuarios conectados en la esquina
        int y = 20;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));
        for (String usuario : usuariosConectados) {
            g2.drawString(usuario, screenWidth - 150, y);
            y += 20;
        }

        g2.dispose();
    }

    // Actualiza la lista de usuarios conectados
    public void updateUserList(String[] usuarios) {
        usuariosConectados.clear();
        usuariosConectados.addAll(Arrays.asList(usuarios));

        jugadoresRemotos.keySet().removeIf(nombre -> !usuariosConectados.contains(nombre));

        repaint();
    }

    // Recibe mensajes del servidor que no sean lista de usuarios
    public void receiveMessage(String mensaje) {
        if (mensaje.startsWith("PLAYER:")) {
            String[] partes = mensaje.substring(7).split(",");
            if (partes.length == 3) {
                String nombre = partes[0];
                int x = Integer.parseInt(partes[1]);
                int y = Integer.parseInt(partes[2]);

                if (!nombre.equals(userName)) {
                    RemotePlayer rp = jugadoresRemotos.get(nombre);
                    if (rp == null) {
                        rp = new RemotePlayer(nombre, x, y);
                        jugadoresRemotos.put(nombre, rp);
                    } else {
                        rp.updatePosition(x, y);
                    }
                }
            }
        }

    }

    // Método opcional si estás usando resize del JFrame
    public void resize(int newWidth, int newHeight) {
        // Por ahora no cambia tamaño interno, pero podrías escalar aquí
    }
}
