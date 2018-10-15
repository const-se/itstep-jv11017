package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Класс сервера
 */
public class Server implements Connection.StopCallback {
    /**
     * Номер порта
     */
    private int port;

    /**
     * Серверный сокет
     */
    private ServerSocket serverSocket;

    /**
     * Конструктор сервера
     * @param port Номер порта
     */
    Server(int port) {
        this.port = port;
    }

    /**
     * Старт сервера
     * Здесь мы начинаем слушать порт на адресе localhost
     * Если кто-то к нам подключится, то мы создадим для подключения класс Connection
     * и поместим его в новый поток, чтобы можно было обрабатывать несколько соединений одновременно
     * @throws IOException
     */
    void start() throws IOException {
        // Создаем серверный сокет и начинаем слушать порт
        this.serverSocket = new ServerSocket(this.port);

        // Бесконечный цикл
        while (true) {
            // Ждем, пока к нам не подключится клиент. Как только произойдет соединение,
            // метод accept() вернет нам клиентский сокет
            Socket clientSocket = this.serverSocket.accept();
            // Создаем соединение с сервером, передаем в него клиентский сокет и сам сервер
            // для обратного вызова метода stop()
            Connection connection = new Connection(clientSocket, this);
            // Создаем новый поток для соединения
            Thread thread = new Thread(connection);
            // Говорим, что поток должен выполняться как демон
            thread.setDaemon(true);
            // Стартуем поток
            thread.start();
        }
    }

    /**
     * Останов сервера
     * @throws IOException
     */
    public void stop() throws IOException {
        // Если серверный сокет до этого не был закрыт, ...
        if (!this.serverSocket.isClosed()) {
            // ...то закрываем его
            this.serverSocket.close();
        }
    }
}
