package com.dimata.helpdesk.exception;

import lombok.Getter;

public enum ExceptionCode {
    F_NV("Form not Valid"),
    JWT_IS_EXPIRED("Jwt kedaluarsa, silahkan request ulang"),
    FORBIDDEN("Anda tidak berhak mengakses api ini!"),
    //---------- HANYA MODIF DIBAGIAN BAWAH IN
    USERNAME_NOT_FOUND("User dengan username ini tidak ditemukan"),
    PASSWORD_NOT_MATCH("Password tidak cocok!"),
    UNAUTHORIZED("Bearer token harus di isi!"),
    TOKEN_NOT_VALID("Jwt tidak valid, jwt harus bertipe ACCESS bukan REFRESH"),
    TOKEN_NOT_REGISTERED("Token tidak terdaftar!"),
    SESSION_NOT_FOUND("Session tidak ditemukan / tidak aktif / telah expired, silahkan login ulang"),
    USER_NOT_FOUND("User tidak ditemukan!"),
    ROLE_NOT_FOUND("Role tidak ditemukan!"),
    ROLE_NAME_EXISTS("Role dengan nama ini telah ada!"),
    PERMISSION_NAME_ALREADY_EXISTS("Permission dengan nama ini telah ada!"),
    PERMISSION_NOT_FOUND("permissionId tidak ditemukan"),
    CURRENCY_NOT_FOUND("currencyId tidak ditemukan!"),
    TEAM_NOT_FOUND("teamId tidak ditemukan!"),
    PROJECT_TYPE_NOT_FOUND("projectTypeId tidak ditemukan!"),
    CUSTOMER_NOT_FOUND("customerId tidak ditemukan!"),
    PROJECT_NOT_FOUND("projectId tidak ditemukan!"),
    TASK_NOT_FOUND("taskId tidak ditemukan!"),
    FILE_NOF_FOUND("FIle tidak ditemukan / rusak")
    ;

    @Getter
    private final String message;

    ExceptionCode(String message) {
        this.message = message;
    }

}