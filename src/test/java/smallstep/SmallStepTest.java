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

    @Test void evaluate_variable() {
        var env = new SmallStep.Environment();
        env.set("y", asNumber(15));

        var exp = new SmallStep.Add(
                asNumber(1),
                new SmallStep.Variable("y")
        );

        var result = AbstractMachine.evaluate(exp, env);

        assertThat(result).isEqualTo(asNumber(16));
    }

    @Test void assignment() {
        var stmt = new SmallStep.Assignment(
                "x",
                new SmallStep.Add(
                        asNumber(1),
                        asNumber(3)
                ));

        var result = AbstractMachine.evaluate(stmt);

        assertThat(result.get("x")).isEqualTo(asNumber(4));
    }

    @Test void test_if() {
        var stmt = new SmallStep.If(
                new SmallStep.LessThan(
                        new SmallStep.Mult(
                                asNumber(2), asNumber(0)
                        ),
                        new SmallStep.Add(
                                asNumber(2), asNumber(0)
                        )
                ),
                new SmallStep.Sequence(
                        new SmallStep.Assignment("x", asNumber(1)),
                        new SmallStep.Assignment("y", asNumber(2))),
                new SmallStep.Sequence(
                        new SmallStep.Assignment("x", asNumber(3)),
                        new SmallStep.Assignment("y", asNumber(4))
                ));

        var result = AbstractMachine.evaluate(stmt);

        assertThat(result.get("x")).isEqualTo(asNumber(1));
        assertThat(result.get("y")).isEqualTo(asNumber(2));
    }
}