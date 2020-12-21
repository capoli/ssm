package guru.springframework.msscssm.guards;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.services.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.guard.Guard;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

/**
 * Require the paymentId header to be present to execute action
 * Guards can also be used for business rules etc
 *
 * @author Olivier Cappelle
 * @version x.x.x
 * @see
 * @since x.x.x 21/12/2020
 **/
@Slf4j
@Component
public class PaymentIdGuard implements Guard<PaymentState, PaymentEvent> {

    @Override
    public boolean evaluate(StateContext<PaymentState, PaymentEvent> context) {
        return nonNull(context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER));
    }
}
