package com.example.circus_bot.services;

import com.example.circus_bot.entity.Ticket;
import com.example.circus_bot.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.circus_bot.bot_phrases.ButtonTexts.*;

/**
 * Class that provides required buttons to fulfill all card data
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckRequiredDataService {

    private final TicketRepository ticketRepository;

    /**
     * Points which data is missing providing button names
     * to close gaps.
     *
     * @param chatId of user who writes to the bot
     * @return a list of button names that should be mandatory pressed
     * for ticket ordering
     */
    // TODO: remove SOUT!!!
    public List<String> findButtonNamesForMissingData(long chatId) {
        Optional<Ticket> ticket = ticketRepository.getTicketByChatId(chatId);
        List<String> buttons = findNullFields(ticket);
        log.info("Required buttons for the user ({}) are {}", chatId, buttons);

        return buttons;
    }

    private List<String> findNullFields(Optional<Ticket> ticket) {
        if (ticket.isEmpty())
            return List.of(INSERT_DATE_BUTTON, INSERT_PHONE_NUMBER_BUTTON, INSERT_FIRST_AND_LASTNAME_BUTTON);

        List<String> list = new ArrayList<>();
        if (ticket.get().getFullName() == null)
            list.add(INSERT_FIRST_AND_LASTNAME_BUTTON);
        if (ticket.get().getDateTime() == null)
            list.add(INSERT_DATE_BUTTON);
        if (ticket.get().getPhoneNumber() == null)
            list.add(INSERT_PHONE_NUMBER_BUTTON);

        return list;
    }
}
