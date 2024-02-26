package io.tg.timaxbeta.service;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import io.tg.timaxbeta.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {
    final BotConfig config;
    public TelegramBot(BotConfig config) {
        this.config = config;
    }
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }
    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    sendMessage(chatId, "your username is " + update.getMessage().getChat().getUserName());
                    if( "itskyouma".equals(update.getMessage().getChat().getUserName())){
                        sendMessage(chatId, "Ааа это ты гавнюк пошел на хуй!");

                    }
                    if( "Allaceonme".equals(update.getMessage().getChat().getUserName())){
                        sendMessage(chatId, "здравстуй хозяин");

                    }
                    if( "maidenscarlett".equals(update.getMessage().getChat().getUserName())){
                        sendMessage(chatId, "аа это ты Акбийке, узнаю тебя! ");
                    }
                    sendMessage(chatId, "to see available commands write '/commands'");
                    break;
                case "/commands":

                    sendMessage(chatId, "/start\nкто ты?\nкто я?\nfacts\n/dog");
                    break;
                case "кто ты?":
                    sendMessage(chatId, "кто я? ну, я бот");
                    break;
                case "кто я?":
                    sendMessage(chatId, "ты гавно на палочке! хехех");
                    break;
                case "facts":
                    String joke = fetchChuckNorrisJoke();
                    sendMessage(chatId, joke);
                    break;
                case "/dog":
                    sendRandomDogImage(chatId);
                    break;

                default: sendMessage(chatId, "Sorry, command was not recognized");


            }
        }

    }
    private String fetchChuckNorrisJoke() {
        try {
            URL url = new URL("https://api.chucknorris.io/jokes/random");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                return "Joke API server problem. Try again later.";
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);
            }

            conn.disconnect();

            // Assuming the API returns a JSON object with the joke in a field named "value"
            String json = response.toString();
            // Simple extraction of the "value" field from the JSON response
            String joke = json.split("\"value\":\"")[1].split("\"")[0];
            return joke;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error occured: " + e.getMessage());
            return "Failed to fetch joke. Try again later.";
        }
    }

    private void startCommandReceived(long chatId, String name)   {


        String answer = "Hi, " + name + ", nice to meet you!";
        sendMessage(chatId, answer);
        log.info("Replied to user " + name);

    }
    private void sendMessage(long chaId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chaId));
        message.setText(textToSend);

        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error("Error occured: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void sendRandomDogImage(long chatId) {
        try {
            URL url = new URL("https://dog.ceo/api/breeds/image/random");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            Scanner scanner = new Scanner(url.openStream());
            StringBuilder response = new StringBuilder();
            while(scanner.hasNext()) {
                response.append(scanner.nextLine());
            }
            scanner.close();

            // Parse JSON response to extract image URL
            String imageUrl = response.toString().split("\"message\":\"")[1].split("\"")[0];
            InputStream inputStream = new URL(imageUrl).openStream();
            InputFile inputFile = new InputFile(inputStream, "dog.jpg");
            // Send image URL to user
            SendPhoto photo = new SendPhoto();
            photo.setChatId(String.valueOf(chatId));
            photo.setPhoto(inputFile);

            execute(photo);
        } catch (Exception e) {
            log.error("Error occured: " + e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public String getBotToken(){
        return config.getToken();
    }
}
