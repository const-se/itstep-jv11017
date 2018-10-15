package com.company;

import java.io.*;
import java.net.Socket;

/**
 * Класс соединения клиента с нашим сервером
 */
public class Connection implements Runnable {
    /**
     * Обратный вызов останова сервера
     */
    private StopCallback callback;

    /**
     * Входящий поток данных клиентского сокета
     */
    private InputStream input;

    /**
     * Исходящий поток данных клиентского сокета
     */
    private OutputStream output;

    /**
     * Клиентский сокет
     */
    private Socket socket;

    /**
     * Интерфейс для обратного вызова останова сервера
     */
    interface StopCallback {
        void stop() throws IOException;
    }

    /**
     * Конструктор соединения клиента с сервером
     * @param socket Клиентский сокет
     * @param callback Обратный вызов (колбэк) останова сервера
     * @throws IOException
     */
    Connection(Socket socket, StopCallback callback) throws IOException {
        this.socket = socket;
        this.callback = callback;
        // Сохраняем входящий поток данных сокета в отдельное свойство класса
        this.input = socket.getInputStream();
        // Сохраняем исходящий поток данных сокета в отдельное свойство класса
        this.output = socket.getOutputStream();
    }

    public void run() {
        try {
            // Вспомогательный инструмент для работы с входящим потоком данных сокета
            DataInputStream inputStream = new DataInputStream(this.input);
            // Вспомогательный инструмент для работы с исходящим потоком данных сокета
            DataOutputStream outputStream = new DataOutputStream(this.output);
            // Сначала приветствуем нового клиента
            outputStream.writeChars("Hello!\n");

            // Бесконечный цикл
            while (true) {
                // Читаем входящий поток
                String inputString = inputStream.readLine();
                // Выводим в консоль полученное сообщение
                System.out.println(inputString);

                // Если полученное сообщение - команда /quit, ...
                if (inputString.equalsIgnoreCase("/quit")) {
                    // ...то закрываем клиентский сокет...
                    this.close();
                    // ...и выходим из бесконечного цикла для завершения соединения
                    break;
                }

                // Если полученное сообщение - команда /stop, ...
                if (inputString.equalsIgnoreCase("/stop")) {
                    // ...то закрываем клиентский сокет...
                    this.close();
                    // ...и вызываем останов сервера,...
                    this.callback.stop();
                    // ...а так же выходим из бесконечного цикла для завершения соединения
                    break;
                }

                // Добавляем к полученному сообщению перенос строки
                inputString = inputString + "\n";
                // Отдаем полученное сообщение обратно клиенту (т.е. прикидываемся повторюшей)
                outputStream.writeChars(inputString);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Закрытие клиентского сокета
     * @throws IOException
     */
    private void close() throws IOException {
        // Закрываем входящий поток данных сокета
        this.input.close();
        // Закрываем исходящий поток данных сокета
        this.output.close();
        // Закрываем клиентский сокет
        this.socket.close();
    }
}
