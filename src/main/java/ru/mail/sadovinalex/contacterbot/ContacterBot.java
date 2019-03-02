package ru.mail.sadovinalex.contacterbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalTime;


public class ContacterBot extends TelegramLongPollingBot {
    private  String botName;
    private  String token;
    ContacterBot(String name, String token)
    {

        botName=name;
        this.token=token;

    }
    @SuppressWarnings("deprecation") // Означает то, что в новых версиях метод уберут или заменят
    private void sendMsg(Message msg, String text) {
        SendMessage s = new SendMessage();
        s.enableMarkdown(true);
        s.setReplyToMessageId(msg.getMessageId());
        s.setChatId(msg.getChatId());
        s.setText(text);
        try {
            execute(s);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        if (update.hasMessage() && msg.hasText()) {
            String mainMsg[]=msg.getText().split(" ");
            switch (mainMsg[0]) {
                case "/start":
                    sendMsg(msg, "What you need young podawan. It is bot developed by Sadovin Alexander"); // Call method to send the message
                    break;
                case "/time":
                    LocalTime time = LocalTime.now();
                    sendMsg(msg, Integer.toString(time.getHour())+":" + Integer.toString(time.getMinute())+":"+
                            Integer.toString(time.getSecond()));
                    break;
                case "/valute":
                    if(mainMsg.length<2)
                    {
                        try {
                            sendMsg(msg,getValute("USD"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            sendMsg(msg, getValute(mainMsg[1]));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case "/help":
                    sendMsg(msg,"/start\n"+
                            "/time\n"+
                            "/valute $value=USD,EUR,KZH...Example: /valute USD"
                    );
                    break;
                default:
                    sendMsg(msg,"Don't correct command. Enter /help to learn other commands");
                    break;
            }
        }
    }

    private String parseJson(String json,String val) throws IOException {
        ObjectMapper mapper=new ObjectMapper();
        JsonNode arrNode = mapper.readTree(json).get("Valute");
        String res=arrNode.get(val).toString();
        System.out.println(res);
        if(res.isEmpty())
        {
            return "0";
        }else
            return res;

    }
    public String getValute(String val) throws IOException {
        HttpsURLConnection connection=null;
        String res=null;
        try {
            URL valute = new URL("https://www.cbr-xml-daily.ru/daily_json.js");
            connection = (HttpsURLConnection) valute.openConnection();
            connection.connect();
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String json;
            while ((json = br.readLine()) != null) {
                sb.append(json);
            }
            res=sb.toString();
            br.close();
            isr.close();
            connection.disconnect();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return parseJson(res,val);
    }
    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return this.token;
    }
}
