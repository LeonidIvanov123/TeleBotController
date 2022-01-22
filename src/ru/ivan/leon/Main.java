package ru.ivan.leon;

import java.sql.SQLException;

public class Main {

    //Идентификационные данные вынесены в БД

    static String botAddress;

    public static void main(String[] args) throws SQLException {
        long lastidupdate;

        DbConnector mydatabase = new DbConnector("jdbc:mysql://127.0.0.1:3306/myDBforbot", "myDBforbot.db3");
        System.out.println("Подключение к БД бота: " + mydatabase.connectToDB());

        botAddress = mydatabase.getBotAddress();
        BotCommand bot = new BotCommand(botAddress); //инициализация бота
       //JsonParse jp = new JsonParse();


        lastidupdate = mydatabase.getLastupdID(); //id последнего обновления от бота в


        System.out.println("MAX index in table =  " + mydatabase.getLastupdID());

        String data = bot.getDataBot();

        //Если есть сообщения
        if(data != "") {
            System.out.println(data);
           // jp.parseString(data);
        }else
            System.out.println("Нет новых сообщений");


    }

}
