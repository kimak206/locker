package com.example.locker.dto;

import lombok.Data;

@Data
public class LockerPasswordResponse {
    private Long lockerId;
    private String lockerPassword;
    private String message;
}
