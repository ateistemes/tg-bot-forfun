package io.tg.timaxbeta.service;

import io.tg.timaxbeta.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
                    if( update.getMessage().getChat().getUserName() == "@itskyouma"){
                        sendMessage(chatId, "Ааа это ты гавнюк пошел на хуй!");

                    }
                    sendMessage(chatId, "to see available commands write '/commands'");
                    break;
                case "/commands":

                    sendMessage(chatId, "/start\n/кто ты?\n/кто я?");
                    break;
                case "/кто ты?":
                    sendMessage(chatId, "кто я? ну, я бот");
                    break;
                case "/кто я?":
                    sendMessage(chatId, "ты гавно на палочке! хехех");

                default: sendMessage(chatId, "Sorry, command was not recognized");


            }
        }

    }
    private void startCommandReceived(long chatId, String name)   {


        String answer = "Hi, " + name + ", nice to meet you!";
        sendMessage(chatId, answer);

    }
    private void sendMessage(long chaId, String textToSend){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chaId));
        message.setText(textToSend);

        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public String getBotToken(){
        return config.getToken();
    }
}
