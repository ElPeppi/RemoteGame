package org.takre.core.network;

import jakarta.websocket.*;
import java.net.URI;
import org.takre.core.views.GamePanel;

@ClientEndpoint
public class WebSocketClient {

    private Session session;
    private String userName;
    private GamePanel gamePanel;

    public WebSocketClient(String userName, GamePanel gamePanel) {
        this.userName = userName;
        this.gamePanel = gamePanel;
        connect();
    }

    private void connect() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI("wss://servergame-production-422a.up.railway.app/ws"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enviar(String mensaje) {
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendText("PLAYER:" + userName + "," + mensaje);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Conectado al servidor WebSocket");
    }

    @OnMessage
    public void onMessage(String mensaje) {
        // Reenviamos el mensaje al GamePanel
        gamePanel.receiveMessage(mensaje);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Desconectado del servidor WebSocket: " + reason);
    }
}
