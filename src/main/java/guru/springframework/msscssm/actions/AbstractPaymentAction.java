package guru.springframework.msscssm.actions;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.services.PaymentServiceImpl;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;

/**
 * @author Olivier Cappelle
 * @version x.x.x
 * @see
 * @since x.x.x 21/12/2020
 **/
public abstract class AbstractPaymentAction implements Action<PaymentState, PaymentEvent> {

    protected Message<PaymentEvent> buildMessage(StateContext<PaymentState, PaymentEvent> context, PaymentEvent event) {
        Object paymentIdHeader = context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER);
        return MessageBuilder.withPayload(event)
                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, paymentIdHeader)
                .build();
    }
}
