package com.dimata.helpdesk.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Pesan exception sederhana,
 *
 * @author Hariyogi
 * @since 2 Sep 2020
 */
@Data
@AllArgsConstructor
public class ExceptionMessage {
    private final String timestamp = LocalDateTime.now().toString();
    private String httpCode;
    private String message;
}