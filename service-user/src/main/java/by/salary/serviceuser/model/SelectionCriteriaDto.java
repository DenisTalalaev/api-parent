package by.salary.serviceuser.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SelectionCriteriaDto {

    Map<String, Map<String, String[]>> filter;

    Map<String, String>  order;

    Map<String, String> pagination;
}
