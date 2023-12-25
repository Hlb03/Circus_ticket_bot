package com.example.circus_bot.bot_phrases;

public interface Constants {
    String GREETING_MESSAGE = "Nice to see you in circus tickets ordering Telegram bot, ...!\n\nTo see all available options you may type /help command";

    String HELP_MESSAGE = """
            To order a ticket you may press the order button and provide the following information:\s
            - Date in the following format 22/01/2023 15:50
            - Phone number like 0671112233
            - First and last name starting with capital letters (e.g. Marian Smith)
            """;

    String START_ORDERING_MESSAGE = "Press the button below to start ordering process";

    String TICKET_ENTER_DATA_MESSAGE = "Press one of the following buttons and enter selected data";

    String INSERT_DATE_MESSAGE = "Provide date info in the format like *29/12/2023 15:50*";

    String INSERT_PHONE_NUMBER_MESSAGE = "Provide your phone number in the following format *0671112233*";

    String INSERT_FULL_NAME_MESSAGE = "Enter you first and last name like *Shawn Wallace*";

    String TICKET_REPRESENTATION_MESSAGE = """
            Here is your ticket information:
                        
            Unique ticket identifier `ticket_id`
            Attendance date and time: *date_time*
            Ordered for the *ticket_name* person
            Visitor contact information - *phone_number*
            """;

    String RESEND_OPERATOR_MESSAGE = """
            All messages, typed after this one will be redirected to the operator.
            Feel free to ask any questions.
            """;

    String OPERATOR_MESSAGE = """
            User with tag/name user_info writes the following:
            
            `message_text`
            """;
}
