package bigStep;

import bigStep.BigStep.Add;
import bigStep.BigStep.Environment;
import org.junit.jupiter.api.Test;

import static bigStep.BigStep.asNumber;
import static org.assertj.core.api.Assertions.assertThat;

class BigStepTest {

    private static final Environment EMPTY = new Environment();

    @Test void evaluate_number() {
        var exp = asNumber(42);

        var result = exp.evaluate(EMPTY);

        assertThat(result).isEqualTo(asNumber(42));
    }

    @Test void evaluate_add() {
        var exp = new Add(asNumber(1), asNumber(3));

        var result = exp.evaluate(EMPTY);

        assertThat(result).isEqualTo(asNumber(4));
    }


    @Test void evaluate_add_a_lot() {
        var exp = new Add(
                new Add(
                        asNumber(1),
                        asNumber(2)),
                new Add(
                        asNumber(3),
                        asNumber(4))
        );

        var result = exp.evaluate(EMPTY);

        assertThat(result).isEqualTo(asNumber(10));
    }
}