package bigStep;

import bigStep.BigStep.Expression;

import static java.lang.Character.isAlphabetic;
import static java.lang.Character.isDigit;

class Parser {


    /*
    EXP: EXP | TERM
    TERM: N | V | (EXP)
    N: 0|1|2
    V: a|b
    * */
    char[] tokens;
    int i;

    Expression parse(char[] tokens) {
        this.tokens = tokens;
        this.i = 0;

        return parseExp();
    }

    private Expression parseExp() {
        Expression left = parseTerm();
        while (nextIs('+')) {
            i++;
            left = new BigStep.Add(left, parseTerm());
        }
        return left;
    }

    private Expression parseTerm() {
        if (isDigit(tokens[i])) {
            return new BigStep.Number(tokens[i++] - '0');
        }
        if (isAlphabetic(tokens[i])) {
            return new BigStep.Variable(String.valueOf(tokens[i++]));
        }
        if (nextIs('(')) {
            i++;
            var e = parseExp();
            i++; // consume ")"
            return e;
        }

        return null;
    }

    private boolean nextIs(char c) {
        return i < tokens.length && tokens[i] == c;
    }
}
