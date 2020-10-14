package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private List<ClientHandler> clients; // лист входящий клиентов
    private AuthService authService;

    public Server() {
        clients = new CopyOnWriteArrayList<>(); // создаем арай лист потокобезопасный
        authService = new SimpleAuthService();
        ServerSocket server = null;
        Socket socket = null;
        final int PORT = 8189;

        try {
            server = new ServerSocket(PORT);
            System.out.println("Server started");

            while (true) {
                socket = server.accept();
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // Этот метод проходится по всем клиентам и добавляет сообщение ему (можно наверное его как-то изменить либо для него создать метод для
    // того чтобы конкретному пользователю отправлять)
    public void broadcastMsg(ClientHandler sender, String msg) {
        String message = String.format("[ %s ]: %s", sender.getNickname(), msg);
        for (ClientHandler c : clients) {
            c.sendMsg(message);
        }
    }

    // Метод добавления клиента в лист
    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    // Метод убирания клиента из листа
    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }


    public AuthService getAuthService() {
        return authService;
    }
}
