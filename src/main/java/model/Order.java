package model;

import lombok.*;

import java.util.Date;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
public class Order {
    public int id;
    public Date buyDate;
    public String paymentMethod;
    public Boolean paymentStatus;
    public String coupon;
    public String paymentId;
    public User user;
    public Set<OrderDetail> orderDetails;


}
