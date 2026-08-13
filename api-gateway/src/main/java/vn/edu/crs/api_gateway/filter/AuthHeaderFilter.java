package vn.edu.crs.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class AuthHeaderFilter implements GlobalFilter, Ordered {

    // Danh sách các đường dẫn không cần token
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth",
            "/api/public"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1. Nếu là đường dẫn PUBLIC thì cho qua ngay
        boolean isPublic = PUBLIC_PATHS.stream().anyMatch(path::startsWith);
        if (isPublic) {
            return chain.filter(exchange);
        }

        // 2. Kiểm tra Header Authorization (chấp nhận cả chữ hoa lẫn chữ thường)
        List<String> authHeaders = request.getHeaders().get("Authorization");
        if (authHeaders == null || authHeaders.isEmpty()) {
            authHeaders = request.getHeaders().get("authorization");
        }

        // Nếu không gửi Token -> Trả về 401
        if (authHeaders == null || authHeaders.isEmpty()) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        // 3. Có Header Token -> Cho phép request đi tiếp sang Service con (Course Service, Registration Service...)
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}