package denotational;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static denotational.Denotational.*;
import static org.assertj.core.api.Assertions.assertThat;

class DenotationalTest {

//    private static final Environment EMPTY = new Environment();

    @Test void evaluate_number() {
        var exp = asNumber(42);

        var code = exp.toJS();

        System.out.println(code);
        var result = JSRunner.run(code, Map.of());
        System.out.println(result);
        assertThat(result).isEqualTo("42");
    }

    @Test void evaluate_add() {
        var exp = new Add(asNumber(1), asNumber(3));

        var code = exp.toJS();

        System.out.println(code);
        var result = JSRunner.run(code, Map.of());
        System.out.println(result);
        assertThat(result).isEqualTo("4");
    }

    @Test void evaluate_add_a_lot() {
        var exp = new Add(
                new Add(asNumber(1), asNumber(2)),
                new Add(asNumber(3), asNumber(4))
        );

        var code = exp.toJS();

        System.out.println(code);
        var result = JSRunner.run(code, Map.of());
        System.out.println(result);
        assertThat(result).isEqualTo("10");
    }

    @Test void evaluate_mult() {
        var exp = new Mult(asNumber(5), asNumber(3));

        var code = exp.toJS();

        System.out.println(code);
        var result = JSRunner.run(code, Map.of());
        System.out.println(result);
        assertThat(result).isEqualTo("15");
    }

    @Test void evaluate_variable() {
        var env = Map.of("y", "15");
        var exp = new Add(asNumber(1), new Variable("y"));

        var code = exp.toJS();

        System.out.println(code);
        var result = JSRunner.run(code, env);
        System.out.println(result);
        assertThat(result).isEqualTo("16");
    }

    @Test void evaluate_lessThan() {
        var exp = new LessThan(asNumber(5), asNumber(3));
        var code = exp.toJS();

        System.out.println(code);
        var result = JSRunner.run(code, Map.of());
        System.out.println(result);
        assertThat(result).isEqualTo("false");

        exp = new LessThan(asNumber(3), asNumber(5));
        code = exp.toJS();

        System.out.println(code);
        result = JSRunner.run(code, Map.of());
        System.out.println(result);
        assertThat(result).isEqualTo("true");
    }

    @Test void assignment() {
        var stmt = new Assignment(
                "x",
                new Add(asNumber(1), asNumber(3))
        );

        var code = stmt.toJS();

        System.out.println(code);
        var result = JSRunner.run(code, Map.of());
        System.out.println(result);
        assertThat(result).isEqualTo("Map(1) { 'x' => 4 }");
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

        var code = stmt.toJS();

        System.out.println(code);
        var result = JSRunner.run(code, Map.of());
        System.out.println(result);
        assertThat(result).isEqualTo("Map(2) { 'x' => 1, 'y' => 2 }");
    }

}