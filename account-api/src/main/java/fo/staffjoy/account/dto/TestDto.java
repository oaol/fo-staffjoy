package fo.staffjoy.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @AllArgsConstructor @Builder 需要现时存在
 * 
 * @author bryce
 * @Date Aug 11, 2019
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDto {

    private String hello;
    public static void main(String[] args) {
        TestDto testDto = TestDto.builder().hello("11").build();
        System.out.println(testDto);
    }

}
