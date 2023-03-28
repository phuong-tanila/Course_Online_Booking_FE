/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fa.training.frontend.helpers.jwt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;

/**
 *
 * @author 15tha
 */
public class JwtClaims{
    public String sub;
    public String fullname;
    public String avatar;
    public String role;
    public Date exp;
    public Date iat;
}