package ru.ivan.leon;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BotCommand {

    String botAddress;
    boolean statusConnect = true;

    BotCommand(String url){
        botAddress = url;
        stateConnect();
    }

    public void stateConnect(){
        System.out.print("Connected to Telegram Bot: ");
        for(int i = 1; i<10; i++) {
            try {
                URLConnection url = new URL(botAddress + "getMe").openConnection();
                InputStreamReader is = new InputStreamReader(url.getInputStream());
                statusConnect = true; //если не ушли в catch
            } catch (IOException e) {
                System.out.print(i + " ... ");
                statusConnect = false;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            if(statusConnect){
                System.out.println("sucsess.");
                break;
            }
        }
        if(!statusConnect){
            System.out.println("Do not connected to Telegram");
            System.exit(1);
        }
    }

    public String getDataBot(long offset){
        StringBuilder sb = new StringBuilder();
        URL mybot = null;
        try {
            if(offset == 1){
                mybot = new URL(botAddress + "getupdates");
                //if ERROR = in file 'createDBforBot.sql' add you telegrambot address!!!!
            }else{
                mybot = new URL(botAddress + "getupdates" + "?offset=" + offset);
            }
            URLConnection tlg = mybot.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(tlg.getInputStream()));

            String inputData = "";
            while ((inputData = in.readLine())!=null){
                sb.append(inputData);
            }

            statusConnect = true;
        } catch (IOException e) {
            System.out.println("Ошибка при установлении соединения с telegram(BotCommand" + "offset = " + offset
            + "\nОшибка в адресе бота или нет соединения с telegram.org.");
            statusConnect = false;
            //e.printStackTrace();
        }

        return sb.toString();
    }

    public void sendtoChat(long chatId, String text){
        String urlcommand = botAddress + "sendMessage" + "?chat_id=" + chatId + "&text="+ text + "";
        try {
            URL url = new URL(urlcommand);
            URLConnection urlcon = url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
            in.read();
            in.close();

        //    initbotKeyboard(chatId);
        } catch (IOException e) {
            System.out.println("Не получается ответить юзеру (BotCommand.sendtoChat)");
            e.printStackTrace();
        }
    }

    public ArrayList<RequestStruct> parseData(String str) throws UnsupportedEncodingException {
        ArrayList<RequestStruct> dataarr = new ArrayList<>();

        while (str.indexOf("update_id") != -1) {
            RequestStruct r = new RequestStruct();

            int indexIDst = str.indexOf("update_id");
            str = str.substring(indexIDst);
            int indexIDend = str.indexOf(",");
            String updID = str.substring(11, indexIDend);
            r.update_id = Long.parseLong(updID);
            System.out.println("Update Id = " + updID + " convert to Long = " + r.update_id);

            str = str.substring(str.indexOf(updID) + updID.length() + 1);
            str = str.substring(str.indexOf("\"id\":")+ 6);
            r.user_id = Long.parseLong(str.substring(0, str.indexOf(",")));

            str = str.substring(str.indexOf("\"username\":") + 11);
            r.username = str.substring(1, str.indexOf(",")-1);

            str = str.substring(str.indexOf("\"chat\":{\"id\":")+ 12); //от этой подстроки и до конца строки
            r.chat_id = Long.parseLong(str.substring(1, str.indexOf(",")));

            str = str.substring(str.indexOf("\"text\":") + 7);
            r.text = unitochar(str.substring(1, str.indexOf("}")-1));
            dataarr.add(r);
    }
        return dataarr;
    }

    String unitochar(String text){
        String res = "";
        String temp = text;
        while (temp != "") {
            while (temp.indexOf("\\u") != -1) {
                res = res + temp.substring(0, temp.indexOf("\\u"));
                temp = temp.substring(temp.indexOf("\\u"), temp.length());
                String nextch = temp.substring(2, 6);
                int i = Integer.parseInt(nextch, 16);
                res = res + (char)i;
                temp = temp.substring(6, temp.length());
            }
            res = res + temp;
            temp = "";
        }
        return res;
    }

    String searchusercommand(RequestStruct req, DbConnector db) throws SQLException, IOException, ParseException {
        String result = "";
        switch (req.text) {
            case ("Weather"):
                String urlweather = db.getWeatherAddress();
                URL wurl = new URL(urlweather);
                URLConnection wurlcon = wurl.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(wurlcon.getInputStream()));
                String inputData = "";
                while ((inputData = in.readLine())!=null){
                    result = result + inputData + '\n';
                }

                Object obj = new JSONParser().parse(result);
                JSONObject jo = (JSONObject) obj;
                JSONObject temp = (JSONObject) jo.get("main");
                result = "*********Погода*********" + "%0AГород: " + jo.get("name").toString() +
                        "%0AОщущается как: " + temp.get("feels_like").toString() +
                        "%0AТекущая температура: " + temp.get("temp").toString() +
                        "%0AАтм. давление: " + temp.get("pressure").toString();
                break;
            case ("Time"):
                Date dt = new Date();
                result = "Текущее время сервера: %0A " + dt.toString();
                break;
            case ("ScreenServer"):

                    //у докер контейнера нет интерфейса))
                    //Robot robot = new Robot();
                    //BufferedImage screenShot = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
                    //ImageIO.write(screenShot, "JPG", new File("d:\\"+formatter.format(now.getTime())+".jpg"));
                db.writeLOG("Делаем скриншот в " + new Date().toString());
                break;
            case ("Task"):
                //создаем таск с напоминанием через n-минут
                result = "Введите команду формата <Task + [время в минутах] + [событие(напоминание)]>";
                break;
        }
        if(!result.equals(""))
            db.writeLOG("MSG to user "+ req.username + " : " + result);
        return result;
    }

    void initbotKeyboard(long chatid) throws IOException {
        //Добавим клавиши для пользователя
        String btn = "{\n" +
                "  \"chat_id\":"+ chatid + ",\n" +
                "  \"text\": \"Погода\",\n" +
                "  \"reply_markup\": {\n" +
                "    \"keyboard\": [\n" +
                "      [\n" +
                "        {\"text\": \"weather\"},\n" +
                "        {\"text\": \"time\"}\n" +
                "      ],\n" +
                "      [\n" +
                "        {\"text\": \"Кнопка 3\"},\n" +
                "        {\"text\": \"Кнопка 4\"}\n" +
                "      ]\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        URL url = new URL(botAddress + "sendMessage");

        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setFixedLengthStreamingMode(btn.getBytes(StandardCharsets.UTF_8).length);
        con.connect();

            OutputStream os = con.getOutputStream();
            os.write(btn.getBytes(StandardCharsets.UTF_8));
            os.close();
            con.disconnect();
/*
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(btn);
        out.flush();
        out.close();
        con.disconnect();*/
    }
}

