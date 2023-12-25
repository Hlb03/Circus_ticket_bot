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

import java.util.List;

import static com.example.circus_bot.bot_phrases.ButtonTexts.START_ORDER_BUTTON;
import static com.example.circus_bot.utils.CreateButtonsUtil.createButtons;
import static com.example.circus_bot.utils.SendMessageUtils.createMessage;
import static com.example.circus_bot.utils.UserInfoUtil.getUsernameOrFullName;

/**
 * Class contains whole circus ticket bot business logic
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ResponseUserMessageService {

    private final TicketRepository ticketRepository;
    @Value("${telegram.operator.id}")
    private long operatorChatId;

    /**
     * Greets a user after his/her first appeal to the bot
     *
     * @param chatId of user who writes to the bot
     * @param from whom message was sent
     * @return greeting message
     */
    public SendMessage instructNewUser(long chatId, String from) {
        log.info("CHAT ID IS ---> {}", chatId);
        return createMessage(chatId, Constants.GREETING_MESSAGE.replace("...", from));
    }

    /**
     * Returns basic information about bot and its workflow
     *
     * @param chatId of user who writes to the bot
     * @return general bot description message
     */
    public SendMessage helpInfo(long chatId) {
        return createMessage(chatId, Constants.HELP_MESSAGE);
    }

    /**
     * Gives user opportunity to start ordering a ticket
     *
     * @param chatId of user who writes to the bot
     * @return message with button that corresponds to start ordering process
     */
    public SendMessage startOrdering(long chatId) {
        return setButtonsAndSendMessage(chatId, Constants.START_ORDERING_MESSAGE, List.of(START_ORDER_BUTTON));
    }

    /**
     * Choose one of missing ticket information gaps
     *
     * @param chatId of user who writes to the bot
     * @param requiredButtonNames buttons left to set all obligatory ticket data
     * @return message with buttons to give certain data type
     */
    public SendMessage provideOrderingButtons(long chatId, List<String> requiredButtonNames) {
        return setButtonsAndSendMessage(chatId,
                Constants.TICKET_ENTER_DATA_MESSAGE,
                requiredButtonNames
        );
    }

    /**
     * Method to inform user about data pattern and
     * expect chosen one from him/her.
     *
     * @param chatId of user who writes to the bot
     * @param message that shows datetime pattern
     * @param errorText showing user incorrect insertion attempt
     * @return message with required data pattern
     */
    public SendMessage insertTicketData(long chatId, String message, String errorText) {
        return createMessage(chatId, errorText + message, new ReplyKeyboardRemove(true));
    }

    /**
     * Informs that user did data entering well
     *
     * @param chatId of user who writes to the bot
     * @param insertedField data name of inserted field
     * @return message that data was collected successfully
     */
    public SendMessage responseToDataInsertion(long chatId, String insertedField) {
        return createMessage(chatId, String.format("%s was successfully selected.", insertedField));
    }

    /**
     * Returns message with ordered ticket by a user
     *
     * @param chatId of user who writes to the bot
     * @return message that contains full ticket info
     */
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

    /**
     * Returns warning message that the following messages would be resent
     *
     * @param chatId of user who writes to the bot
     * @return message with warning of message resend
     */
    public SendMessage informAboutMessageResend(long chatId) {
        return createMessage(chatId, Constants.RESEND_OPERATOR_MESSAGE);
    }

    /**
     * Forms message for the operator
     *
     * @param message that was sent by a user
     * @return message to be sent to bot operator
     */
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

    private SendMessage setButtonsAndSendMessage(long chatId, String messageText, List<String> buttonTexts) {
        return createMessage(chatId, messageText, createButtons(buttonTexts));
    }
}
