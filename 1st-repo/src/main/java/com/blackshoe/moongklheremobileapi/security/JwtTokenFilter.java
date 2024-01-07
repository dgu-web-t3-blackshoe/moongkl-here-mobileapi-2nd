package com.blackshoe.moongklheremobileapi.security;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        log.info("JwtTokenFilter.doFilterInternal");
        try {

            String token = getTokenFromRequestHeader(request);

            if (token != null && jwtTokenProvider.validateToken(token)) {

                String email = jwtTokenProvider.getEmailFromToken(token);
                UUID userId = jwtTokenProvider.getUserIdFromToken(token);  // userId를 가져옵니다.

                UserDetails userDetails;
                userDetails = userDetailsService.loadUserByUsername(email);

                AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        AuthorityUtils.NO_AUTHORITIES
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
                securityContext.setAuthentication(authentication);

                SecurityContextHolder.setContext(securityContext);
                log.info("SecurityContextHolder.getContext().setAuthentication(authentication)" + authentication);
            }
            //spring security set user
            chain.doFilter(request, response);

        }
        catch(Exception e){
            log.error("Could not set user authentication in security context", e);
        }
    }

    private String getTokenFromRequestHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }



}
