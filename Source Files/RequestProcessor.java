
import server.Processor;
import expression.Evaluator;
import request.ComputationRequest;
import request.ComputationRequest.ComputationKind;
import request.Request;
import request.RequestParser;
import request.StatRequest;
import server.ServerStat;
import util.MathUtils;
import util.Timer;

public class RequestProcessor implements Processor {

    @Override
    public String process(String input, ServerStat stat) {
        Timer timer = new Timer();
        String response;
        try {
            RequestParser parser = new RequestParser(input);
            Request req;
            try {
                req = parser.parse();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("(RequestParsingError) " + e.getMessage());
            }

            response = switch (req.getType()) {
                case STAT_REQUEST ->
                    statRequestProcess((StatRequest) req, stat);
                case COMPUTATION_REQUEST ->
                    computationRequestProcess((ComputationRequest) req);
            };
            float elapsedTime = timer.getElapsedTime();
            stat.updateStat(elapsedTime);
            response = String.format("OK;%s;%s", String.format("%.3f", timer.getElapsedTime()).replaceAll(",", "."), response);

        } catch (IllegalArgumentException e) {
            response = String.format("ERR;%s", e.getMessage());
        } catch (Exception e) {
            response = String.format("ERR;(GenericError) %s", e.getMessage());
            throw e;
        }
        return response;

    }

    private String statRequestProcess(StatRequest request, ServerStat stat) {
        return switch (request.getStatRequestType()) {
            case STAT_REQS ->
                String.valueOf(stat.getNumberOfOkResponse());
            case STAT_AVG_TIME ->
                String.valueOf(String.format("%.3f", stat.getAverageResponseTime()).replaceAll(",", "."));
            case STAT_MAX_TIME ->
                String.valueOf(String.format("%.3f", stat.getMaximumResponseTime()).replaceAll(",", "."));
        };
    }

    private String computationRequestProcess(ComputationRequest request) {
        if (request.getComputationKind() == ComputationKind.COUNT) {
            return String.valueOf(request.getTuples().size());
        }
        Double[][] results;
        try {
            results = Evaluator.eval(request.getExpressions(), request.getTuples());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("(ExpressionParsingError) " + e.getMessage());
        }
        return switch (request.getComputationKind()) {
            case MAX ->
                String.valueOf(MathUtils.max(results));
            case MIN ->
                String.valueOf(MathUtils.min(results));
            case AVG ->
                String.valueOf(MathUtils.avg(results));
            case COUNT ->
                String.valueOf(request.getTuples().size());
        };
    }
}
