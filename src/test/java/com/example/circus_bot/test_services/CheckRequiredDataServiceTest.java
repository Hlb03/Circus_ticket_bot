package com.example.circus_bot.test_services;


import com.example.circus_bot.entity.Ticket;
import com.example.circus_bot.repository.TicketRepository;
import com.example.circus_bot.services.CheckRequiredDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.circus_bot.bot_phrases.ButtonTexts.*;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CheckRequiredDataServiceTest {

    @Mock
    TicketRepository ticketRepository;

    CheckRequiredDataService requiredDataService;

    final long chatId = 1836498L;

    @BeforeEach
    public void init() {
        requiredDataService = new CheckRequiredDataService(ticketRepository);
    }

    @Test
    void findButtonNamesForMissingData_NoData() {
        when(ticketRepository.getTicketByChatId(chatId)).thenReturn(Optional.empty());

        assertIterableEquals(requiredDataService.findButtonNamesForMissingData(chatId),
                List.of(INSERT_DATE_BUTTON, INSERT_PHONE_NUMBER_BUTTON, INSERT_FIRST_AND_LASTNAME_BUTTON));

        verify(ticketRepository, times(1)).getTicketByChatId(chatId);
    }

    @Test
    void findButtonNamesForMissingData_DatePresent() {
        when(ticketRepository.getTicketByChatId(chatId)).thenReturn(Optional.of(
                Ticket.builder()
                        .dateTime(LocalDateTime.now())
                        .build())
        );

        assertIterableEquals(requiredDataService.findButtonNamesForMissingData(chatId),
                List.of(INSERT_FIRST_AND_LASTNAME_BUTTON, INSERT_PHONE_NUMBER_BUTTON));

        verify(ticketRepository, times(1)).getTicketByChatId(chatId);
    }

    @Test
    void findButtonNamesForMissingData_PhoneNumberPresent() {
        when(ticketRepository.getTicketByChatId(chatId)).thenReturn(Optional.of(
                Ticket.builder()
                        .phoneNumber("Phone number")
                        .build())
        );

        assertIterableEquals(requiredDataService.findButtonNamesForMissingData(chatId),
                List.of(INSERT_FIRST_AND_LASTNAME_BUTTON, INSERT_DATE_BUTTON));

        verify(ticketRepository, times(1)).getTicketByChatId(chatId);
    }

    @Test
    void findButtonNamesForMissingData_FullNamePresent() {
        when(ticketRepository.getTicketByChatId(chatId)).thenReturn(Optional.of(
                Ticket.builder()
                        .fullName("First and last names")
                        .build())
        );

        assertIterableEquals(requiredDataService.findButtonNamesForMissingData(chatId),
                List.of(INSERT_DATE_BUTTON, INSERT_PHONE_NUMBER_BUTTON));

        verify(ticketRepository, times(1)).getTicketByChatId(chatId);
    }

    @Test
    void findButtonNamesForMissingData_NameAndPhonePresent() {
        when(ticketRepository.getTicketByChatId(chatId)).thenReturn(Optional.of(
                Ticket.builder()
                        .fullName("Person name")
                        .phoneNumber("Phone number")
                        .build())
        );

        assertIterableEquals(requiredDataService.findButtonNamesForMissingData(chatId),
                List.of(INSERT_DATE_BUTTON));

        verify(ticketRepository, times(1)).getTicketByChatId(chatId);
    }

    @Test
    void findButtonNamesForMissingData_NameAndDatePresent() {
        when(ticketRepository.getTicketByChatId(chatId)).thenReturn(Optional.of(
                Ticket.builder()
                        .fullName("Person name")
                        .dateTime(LocalDateTime.now())
                        .build())
        );

        assertIterableEquals(requiredDataService.findButtonNamesForMissingData(chatId),
                List.of(INSERT_PHONE_NUMBER_BUTTON));

        verify(ticketRepository, times(1)).getTicketByChatId(chatId);
    }

    @Test
    void findButtonNamesForMissingData_DateAndNumberPresent() {
        when(ticketRepository.getTicketByChatId(chatId)).thenReturn(Optional.of(
                Ticket.builder()
                        .dateTime(LocalDateTime.now())
                        .phoneNumber("Phone number")
                        .build())
        );

        assertIterableEquals(requiredDataService.findButtonNamesForMissingData(chatId),
                List.of(INSERT_FIRST_AND_LASTNAME_BUTTON));

        verify(ticketRepository, times(1)).getTicketByChatId(chatId);
    }

    @Test
    void findButtonNamesForMissingData_AllPresent() {
        when(ticketRepository.getTicketByChatId(chatId)).thenReturn(Optional.of(
                Ticket.builder()
                        .fullName("Person name")
                        .phoneNumber("Phone number")
                        .dateTime(LocalDateTime.now())
                        .build())
        );

        assertIterableEquals(requiredDataService.findButtonNamesForMissingData(chatId),
                List.of());

        verify(ticketRepository, times(1)).getTicketByChatId(chatId);
    }
}