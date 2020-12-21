package guru.springframework.msscssm.actions;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @author Olivier Cappelle
 * @version x.x.x
 * @see
 * @since x.x.x 21/12/2020
 **/
@Slf4j
@Component
public class PreAuthAction extends AbstractPaymentAction {

    @Override
    public void execute(StateContext<PaymentState, PaymentEvent> context) {
        log.debug("PreAuth was called!");

        Message<PaymentEvent> message;
        //Random approve or decline payment
        if (new Random().nextInt(10) < 8) {
            log.debug("Approved");
            message = buildMessage(context, PaymentEvent.PRE_AUTH_APPROVED);
        } else {
            log.debug("Declined! No Cred!!");
            message = buildMessage(context, PaymentEvent.PRE_AUTH_DECLINED);
        }
        context.getStateMachine().sendEvent(message);
    }
}
