package ru.ivan.leon;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {

    //Идентификационные данные вынесены в БД
    static String botAddress;

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Старт программы"); //настроить лог в БД (logtable)
        WorkThread wtr = new WorkThread();
        Thread wtrTread = new Thread(wtr);
        wtrTread.start();
        wtrTread.join(); //Ждем завершения работы потока wtr

        System.out.println("Программа остановлена");
    }

}

class WorkThread implements Runnable{

    static String botAddress;
    long lastidupdate = 0;
    static DbConnector mydatabase;
    static BotCommand bot;

    @Override
    public void run() {
        boolean threadIsRun = true;
        try {
            init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while(threadIsRun){
            String data = bot.getDataBot(lastidupdate + 1); // Запрашиваем еще не обработанные сообщения
            ArrayList<RequestStruct> d = null; //разбираем данные от бота в структуру
            try {
                d = bot.parseData(data);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            for(int i = 0; i < d.size(); i++) {
                try {
                    mydatabase.writetoDB(d.get(i));
                } catch (SQLException e) {
                    System.out.println("Ошибка с записью в БД (Run())");
                }
                bot.sendtoChat(d.get(i).chat_id, "Сообщение: " + d.get(i).text + " принято ботом.");
                try {
                    String st = bot.searchusercommand(d.get(i), mydatabase);
                    if(!(st.equals(""))) {
                        bot.sendtoChat(d.get(i).chat_id, bot.searchusercommand(d.get(i), mydatabase));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Если было получено хоть одно сообщение в этом цикле - Обновляем индекс последнего апдейта
            if(d.size() >0) {
                lastidupdate = d.get(d.size() - 1).update_id; //в процессе работы актуальный update_id берем из запросов
                if((d.get(d.size() - 1).text.compareTo("Stopbot")) == 0) {
                    bot.sendtoChat(d.get(d.size() - 1).chat_id, "Bot has stopped!");
                    threadIsRun = false;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            mydatabase.writeLOG("Завершение потока run().Выход из программы");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void init() throws SQLException {
        mydatabase = new DbConnector("jdbc:mysql://127.0.0.1:3306/myDBforbot", "myDBforbot.db3");
        System.out.println("Подключение к БД бота:(Main) " + mydatabase.connectToDB());
        botAddress = mydatabase.getBotAddress();
        bot = new BotCommand(botAddress); //инициализация бота

        try {
            lastidupdate = mydatabase.getLastupdID(); //id последнего обновления от бота(берем из БД при инициализации потока, запуске программы)
        } catch (SQLException e) {
            System.out.println("Ошибка с чтением lastupdate из БД (Run())");
        }
        mydatabase.writeLOG("Инициализация выполнена. init()");
    }
}

class RequestStruct{
    long update_id;
    long user_id;
    String username;
    long chat_id;
    String text;
}