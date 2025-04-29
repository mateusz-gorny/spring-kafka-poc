package pl.monify.workflows.application;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import pl.monify.workflows.domain.NextActionDefinition;

import java.util.HashMap;
import java.util.Map;

public final class SpELActionInputMapper implements ActionInputMapper {
    private final ExpressionParser parser = new SpelExpressionParser();

    @Override
    public Map<String, Object> map(NextActionDefinition nextDefinition,
                                   Map<String, Object> context,
                                   Map<String, Object> output) {
        Map<String, Object> input = new HashMap<>();
        EvaluationContext evalCtx = new StandardEvaluationContext();
        evalCtx.setVariable("output", output);
        evalCtx.setVariable("context", context);

        for (var e : nextDefinition.outputToInputMapping().entrySet()) {
            String inputKey = e.getKey();
            String spel = e.getValue();
            Expression expr = parser.parseExpression(spel);
            Object value = expr.getValue(evalCtx);
            input.put(inputKey, value);
        }

        return input;
    }
}
