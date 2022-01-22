package ru.ivan.leon;

import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

    //Идентификационные данные вынесены в БД

    static String botAddress;

    public static void main(String[] args) throws SQLException {
        long lastidupdate;

        DbConnector mydatabase = new DbConnector("jdbc:mysql://127.0.0.1:3306/myDBforbot", "myDBforbot.db3");
        System.out.println("Подключение к БД бота:(Main) " + mydatabase.connectToDB());

        botAddress = mydatabase.getBotAddress();
        BotCommand bot = new BotCommand(botAddress); //инициализация бота


        lastidupdate = mydatabase.getLastupdID(); //id последнего обновления от бота
        System.out.println("MAX update_id in table =  " + lastidupdate);
        String data = bot.getDataBot(lastidupdate + 1); // Запрашиваем еще не обработанные сообщения
        //В строке data ответ от бота
        ArrayList<RequestStruct> d = bot.parseData(data); //резбираем данные от бота в структуру
        for(int i = 0; i < d.size(); i++)
            mydatabase.writetoDB(d.get(i));


        //Если есть сообщения, пишем в БД
        if(data != "") {
            System.out.println(data);

        }else
            System.out.println("Нет новых сообщений");


    }

}

class RequestStruct{
    long update_id;
    long user_id;
    String username;
    long chat_id;
    String text;
}