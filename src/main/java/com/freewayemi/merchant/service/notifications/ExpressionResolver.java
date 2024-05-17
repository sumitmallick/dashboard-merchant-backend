package com.freewayemi.merchant.service.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExpressionResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpressionResolver.class);

    private static final ParserContext PARSER_CONTEXT = new TemplateParserContext();
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final EvaluationContext evaluationContext;

    @Autowired
    public ExpressionResolver(BeanFactory beanFactory) {
        StandardEvaluationContext standardEvaluationContext = new StandardEvaluationContext();
        standardEvaluationContext.addPropertyAccessor(new MapAccessor());
        standardEvaluationContext.setBeanResolver(
                new PackageRestrictedBeanFactoryResolver(beanFactory,
                        Collections.singletonList(this.getClass().getPackage().getName())));
        this.evaluationContext = standardEvaluationContext;
    }

    // evaluate template expression
    public String evaluateTemplateExpression(String expression, Object root) {
        return expressionParser.parseExpression(expression, PARSER_CONTEXT).getValue(evaluationContext, root,
                String.class);
    }

    // evaluate expression
    public <T> T evaluateExpression(String expression, Object root, Class<T> type) {
        return expressionParser.parseExpression(expression).getValue(evaluationContext, root, type);
    }

    // helper method to evaluate template map
    public Map<String, Object> resolveTemplateExpressionMap(Map<String, Object> evalMap, Object rootObject) {
        Map<String, Object> resolvedMap = new HashMap<>();
        for (String key: evalMap.keySet()) {
            resolvedMap.put(key, resolveTemplateExpression(evalMap.get(key), rootObject));
        }
        return resolvedMap;
    }

    // helper method to evaluate template map or expression
    private Object resolveTemplateExpression(Object evalObj, Object rootObject) {
        if (evalObj instanceof String) {
            return evaluateTemplateExpression((String) evalObj, rootObject);
        }
        if (evalObj instanceof Map) {
            return resolveTemplateExpressionMap((Map<String, Object>) evalObj, rootObject);
        }
        return evalObj;
    }
}
