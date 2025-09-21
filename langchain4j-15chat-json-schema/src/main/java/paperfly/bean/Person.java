package paperfly.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Person 类
 * 对应 OpenAI JsonObjectSchema
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    /** 姓名 */
    private String name;

    /** 年龄 */
    private Integer age;

    /** 身高（米） */
    private Double height;

    /** 是否已婚 */
    private Boolean married;
}
