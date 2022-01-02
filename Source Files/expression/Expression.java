package expression;

public class Expression {

    private final String expression;
    private final Node rootNode;

    public Expression(String expression) throws IllegalArgumentException {
        this.expression = expression;
        this.rootNode = (new Parser(expression)).parse();
    }

    public String getExpression() {
        return expression;
    }

    public Node getRootNode() {
        return rootNode;
    }
}
