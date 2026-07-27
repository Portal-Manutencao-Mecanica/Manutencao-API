package com.weg.Maintenance_API.exception.type;

public class NotificationDeliveryException extends RuntimeException {

    public NotificationDeliveryException(Throwable cause) {
        super("NÃ£o foi possÃ­vel enviar a notificaÃ§Ã£o por e-mail.", cause);
    }
}
