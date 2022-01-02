package request;

import expression.Expression;
import expression.Parser.TokenType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import request.ComputationRequest.ComputationKind;
import request.ComputationRequest.ValuesKind;
import request.StatRequest.StatRequestType;

public class RequestParser {

    private final String input;
    public static final String VARIABLE_NAME_REGEX = TokenType.VARIABLE.getRegex();

    public RequestParser(String input) {
        this.input = input;
    }

    public Request parse() throws IllegalArgumentException {
        String[] component = input.split(";");

        if (component.length == 1) {
            return parseStatRequest(component[0]);
        } else if (component.length == 2) {
            throw new IllegalArgumentException("Request not complete");
        }

        String[] compReqSettings = component[0].split("_");
        if (compReqSettings.length != 2) {
            throw new IllegalArgumentException("Incorrect number of properties in the computation request");
        }
        ComputationKind computationKind = getComputationKind(compReqSettings[0]);
        ValuesKind valuesKind = getValuesKind(compReqSettings[1]);
        String[] variables = component[1].split(",");
        for (String variable : variables) {
            if (variable.split(":").length != 4) {
                throw new IllegalArgumentException("One or more variables do not have the correct number of values");
            }
            if (!Pattern.compile(VARIABLE_NAME_REGEX).matcher(variable.split(":")[0]).find(0)) {
                throw new IllegalArgumentException("One or more variables do not have correct names");
            }
        }
        Map<String, List<Double>> variableValues;
        try {
            variableValues = getVariableValues(variables);

        } catch (NullPointerException | NumberFormatException e) {
            throw new NumberFormatException("It was not possible to parse one or more values in the VariableValuesFunction");
        }
        Map<String, List<Double>> variableValFunction = getVariableValuesFunction(variableValues);
        if (valuesKind == ValuesKind.LIST) {
            int valuesSize = variableValFunction.values().iterator().next().size();
            variableValFunction.values().stream().filter(values -> (values.size() != valuesSize)).forEachOrdered(_item -> {
                throw new IllegalArgumentException("LIST option cannot be used, variable value lists do not have the same length");
            });
        }
        List<Map<String, Double>> tuples = valuesKind == ValuesKind.GRID ? fromVariableValFunctionToGridTuples(variableValFunction) : fromVariableValFunctionToListTuples(variableValFunction);

        return new ComputationRequest(valuesKind, computationKind, tuples, Arrays.stream(component).skip(2).map(e -> new Expression(e)).collect(Collectors.toList()));
    }

    private StatRequest parseStatRequest(String request) throws IllegalArgumentException {
        return switch (request) {
            case StatRequest.STAT_REQS_COMMAND ->
                new StatRequest(StatRequestType.STAT_REQS);
            case StatRequest.STAT_AVG_TIME_COMMAND ->
                new StatRequest(StatRequestType.STAT_AVG_TIME);
            case StatRequest.STAT_MAX_TIME_COMMAND ->
                new StatRequest(StatRequestType.STAT_MAX_TIME);
            default ->
                throw new IllegalArgumentException("Command not recognised");
        };
    }

    private ComputationKind getComputationKind(String parameter) throws IllegalArgumentException {
        return switch (parameter) {
            case ComputationRequest.MIN_COMMAND ->
                ComputationKind.MIN;
            case ComputationRequest.MAX_COMMAND ->
                ComputationKind.MAX;
            case ComputationRequest.AVG_COMMAND ->
                ComputationKind.AVG;
            case ComputationRequest.COUNT_COMMAND ->
                ComputationKind.COUNT;
            default ->
                throw new IllegalArgumentException("ComputationKind of the request is not correct");
        };
    }

    private ValuesKind getValuesKind(String parameter) throws IllegalArgumentException {
        return switch (parameter) {
            case ComputationRequest.GRID_COMMAND ->
                ValuesKind.GRID;
            case ComputationRequest.LIST_COMMAND ->
                ValuesKind.LIST;
            default ->
                throw new IllegalArgumentException("ValuesKind of the request is not correct");
        };
    }

    private Map<String, List<Double>> getVariableValues(String[] variables) {
        return Arrays.asList(variables)
                .stream().map(s -> s.split(":"))
                .collect(Collectors.toMap(e -> e[0], e -> Arrays.asList(e)
                .subList(1, e.length)
                .stream().map(s -> Double.parseDouble(s))
                .collect(Collectors.toList())));
    }

    private Map<String, List<Double>> getVariableValuesFunction(Map<String, List<Double>> variableValues) throws IllegalArgumentException {
        Map<String, List<Double>> variableValuesFunction = new HashMap<>();
        variableValues.entrySet().forEach(variable -> {
            List<Double> values = new ArrayList<>();
            double min = variable.getValue().get(0);
            double step = variable.getValue().get(1);
            double max = variable.getValue().get(2);
            if (step <= 0) {
                throw new IllegalArgumentException("Step in variable values function cannot be less than or equal to zero");
            }
            for (int i = 0; min + step * i <= max; i++) {
                values.add((double) (min + i * step));
            }
            variableValuesFunction.put(variable.getKey(), values);
        });
        return variableValuesFunction;
    }

    private List<Map<String, Double>> fromVariableValFunctionToGridTuples(Map<String, List<Double>> variableValuesFunction) {
        return variableValuesFunction.entrySet().stream()
                .map(entry -> entry.getValue().stream()
                .map(str -> Map.of(entry.getKey(), str))
                .collect(Collectors.toList()))
                .reduce((list1, list2) -> list1.stream()
                .flatMap(map1 -> list2.stream()
                .map(map2 -> {
                    Map<String, Double> mp = new HashMap<>();
                    mp.putAll(map1);
                    mp.putAll(map2);
                    return mp;
                }))
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    private List<Map<String, Double>> fromVariableValFunctionToListTuples(Map<String, List<Double>> variableValuesFunction) {
        return variableValuesFunction.entrySet().stream()
                .map(entry -> entry.getValue().stream()
                .map(str -> Map.of(entry.getKey(), str))
                .collect(Collectors.toList()))
                .reduce((list1, list2) -> list1.stream().map(map1 -> {
            Map<String, Double> mapp = new HashMap<>();
            mapp.putAll(map1);
            Map.Entry<String, Double> el = list2.get(list1.indexOf(map1)).entrySet().iterator().next();
            mapp.put(el.getKey(), el.getValue());
            return mapp;
        })
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
