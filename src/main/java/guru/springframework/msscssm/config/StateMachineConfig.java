package guru.springframework.msscssm.config;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.guard.Guard;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;


/**
 * @author Olivier Cappelle
 * @version x.x.x
 * @see
 * @since x.x.x 21/12/2020
 **/
@Slf4j
//to have a component to generate state machine
@EnableStateMachineFactory
@Configuration
//Extend base or enum adapter in our case
//public class StateMachineConfig extends EnumStateMachineConfigurerAdapter<PaymentState, PaymentEvent> {
@RequiredArgsConstructor
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {
    //    private final PaymentIdGuard paymentIdGuard;
//    private final PreAuthAction preAuthAction;
//    private final PreAuthApprovedAction preAuthApprovedAction;
//    private final PreAuthDeclinedAction preAuthDeclinedAction;
//    private final AuthAction authAction;
//    private final AuthApprovedAction authApprovedAction;
//    private final AuthDeclinedAction authDeclinedAction;
    private final Guard<PaymentState, PaymentEvent> paymentIdGuard;
    private final Action<PaymentState, PaymentEvent> preAuthAction;
    private final Action<PaymentState, PaymentEvent> preAuthApprovedAction;
    private final Action<PaymentState, PaymentEvent> preAuthDeclinedAction;
    private final Action<PaymentState, PaymentEvent> authAction;
    private final Action<PaymentState, PaymentEvent> authApprovedAction;
    private final Action<PaymentState, PaymentEvent> authDeclinedAction;

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.AUTH)
                .end(PaymentState.PRE_AUTH_ERROR)
                .end(PaymentState.AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions
                //first line doesn't cause a state transition because target state is still 'new' and triggers 'pre_authorize' event
                .withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE)
                .action(preAuthAction).guard(paymentIdGuard)
                .and()
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
                .action(preAuthApprovedAction).guard(paymentIdGuard)
                .and()
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED)
                .action(preAuthDeclinedAction).guard(paymentIdGuard)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH).event(PaymentEvent.AUTHORIZE)
                .action(authAction).guard(paymentIdGuard)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH).event(PaymentEvent.AUTH_APPROVED)
                .action(authApprovedAction).guard(paymentIdGuard)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINED)
                .action(authDeclinedAction).guard(paymentIdGuard);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                log.info("stateChanged(from: {}, to: {})", from, to);
            }
        };

        config.withConfiguration()
                .listener(adapter);
    }

//    /**
//     * Require the paymentId header to be present to execute action
//     * Guards can also be used for business rules etc
//     * @return
//     */
//    public Guard<PaymentState, PaymentEvent> paymentIdGuard() {
//        return context -> nonNull(context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER));
//    }

    /*private Action<PaymentState, PaymentEvent> preAuthAction() {
        return context -> {
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
        };
    }*/

    /*private Action<PaymentState, PaymentEvent> authAction() {
        return context -> {
            log.debug("Auth was called!");

            Message<PaymentEvent> message;
            if (new Random().nextInt(10) < 8) {
                log.debug("Approved authorization");
                message = buildMessage(context, PaymentEvent.AUTH_APPROVED);
            } else {
                log.debug("Declined authorization!");
                message = buildMessage(context, PaymentEvent.AUTH_DECLINED);
            }
            context.getStateMachine().sendEvent(message);
        };
    }*/

    /*private Message<PaymentEvent> buildMessage(StateContext<PaymentState, PaymentEvent> context, PaymentEvent event) {
        Object paymentIdHeader = context.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER);
        return MessageBuilder.withPayload(event)
                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, paymentIdHeader)
                .build();
    }*/
}
