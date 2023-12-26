package com.example.circus_bot.test_response_handler;


import com.example.circus_bot.bot_phrases.ButtonTexts;
import com.example.circus_bot.exceptions.IncorrectInsertedDataException;
import com.example.circus_bot.response_handler.ResponseHandler;
import com.example.circus_bot.services.CheckRequiredDataService;
import com.example.circus_bot.services.DataInsertionService;
import com.example.circus_bot.services.ResponseUserMessageService;
import com.example.circus_bot.workflow_states.States;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ResponseHandlerTest {

    @Mock
    SilentSender sender;
    @Mock
    DBContext context;
    @Mock
    DataInsertionService insertionService;
    @Mock
    CheckRequiredDataService requiredDataService;
    @Mock
    ResponseUserMessageService userMessageService;

    ResponseHandler handler;

    Message message;
    SendMessage msg;

    final Map<Object, Object> states = new HashMap<>();

    @BeforeEach
    public void init() {
        when(context.getMap("CHAT_STATES")).thenReturn(states);
        handler = new ResponseHandler(sender, context, insertionService, requiredDataService, userMessageService);
        message = new Message();
        message.setFrom(new User(22L, "First", false, "Last", "@Some_name", null, null, null, null, null, null));
    }

    @Test
    void testReplyToUser_GreetingCase() {
        final long chatId = 100L;

        handler.replyToUserRequest(chatId, message);

        verify(userMessageService, times(1)).instructNewUser(chatId, message.getFrom().getFirstName());

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.START_ORDERING, states.get(chatId));

        verify(sender, times(1)).execute(msg);
    }

    @Test
    void testReplyToUser_StartOrderingCase() {
        final long chatId = 149902L;
        states.put(chatId, States.START_ORDERING);

        handler.replyToUserRequest(chatId, message);

        verify(userMessageService, times(1)).helpInfo(chatId);
        verify(userMessageService, times(1)).startOrdering(chatId);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.ORDERING, states.get(chatId));

        verify(sender, times(2)).execute(msg);
    }

    @Test
    void testReplyToUser_OrderingCase() {
        final long chatId = 2221309L;
        states.put(chatId, States.ORDERING);

        handler.replyToUserRequest(chatId, message);

        verify(sender, times(1)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.INFO_FULFILL, states.get(chatId));
    }

    @Test
    void testReplyToUser_InfoFulfillCaseFullNameInput() {
        final long chatId = 12534101L;
        states.put(chatId, States.INFO_FULFILL);
        message.setText(ButtonTexts.INSERT_FIRST_AND_LASTNAME_BUTTON);

        handler.replyToUserRequest(chatId, message);

        verify(sender, times(1)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.FULL_NAME_INPUT, states.get(chatId));
    }

    @Test
    void testReplyToUser_InfoFulfillCaseDateInput() {
        final long chatId = 14137841L;
        states.put(chatId, States.INFO_FULFILL);
        message.setText(ButtonTexts.INSERT_DATE_BUTTON);

        handler.replyToUserRequest(chatId, message);

        verify(sender, times(1)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.DATE_INPUT, states.get(chatId));
    }

    @Test
    void testReplyToUser_InfoFulfillCasePhoneInput() {
        final long chatId = 1049205L;
        states.put(chatId, States.INFO_FULFILL);
        message.setText(ButtonTexts.INSERT_PHONE_NUMBER_BUTTON);

        handler.replyToUserRequest(chatId, message);

        verify(sender, times(1)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.PHONE_NUMBER_INPUT, states.get(chatId));
    }

    @Test
    void testReplyToUser_DateInputCase_FirstInput() throws IncorrectInsertedDataException {
        final long chatId = 751687L;
        states.put(chatId, States.DATE_INPUT);

        when(requiredDataService.findButtonNamesForMissingData(chatId)).thenReturn(List.of("Some data"));

        handler.replyToUserRequest(chatId, message);

        verify(insertionService, times(1)).parseDateInfo(chatId, message);
        verify(sender, times(2)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.INFO_FULFILL, states.get(chatId));
    }

    @Test
    void testReplyToUser_DateInputCase_LastInput() throws IncorrectInsertedDataException {
        final long chatId  = 289394L;
        states.put(chatId, States.DATE_INPUT);

        handler.replyToUserRequest(chatId, message);

        verify(insertionService, times(1)).parseDateInfo(chatId, message);
        verify(sender, times(3)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.CONTACT_OPERATOR, states.get(chatId));
    }

    @Test
    void testReplyToUser_DateInputCase_WrongDateInputPattern() throws IncorrectInsertedDataException {
        final long chatId = 89953L;
        states.put(chatId, States.DATE_INPUT);

        IncorrectInsertedDataException dataException = new IncorrectInsertedDataException("Error_msg");
        doThrow(dataException).when(insertionService).parseDateInfo(chatId, message);

        handler.replyToUserRequest(chatId, message);

        verify(sender, times(1)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.DATE_INPUT, states.get(chatId));
    }

    @Test
    void testReplyToUser_PhoneInputCase_FirstInput() throws IncorrectInsertedDataException {
        final long chatId = 9079461L;
        states.put(chatId, States.PHONE_NUMBER_INPUT);

        when(requiredDataService.findButtonNamesForMissingData(chatId)).thenReturn(List.of("Random text"));

        handler.replyToUserRequest(chatId, message);

        verify(insertionService, times(1)).parsePhoneNumberInfo(chatId, message);
        verify(sender, times(2)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.INFO_FULFILL, states.get(chatId));
    }

    @Test
    void testReplyToUser_PhoneInputCase_LastInput() throws IncorrectInsertedDataException {
        final long chatId  = 9357189L;
        states.put(chatId, States.PHONE_NUMBER_INPUT);

        handler.replyToUserRequest(chatId, message);

        verify(insertionService, times(1)).parsePhoneNumberInfo(chatId, message);
        verify(sender, times(3)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.CONTACT_OPERATOR, states.get(chatId));
    }

    @Test
    void testReplyToUser_PhoneInputCase_WrongPhoneInputPattern() throws IncorrectInsertedDataException {
        final long chatId = 876517880L;
        states.put(chatId, States.PHONE_NUMBER_INPUT);

        IncorrectInsertedDataException dataException = new IncorrectInsertedDataException("Mismatch with the pattern");
        doThrow(dataException).when(insertionService).parsePhoneNumberInfo(chatId, message);

        handler.replyToUserRequest(chatId, message);

        verify(sender, times(1)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.PHONE_NUMBER_INPUT, states.get(chatId));
    }

    @Test
    void testReplyToUser_FullNameInputCase_FirstInput() throws IncorrectInsertedDataException {
        final long chatId = 7471983L;
        states.put(chatId, States.FULL_NAME_INPUT);

        when(requiredDataService.findButtonNamesForMissingData(chatId)).thenReturn(List.of("First element", "Second element"));

        handler.replyToUserRequest(chatId, message);

        verify(insertionService, times(1)).parseFullName(chatId, message);
        verify(sender, times(2)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.INFO_FULFILL, states.get(chatId));
    }

    @Test
    void testReplyToUser_FullNameInputCase_LastInput() throws  IncorrectInsertedDataException {
        final long chatId = 928938L;
        states.put(chatId, States.FULL_NAME_INPUT);

        handler.replyToUserRequest(chatId, message);

        verify(insertionService, times(1)).parseFullName(chatId, message);
        verify(sender, times(3)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.CONTACT_OPERATOR, states.get(chatId));
    }

    @Test
    void testReplyToUser_FullNameInputCase_WrongInputNamePattern() throws IncorrectInsertedDataException {
        final long chatId = 8753156L;
        states.put(chatId, States.FULL_NAME_INPUT);

        IncorrectInsertedDataException insertedDataException = new IncorrectInsertedDataException("Wrong full name");
        doThrow(insertedDataException).when(insertionService).parseFullName(chatId, message);

        handler.replyToUserRequest(chatId, message);

        verify(sender, times(1)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.FULL_NAME_INPUT, states.get(chatId));
    }

    @Test
    void testReplyToUser_TicketOutputCase() {
        final long chatId = 9171893L;
        states.put(chatId, States.TICKET_OUTPUT);

        handler.replyToUserRequest(chatId, message);

        verify(sender, times(2)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.CONTACT_OPERATOR, states.get(chatId));
    }

    @Test
    void testReplyToUser_ContactOperatorCase() {
        final long chatId = 11111L;
        states.put(chatId, States.CONTACT_OPERATOR);

        handler.replyToUserRequest(chatId, message);

        verify(sender, times(1)).execute(msg);

        assertEquals(Set.of(chatId), states.keySet());
        assertEquals(States.CONTACT_OPERATOR, states.get(chatId));
    }
}
