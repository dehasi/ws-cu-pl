package bigStep;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class BigStep {

    static class Environment {
        final Map<String, Expression> map;

        Environment(Map<String, Expression> map) {this.map = map;}

        public Environment() {
            this(new HashMap<>());
        }

        void set(String name, Expression value) {
            map.put(name, value);
        }

        Expression get(String name) {
            return Objects.requireNonNull(
                    map.get(name),
                    "Nothing found for name: " + name
            );
        }

        public Environment copy() {
            return new Environment(new HashMap<>(map));
        }

        @Override public String toString() {
            return map.toString();
        }
    }

    interface Expression {
        Expression evaluate(Environment env);
    }

    record Number(int value) implements Expression {
        @Override public Expression evaluate(Environment env) {
            return this;
        }

        @Override public String toString() {
            return String.valueOf(value);
        }

    }

    record Variable(String name) implements Expression {
        @Override public Expression evaluate(Environment env) {
            return this;
        }

        @Override public String toString() {
            return name;
        }
    }

    static Number asNumber(Expression exp) {
        return (Number) exp;
    }

    static Number asNumber(int value) {
        return new Number(value);
    }

    record Add(Expression left, Expression right) implements Expression {

        @Override public Expression evaluate(Environment env) {
            var l = left.evaluate(env);
            var r = right.evaluate(env);
            return new Number(
                    asNumber(l).value + asNumber(r).value);
        }

        @Override public String toString() {
            return String.format("%s + %s", left, right);
        }
    }

}