package com.example.circus_bot.services;

import com.example.circus_bot.bot_phrases.Constants;
import com.example.circus_bot.entity.Ticket;
import com.example.circus_bot.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import static com.example.circus_bot.bot_phrases.ButtonTexts.START_ORDER_BUTTON;
import static com.example.circus_bot.utils.CreateButtonsUtil.createButtons;
import static com.example.circus_bot.utils.SendMessageUtils.createMessage;
import static com.example.circus_bot.utils.UserInfoUtil.getUsernameOrFullName;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResponseUserMessageService {

    private final TicketRepository ticketRepository;
    @Value("${telegram.operator.id}")
    private long operatorChatId;

    public SendMessage instructNewUser(long chatId, String from) {
        log.info("CHAT ID IS ---> {}", chatId);
        return createMessage(chatId, Constants.GREETING_MESSAGE.replace("...", from));
    }

    public SendMessage helpInfo(long chatId) {
        return createMessage(chatId, Constants.HELP_MESSAGE);
    }

    public SendMessage startOrdering(long chatId) {
        return setButtonsAndSendMessage(chatId, Constants.START_ORDERING_MESSAGE, START_ORDER_BUTTON);
    }

    public SendMessage provideOrderingButtons(long chatId, String[] requiredButtonNames) {
        return setButtonsAndSendMessage(chatId,
                Constants.TICKET_ENTER_DATA_MESSAGE,
                requiredButtonNames
        );
    }

    public SendMessage insertTicketData(long chatId, String message, String errorText) {
        return createMessage(chatId, errorText + message, new ReplyKeyboardRemove(true));
    }

    public SendMessage responseToDataInsertion(long chatId, String insertedField) {
        return createMessage(chatId, String.format("%s was successfully selected.", insertedField));
    }

    public SendMessage outputFinalTicket(long chatId) {
        Ticket ticket = ticketRepository.getTicketByChatId(chatId).get();
        return createMessage(
                chatId,
                Constants.TICKET_REPRESENTATION_MESSAGE
                        .replace("ticket_id", ticket.getId())
                        .replace("date_time", ticket.getDateTime().toString())
                        .replace("ticket_name", ticket.getFullName())
                        .replace("phone_number", ticket.getPhoneNumber())
        );
    }

    public SendMessage informAboutMessageResend(long chatId) {
        return createMessage(chatId, Constants.RESEND_OPERATOR_MESSAGE);
    }

    public SendMessage resendMessageToOperator(Message message) {
        log.info("MESSAGE FROM USER: {}", message);
        log.info("MESSAGE FROM: {}", message.getFrom());
        return createMessage(
                operatorChatId,
                Constants.OPERATOR_MESSAGE
                        .replace("user_info",
                                getUsernameOrFullName(message)
                        )
                        .replace("message_text", message.getText())
        );
    }

    private SendMessage setButtonsAndSendMessage(long chatId, String messageText, String... buttonTexts) {
        return createMessage(chatId, messageText, createButtons(buttonTexts));
    }
}
