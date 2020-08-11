package com.example.devices.web.rest.errors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.DefaultProblem;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemBuilder;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.violations.ConstraintViolationProblem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller advice to translate the server side exceptions to client-friendly json structures.
 * The error response follows RFC7807 - Problem Details for HTTP APIs (https://tools.ietf.org/html/rfc7807).
 */
@ControllerAdvice
public class ExceptionTranslator implements ProblemHandling {

    private static final String MESSAGE_KEY = "message";
    private static final String ERR_VALIDATION = "error.validation";


    @Override
    public ResponseEntity<Problem> process(@Nullable ResponseEntity<Problem> entity, NativeWebRequest request) {
        if (entity == null) {
            return null;
        }
        Problem problem = entity.getBody();
        if (!(problem instanceof ConstraintViolationProblem || problem instanceof DefaultProblem)) {
            return entity;
        }
        ProblemBuilder problemBuilder = Problem.builder()
                .withStatus(problem.getStatus())
                .withTitle(problem.getTitle())
                .with("path", request.getNativeRequest(HttpServletRequest.class).getRequestURI());
        if (problem instanceof ConstraintViolationProblem) {
            problemBuilder
                    .with("violations", ((ConstraintViolationProblem) problem).getViolations())
                    .with(MESSAGE_KEY, ERR_VALIDATION);
        } else {
            problemBuilder
                    .withDetail(problem.getDetail())
                    .withCause(((ThrowableProblem) problem).getCause())
                    .withInstance(problem.getInstance());
            problem.getParameters().forEach(problemBuilder::with);
            if (!problem.getParameters().containsKey(MESSAGE_KEY) && problem.getStatus() != null) {
                problemBuilder.with(MESSAGE_KEY, "error.http." + problem.getStatus().getStatusCode());
            }
        }
        return new ResponseEntity<>(problemBuilder.build(), entity.getHeaders(), entity.getStatusCode());
    }

    @Override
    public ResponseEntity<Problem> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, @Nonnull NativeWebRequest request) {
        BindingResult result = exception.getBindingResult();
        List<FieldErrorMV> fieldErrors = result.getFieldErrors().stream()
                .map(f -> new FieldErrorMV(
                        f.getObjectName().replaceFirst("Dto$", ""),
                        f.getField(),
                        f.getDefaultMessage()))
                .collect(Collectors.toList());
        Problem problem = Problem.builder()
                .withTitle("Method argument not valid")
                .withStatus(defaultConstraintViolationStatus())
                .with(MESSAGE_KEY, ERR_VALIDATION)
                .with("fieldErrors", fieldErrors)
                .build();
        return create(exception, problem, request);
    }
}
