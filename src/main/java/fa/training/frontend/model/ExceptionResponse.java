package fa.training.frontend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class ExceptionResponse {
    public String exceptionType;
    public String messages;
}