package com.weg.Maintenance_API.exception.type;

public class NotificationDeliveryException extends RuntimeException {

    public NotificationDeliveryException(Throwable cause) {
        super("Não foi possível enviar a notificação por e-mail.", cause);
    }
}
