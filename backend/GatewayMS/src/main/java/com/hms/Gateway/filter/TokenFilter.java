package com.hms.Gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;



@Component
public class TokenFilter extends AbstractGatewayFilterFactory<TokenFilter.Config> {
    private static final String SECRET = "8e7182879be9f49cb9428411bd3815643c4b7f6e669e2ac737d5e8b8f5548edf0cffc77b817b17af0a8726f5756a072679a7502bae4e17f79823e1e4390cf460";

    public TokenFilter(){
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) ->{
            String path = exchange.getRequest().getPath().toString();
            if(path.equals("/user/login")|| path.equals("/user/register")){

                return chain.filter(exchange.mutate().request(r->r.header("X-Secret-Key", "SECRET")).build());
            }
            HttpHeaders header = exchange.getRequest().getHeaders();
            if(!header.containsKey(HttpHeaders.AUTHORIZATION)){
                throw  new RuntimeException("Authorization header is missing");

            }
            String authHeader = header.getFirst(HttpHeaders.AUTHORIZATION);
            if(authHeader == null || !authHeader.startsWith("Bearer")){
                throw   new RuntimeException("Invalid Authorization header format");
            }
           String token = authHeader.substring(7);
            try{
                Claims claim = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
                exchange=exchange.mutate().request(r->r.header("X-Secret-Key","SECRET")).build();
            } catch (Exception e) {
                throw new RuntimeException("TOKEN IS INVALID");
            }
           return chain.filter(exchange);
        } ;
    }


    public static class Config{

    }
}
