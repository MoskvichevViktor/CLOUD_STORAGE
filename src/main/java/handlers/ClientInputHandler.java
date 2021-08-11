package handlers;

import java.util.Scanner;

public class ClientInputHandler {
    public static String getFilename(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Введите имя файла: ");

        return scanner.next();
    }

    public static String getChoice(){
        Scanner scanner = new Scanner(System.in);

        System.out.println("Список файлов на сервере - 1\n" +
                "Загрузить файл - 2\n" +
                "Удалить файл - 3\n" +
                "Скачать файл -4\n");

        return scanner.next();
    }
}