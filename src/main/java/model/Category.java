package model;

import lombok.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
public class Category {
	public int id;
	public String categoryName;
	public Set<Course> courses;
}
