package com.company;

import java.io.IOException;

/**
 * Главный класс приложения
 */
public class Main {
    public static void main(String[] args) throws IOException {
        // Создаем сервер на порту 10000
        Server server = new Server(10000);
        // Стартуем сервер
        server.start();
    }
}
