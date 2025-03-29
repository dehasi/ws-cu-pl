package denotational;


import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class Denotational {

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

    interface Statement {
        Environment evaluate(Environment env);

        String toJS();

    }

    record Assignment(String name, Expression expression) implements Statement {
        @Override public Environment evaluate(Environment env) {
            var new_env = env.copy();
            new_env.set(name, expression.evaluate(env));
            return new_env;
        }

        @Override public String toJS() {
            return String.format("(e) => { const n_e = new Map(e); n_e.set('%s', ((%s)(e))); return n_e; }", name, expression.toJS());
        }

        @Override public String toString() {
            return String.format("%s = %s", name, expression);
        }
    }

    record Sequence(Statement first, Statement second) implements Statement {
        @Override public Environment evaluate(Environment env) {
            Environment new_env = first.evaluate(env);
            return second.evaluate(new_env);
        }

        @Override public String toJS() {

            return String.format("(e) => { return (%s)((%s)(e)) }", second.toJS(), first.toJS());
        }

        @Override public String toString() {
            return String.format("%s; %s", first, second);
        }
    }

    record If(Expression condition, Statement consequence, Statement alternative) implements Statement {
        @Override public Environment evaluate(Environment env) {
            var result = condition.evaluate(env);
            if (result.equals(new Bool(true)))
                return consequence.evaluate(env);

            if (result.equals(new Bool(false)))
                return alternative.evaluate(env);
            throw new IllegalStateException("Expected Bool, got: " + result);
        }

        @Override public String toJS() {
            return String.format("(e) => {" +
                                 " if( (%s)(e) ) {" +
                                 " return (%s)(e)" +
                                 "} else { " +
                                 " return (%s)(e)" +
                                 "}", condition.toJS(), consequence.toJS(), alternative.toJS());
        }

        @Override public String toString() {
            return String.format("if (%s) { %s } else { %s }", condition, consequence, alternative);
        }
    }

    record While(Expression condition, Statement body) implements Statement {

        @Override public Environment evaluate(Environment env) {
            var result = condition.evaluate(env);
            if (result.equals(new Bool(false)))
                return env;
            var new_env = body.evaluate(env);
            return this.evaluate(new_env);
        }

        @Override public String toJS() {
            return "";
        }

        @Override public String toString() {
            return String.format("while ( %s ) { %s }", condition, body);
        }
    }


    interface Expression {
        Expression evaluate(Environment env);

        String toJS();
    }

    record Number(int value) implements Expression {
        @Override public Expression evaluate(Environment env) {
            return this;
        }

        @Override public String toJS() {
            return String.format("(e) => { return %s; }", value);
        }

        @Override public String toString() {
            return String.valueOf(value);
        }
    }

    record Bool(boolean value) implements Expression {
        @Override public Expression evaluate(Environment env) {
            return this;
        }

        @Override public String toString() {
            return String.valueOf(value);
        }

        @Override public String toJS() {
            return String.format("(e) => { return %s; }", value);
        }
    }

    record Variable(String name) implements Expression {
        @Override public Expression evaluate(Environment env) {
            return env.get(name);
        }

        @Override public String toString() {
            return name;
        }

        @Override public String toJS() {
            return String.format("(e) => { return e.get('%s'); }", name);
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

        @Override public String toJS() {
            return String.format("(e) => { return ((%s)(e)) + ((%s)(e)); }", left.toJS(), right.toJS());
        }

        @Override public String toString() {
            return String.format("%s + %s", left, right);
        }
    }

    record Mult(Expression left, Expression right) implements Expression {

        @Override public Expression evaluate(Environment env) {
            var l = left.evaluate(env);
            var r = right.evaluate(env);
            return new Number(
                    asNumber(l).value * asNumber(r).value);
        }

        @Override public String toJS() {
            return String.format("(e) => { return ((%s)(e)) * ((%s)(e)); }", left.toJS(), right.toJS());
        }

        @Override public String toString() {
            return String.format("%s * %s", left, right);
        }
    }

    record LessThan(Expression left, Expression right) implements Expression {

        @Override public Expression evaluate(Environment env) {
            var l = left.evaluate(env);
            var r = right.evaluate(env);
            return new Bool(asNumber(l).value < asNumber(r).value);
        }

        @Override public String toJS() {
            return String.format("(e) => { return ((%s)(e)) < ((%s)(e)); }", left.toJS(), right.toJS());
        }

        @Override public String toString() {
            return String.format("%s < %s", left, right);
        }
    }
}