package cn.github.zeroclian.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * @Author: ZeroClian
 * @Date: 2021-10-12 11:04 上午
 */
@Component
public class TokenFilter implements GlobalFilter, Ordered {

    private final static String TOKEN = "token";
    private final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("TokenFilter开始............");
        getAllHeadersRequest(exchange.getRequest());
        //拦截的逻辑。根据具体业务逻辑做拦截。
        List<String> token = exchange.getRequest().getHeaders().get(TOKEN);
        if (token == null || token.isEmpty()) {
            logger.info("TOKEN is empty...");
            //设置status和body
            return Mono.defer(() -> {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);//设置status
                final ServerHttpResponse response = exchange.getResponse();
                byte[] bytes = "{\n    \"code\":\"41\",\n    \"message\":\"非法访问,没有检测到token\"\n}".getBytes(StandardCharsets.UTF_8);
                DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                //设置header
                response.getHeaders().set(TOKEN, HttpStatus.UNAUTHORIZED.name());
                logger.info("TokenFilter拦截非法请求，没有检测到token............");
                return response.writeWith(Flux.just(buffer));//设置body
            });
        }
        //没有被if条件拦截，就放行
        return chain.filter(exchange);
    }

    private Map getAllHeadersRequest(ServerHttpRequest request) {
        logger.info("getAllHeadersRequest开始............");
        Map map = new HashMap();
        HttpHeaders hearders = request.getHeaders();
        Iterator it = hearders.keySet().iterator();
        while (it.hasNext()) {
            String keyName = (String) it.next();

            List<String> headValues = hearders.get(keyName);
            if (headValues.size() >= 1) {
                String kvalue = headValues.get(0);
                logger.info("request header的key：" + keyName + ",值：" + kvalue);
                map.put(keyName, kvalue);
            }
        }
        logger.info("getAllHeadersRequest结束............");
        return map;
    }

}
