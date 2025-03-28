package smallstep;

import org.junit.jupiter.api.Test;
import smallstep.SmallStep.AbstractMachine;

import static org.assertj.core.api.Assertions.assertThat;
import static smallstep.SmallStep.asNumber;

class SmallStepTest {

    @Test void evaluate_number() {
        var exp = asNumber(42);

        var result = AbstractMachine.evaluate(exp);

        assertThat(result).isEqualTo(asNumber(42));
    }

    @Test void evaluate_add() {
        var exp = new SmallStep.Add(
                asNumber(1),
                asNumber(3)
        );

        var result = AbstractMachine.evaluate(exp);

        assertThat(result).isEqualTo(asNumber(4));
    }

    @Test void evaluate_add_a_lot() {
        var exp = new SmallStep.Add(
                new SmallStep.Add(
                        asNumber(1),
                        asNumber(2)
                ),
                new SmallStep.Add(
                        asNumber(3),
                        asNumber(4)
                )
        );

        var result = AbstractMachine.evaluate(exp);

        assertThat(result).isEqualTo(asNumber(10));
    }

    @Test void evaluate_mult() {
        var exp = new SmallStep.Mult(
                asNumber(5),
                asNumber(3)
        );

        var result = AbstractMachine.evaluate(exp);

        assertThat(result).isEqualTo(asNumber(15));
    }
}