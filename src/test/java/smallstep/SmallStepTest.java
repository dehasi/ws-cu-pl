package smallstep;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static smallstep.SmallStep.*;

class SmallStepTest {

    @Test void evaluate_number() {
        var exp = asNumber(42);

        var result = AbstractMachine.evaluate(exp);

        assertThat(result).isEqualTo(asNumber(42));
    }

    @Test void evaluate_add() {
        var exp = new Add(
                asNumber(1),
                asNumber(3)
        );

        var result = AbstractMachine.evaluate(exp);

        assertThat(result).isEqualTo(asNumber(4));
    }

    @Test void evaluate_add_a_lot() {
        var exp = new Add(
                new Add(
                        asNumber(1),
                        asNumber(2)
                ),
                new Add(
                        asNumber(3),
                        asNumber(4)
                )
        );

        var result = AbstractMachine.evaluate(exp);

        assertThat(result).isEqualTo(asNumber(10));
    }

    @Test void evaluate_mult() {
        var exp = new Mult(
                asNumber(5),
                asNumber(3)
        );

        var result = AbstractMachine.evaluate(exp);

        assertThat(result).isEqualTo(asNumber(15));
    }

    @Test void evaluate_variable() {
        var env = new Environment();
        env.set("y", asNumber(15));

        var exp = new Add(
                asNumber(1),
                new Variable("y")
        );

        var result = AbstractMachine.evaluate(exp, env);

        assertThat(result).isEqualTo(asNumber(16));
    }

    @Test void assignment() {
        var stmt = new Assignment(
                "x",
                new Add(
                        asNumber(1),
                        asNumber(3)
                ));

        var result = AbstractMachine.evaluate(stmt);

        assertThat(result.get("x")).isEqualTo(asNumber(4));
    }

    @Test void reduce_if() {
        var stmt = new SmallStep.If(
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

        var result = AbstractMachine.evaluate(stmt);

        assertThat(result.get("x")).isEqualTo(asNumber(1));
        assertThat(result.get("y")).isEqualTo(asNumber(2));
    }

    @Test void reduce_while() {
        var env = new Environment(Map.of("x", asNumber(0)));
        var stmt = new While(
                new LessThan(new Variable("x"), asNumber(4)),
                new Assignment("x", new Add(new Variable("x"), asNumber(1)))
        );

        var result = AbstractMachine.evaluate(stmt, env);
        assertThat(result.get("x")).isEqualTo(asNumber(4));
    }
}