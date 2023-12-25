package com.example.circus_bot.services;

import com.example.circus_bot.entity.Ticket;
import com.example.circus_bot.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.circus_bot.bot_phrases.ButtonTexts.*;

@Service
@RequiredArgsConstructor
public class CheckRequiredDataService {

    private final TicketRepository ticketRepository;

    public String[] findButtonNamesForMissingData(long chatId) {
        Optional<Ticket> ticket = ticketRepository.getTicketByChatId(chatId);
        String[] buttons = findNullFields(new ArrayList<>(), ticket);
        System.out.println("BUTTONS: " + Arrays.toString(buttons));

        return buttons;
    }

    private String[] findNullFields(List<String> list, Optional<Ticket> ticket) {
        if (ticket.isEmpty())
            return new String[] {INSERT_DATE_BUTTON, INSERT_PHONE_NUMBER_BUTTON, INSERT_FIRST_AND_LASTNAME_BUTTON};

        if (ticket.get().getFullName() == null)
            list.add(INSERT_FIRST_AND_LASTNAME_BUTTON);
        if (ticket.get().getDateTime() == null)
            list.add(INSERT_DATE_BUTTON);
        if (ticket.get().getPhoneNumber() == null)
            list.add(INSERT_PHONE_NUMBER_BUTTON);

        return list.toArray(new String[0]);
    }
}
