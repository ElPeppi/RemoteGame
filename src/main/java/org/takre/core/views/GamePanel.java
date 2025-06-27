package org.takre.core.views;

import org.takre.core.controllers.KeyControllers.KeyHandler;
import org.takre.core.models.entity.Player;
import org.takre.core.models.entity.RemotePlayer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.takre.core.network.WebSocketClient;

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
    private WebSocketClient webSocketClient;

    // FPS
    int FPS = 60;

    // Entrada de teclado y jugador
    KeyHandler keyH = new KeyHandler();

    public KeyHandler getKeyH() {
        return keyH;
    }
    Player player;

    public void setPlayer(Player player) {
        this.player = player;
    }

    // Red
    private String userName;

    public String getUserName() {
        return userName;
    }

    // Hilo de juego
    Thread gameThread;

    // Lista de usuarios conectados
    private java.util.List<String> usuariosConectados = new ArrayList<>();
    private Map<String, RemotePlayer> jugadoresRemotos = new HashMap<>();

    // Constructor
    public GamePanel(String userName) {
        this.userName = userName;

        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.BLACK);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);

        tileSizeX = screenWidth / maxScreenCol;
        tileSizeY = screenHeight / maxScreenRow;
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

        // Enviar posición constantemente mientras se esté moviendo
        if (player.isMoving()) {
            int x = player.x;
            int y = player.y;
            String direction = player.direction;
            if (webSocketClient != null) {
                webSocketClient.enviar(x + "," + y + "," + direction);
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
            System.out.println(jugadoresRemotos.values().size());
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
        boolean huboCambio = false;
        usuariosConectados.clear();
        usuariosConectados.addAll(Arrays.asList(usuarios));

        // Elimina jugadores desconectados
        if (jugadoresRemotos.keySet().removeIf(nombre -> !usuariosConectados.contains(nombre))) {
            huboCambio = true;
        }

    }

    // Recibe mensajes del servidor que no sean lista de usuarios
    public void receiveMessage(String mensaje) {
        if (mensaje.startsWith("PLAYER:")) {
            mensaje = mensaje.substring(7);

            for (String entry : mensaje.split("\\|")) {
                String[] datos = entry.split(",");
                if (datos.length != 4) {
                    continue; // Evita errores
                }
                String nombre = datos[0];
                int x, y;
                try {
                    x = Integer.parseInt(datos[1]);
                    y = Integer.parseInt(datos[2]);
                } catch (NumberFormatException e) {
                    continue; // datos inválidos
                }
                String dir = datos[3];

                if (!nombre.equals(userName)) {
                    jugadoresRemotos.compute(nombre, (k, rp) -> {
                        if (rp == null) {
                            return new RemotePlayer(nombre, x, y, dir, this);
                        }
                        rp.updatePlayer(x, y, dir);
                        return rp;
                    });
                }
            }
        }

    }

    // Método opcional si estás usando resize del JFrame
    public void resize(int newWidth, int newHeight) {
        // Por ahora no cambia tamaño interno, pero podrías escalar aquí
    }

    public void setWebSocketClient(WebSocketClient clienteWS) {
        this.webSocketClient = clienteWS;
    }
}
