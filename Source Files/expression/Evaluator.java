package expression;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Evaluator {

    private final Expression expression;

    public Evaluator(Expression expression) {
        this.expression = expression;
    }

    public Double eval(Map<String, Double> variableValues) throws IllegalArgumentException {
        return eval(expression.getRootNode(), variableValues);
    }

    public Double[] eval(List<Map<String, Double>> tuples) throws IllegalArgumentException {
        if (tuples.isEmpty()) {
            Double[] res = new Double[1];
            res[0] = eval(Collections.emptyMap());
            return res;
        }
        return tuples.stream().map(t -> eval(t)).toArray(Double[]::new);
    }

    public static Double[][] eval(List<Expression> expressions, List<Map<String, Double>> tuples) throws IllegalArgumentException {
        return expressions.stream().map(exp -> (new Evaluator(exp)).eval(tuples)).toArray(Double[][]::new);
    }

    private Double eval(Node node, Map<String, Double> variableValues) throws IllegalArgumentException {
        if (node.getClass().equals(Constant.class)) {
            return ((Constant) node).getValue();
        } else if (node.getClass().equals(Variable.class)) {
            if (!variableValues.containsKey(((Variable) node).getName())) {
                throw new IllegalArgumentException(String.format("Missing values for variable %s in VariableValuesFunction", ((Variable) node).getName()));
            } else {
            }
            Double value = variableValues.get(((Variable) node).getName());
            return value;
        } else if (node.getClass().equals(Operator.class)) {
            return switch (((Operator) node).getType().getSymbol()) {
                case '+' ->
                    eval(node.getChildren().get(0), variableValues) + eval(node.getChildren().get(1), variableValues);
                case '-' ->
                    eval(node.getChildren().get(0), variableValues) - eval(node.getChildren().get(1), variableValues);
                case '*' ->
                    eval(node.getChildren().get(0), variableValues) * eval(node.getChildren().get(1), variableValues);
                case '/' ->
                    eval(node.getChildren().get(0), variableValues) / eval(node.getChildren().get(1), variableValues);
                case '^' ->
                    Math.pow(eval(node.getChildren().get(0), variableValues), eval(node.getChildren().get(1), variableValues));
                default ->
                    null;
            };
        } else {
            return null;
        }
    }
}
