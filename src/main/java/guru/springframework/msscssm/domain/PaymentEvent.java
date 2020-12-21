package guru.springframework.msscssm.domain;

/**
 * @author Olivier Cappelle
 * @version x.x.x
 * @see
 * @since x.x.x 21/12/2020
 **/
public enum PaymentEvent {
    PRE_AUTHORIZE, PRE_AUTH_APPROVED, PRE_AUTH_DECLINED, AUTHORIZE, AUTH_APPROVED, AUTH_DECLINED
}
