package com.example.circus_bot.repository;

import com.example.circus_bot.entity.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Used to execute queries in database
 */
@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {

    /**
     * Return Ticket entity from database storage
     *
     * @param chatId of user who writes to the bot
     * @return ticket in Optional coverage to get rid of NPE (Null Pointer Exception)
     */
    Optional<Ticket> getTicketByChatId(long chatId);
}
