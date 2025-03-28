package smallstep;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class SmallStep {

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
    }

    static class AbstractMachine {
        static Environment evaluate(Statement stmt) {
            return evaluate(stmt, new Environment());
        }

        static Environment evaluate(Statement stmt, Environment env) {
            while (stmt.reducible()) {
                System.out.printf("%s, %s", stmt, env);
                var result = stmt.reduce(env);
                stmt = result.statement();
                env = result.environment();
            }
            System.out.printf("%s, %s", stmt, env);
            return env;
        }

        static Expression evaluate(Expression e) {
            return evaluate(e, new Environment());
        }

        static Expression evaluate(Expression e, Environment env) {

            while (e.reducible()) {
                e = step(e, env);
            }
            System.out.println(e);
            return e;
        }

        private static Expression step(Expression e, Environment env) {
            System.out.println(e);
            return e.reduce(env);
        }
    }

    record StatementResult(
            Statement statement, Environment environment
    ) {}

    static sealed abstract class Statement {
        boolean reducible() {
            return true;
        }

        abstract StatementResult reduce(Environment env);
    }

    static final class DoNothing extends Statement {
        @Override boolean reducible() {
            return false;
        }

        @Override StatementResult reduce(Environment env) {
            throw new IllegalStateException("DoNothing is not reducible");
        }
    }

    static final class Assignment extends Statement {
        final String name;
        final Expression expression;

        Assignment(String name, Expression expression) {
            this.name = name;
            this.expression = expression;
        }

        @Override StatementResult reduce(Environment env) {

            if (expression.reducible()) {
                return new StatementResult(
                        new Assignment(name, expression.reduce(env)),
                        env
                );
            } else {
                var newEnv = env.copy();
                newEnv.set(name, expression);

                return new StatementResult(
                        new DoNothing(),
                        newEnv
                );
            }
        }
    }


    static abstract sealed class Expression {
        boolean reducible() {
            return true;
        }

        abstract Expression reduce(Environment env);
    }


    static final class Variable extends Expression {
        final String name;

        Variable(String name) {this.name = name;}

        @Override Expression reduce(Environment env) {
            return env.get(name);
        }

        @Override public String toString() {
            return name;
        }
    }

    static final class Number extends Expression {
        final int value;

        Number(int value) {this.value = value;}

        @Override boolean reducible() {
            return false;
        }

        @Override Expression reduce(Environment env) {
            throw new IllegalStateException("Number is not reducible");
        }

        @Override public String toString() {
            return String.valueOf(value);
        }

        @Override public boolean equals(Object obj) {
            if (obj instanceof Number that)
                return this.value == that.value;
            return false;
        }
    }

    static Number asNumber(Expression exp) {
        return (Number) exp;
    }

    static Number asNumber(int value) {
        return new SmallStep.Number(value);
    }

    static final class Add extends Expression {

        final Expression left;
        final Expression right;

        Add(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        @Override Expression reduce(Environment env) {
            if (left.reducible())
                return new Add(left.reduce(env), right);
            if (right.reducible())
                return new Add(left, right.reduce(env));

            return new Number(
                    asNumber(left).value + asNumber(right).value
            );
        }

        @Override public String toString() {
            return String.format("%s + %s", left, right);
        }
    }

    static final class Mult extends Expression {

        final Expression left;
        final Expression right;

        Mult(Expression left, Expression right) {
            this.left = left;
            this.right = right;
        }

        @Override Expression reduce(Environment env) {
            if (left.reducible())
                return new Mult(left.reduce(env), right);
            if (right.reducible())
                return new Mult(left, right.reduce(env));

            return new Number(
                    asNumber(left).value * asNumber(right).value
            );
        }

        @Override public String toString() {
            return String.format("%s * %s", left, right);
        }
    }
}
