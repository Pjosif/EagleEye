package org.micros.zuulsvr.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.stereotype.Component;


@Component
public class ResponseFilter extends ZuulFilter {
    private static final int FILTER_ORDER = 1;
    private static final boolean SHOULD_FILTER = true;
    private static final Logger logger = LoggerFactory.getLogger(ResponseFilter.class);

    @Autowired
    private FilterUtils filterUtils;

    @Autowired
    Tracer tracer;

    @Override
    public String filterType() {
        return FilterUtils.POST_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();

        logger.debug("Adding the correlation id to the outbound headers. {}", tracer.getCurrentSpan().traceIdString());
        ctx.getResponse().addHeader(FilterUtils.CORRELATION_ID, tracer.getCurrentSpan().traceIdString());

        logger.debug("Completing outgoing request for {}.", ctx.getRequest().getRequestURI());

        return null;
    }
}
