package com.freewayemi.merchant.service.notifications;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.AccessException;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.EvaluationContext;

import java.util.List;

public class PackageRestrictedBeanFactoryResolver implements BeanResolver {

    private final BeanFactoryResolver beanFactoryResolver;
    private final List<String> allowedPackages;

    public PackageRestrictedBeanFactoryResolver(BeanFactory beanFactory, List<String> allowedPackages) {
        beanFactoryResolver = new BeanFactoryResolver(beanFactory);
        this.allowedPackages = allowedPackages;
    }

    @Override
    public Object resolve(EvaluationContext context, String beanName) throws AccessException {
        Object resolvedBean = beanFactoryResolver.resolve(context, beanName);
        if (allowedPackages.contains(resolvedBean.getClass().getPackage().getName())) {
            return resolvedBean;
        }
        throw new AccessException("Bean is restricted for use in expression");
    }
}
