package org.takre.core.views;

import org.takre.core.controllers.KeyControllers.KeyHandler;
import org.takre.core.controllers.ThreadReadController;
import org.takre.core.models.entity.Player;

import javax.swing.*;
import java.awt.*;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

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

    // Actualización del jugador (juego)
    public void update() {
        player.update();
    }

    // Dibujo del juego + usuarios conectados
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        player.draw(g2);

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
        repaint();
    }

    // Recibe mensajes del servidor que no sean lista de usuarios
    public void receiveMessage(String mensaje) {
        System.out.println("Mensaje recibido: " + mensaje);
        // Aquí podrías agregarlo a un chat o mostrarlo en pantalla
    }
}
