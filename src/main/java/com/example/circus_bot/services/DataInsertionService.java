package com.example.circus_bot.services;

import com.example.circus_bot.entity.Ticket;
import com.example.circus_bot.exceptions.IncorrectInsertedDataException;
import com.example.circus_bot.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Class responsible for updating ticket information
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInsertionService {

    private final TicketRepository ticketRepository;

    /**
     * Parses and inserts in database datetime received from user
     *
     * @param chatId of user who writes to the bot
     * @param message contains ticket data
     * @throws IncorrectInsertedDataException when data is in a wrong format
     */
    public void parseDateInfo(long chatId, Message message) throws IncorrectInsertedDataException {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(message.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            Ticket ticket = checkDataPresence(chatId);
            ticket.setDateTime(localDateTime);
            ticketRepository.save(ticket);
            log.info("PARSED DATE: {}", localDateTime);

            log.info("COMPARING DATES: {}", LocalDateTime.now().isBefore(localDateTime));
        } catch (DateTimeParseException e) {
            throw new IncorrectInsertedDataException(String.format("Date was inserted in a wrong way, %s\n\n", message.getText()));
        }
    }

    /**
     * Parses and inserts in database user phone number
     *
     * @param chatId of user who writes to the bot
     * @param message contains ticket data
     * @throws IncorrectInsertedDataException when data is in a wrong format
     */
    public void parsePhoneNumberInfo(long chatId, Message message) throws IncorrectInsertedDataException {
        if (message.getText().matches("0[0-9]{9}")) {
            Ticket ticket = checkDataPresence(chatId);
            ticket.setPhoneNumber(message.getText());
            ticketRepository.save(ticket);
        } else throw new
                IncorrectInsertedDataException(String.format("Phone number provided by you (%s), doesn't match patter\n\n", message.getText()));

    }

    /**
     * Parses and inserts in database user's first and last names
     *
     * @param chatId of user who writes to the bot
     * @param message contains ticket data
     * @throws IncorrectInsertedDataException when data is in a wrong format
     */
    public void parseFullName(long chatId, Message message) throws IncorrectInsertedDataException {
        if (message.getText().matches("[A-Z][a-z]{1,10} [A-Z][a-z]{1,10}")) {
            Ticket ticket = checkDataPresence(chatId);
            ticket.setFullName(message.getText());
            ticketRepository.save(ticket);
        } else throw new
                IncorrectInsertedDataException(String.format("Entered first and last name (%s) are not in the suitable format\n\n", message.getText()));
    }

    private Ticket checkDataPresence(long chatId) {
        return ticketRepository.getTicketByChatId(chatId).orElse(
                Ticket.builder()
                        .chatId(chatId)
                        .build()
        );
    }
}
