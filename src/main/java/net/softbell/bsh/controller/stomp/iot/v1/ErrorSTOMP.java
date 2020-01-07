package net.softbell.bsh.controller.stomp.iot.v1;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : Bell(bell@softbell.net)
 * @Description : STOMP Error Controller
 * STOMP publish: /api/stomp/pub
 * STOMP subscribe: /api/stomp/topic, /api/stomp/queue
 */
@RestController
public class ErrorSTOMP
{
	@MessageExceptionHandler
    @SendToUser("/iot/v1/node")
    public String handleException(Throwable exception)
	{
        return exception.getMessage();
    }
}
