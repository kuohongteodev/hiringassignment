package com.hiringassignment.hiringassignment.service.impl;

import com.hiringassignment.hiringassignment.dto.PostDTO;
import com.hiringassignment.hiringassignment.entity.RedditTopPost;
import com.hiringassignment.hiringassignment.service.MemeService;
import com.hiringassignment.hiringassignment.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {

    private final String botName;
    private final MemeService memeService;

    @Autowired
    public TelegramBotService(TelegramBotsApi telegramBotsApi, @Value("${telegram.bot.api-key}") String botToken, @Value("${telegram.bot.name}") String botName, MemeService memeService) throws TelegramApiException {
        super(botToken);
        this.botName = botName;
        this.memeService = memeService;
        telegramBotsApi.registerBot(this);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String chatId = update.getMessage().getChatId().toString();
            String messageText = update.getMessage().getText().trim();

            if (messageText.equalsIgnoreCase("/gettrendingmemes")) {
                List<RedditTopPost> redditTopPostList = memeService.getCrawledPost();
                try {
                    ByteArrayOutputStream byteArrayOutputStream = ExcelUtil.generateExcelFromPosts(redditTopPostList);
                    sendExcelFile(chatId, byteArrayOutputStream);
                } catch (IOException | TelegramApiException e) {
                    log.error(e.getMessage());
                    sendTextMessage(chatId, "Failed, please try again");
                }
            } else {
                sendTextMessage(chatId, "Sorry, I don't recognize that command.");
            }
        }
    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }

    private void sendTextMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to send message", e);
        }
    }

    public void sendExcelFile(String chatId, ByteArrayOutputStream excelStream) throws TelegramApiException {
        InputStream inputStream = new ByteArrayInputStream(excelStream.toByteArray());
        InputFile inputFile = new InputFile(inputStream, "posts.xlsx");

        SendDocument sendDocument = SendDocument.builder()
                .chatId(String.valueOf(chatId))
                .document(inputFile)
                .build();

        execute(sendDocument);
    }
}
