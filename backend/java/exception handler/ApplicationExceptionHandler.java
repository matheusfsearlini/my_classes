package br.com.oauth.exception;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import feign.FeignException;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationExceptionHandler.class);

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler({ EntityNotFoundException.class })
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException exception, HttpServletRequest httpRequest) {

        String userMessage = "NAO FOI POSSIVEL ENCONTRAR REGISTROS NA BASE DE DADOS COM OS CRITERIOS INFORMADOS";
        String devMessage = exception.getMessage();

        List<ApiError<String>> errors = List.of(new ApiError<>(LocalDateTime.now(), devMessage, userMessage, httpRequest.getRequestURI(), null));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ MethodArgumentNotValidException.class })
    public ResponseEntity<?> handleBeanValidation(MethodArgumentNotValidException exception, HttpServletRequest httpRequest) {

        ApiError<String> apiError = new ApiError<>(LocalDateTime.now(), exception.getMessage(), "FORAM ENCONTRADOS ERROS DE VALIDACAO NOS CAMPOS DO OBJETO ENVIADO", httpRequest.getRequestURI(), new ArrayList<>());

        exception.getBindingResult().getFieldErrors().forEach(error -> {
            apiError.getFieldsErrors().add(new ApiFieldError(error.getField(), error.getDefaultMessage()));
        });

        if (apiError.getFieldsErrors().isEmpty()) {
            apiError.setMessage(exception.getBindingResult().getGlobalError().getDefaultMessage());

        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }

    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<?> handleGenericException(RuntimeException exception, HttpServletRequest httpRequest) {

        String messageUser = "ERRO NO PROCESSAMENTO DA REQUISICAO (BACKEND)";

        LOG.error("ERRO: {}", exception.getClass().getCanonicalName());
        exception.printStackTrace();
        String messageDev = exception.toString();

        List<ApiError<String>> errors = List.of(new ApiError<>(LocalDateTime.now(), messageDev, messageUser, httpRequest.getRequestURI(), null));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ ExistingEntityException.class })
    public ResponseEntity<?> handleExistingEntity(ExistingEntityException exception, HttpServletRequest httpRequest) {

        exception.printStackTrace();
        String messageDev = exception.toString();

        List<ApiError<String>> errors = List.of(new ApiError<>(LocalDateTime.now(), messageDev, exception.getMessage(), httpRequest.getRequestURI(), null));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ MissingServletRequestParameterException.class })
    public ResponseEntity<?> handleMissingParameter(MissingServletRequestParameterException exception, HttpServletRequest httpRequest) {

        LOG.error("O PARAMETRO {} DO TIPO {} E OBRIGATORIO E NAO FOI INFORMADO", exception.getParameterName(), exception.getParameterType());
        exception.printStackTrace();
        String messageDev = exception.getMessage();

        ApiError<String> errors = new ApiError<>(LocalDateTime.now(), messageDev, "PARAMETROS OBRIGATORIOS NAO INFORMADOS", httpRequest.getRequestURI(),
                List.of(new ApiFieldError(exception.getParameterName(), exception.getParameterType())));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ MissingPathVariableException.class })
    public ResponseEntity<?> handleMissingPathVariable(MissingPathVariableException exception, HttpServletRequest httpRequest) {

        LOG.error("O PARAMETRO {} DO TIPO {} E OBRIGATORIO E NAO FOI INFORMADO", exception.getParameter().getParameterName(), exception.getParameter().getParameterType());
        exception.printStackTrace();
        String messageDev = exception.getMessage();

        ApiError<String> errors = new ApiError<>(LocalDateTime.now(), messageDev, "PARAMETROS OBRIGATORIOS NAO INFORMADOS", httpRequest.getRequestURI(),
                List.of(new ApiFieldError(exception.getParameter().getParameterName().toString(), exception.getParameter().getParameterType().toString())));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler({ FeignException.Forbidden.class })
    public ResponseEntity<?> handleForbidden(FeignException.Forbidden exception, HttpServletRequest httpRequest) {

        String messageDev = exception.getMessage();

        List<ApiError<String>> errors = List.of(new ApiError<>(LocalDateTime.now(), messageDev, "CLIENTE NAO TEM ACESSO AO MICROSERVICE, ATUALIZE SUAS CREDENCIAIS",
                httpRequest.getRequestURI(), null));

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errors);
    }

}
