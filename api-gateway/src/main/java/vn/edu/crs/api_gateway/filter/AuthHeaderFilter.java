package vn.edu.crs.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthHeaderFilter implements GlobalFilter, Ordered {

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth",
            "/api/public"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // 1. Cho qua nếu thuộc PUBLIC_PATHS HOẶC là request GET lấy danh sách môn học
        boolean isPublicPath = PUBLIC_PATHS.stream().anyMatch(path::startsWith);
        boolean isGetCourse = HttpMethod.GET.equals(method) && path.startsWith("/api/courses");

        if (isPublicPath || isGetCourse) {
            return chain.filter(exchange);
        }

        // 2. Kiểm tra Header Authorization
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            authHeaders = request.getHeaders().get("authorization");
        }

        // Nếu không gửi Token -> Trả về 401
        if (authHeaders == null || authHeaders.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 3. Có Token -> Đi tiếp
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}