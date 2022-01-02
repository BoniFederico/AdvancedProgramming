package request;

import expression.Expression;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ComputationRequest extends Request {

    public static final String GRID_COMMAND = "GRID";
    public static final String LIST_COMMAND = "LIST";

    public static final String MIN_COMMAND = "MIN";
    public static final String MAX_COMMAND = "MAX";
    public static final String AVG_COMMAND = "AVG";
    public static final String COUNT_COMMAND = "COUNT";

    private final ComputationKind computationKind;
    private final ValuesKind valuesKind;
    private final List<Map<String, Double>> tuples;
    private final List<Expression> expressions;

    public enum ValuesKind {
        GRID(GRID_COMMAND),
        LIST(LIST_COMMAND);
        private final String propriety;

        ValuesKind(String propriety) {
            this.propriety = propriety;
        }

        public String getPropriety() {
            return propriety;
        }
    }

    public enum ComputationKind {
        MIN(MIN_COMMAND),
        MAX(MAX_COMMAND),
        AVG(AVG_COMMAND),
        COUNT(COUNT_COMMAND);
        private final String propriety;

        ComputationKind(String propriety) {
            this.propriety = propriety;
        }

        public String getPropriety() {
            return propriety;
        }
    }

    public ComputationRequest(ValuesKind valKind, ComputationKind compKind, List<Map<String, Double>> tuples, List<Expression> expressions) {
        super(RequestType.COMPUTATION_REQUEST);
        this.computationKind = compKind;
        this.valuesKind = valKind;
        this.tuples = tuples;
        this.expressions = expressions;
    }

    public ComputationKind getComputationKind() {
        return computationKind;
    }

    public ValuesKind getValuesKind() {
        return valuesKind;
    }

    public List<Map<String, Double>> getTuples() {
        return tuples;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ComputationRequest req = (ComputationRequest) o;
        return req.computationKind.equals(this.computationKind)
                && req.valuesKind.equals(this.valuesKind)
                && req.tuples.equals(this.tuples)
                && req.expressions.equals(this.expressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.computationKind, this.valuesKind, this.tuples, this.expressions);
    }
}
