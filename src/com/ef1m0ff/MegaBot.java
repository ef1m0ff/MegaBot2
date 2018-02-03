package com.ef1m0ff;

import com.vdurmont.emoji.EmojiParser;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.api.methods.groupadministration.GetChatMemberCount;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.ChatMember;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MegaBot extends TelegramLongPollingBot {

    private final long ADMIN_ID = 494259732;
    private final long MY_ID = 341667634;
    private final String TEST_GROUP_CHAT_ID = "-238551893";
    private final String DATABASE_NAME = "THEDATABASE";
    private final String TABLE_NAME = "Channels";
    private final String TABLE_LOG = "Log";
    private final String TABLE_DATA = "Data";
    private final String categories[] = {"Эрудиция/Книги", "Познавательное", "Медиа", "Развлечения/Юмор", "Магазины", "Здоровье и уход", "Дом и Хобби", "Для мужчин", "Для женщин", "Спорт и Красота", "Другое"};
    private final String categoriesUp[] = {"ЭРУДИЦИЯ/КНИГИ", "ПОЗНАВАТЕЛЬНОЕ", "МЕДИА", "РАЗВЛЕЧЕНИЯ/ЮМОР", "МАГАЗИНЫ", "ЗДОРОВЬЕ И УХОД", "ДОМ И ХОББИ", "ДЛЯ МУЖЧИН", "ДЛЯ ЖЕНЩИН", "СПОРТ И КРАСОТА", "ДРУГОЕ"};
    //public final String[] categories = {"Технологии", "Новости и СМИ", "Блоги", "Юмор и развлечения", "Наука и образование", "Лингвистика", "Психология", "Бизнес и стартапы", "Криптовалюты", "Маркетинг и реклама", "Карьера", "Фильмы и сериалы", "Музыка", "Литература", "Здоровье и спорт", "Путешествия и эмиграция", "Искусство и фото", "Мода и красота", "Медицина", "Игры и приложения", "Еда и напитки", "Продажи", "Цитаты", "Рукоделие", "Для взрослых", "O'zbek tilidagi kanallar", "Другое"};
    // Добавляем Юзернейм канала(нижн и верхн), Название Канала, Категория Канала, Юзернейм Админа, Месседж Айди,  сколько подписчиков, забанен ли, админ ли бот, достаточен ли актив, добавил ли мегу, причина бана
    final String MESSAGE_ID = "messageId";
    final String ID_OF_ADMIN = "idOfAdmin";
    final String USERNAME_OF_ADMIN = "userNameOfAdmin";
    final String USERNAME_OF_CHANNEL = "userNameOfChannel";
    final String USERNAME_OF_CHANNEL_UPPER = "userNameOfChannelUpper";
    final String NAME_OF_CHANNEL = "nameOfChannel";
    final String CATEGORY_OF_CHANNEL = "categoryOfChannel";
    final String BAN_REASON = "banReason";
    final String NUMBER_OF_SUBSCRIBERS = "numberOfSubscribers";
    final String ISBANNED = "isBanned";
    final String ISDELETED = "isDeleted";
    final String POSTMESSAGEID = "postMessageId";
    final String ISADDEDMEGA = "isAddedMega";
    final String ISON = "isOn";
    final String NUMOFSUBS = "numOfSubs";
    final String TIMEOFSTARTING = "timeOfStarting";
    final String MEGA = "mega";
    final String CHATID = "chatId";
    final String GROUPCHATID = "groupChatId";
    final String ADMINUSERNAME = "adminUserName";
    short toDo = 0;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            int user_id = update.getMessage().getFrom().getId();
            long chat_id = update.getMessage().getChatId();
            int message_id = update.getMessage().getMessageId();
            String user_first_name = update.getMessage().getChat().getFirstName();
            String user_username = update.getMessage().getFrom().getUserName();
            String message_text = update.getMessage().getText();
            String nameOfChannel = "";
            String usernameOfChannel = "";
            String temp = "";
            boolean isAllowed = isAdmin("@" + user_username);
            boolean isActive = getBoolean(TABLE_LOG, ISON);
            boolean isChatAllowed = isGroup((int) chat_id);
            if (user_id != MY_ID)
                log(user_first_name, user_username, Long.toString(user_id), message_text, (long) update.getMessage().getDate(), Long.toString(chat_id));
            if (!isChatAllowed && !isAllowed) {
            } else if (message_text.contains("/add")) {
                String res = "";
                if (isActive) {
                    update(TABLE_NAME, ID_OF_ADMIN, Integer.toString(user_id), MESSAGE_ID, message_id);
                    String category = "";
                    int nOfSub;
                    int count = message_text.indexOf('@');
                    try {
                        while (message_text.charAt(count) != ' ') {
                            usernameOfChannel += message_text.charAt(count);
                            count++;
                        }
                        while (message_text.charAt(count) == ' ')
                            count++;
                        while (count != message_text.lastIndexOf('-') - 1)
                            nameOfChannel += message_text.charAt(count++);
                        count = message_text.lastIndexOf('-') + 1;
                        while (message_text.charAt(count) == ' ')
                            count++;
                        while (count != message_text.length())
                            category += message_text.charAt(count++);
                        while (nameOfChannel.charAt(nameOfChannel.length() - 1) == ' ') {
                            for (int i = 0; i < nameOfChannel.length() - 1; i++) {
                                temp += nameOfChannel.charAt(i);
                            }
                            nameOfChannel = temp;
                            temp = "";
                        }
                    } catch (StringIndexOutOfBoundsException e) {
                    }
                    if (isContain(TABLE_NAME, usernameOfChannel.toUpperCase(), USERNAME_OF_CHANNEL_UPPER)) {
                        if (getBoolean(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), ISBANNED))
                            res = "Вы забанены! Причина: " + getString(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), BAN_REASON) + "\nДля разблокировки обратитесь к @A_D_M_N";
                        else if (getBoolean(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), ISDELETED))
                            res = "Ваш канал удалён администратором, ждите следующую мегу";
                        else
                            res = "Канал " + usernameOfChannel + " уже есть в списке.";
                    } else {
                        boolean categoryTrue = false;
                        int temp1 = 0;
                        category = category.toUpperCase();
                        for (int i = 0; i < categoriesUp.length; i++) {
                            if (category.equals(categoriesUp[i])) {
                                categoryTrue = true;
                                temp1 = i;
                                break;
                            }
                        }
                        boolean isAdm = false;
                        switch (isUserAdminOfChannel(user_id, usernameOfChannel)) {
                            case -1:
                                res = "Сделайте меня администратором канала " + usernameOfChannel + " и повторите свою заявку";
                                isAdm = false;
                                break;
                            case 0:
                                res = "Вы не являетесь администратором этого канала, а я являюсь!";
                                isAdm = false;
                                break;
                            case 1:
                                isAdm = true;
                                break;
                        }
                        int ret = 0;
                        String sql = "SELECT " + NUMOFSUBS + " FROM " + TABLE_LOG;
                        try (Connection conn = this.connect();
                             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                            ResultSet rs = pstmt.executeQuery();
                            if (rs.next())
                                ret = rs.getInt(NUMOFSUBS);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        nOfSub = getCountOfMembersFromChannel(usernameOfChannel);
                        if (usernameOfChannel.length() < 6) {
                            res = "Неправильный юзернейм!";
                        } else if (nameOfChannel.isEmpty()) {
                            res = "Введите название канала!";
                        } else if (category.isEmpty() || !categoryTrue) {
                            res = "Такой категории нет!";
                        } else if (!isAdm) {
                        } else if (nOfSub < ret) {
                            res = "У вас недостаточно подписчиков для участия в меге!";
                        } else {
                            res = "Название канала: \"" + nameOfChannel + "\"\n:cop:@" + user_username + " :white_check_mark:" + usernameOfChannel + " <b>добавлен.</b> Категория: " + categories[temp1] + " :busts_in_silhouette:" + Integer.toString(nOfSub);
                            res = EmojiParser.parseToUnicode(res);
                            insertChannelInfo(TABLE_NAME, usernameOfChannel, usernameOfChannel.toUpperCase(), nameOfChannel, categories[temp1], user_username, user_id, message_id, nOfSub, false, false, false);
                        }
                    }
                } else {
                    res = "Мега еще не началась";
                }
                SendMessage msg = new SendMessage().setChatId(chat_id).setText(res).enableHtml(true).setReplyToMessageId(message_id);
                try {
                    execute(msg);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                    delete(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase());
                    try {
                        execute(new SendMessage().setChatId(chat_id).setReplyToMessageId(message_id).setText("Вы ввели неккоректное название!"));
                    } catch (TelegramApiException a) {
                        a.printStackTrace();
                    }
                }
            } else if (message_text.contains("/delete")) {
                String res = "";
                if (isActive) {
                    int count = message_text.indexOf('@');
                    while (count != message_text.length()) {
                        usernameOfChannel += message_text.charAt(count);
                        count++;
                    }
                    if (!isContain(TABLE_NAME, usernameOfChannel.toUpperCase(), USERNAME_OF_CHANNEL_UPPER)) {
                        res = "Канала " + usernameOfChannel + " в списке нет!";
                    } else {
                        boolean isAdm = false;
                        switch (isUserAdminOfChannel(user_id, usernameOfChannel)) {
                            case -1:
                                res = "Сделайте меня администратором " + usernameOfChannel + " и повторите снова!";
                                isAdm = false;
                                break;
                            case 0:
                                res = "Вы не являетесь администратором этого канала!";
                                isAdm = false;
                                break;
                            case 1:
                                isAdm = true;
                                break;
                        }
                        if (usernameOfChannel.length() < 6)
                            res = "Некорректный юзернейм!";
                        else if (isAllowed) {
                            res = "@" + getString(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), USERNAME_OF_ADMIN) + ", Канал " + usernameOfChannel + " удалён <b>Главным Администратором бота</b>";
                            update(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), ISDELETED, true);
                        } else if (getBoolean(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), ISBANNED)) {
                            res = "Вы забанены! Причина: " + getString(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), BAN_REASON);
                        } else if (getBoolean(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), ISDELETED)) {
                            res = "Канал " + usernameOfChannel + " удален администратором, ждите следующей меги";
                        } else if (!isAdm) {
                        } else {
                            res = "Канал " + usernameOfChannel + " успешно <b>удалён</b>";
                            delete(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase());
                        }
                    }
                } else {
                    res = "Мега еще не началась";
                }
                SendMessage m = new SendMessage().setChatId(chat_id).enableHtml(true).setText(res).setReplyToMessageId(message_id/*getInt(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), MESSAGE_ID)*/);
                try {
                    execute(m);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.contains("/help")) {
                showHelp(chat_id, isAllowed);
            } else if (message_text.contains("/list") && isAllowed) {
                try {
                    String list = selectAll(TABLE_NAME);
                    execute(new SendMessage().setChatId(chat_id).setText(list.isEmpty() ? "<b>Список участников пуст!</b>" : "<i>Список участников:</i>\n\n" + list + EmojiParser.parseToUnicode("\nВсего подписчиков: " + totalSubscribers(TABLE_NAME) + "\nВсего каналов: " + totalChannels(TABLE_NAME) + "\n<i>Нажмите на :cop: чтобы перейти к администратору</i>")).enableHtml(true).disableWebPagePreview());
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.contains("/categories") && isAllowed) {
                showCategories(chat_id);
            } else if (message_text.contains("/banlist") && isAllowed) {
                String list = EmojiParser.parseToUnicode(getBanList(TABLE_NAME));
                try {
                    execute(new SendMessage().setChatId(chat_id).enableHtml(true).setText(list.isEmpty() ? "<b>Список забаненных пуст!</b>" : "<i>Список забаненных:</i>\n" + list));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.contains("/ban") && isAllowed) {
                int count = 0;
                boolean isHaveReason = true;
                String banReason = "";
                count = message_text.indexOf('@');
                while (message_text.charAt(count) != ' ') {
                    usernameOfChannel += message_text.charAt(count++);
                    if (count == message_text.length()) {
                        banReason = "Не указана";
                        isHaveReason = false;
                        break;
                    }
                }
                if (isHaveReason) {
                    while (message_text.charAt(count) == ' ')
                        count++;
                    while (count != message_text.length())
                        banReason += message_text.charAt(count++);
                }
                update(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), ISBANNED, true);
                update(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), BAN_REASON, banReason);
                try {
                    execute(new SendMessage().setChatId(chat_id).enableHtml(true).setText("Канал " + usernameOfChannel + " <b>забанен</b>. Причина: <i>" + banReason + "</i>").setReplyToMessageId(message_id)); //getInt(TABLE_NAME, un.toUpperCase(), MESSAGE_ID)
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.contains("/unban")) {
                String res = "";
                int count = 0;
                count = message_text.indexOf('@');
                while (count != message_text.length()) {
                    usernameOfChannel += message_text.charAt(count);
                    count++;
                }
                if (usernameOfChannel.length() < 6) {
                    res = "Неккоректный юзернейм";
                } else {
                    res = "@" + getString(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), USERNAME_OF_ADMIN) + " теперь может участвовать в меге! Для этого нужно подать заявку как все!";
                    delete(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase());
                }
                try {
                    execute(new SendMessage().setReplyToMessageId(getInt(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), MESSAGE_ID)).setChatId(chat_id).setText(res));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.contains("/start") && isAllowed) {
                String txt = "";
                char c;
                int a;
                int count = message_text.indexOf(' ');
                while (count != message_text.length()) {
                    c = message_text.charAt(count++);
                    txt += Character.isLetter(c) || c == ' ' || c == '+' ? "" : c;
                }
                float us = a = Integer.parseInt(txt);
                String cc;
                if (us < 1000) {
                    cc = Integer.toString((int) us);
                } else {
                    us = us / 1000;
                    cc = Float.toString(us);
                    cc = (cc.lastIndexOf('0') == cc.length() - 1) ? cc.replace(".0", "") : cc;
                    cc += "K";
                }
                if (!isContain(TABLE_LOG, Integer.toString(a), NUMOFSUBS)) {
                    txt = "\uD83D\uDCE2<b>МЕГА " + cc + "+ НОЧНАЯ</b>\n:white_check_mark:<b>Приём открыт!</b>\n\n" + showCategories() + "Только эти категории☝\n\n\uD83D\uDD8BШаблон заявки:\n<code>/add @Ссылка Название канала - Категория</code>" +
                            "\n:white_check_mark:Пример\n<code>/add @JootsINFO Инфоцентр - Магазины</code>\n\n:bangbang:<b>Обязательно добавьте бота в администраторы своего канала прежде чем писать сюда</b>:bangbang:\n:no_entry:Накрученные каналы и +18 не принимаем.\n\nПриём заявок до 18:40 по МСК\n\nГрафик МЕГ тут:point_right:@JootsINFO";
                    txt = EmojiParser.parseToUnicode(txt);
                    try (Connection conn = DriverManager.getConnection("jdbc:sqlite:THEDATABASE.db");
                         Statement stmt = conn.createStatement()) {
                        stmt.execute("delete from " + TABLE_LOG);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    String sql = "INSERT INTO " + TABLE_LOG + "(isOn, numOfSubs) VALUES(?,?)";
                    try (Connection conn = this.connect();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setBoolean(1, true);
                        pstmt.setInt(2, a);
                        pstmt.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                    String sql1 = "delete from " + TABLE_NAME + " where " + ISBANNED + " = ?";
                    try (Connection conn = this.connect();
                         PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                        pstmt.setBoolean(1, false);
                        pstmt.executeUpdate();
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    txt = "Мега уже стартовала";
                    String sql = "UPDATE " + TABLE_LOG + " SET "
                            + ISON + " = ?";
                    try (Connection conn = this.connect();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setBoolean(1, true);
                        pstmt.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                String sql = "UPDATE " + TABLE_LOG + " SET "
                        + TIMEOFSTARTING + " = ?";
                try (Connection conn = this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, update.getMessage().getDate());
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                SendMessage msg = new SendMessage().setChatId(chat_id).setText(txt).enableHtml(true);
                try {
                    execute(msg);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.contains("/mega") && isAllowed) {
                SendMessage mega = new SendMessage().enableHtml(true).setChatId(chat_id).setText(getMega("null!"));
                String sql = "UPDATE " + TABLE_LOG + " SET "
                        + MEGA + " = ? ";
                try (Connection conn = this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, getMega("null!"));
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    execute(mega);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.contains("/ka") && isAllowed) {
                int count = message_text.indexOf('@');
                while (count != message_text.length()) {
                    usernameOfChannel += message_text.charAt(count);
                    count++;
                }
                update(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, usernameOfChannel.toUpperCase(), ISDELETED, true);
                try {
                    execute(new SendMessage().setChatId(chat_id).setText("Канал " + usernameOfChannel + " удалён из-за низкой активности"));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.contains("/end") && isAllowed) {
                String sql = "UPDATE " + TABLE_LOG + " SET "
                        + ISON + " = ?";
                try (Connection conn = this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setBoolean(1, false);
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    execute(new SendMessage().setText("<b>Сбор заявок окончен!</b>").setChatId(chat_id).enableHtml(true));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.contains("/restart") && isAllowed) {
                if (!message_text.equals("/restart")) {
                    message_text = message_text.substring(8);
                    message_text = message_text.replaceAll(" ", "");
                    int n = Integer.parseInt(message_text);
                    String sql = "UPDATE " + TABLE_LOG + " SET "
                            + NUMOFSUBS + " = ?";
                    try (Connection conn = this.connect();
                         PreparedStatement pstmt = conn.prepareStatement(sql)) {
                        pstmt.setInt(1, n);
                        pstmt.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                String sql = "UPDATE " + TABLE_LOG + " SET "
                        + ISON + " = ?";
                try (Connection conn = this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setBoolean(1, true);
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                sql = "UPDATE " + TABLE_LOG + " SET "
                        + TIMEOFSTARTING + " = ?";
                try (Connection conn = this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, update.getMessage().getDate());
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    execute(new SendMessage().setText("<b>Сбор возобновлён! Можете кидать заявки!</b>").setChatId(chat_id).enableHtml(true));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.contains("/result") && isAllowed) {
                String res = "";
                String usr = "";
                int bef;
                int af;
                int t_af = 0;
                int t_bef = 0;
                String element;
                String sql = "SELECT userNameOfChannel, numberOfSubscribers FROM " + TABLE_NAME + " WHERE isBanned = ? AND isDeleted = ?";
                try (Connection conn = this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setBoolean(1, false);
                    pstmt.setBoolean(2, false);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        bef = rs.getInt("numberOfSubscribers");
                        usr = rs.getString("userNameOfChannel");
                        af = getCountOfMembersFromChannel(usr);
                        t_af += af;
                        t_bef += bef;
                        if (af > bef)
                            element = ":heavy_plus_sign:<b>" + Integer.toString(af - bef) + "</b>";
                        else if (af < bef)
                            element = ":heavy_minus_sign:<b>" + Integer.toString(bef - af) + "</b>";
                        else
                            element = ":anchor:<b>" + 0 + "</b>";
                        res += usr + "  " + Integer.toString(bef) + " :arrow_right: " + Integer.toString(af) + " " + element + "\n";
                    }
                    res += "\nВсего подписчиков до меги: " + t_bef + "\nВсего подписчиков после меги: " + t_af;
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                res = EmojiParser.parseToUnicode(res);
                try {
                    execute(new SendMessage().enableHtml(true).setChatId(chat_id).setText(res));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.contains("/notmega") && isAllowed) {
                String res = "Список тех, кто не опубликовал мегу:\n";
                String sql = "SELECT userNameOfAdmin, userNameOfChannel FROM " + TABLE_NAME + " WHERE isAddedMega = ? AND isDeleted = ? AND isBanned = ?";
                try (Connection conn = this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setBoolean(1, false);
                    pstmt.setBoolean(2, false);
                    pstmt.setBoolean(3, false);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next())
                        res += ":loudspeaker:" + rs.getString("userNameOfChannel") + " :cop:@" + rs.getString("userNameOfAdmin") + " :no_entry_sign:\n";
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                res = EmojiParser.parseToUnicode(res);
                try {
                    execute(new SendMessage().setText(res).setChatId(chat_id));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.contains("/delch") && isAllowed) {
                String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + ISADDEDMEGA + " = ? AND " + ISBANNED + " = ? AND " + ISDELETED + " = ?";
                try (Connection conn = this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setBoolean(1, false);
                    pstmt.setBoolean(2, false);
                    pstmt.setBoolean(3, false);
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                String un;
                int mid;
                String txt;
                String sql1 = "SELECT userNameOfChannel, " + POSTMESSAGEID + " FROM " + TABLE_NAME + " WHERE isAddedMega = ? AND isDeleted = ? AND isBanned = ?";
                try (Connection conn = this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql1)) {
                    pstmt.setBoolean(1, true);
                    pstmt.setBoolean(2, false);
                    pstmt.setBoolean(3, false);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        un = rs.getString(USERNAME_OF_CHANNEL);
                        mid = rs.getInt(POSTMESSAGEID);
                        txt = getMega(un);
                        EditMessageText new_message = new EditMessageText()
                                .setChatId(un)
                                .setMessageId(mid)
                                .setText(txt)
                                .enableHtml(true);
                        try {
                            editMessageText(new_message);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else if (message_text.contains("/autoadd") && isAllowed) {
                String un;
                int[] mids = new int[300];
                String[] uns = new String[300];
                boolean[] isAd = new boolean[300];
                int i = 0;
                String sql = "SELECT userNameOfChannel FROM " + TABLE_NAME + " WHERE isAddedMega = ? AND isDeleted = ? AND isBanned = ?";
                try (Connection conn = this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setBoolean(1, false);
                    pstmt.setBoolean(2, false);
                    pstmt.setBoolean(3, false);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        un = rs.getString("userNameOfChannel");
                        uns[i] = un.toUpperCase();
                        try {
                            SendMessage msg = new SendMessage().setText(getMega(un)).setChatId(un).enableHtml(true);
                            mids[i] = execute(msg).getMessageId();
                            isAd[i] = true;
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                            isAd[i] = false;
                        }
                        i++;
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                for (int j = 0; j < i; j++) {
                    update(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, uns[j], ISADDEDMEGA, isAd[j]);
                    update(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, uns[j], POSTMESSAGEID, mids[j]);
                }
                try {
                    execute(new SendMessage().setText("Мега автоматически опубликована в каналах. Кто не опубликовал мегу? /notmega").setChatId(chat_id));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("/extra") && (user_id == MY_ID || user_id == ADMIN_ID)) {
                if ((chat_id == MY_ID || chat_id == ADMIN_ID)) {
                    SendMessage message = new SendMessage().setChatId(chat_id).setText("Выберите команду...");
                    ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                    List<KeyboardRow> keyboard = new ArrayList<>();
                    KeyboardRow row = new KeyboardRow();
                    row.add("Добавить администратора");
                    row.add("Удалить администратора");
                    keyboard.add(row);
                    row = new KeyboardRow();
                    row.add("Добавить группу");
                    row.add("Удалить группу");
                    keyboard.add(row);
                    keyboardMarkup.setKeyboard(keyboard);
                    message.setReplyMarkup(keyboardMarkup);
                    try {
                        sendMessage(message); // Sending our message object to user
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        execute(new SendMessage().setText("Эта команда доступна только в лс").setChatId(chat_id).setReplyToMessageId(message_id));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            } else if (chat_id == ADMIN_ID || chat_id == MY_ID) {
                SendMessage msg = new SendMessage().setText("Что-то пошло не так.");
                switch (message_text) {
                    case "Добавить администратора":
                        msg = new SendMessage().setText("Введите юзернейм админа, которого нужно добавить");
                        toDo = 1;
                        break;
                    case "Удалить администратора":
                        msg = new SendMessage().setText("Введите юзернейм админа, которого нужно удалить");
                        toDo = 2;
                        break;
                    case "Добавить группу":
                        msg = new SendMessage().setText("1.Добавьте меня в группу, которую хотите добавить\n2.Отправьте туда следующее сообщение:\n<pre>//botadd</pre>");
                        toDo = 3;
                        break;
                    case "Удалить группу":
                        msg = new SendMessage().setText("1.Добавьте меня в группу, которую хотите удалить\n2.Отправьте туда следующее сообщение:\n<pre>//botremove</pre>");
                        toDo = 4;
                        break;
                    default:
                        if (message_text.startsWith("@")) {
                            int count = 0;
                            String userName = "";
                            while (count != message_text.length())
                                userName += message_text.charAt(count++);
                            if (userName.length() > 5 && !userName.contains(" ")) {
                                if (toDo == 1) {
                                    addAdmin(userName);
                                    msg = new SendMessage().setText("Администратор " + userName + " добавлен.");
                                } else if (toDo == 2) {
                                    deleteAdmin(userName);
                                    msg = new SendMessage().setText("Администратор " + userName + " удален.");
                                }
                            } else {
                                msg = new SendMessage().setText("Неккоректный юзернейм!");
                            }
                        } else {
                            msg = new SendMessage().setText("Что-то пошло не так. Помощь /help");
                        }
                }
                toDo = 0;
                try {
                    List<KeyboardRow> keyboard = new ArrayList<>();
                    KeyboardRow row = new KeyboardRow();
                    row.add("Помощь /help");
                    keyboard.add(row);
                    execute(msg.setChatId(chat_id).setReplyMarkup(new ReplyKeyboardMarkup().setKeyboard(keyboard)).enableHtml(true));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("//botremove") && (user_id == MY_ID || user_id == ADMIN_ID)) {
                deleteGroup((int) chat_id);
                try {
                    execute(new SendMessage().setText("Канал " + update.getMessage().getChat().getTitle() + " успешно удалён!").setChatId((long) user_id));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("//botadd") && (user_id == MY_ID || user_id == ADMIN_ID)) {
                addGroup((int) chat_id);
                try {
                    execute(new SendMessage().setText("Канал " + update.getMessage().getChat().getTitle() + " добавлен!").setChatId((long) user_id));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (message_text.equals("/delall")) {
                String sql = "SELECT userNameOfChannel, postMessageId FROM " + TABLE_NAME + " WHERE isBanned = ? AND isDeleted = ? && isAddedMega = ?";
                try (Connection conn = this.connect();
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setBoolean(1, false);
                    pstmt.setBoolean(2, false);
                    pstmt.setBoolean(3, true);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        DeleteMessage deleteMessage = new DeleteMessage().setChatId(rs.getString(USERNAME_OF_CHANNEL)).setMessageId(rs.getInt(POSTMESSAGEID));
                        try {
                            deleteMessage(deleteMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                SendMessage m = new SendMessage().setChatId(chat_id).enableHtml(true).setText("<b>Недопустимая команда!</b> Помощь /help");
                try {
                    execute(m);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        } else if (update.hasChannelPost() && update.getChannelPost().hasText() && getBoolean(TABLE_LOG, ISON)) {
            String chanPostText = update.getChannelPost().getText();
            if (chanPostText.contains("Подборка Telegram каналов от @JootsINFO. Только РФ каналы")) {
                String chanUserName = "@" + update.getChannelPost().getChat().getUserName();
                int chanMessageId = update.getChannelPost().getMessageId();
                int timeOfPosting = update.getChannelPost().getDate();
                update(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, chanUserName.toUpperCase(), ISADDEDMEGA, true);
                update(TABLE_NAME, USERNAME_OF_CHANNEL_UPPER, chanUserName.toUpperCase(), POSTMESSAGEID, chanMessageId);
            }
        }
    }

    private void log(String first_name, String user_name, String user_id, String txt, long dateL, String chat_id) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(dateL * 1000L);
        SendMessage msg = new SendMessage()
                .setChatId(TEST_GROUP_CHAT_ID)
                .setText(dateFormat.format(date) + "\nСообщение от " + first_name + " @" + user_name + "\nuser_id = " + user_id + " chat_id = " + chat_id + "\nТекст: " + txt);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void showCategories(long chat_id) {
        String txt = "Доступные категории:\n";
        for (String i : categories)
            txt += "  " + EmojiParser.parseToUnicode(":radio_button:") + " " + i + "\n";
        SendMessage msg = new SendMessage().setChatId(chat_id).setText(txt);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String showCategories() {
        String txt = "Категории:\n";
        for (String i : categories)
            txt += "  " + EmojiParser.parseToUnicode(":radio_button:") + " " + i + "\n";
        return txt;
    }

    public void showHelp(long chat_id, boolean isAllowed) {
        String txt = isAllowed ? "Вы являетесь администратором!\nСписок доступных для вас команд:\n/add @Юзернейм Название канала - Категория канала\n" +
                "/delete @Юзернейм Удалить канал с меги\n" +
                "/list Показать список каналов\n" +
                "/categories Показать категории\n" +
                "/ban @Юзернейм Причина\n" +
                "/unban @Юзернейм\n" +
                "/banlist Список забаненных\n" +
                "/help Помощь\n" +
                "/notmega Вывести список тех, кто не опубликовал мегу\n" +
                "/mega Вывести мегу\n" +
                "/result Показать результаты меги\n" +
                "/ka Удалить канал из-за низкой активности\n" +
                "/autoadd Автоматически постить мегу во всех каналах\n" +
                "/end Закрыть сбор заявок\n" +
                "/delch Удалить с меги лишние каналы\n" +
                "/restart Возобновить сбор заявок\n" +
                "/start Число_подписчиков\n" +
                "/extra <i>Только для главного</i> Дополнительное меню" :
                "/add @Юзернейм Название канала - Категория канала <i>Добавить канал</i>\n<code>/add @JootsINFO Сеть Joots - Другое</code>\n" +
                        "/delete @Юзернейм <i>Удалить канал</i>\n<code>/delete @JootsINFO</code>";
        SendMessage msg = new SendMessage().setChatId(chat_id).setText(txt).enableHtml(true);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void createNewDatabase() {
        String url = "jdbc:sqlite:THEDATABASE.db";
        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void createNewTable(String table_name) {
        String url = "jdbc:sqlite:THEDATABASE.db";
        String sql = "CREATE TABLE IF NOT EXISTS " + table_name + " (\n"
                + "	messageId integer,\n"
                + "	userNameOfAdmin text,\n"
                + "	userNameOfChannel text,\n"
                + "	userNameOfChannelUpper text,\n"
                + "	nameOfChannel text,\n"
                + "	categoryOfChannel text,\n"
                + "	banReason text,\n"
                + " idOfAdmin integer,\n"
                + "	numberOfSubscribers integer,\n"
                + "	isBanned boolean,\n"
                + "	isDeleted boolean,\n"
                + "	postMessageId integer,\n"
                + "	isAddedMega boolean\n"
                + ");";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getBanList(String table_name) {
        String ret = "";
        String sql = "SELECT userNameOfChannel, userNameOfAdmin, banReason FROM " + table_name + " WHERE isBanned = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, true);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next())
                ret += ":mute:" + rs.getString("userNameOfChannel") + " :cop:@" + rs.getString("userNameOfAdmin") + " :no_entry:" + rs.getString("banReason") + "\n";
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public String getString(String table_name, String check_row, String check_value, String row) {
        String ret = "";
        String sql = "SELECT " + row + " FROM " + table_name + " WHERE " + check_row + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, check_value);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                ret = rs.getString(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public int getInt(String table_name, String check_row, String check_value, String row) {
        int ret = 0;
        String sql = "SELECT " + row + " FROM " + table_name + " WHERE " + check_row + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, check_value);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                ret = rs.getInt(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean getBoolean(String table_name, String check_row, String check_value, String row) {
        boolean ret = false;
        String sql = "SELECT " + row + " FROM " + table_name + " WHERE " + check_row + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, check_value);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                ret = rs.getBoolean(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public boolean getBoolean(String table_name, String row) {
        boolean ret = false;
        String sql = "SELECT " + row + " FROM " + table_name;
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                ret = rs.getBoolean(row);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = "jdbc:sqlite:THEDATABASE.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public void insertChannelInfo(String table, String ch_un, String ch_un_up, String ch_n, String ch_c, String adm_un, int adm_id, int adm_mid, int subs, boolean is_bnd, boolean is_del, boolean is_add) {
        String sql = "INSERT INTO " + table + "(messageId,userNameOfAdmin,userNameOfChannel,userNameOfChannelUpper,nameOfChannel,categoryOfChannel,numberOfSubscribers,idOfAdmin,isBanned,isDeleted,isAddedMega) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(6, ch_c);
            pstmt.setString(2, adm_un);
            pstmt.setString(3, ch_un);
            pstmt.setString(4, ch_un_up);
            pstmt.setString(5, ch_n);
            pstmt.setInt(1, adm_mid);
            pstmt.setInt(7, subs);
            pstmt.setInt(8, adm_id);
            pstmt.setBoolean(9, is_bnd);
            pstmt.setBoolean(10, is_del);
            pstmt.setBoolean(11, is_add);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(String table, String row, String row_value, String upd_row, String upd_row_value) {
        String sql = "UPDATE " + table + " SET "
                + upd_row + " = ? "
                + "WHERE " + row + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, upd_row_value);
            pstmt.setString(2, row_value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String table, String row, String row_value, String upd_row, boolean upd_row_value) {
        String sql = "UPDATE " + table + " SET "
                + upd_row + " = ? "
                + "WHERE " + row + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, upd_row_value);
            pstmt.setString(2, row_value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String table, String row, int row_value, String upd_row, boolean upd_row_value) {
        String sql = "UPDATE " + table + " SET "
                + upd_row + " = ? "
                + "WHERE " + row + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, upd_row_value);
            pstmt.setInt(2, row_value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String table, String row, String row_value, String upd_row, int upd_row_value) {
        String sql = "UPDATE " + table + " SET "
                + upd_row + " = ? "
                + "WHERE " + row + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, upd_row_value);
            pstmt.setString(2, row_value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String table, String row, String row_value) {
        String sql = "DELETE FROM " + table + " WHERE " + row + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, row_value);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String selectAll(String table_name) {
        String res = "";
        String sql = "SELECT idOfAdmin, userNameOfChannel, nameOfChannel, categoryOfChannel, numberOfSubscribers FROM " + table_name + " WHERE isBanned = ? AND isDeleted = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, false);
            pstmt.setBoolean(2, false);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                res += ":loudspeaker:<a href=\"t.me/" + rs.getString("userNameOfChannel").replaceFirst("@", "") + "\">" +
                        rs.getString("userNameOfChannel") + "</a> " +
                        //":clipboard: " + rs.getString("categoryOfChannel") + " " +
                        ":busts_in_silhouette:<b>" + rs.getInt("numberOfSubscribers") + "</b>" + " <a href=\"tg://user?id=" + rs.getInt("idOfAdmin") + "\">:cop:</a>\n";
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        res = EmojiParser.parseToUnicode(res);
        return res;
    }

    public int totalSubscribers(String table_name) {
        int res = 0;
        String sql = "SELECT numberOfSubscribers FROM " + table_name + " WHERE isBanned = ? AND isDeleted = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, false);
            pstmt.setBoolean(2, false);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                res += rs.getInt("numberOfSubscribers");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

    public int totalChannels(String table_name) {
        int res = 0;
        String sql = "SELECT numberOfSubscribers FROM " + table_name + " WHERE isBanned = ? AND isDeleted = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, false);
            pstmt.setBoolean(2, false);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                res++;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

    public boolean isContain(String table_name, String entry, String row) {
        boolean ret;
        String sql = "select * from " + table_name + " where " + row + " like '" + entry + "'";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                ret = true;
            else
                ret = false;
        } catch (SQLException e) {
            ret = false;
            e.printStackTrace();
        }
        return ret;
    }

    public int getCountOfMembersFromChannel(String channel_username) {
        int members_count = 0;
        GetChatMemberCount getChatMemberCount = new GetChatMemberCount();
        getChatMemberCount.setChatId(channel_username);
        try {
            members_count = getChatMemberCount(getChatMemberCount); // gets members count
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return members_count;
    }

    public int isUserAdminOfChannel(int user_id, String channel_id) {
        int ret;
        GetChatMember getChatMember = new GetChatMember();
        getChatMember.setChatId(channel_id);
        getChatMember.setUserId(user_id);
        try {
            ChatMember member = getChatMember(getChatMember);
            ret = (member.getStatus().contains("administrator") || member.getStatus().contains("creator")) ? 1 : 0;
        } catch (TelegramApiException e) {
            e.printStackTrace();
            ret = -1;
        }
        return ret;
    }

    public String getCategory(String table_name, String category_name, String channelUsername) {
        String res = "";
        String temp;
        String sql = "SELECT userNameOfChannel, nameOfChannel FROM " + table_name + " WHERE categoryOfChannel = ? AND isBanned = ? AND isDeleted = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, category_name);
            pstmt.setBoolean(2, false);
            pstmt.setBoolean(3, false);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                temp = rs.getString("userNameOfChannel");
                if (!temp.toUpperCase().equals(channelUsername.toUpperCase()))
                    res += temp + " - " + rs.getString("nameOfChannel") + "\n";
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

    public String getMega(String channelUsername) {
        String txt = "Подборка Telegram каналов от @JootsINFO. Только РФ каналы\n";
        for (String i : categories)
            txt += "\n" + EmojiParser.parseToUnicode(":radio_button:") + "<b>" + i + "</b>\n" + getCategory(TABLE_NAME, i, channelUsername);
        txt += "\n\nПродвижение каналов @JootsINFO";
        return txt;
    }

    public void addAdmin(String user) {
        String sql = "INSERT INTO " + TABLE_DATA + "(" + ADMINUSERNAME + ") VALUES(?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.toUpperCase());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteAdmin(String user) {
        String sql2 = "delete from " + TABLE_DATA + " where " + ADMINUSERNAME + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql2)) {
            pstmt.setString(1, user.toUpperCase());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isAdmin(String user) {
        boolean ret = false;
        String sql = "SELECT " + ADMINUSERNAME + " FROM " + TABLE_DATA + " WHERE " + ADMINUSERNAME + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.toUpperCase());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                ret = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            ret = false;
        }
        return ret;
    }

    public void addGroup(int chatid) {
        String sql = "INSERT INTO " + TABLE_DATA + "(" + GROUPCHATID + ") VALUES(?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, chatid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteGroup(int chatId) {
        String sql2 = "delete from " + TABLE_DATA + " where " + GROUPCHATID + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql2)) {
            pstmt.setInt(1, chatId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean isGroup(int chatId) {
        boolean ret = false;
        String sql = "SELECT " + GROUPCHATID + " FROM " + TABLE_DATA + " WHERE " + GROUPCHATID + " = ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, chatId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next())
                ret = true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            ret = false;
        }
        return ret;
    }

    @Override
    public String getBotUsername() {
        return "JootsRU_bot";
    }

    @Override
    public String getBotToken() {
        return "395778431:AAFJncQ_Np2D0iGM512T-oYelfz6Bo2rHik";
    }

}