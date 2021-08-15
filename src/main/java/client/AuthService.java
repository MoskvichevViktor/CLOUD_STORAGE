package client;


import java.sql.*;
import java.util.Scanner;

public class AuthService {

     private static Connection connection;
     private static Statement statement;

     public static void connect() {
          try {
               Class.forName("org.sqlite.JDBC");
               connection = DriverManager.getConnection("jdbc:sqlite:bd.db");
               statement = connection.createStatement();
          } catch (ClassNotFoundException | SQLException e) {
               e.printStackTrace();
          }
     }

     public static String getLogin(){
          Scanner scanner = new Scanner(System.in);
          System.out.println("Введите логин: ");
          return scanner.next();
     }
     public static String getPassword(){
          Scanner scanner = new Scanner(System.in);
          System.out.println("Введите пароль: ");
          return scanner.next();
     }

     public static String getLoginByLoginAndPass(String login, String password) {
          String query = String.format("select login from users where login='%s' and password='%s'", login, password);
          try {
               ResultSet rs = statement.executeQuery(query); // возвращает выборку через select

               if (rs.next()) {
                         return rs.getString("login");

               }
          }catch (SQLException throwables) {
               throwables.printStackTrace();
          }
          return null;
     }

     public static void disconnect() {
          try {
               connection.close();
          } catch (SQLException e) {
               e.printStackTrace();
          }
     }
}

