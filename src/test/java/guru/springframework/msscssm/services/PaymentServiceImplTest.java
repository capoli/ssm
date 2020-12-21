package guru.springframework.msscssm.services;

import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIn.oneOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PaymentServiceImplTest {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder().amount(new BigDecimal("12.95")).build();
    }

    //transactional needs to be added here because we try to access
    // the sout payment.toString() outside the transactional boundries
    @Transactional
    @RepeatedTest(15)
    void testPreAuth() {
        //region preAuth
        var savedPayment = paymentService.newPayment(this.payment);

        System.out.println("Should be new");
        System.out.println(savedPayment.getState());
        assertEquals(PaymentState.NEW, savedPayment.getState());

        var preAuthStateMachine = paymentService.preAuth(savedPayment.getId());
        var preAuthedPayment = paymentRepository.getOne(savedPayment.getId());

        System.out.println("Should be pre_auth or pre_auth_error");
        System.out.println(preAuthStateMachine.getState().getId());
        System.out.println(preAuthedPayment);
        assertThat(preAuthedPayment.getState(), oneOf(PaymentState.PRE_AUTH, PaymentState.PRE_AUTH_ERROR));
        //endregion section
    }

    @Transactional
    @RepeatedTest(15)
    void testAuth() {
        Payment savedPayment = paymentService.newPayment(this.payment);
        StateMachine<PaymentState, PaymentEvent> preAuthStateMachine = paymentService.preAuth(savedPayment.getId());
        //region authorize
        if (preAuthStateMachine.getState().getId() == PaymentState.PRE_AUTH) {
            System.out.println("Payment is pre_auth");
            StateMachine<PaymentState, PaymentEvent> authStateMachine = paymentService.authorizePayment(savedPayment.getId());
            Payment authPayment = paymentRepository.getOne(savedPayment.getId());
            System.out.println("Should be auth or auth_error");
            System.out.println(authStateMachine.getState().getId());
            System.out.println(authPayment);
            assertThat(authPayment.getState(), oneOf(PaymentState.AUTH, PaymentState.AUTH_ERROR));
        } else {
            System.out.println("Payment failed pre_auth");
        }
        //endregion
    }
}