package com.example.circus_bot.test_services;

import com.example.circus_bot.exceptions.IncorrectInsertedDataException;
import com.example.circus_bot.repository.TicketRepository;
import com.example.circus_bot.services.DataInsertionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataInsertionServiceTest {

    @InjectMocks
    DataInsertionService insertionService;

    @Mock
    TicketRepository ticketRepository;

    @Mock
    Message message;

    final long chatId = 1L;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void parseDateThrowsException_PatterMismatch() {
        when(message.getText()).thenReturn("12-02-2024 13:01");

        assertThrows(IncorrectInsertedDataException.class, () -> insertionService.parseDateInfo(chatId, message));
    }

    @Test
    void parseDateThrowsException_RandomText() {
        when(message.getText()).thenReturn("Some random text");

        assertThrows(IncorrectInsertedDataException.class, () -> insertionService.parseDateInfo(chatId, message));
    }

    @Test
    void parseDateSuccessfully() {
        when(message.getText()).thenReturn("12/12/2024 19:21");

        assertDoesNotThrow(() -> insertionService.parseDateInfo(chatId, message));
    }

    @Test
    void parsePhoneNumberThrowsException_PatternMismatch() {
        when(message.getText()).thenReturn("+380674512203");

        assertThrows(IncorrectInsertedDataException.class, () -> insertionService.parsePhoneNumberInfo(chatId, message));
    }

    @Test
    void parsePhoneNumberThrowsException_NumberWithSpaces() {
        when(message.getText()).thenReturn("+38 067 451 22 03");

        assertThrows(IncorrectInsertedDataException.class, () -> insertionService.parsePhoneNumberInfo(chatId, message));
    }

    @Test
    void parsePhoneNumberSuccessfully() {
        when(message.getText()).thenReturn("0674512203");

        assertDoesNotThrow(() -> insertionService.parsePhoneNumberInfo(chatId, message));
    }

    @Test
    void parseFullNameThrowsException_TooLong() {
        when(message.getText()).thenReturn("Patrick van Leuven");

        assertThrows(IncorrectInsertedDataException.class, () -> insertionService.parseFullName(chatId, message));
    }

    @Test
    void parseFullNameThrowsException_PatternMismatch() {
        when(message.getText()).thenReturn("kevin smith");

        assertThrows(IncorrectInsertedDataException.class, () -> insertionService.parseFullName(chatId, message));
    }

    @Test
    void parseFullNameSuccessfully() {
        when(message.getText()).thenReturn("Samantha Wallace");

        assertDoesNotThrow(() -> insertionService.parseFullName(chatId, message));
    }
}
