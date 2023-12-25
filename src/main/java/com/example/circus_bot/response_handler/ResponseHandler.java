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
import static com.example.circus_bot.utils.SendMessageUtils.createMessage;
import static com.example.circus_bot.workflow_states.States.*;

@Slf4j
public class ResponseHandler {
    private final SilentSender sender;
    private final Map<Long, States> userState;
    private final DataInsertionService insertionService;
    private final CheckRequiredDataService requiredDataService;
    private final ResponseUserMessageService responseService;

    public ResponseHandler(SilentSender sender, DBContext context, DataInsertionService insertionService, CheckRequiredDataService requiredDataService, ResponseUserMessageService responseService) {
        this.sender = sender;
        userState = context.getMap("CHAT_STATES");
        this.insertionService = insertionService;
        this.requiredDataService = requiredDataService;
        this.responseService = responseService;
    }

    public void replyToUserRequest(long chatId, Message message) {
        userState.putIfAbsent(chatId, GREETING);

        log.info("MESSAGE AND CONTACT {} -> {}", message.getText(), message.getContact());
//        if (message.getText().equalsIgnoreCase("/stop")) {
//            TODO: add logic for stopping the bot
//        }

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

    private void sendMessage(SendMessage message) {
        sender.execute(message);
    }

    private void reactToUserDataInsertion(long chatId, String insertedDataName) {
        sendMessage(responseService.responseToDataInsertion(chatId, insertedDataName));
        updateUserWorkflowState(chatId, ORDERING);

        if (requiredDataService.findButtonNamesForMissingData(chatId).length == 0)
            updateUserWorkflowState(chatId, TICKET_OUTPUT);
        replyToUserRequest(chatId, new Message());
    }

    private void updateUserWorkflowState(long chatId, States nextState) {
        userState.put(chatId, nextState);
    }
}
