package smallstep;

class SmallStep {

    static class AbstractMachine {
        static Expression evaluate(Expression e) {

            while (e.reducible()) {
                e = step(e);
            }
            System.out.println(e);
            return e;
        }

        private static Expression step(Expression e) {
            System.out.println(e);
            return e.reduce();
        }
    }

    static abstract sealed class Expression {
        boolean reducible() {
            return true;
        }

        abstract Expression reduce();
    }

    static final class Number extends Expression {
        final int value;

        Number(int value) {this.value = value;}

        @Override boolean reducible() {
            return false;
        }

        @Override Expression reduce() {
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

        @Override Expression reduce() {
            if (left.reducible())
                return new Add(left.reduce(), right);
            if (right.reducible())
                return new Add(left, right.reduce());

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

        @Override Expression reduce() {
            if (left.reducible())
                return new Add(left.reduce(), right);
            if (right.reducible())
                return new Add(left, right.reduce());

            return new Number(
                    asNumber(left).value * asNumber(right).value
            );
        }

        @Override public String toString() {
            return String.format("%s * %s", left, right);
        }
    }
}
