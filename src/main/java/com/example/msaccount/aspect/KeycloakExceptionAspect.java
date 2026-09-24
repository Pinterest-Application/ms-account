package com.example.msaccount.aspect;

import com.example.libexception.exception.BadRequestException;
import com.example.libexception.exception.ConflictException;
import com.example.libexception.exception.NotFoundException;
import com.example.msaccount.error.UserErrorCode;
import jakarta.ws.rs.WebApplicationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class KeycloakExceptionAspect {

    @Around("execution(* com.example.msaccount.service.UserService.*(..))")
    public Object translateKeycloakExceptions(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (WebApplicationException ex) {
            int status = ex.getResponse().getStatus();
            String userId = joinPoint.getArgs().length > 0 ? String.valueOf(joinPoint.getArgs()[0]) : "unknown";

            throw switch (status) {
                case 404 -> new NotFoundException(
                        UserErrorCode.USER_NOT_FOUND,
                        "User with ID " + userId + " does not exist."
                );
                case 409 -> new ConflictException(
                        UserErrorCode.USER_CONFLICT,
                        "Conflict occurred modifying user with ID " + userId + "."
                );
                case 400 -> new BadRequestException(
                        UserErrorCode.INVALID_USER_DATA,
                        "Bad request sent to Keycloak for user ID " + userId + "."
                );
                default -> ex; // 500s bubble to GlobalExceptionHandler and error reporter
            };
        }
    }
}