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
import java.util.Map;
import java.util.stream.Collectors;

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

                    String insightsMessage = generateEnhancedInsights(redditTopPostList);
                    sendTextMessage(chatId, insightsMessage);

                } catch (IOException | TelegramApiException e) {
                    log.error(e.getMessage(), e);
                    sendTextMessage(chatId, "Failed to generate the report, please try again.");
                }
            }
            else if (messageText.toLowerCase().startsWith("/gettopmemes")) {
                handleGetTopMemes(chatId, messageText);
            }
            else if (messageText.equalsIgnoreCase("/help")) {
                sendHelpMessage(chatId);
            }
            else {
                sendHelpMessage(chatId);
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

    private String generateEnhancedInsights(List<RedditTopPost> posts) {
        if (posts == null || posts.isEmpty()) {
            return "No trending memes found at the moment. Try again later!";
        }

        RedditTopPost topPost = posts.stream()
                .max((p1, p2) -> Integer.compare(p1.getScore(), p2.getScore()))
                .orElse(null);

        double avgScore = posts.stream()
                .mapToInt(RedditTopPost::getScore)
                .average()
                .orElse(0);

        Map<String, Long> subredditCount = posts.stream()
                .collect(Collectors.groupingBy(RedditTopPost::getSubreddit, Collectors.counting()));

        StringBuilder sb = new StringBuilder();
        sb.append("🔥 Trending Memes Report\n\n");
        sb.append("Total memes: ").append(posts.size()).append("\n");
        sb.append(String.format("Average score: %.0f\n", avgScore));

        sb.append("\n<b>🏆 Top Meme:</b>\n");
        sb.append(topPost.getTitle()).append("\n");
        sb.append("Score: ").append(topPost.getScore()).append("\n");
        sb.append("Subreddit: ").append(topPost.getSubreddit()).append("\n");
        String author = topPost.getAuthor();
        if (author != null && !author.isEmpty()) {
            String redditProfileUrl = "https://www.reddit.com/user/" + author;
            sb.append("Author: <a href=\"").append(redditProfileUrl).append("\">").append(author).append("</a>\n");
        }
        sb.append("\n");

        sb.append("📊 Posts per Subreddit:\n");
        subredditCount.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(3) // top 3
                .forEach(entry -> sb.append(String.format("• %s: %d posts\n", entry.getKey(), entry.getValue())));

        sb.append("\nYou can:\n");
        sb.append("• /gettrendingmemes - Download the report again\n");
        sb.append("• /gettopmemes - Get top 5 memes only!\n");
        sb.append("Stay tuned for more memes! 🎉");

        return sb.toString();
    }

    private void handleGetTopMemes(String chatId, String messageText) {
        List<RedditTopPost> allPosts = memeService.getCrawledPost();

        String[] parts = messageText.split("\\s+", 2);
        String subredditFilter = parts.length == 2 ? parts[1].trim().toLowerCase() : null;

        List<RedditTopPost> filteredPosts;
        if (subredditFilter == null || subredditFilter.isEmpty()) {
            filteredPosts = allPosts;
        } else {
            filteredPosts = allPosts.stream()
                    .filter(p -> p.getSubreddit() != null && p.getSubreddit().equalsIgnoreCase(subredditFilter))
                    .toList();
            if (filteredPosts.isEmpty()) {
                sendTextMessage(chatId, "No trending memes found for subreddit: " + subredditFilter);
                return;
            }
        }

        List<RedditTopPost> topPosts = filteredPosts.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getScore(), p1.getScore()))
                .limit(5)
                .toList();

        StringBuilder response = new StringBuilder();
        if (subredditFilter != null) {
            response.append("Top 5 memes in subreddit <b>").append(subredditFilter).append("</b>:\n\n");
        } else {
            response.append("Top 5 trending memes across all subreddits:\n\n");
        }

        for (int i = 0; i < topPosts.size(); i++) {
            RedditTopPost post = topPosts.get(i);
            response.append(i + 1).append(". ");
            response.append(post.getTitle());
            response.append("\n   Score: ").append(post.getScore());
            response.append(", Subreddit: ").append(post.getSubreddit());
            String author = post.getAuthor();
            if (author != null && !author.isEmpty()) {
                String authorLink = "https://www.reddit.com/user/" + author;
                response.append(", Author: <a href=\"").append(authorLink).append("\">").append(author).append("</a>");
            }
            response.append("\n\n");
        }

        response.append("Use /gettrendingmemes to get memes report and more!");

        SendMessage message = new SendMessage(chatId, response.toString());
        message.setParseMode("HTML");

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to send message", e);
        }
    }

    private void sendHelpMessage(String chatId) {
        String helpText = "Sorry, I didn't recognize that command.\n\n"
                + "Available commands:\n"
                + "/gettrendingmemes - Download trending memes report as Excel + summary\n"
                + "/gettopmemes [subreddit] - Show top 5 memes overall or filtered by subreddit\n"
                + "/help - Show this help message";
        sendTextMessage(chatId, helpText);
    }
}
