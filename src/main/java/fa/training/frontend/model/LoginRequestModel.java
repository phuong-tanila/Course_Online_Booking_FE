package fa.training.frontend.model;

import lombok.*;

@Getter@Setter
@NoArgsConstructor@AllArgsConstructor
@ToString
public class LoginRequestModel {
    public String email;
    public String password;
}
