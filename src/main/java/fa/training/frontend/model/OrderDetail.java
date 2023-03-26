package fa.training.frontend.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.text.DecimalFormat;

@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_detail")
public class OrderDetail {
    public int id;
    public int price;
    public Order order;
    public Feedback feedback;
    public Course course;
    public String getTuitionFeeFormat() {
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        return  (formatter.format(this.price) + " VNĐ").replace(",", ".");
    }
}
