package denotational;

import denotational.Denotational.Add;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static denotational.Denotational.asNumber;
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
}