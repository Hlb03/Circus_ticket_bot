package com.example.circus_bot.workflow_states;

/**
 * Represents Telegram Bot workflow states
 */
public enum States {
    GREETING,
    START_ORDERING,
    ORDERING,
    INFO_FULFILL,
    PHONE_NUMBER_INPUT,
    FULL_NAME_INPUT,
    DATE_INPUT,
    TICKET_OUTPUT,
    CONTACT_OPERATOR
}
