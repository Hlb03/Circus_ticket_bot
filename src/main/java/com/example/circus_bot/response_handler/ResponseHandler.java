package com.example.circus_bot.response_handler;

import com.example.circus_bot.bot_phrases.Constants;
import com.example.circus_bot.exceptions.IncorrectInsertedDataException;
import com.example.circus_bot.services.CheckRequiredDataService;
import com.example.circus_bot.services.DataInsertionService;
import com.example.circus_bot.services.ResponseUserMessageService;
import com.example.circus_bot.workflow_states.States;
import lombok.extern.slf4j.Slf4j;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Map;

import static com.example.circus_bot.bot_phrases.ButtonTexts.*;
import static com.example.circus_bot.workflow_states.States.*;

/**
 * Class responsible for giving replies to bot users
 */
@Slf4j
public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, States> userState;
    private final DataInsertionService insertionService;
    private final CheckRequiredDataService requiredDataService;
    private final ResponseUserMessageService responseService;

    public ResponseHandler(SilentSender sender, DBContext context, DataInsertionService insertionService,
                           CheckRequiredDataService requiredDataService, ResponseUserMessageService responseService) {
        this.sender = sender;
        userState = context.getMap("CHAT_STATES");
        this.insertionService = insertionService;
        this.requiredDataService = requiredDataService;
        this.responseService = responseService;
    }

    /**
     * Replies to user queries/messages and informs them what they can do.
     * <p>
     * According to the user state (information about which state of bot usage is user currently on),
     * which updates each time user presses buttons or types
     * returns certain response to a user with following instructions.
     * <p>
     * In case when a user enters ticket information in a wrong way notifies that it was done incorrect
     * and should be done one more time.
     *
     * @param chatId of user who writes to the bot
     * @param message informs who and what typed to the bot
     */
    public void replyToUserRequest(long chatId, Message message) {
        userState.putIfAbsent(chatId, GREETING);

        log.info("MESSAGE AND CONTACT {} -> {}", message.getText(), message.getContact());

        log.info("State: {}", userState.values());
        log.info("Message: {}", message);
        log.info("CHAT ID: {}", chatId);
        log.info("CHECK USER STATE: {}", userState.get(chatId));
        switch (userState.get(chatId)) {
            case GREETING -> {
                sendMessage(responseService.instructNewUser(chatId, message.getFrom().getFirstName()));
                updateUserWorkflowState(chatId, START_ORDERING);
            }
            case START_ORDERING -> {
                sendMessage(responseService.helpInfo(chatId));
                sendMessage(responseService.startOrdering(chatId));
                updateUserWorkflowState(chatId, ORDERING);
            }
            case ORDERING -> {
                sendMessage(
                        responseService.provideOrderingButtons(
                                chatId,
                                requiredDataService.findButtonNamesForMissingData(chatId)
                        )
                );
                updateUserWorkflowState(chatId, INFO_FULFILL);
            }
            case INFO_FULFILL -> insertTicketInfo(chatId, message);
            case DATE_INPUT -> {
                try {
                    insertionService.parseDateInfo(chatId, message);
                    reactToUserDataInsertion(chatId, "Date");
                } catch (IncorrectInsertedDataException e) {
                    sendMessage(responseService.insertTicketData(chatId, Constants.INSERT_DATE_MESSAGE, e.getMessage()));
                    updateUserWorkflowState(chatId, DATE_INPUT);
                }
            }
            case PHONE_NUMBER_INPUT -> {
                try {
                    insertionService.parsePhoneNumberInfo(chatId, message);
                    reactToUserDataInsertion(chatId, "Phone number");
                } catch (IncorrectInsertedDataException e) {
                    sendMessage(responseService.insertTicketData(chatId, Constants.INSERT_PHONE_NUMBER_MESSAGE, e.getMessage()));
                    updateUserWorkflowState(chatId, PHONE_NUMBER_INPUT);
                }
            }
            case FULL_NAME_INPUT -> {
                try {
                    insertionService.parseFullName(chatId, message);
                    reactToUserDataInsertion(chatId, "Full name");
                } catch (IncorrectInsertedDataException e) {
                    sendMessage(responseService.insertTicketData(chatId, Constants.INSERT_FULL_NAME_MESSAGE, e.getMessage()));
                    updateUserWorkflowState(chatId, FULL_NAME_INPUT);
                }
            }
            case TICKET_OUTPUT -> {
                sendMessage(responseService.outputFinalTicket(chatId));
                sendMessage(responseService.informAboutMessageResend(chatId));
                updateUserWorkflowState(chatId, CONTACT_OPERATOR);
            }
            case CONTACT_OPERATOR -> sendMessage(responseService.resendMessageToOperator(message));
        }
    }

    /**
     * According to selected data insertion type (phone number, full name or datetime)
     * sends message with required data pattern and update the user state.
     *
     * @param chatId of user who writes to the bot
     * @param text that represents which info type user want to enter
     */
    private void insertTicketInfo(long chatId, Message text) {
        switch (text.getText()) {
            case INSERT_FIRST_AND_LASTNAME_BUTTON -> {
                sendMessage(responseService.insertTicketData(chatId, Constants.INSERT_FULL_NAME_MESSAGE, ""));
                updateUserWorkflowState(chatId, FULL_NAME_INPUT);
            }
            case INSERT_DATE_BUTTON -> {
                sendMessage(responseService.insertTicketData(chatId, Constants.INSERT_DATE_MESSAGE, ""));
                updateUserWorkflowState(chatId, DATE_INPUT);
            }
            case INSERT_PHONE_NUMBER_BUTTON -> {
                sendMessage(responseService.insertTicketData(chatId, Constants.INSERT_PHONE_NUMBER_MESSAGE, ""));
                updateUserWorkflowState(chatId, PHONE_NUMBER_INPUT);
            }
        }
    }

    /**
     * Executes sending message
     *
     * @param message including chatId and response to a user
     */
    private void sendMessage(SendMessage message) {
        sender.execute(message);
    }

    /**
     * Sends message to a user to inform that data was successfully absorbed.
     * <p>
     * Check whether more data is required, if not -> sets a user to ticket receiving state.
     * Recursively calls method replyToUserRequest() to response to the user state update.
     *
     * @param chatId of user who writes to the bot
     * @param insertedDataName name of data supplied by user
     */
    private void reactToUserDataInsertion(long chatId, String insertedDataName) {
        sendMessage(responseService.responseToDataInsertion(chatId, insertedDataName));
        updateUserWorkflowState(chatId, ORDERING);

        if (requiredDataService.findButtonNamesForMissingData(chatId).size() == 0)
            updateUserWorkflowState(chatId, TICKET_OUTPUT);
        replyToUserRequest(chatId, new Message());
    }

    /**
     * Updates user state which makes a step forward in the bot working process
     *
     * @param chatId of user who writes to the bot
     * @param nextState of bot workflow
     */
    private void updateUserWorkflowState(long chatId, States nextState) {
        userState.put(chatId, nextState);
    }
}
