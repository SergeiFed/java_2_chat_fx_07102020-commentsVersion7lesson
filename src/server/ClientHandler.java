package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler { // Класс отвечает за работу с отдельным клиентом
    DataInputStream in; // входной потом с сервера
    DataOutputStream out; // исходящий поток с сервера
    Server server; // сервер
    Socket socket; // сокет клиента

    private String nickname; // ник клиента
    private String login; // логин

    public ClientHandler(Server server, Socket socket) { // конструктор
        try {
            this.server = server; // введенный сервер
            this.socket = socket; // введенный соккет
            in = new DataInputStream(socket.getInputStream()); // создаем входящий поток
            out = new DataOutputStream(socket.getOutputStream()); // создаем исходящий поток
            System.out.println("Client connected " + socket.getRemoteSocketAddress());
            //

            new Thread(() -> {
                try {
                    //цикл аутентификации
                    while (true) {
                        String str = in.readUTF();

                        if (str.startsWith("/auth ")) {
                            String[] token = str.split("\\s");
                            if (token.length < 3) {
                                continue;
                            }
                            String newNick = server.getAuthService()
                                    .getNicknameByLoginAndPassword(token[1], token[2]);
                            if (newNick != null){
                                nickname = newNick;
                                server.subscribe(this);
                                sendMsg("/authok "+ newNick);
                                break;
                            } else {
                                sendMsg("Неверный логин / пароль");
                            }
                        }
                    }
                    //цикл работы


                    while (true) {
                        String str = in.readUTF(); // Тоже что у клиента.

                        if (str.equals("/end")) { // если сообщение энд
                            sendMsg("/end"); // отправляем клиенту сообщение
                            break; //
                        }
                        server.broadcastMsg(this, str); //
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally { // сработает только когда сработает энд
                    server.unsubscribe(this); // отключаем клиента от сервера
                    System.out.println("Client disconnected " + socket.getRemoteSocketAddress());
                    //
                    try {
                        socket.close();
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMsg(String msg) { // Метод который отправляет клиенту сообщения
        try {
            out.writeUTF(msg); // Отправляем введенное по исходящему поток
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getNickname() {
        return nickname;
    }
}
