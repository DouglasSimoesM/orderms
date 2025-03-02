package tech.buildrun.btg.orderms.controller.allexception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NullPointerException.class)
    private ResponseEntity<String> orderNullPointer (NullPointerException e){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Id não encontrado, atualize e tente novamente");
    }
}