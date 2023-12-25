package com.example.circus_bot.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "tickets")
@Builder
public class Ticket {
    @Id
    private String id;
    private LocalDateTime dateTime;
    private String fullName;
    private String phoneNumber;
    private long chatId;
}
