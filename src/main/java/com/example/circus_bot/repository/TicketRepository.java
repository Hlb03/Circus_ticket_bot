package com.example.circus_bot.repository;

import com.example.circus_bot.entity.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TicketRepository extends MongoRepository<Ticket, String> {

    Optional<Ticket> getTicketByChatId(long chatId);
}
