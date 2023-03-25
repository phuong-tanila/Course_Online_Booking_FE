package model;

import lombok.*;

import java.text.DecimalFormat;
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
    public Set<OrderDetail> orderDetails;
}
