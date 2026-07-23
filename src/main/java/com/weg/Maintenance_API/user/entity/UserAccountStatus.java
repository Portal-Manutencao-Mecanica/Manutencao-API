package com.weg.Maintenance_API.user.entity;

public enum UserAccountStatus {
    PENDING_FIRST_ACCESS,
    ACTIVE,
    TEMPORARILY_LOCKED,
    BLOCKED,
    DISABLED,
    PASSWORD_EXPIRED
}
