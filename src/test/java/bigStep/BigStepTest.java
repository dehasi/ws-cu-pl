package bigStep;

import org.junit.jupiter.api.Test;

import static bigStep.BigStep.*;
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
                new Add(asNumber(1), asNumber(2)),
                new Add(asNumber(3), asNumber(4))
        );

        var result = exp.evaluate(EMPTY);

        assertThat(result).isEqualTo(asNumber(10));
    }

    @Test void evaluate_mult() {
        var exp = new Mult(asNumber(5), asNumber(3));

        var result = exp.evaluate(EMPTY);

        assertThat(result).isEqualTo(asNumber(15));
    }

    @Test void evaluate_variable() {
        var env = new Environment();
        env.set("y", asNumber(15));
        var exp = new Add(asNumber(1), new Variable("y"));

        var result = exp.evaluate(env);

        assertThat(result).isEqualTo(asNumber(16));
    }

    @Test void evaluate_lessThan() {
        var exp = new LessThan(asNumber(5), asNumber(3));
        var result = exp.evaluate(EMPTY);
        assertThat(result).isEqualTo(new Bool(false));

        exp = new LessThan(asNumber(3), asNumber(5));
        result = exp.evaluate(EMPTY);
        assertThat(result).isEqualTo(new Bool(true));
    }

    @Test void assignment() {
        var stmt = new Assignment(
                "x",
                new Add(
                        asNumber(1),
                        asNumber(3)
                ));

        var result = stmt.evaluate(EMPTY);

        assertThat(result.get("x")).isEqualTo(asNumber(4));
    }

    @Test void evaluate_if() {
        var stmt = new If(
                new LessThan(
                        new Mult(
                                asNumber(2), asNumber(0)
                        ),
                        new Add(
                                asNumber(2), asNumber(0)
                        )
                ),
                new Sequence(
                        new Assignment("x", asNumber(1)),
                        new Assignment("y", asNumber(2))),
                new Sequence(
                        new Assignment("x", asNumber(3)),
                        new Assignment("y", asNumber(4))
                ));

        var result = stmt.evaluate(EMPTY);

        assertThat(result.get("x")).isEqualTo(asNumber(1));
        assertThat(result.get("y")).isEqualTo(asNumber(2));
    }
}