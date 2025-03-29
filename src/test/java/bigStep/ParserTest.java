package bigStep;

import org.junit.jupiter.api.Test;

class ParserTest {

    @Test void parse() {
        Parser parser = new Parser();

        var ast = parser.parse(new char[]{'1', '+', 'y'});

        System.out.println(ast);

        BigStep.Environment env = new BigStep.Environment();
        env.set("y", new BigStep.Number(4));
        var resul = ast.evaluate(env);
        System.out.println(resul);
    }
}