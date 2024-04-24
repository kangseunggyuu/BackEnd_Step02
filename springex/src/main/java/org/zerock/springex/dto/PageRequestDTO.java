package org.zerock.springex.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.Arrays;

/* url 요청이 들어왔을 때, 함께 넘어오는
page, size값을 저장하기 위한 클래스
* */

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    @Min(value = 1)
    @Positive
    private int page = 1;           // 페이지 번호

    @Builder.Default
    @Min(value = 10)
    @Max(value = 100)
    @Positive
    private int size = 10;          // 페이지 당 보여줄 정보의 크기

    private String link;
    
    // 해당 페이지 이전에 몇개를 건너띄어야 하는지 계산하는 메서드
    public int getSkip(){
        return (page-1) * 10;
    }

    /* /todo/list에서 /todo/read, /todo/modify등으로 이동했다가
    다시 /todo/list로 돌아올 때 현재 내 페이지로 돌아오도록 하기 위해
    이 링크정보를 붙여서 url이동을 하는 용도로 사용한다.
    * */
    public String getLink(){
        
        // 페이징 처리를 하기 위한 조건
        StringBuilder builder = new StringBuilder();
        builder.append("page=" + this.page);
        builder.append("&size=" + this.size);

        // 완료 여부 조건
        if(finished){
            builder.append("&finished=on");
        }

        // 제목/작성자 조건
        if(types != null && types.length > 0){
            for(int i=0;i<types.length;i++){
                builder.append("&type=" + types[i]);
            }
        }

        // 검색어 조건
        if(keyword != null){
            try{
                builder.append("&keyword=" + URLEncoder.encode(keyword, "UTF-8"));
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }
        }

        // 시작 시간
        if(from != null){
            builder.append("&from=" + from.toString());
        }

        // 종료 시간
        if(to != null){
            builder.append("&to=" + to.toString());
        }

        return builder.toString();
    }

//    --------------검색 조건 관련 필드------------------------------------------

    private String[] types;     // 제목(t), 작성자(w)
    private String keyword;     // 검색어
    private boolean finished;   // 완료여부
    private LocalDate from;     // 시작시간
    private LocalDate to;       // 종료시간

    public boolean checkType(String type){
        if(this.types == null || this.types.length == 0){
            return false;
        }

        // types내의 type가 1개라도 일치하면 true를 리턴
        // 1개도 일치하지 않으면 false를 리턴
        return Arrays.stream(this.types).anyMatch(type::equals);
    }
}
