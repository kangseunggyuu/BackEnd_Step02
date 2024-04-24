package org.zerock.b01.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

/* Controller에서 View */
@Getter
@ToString
public class PageResponseDTO<E> {

    private int page;   // 몇 페이지
    private int size;   // 한개 페이지의 크기
    private int total;  // 전체 row 갯수

    private int start;  // 시작 페이지 번호
    private int end;    // 끝 페이지 번호

    private boolean prev;   // 이전 페이지의 존재 여부
    private boolean next;   // 다음 페이지의 존재 여부

    private List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total){

        if(total <= 0){
            return;
        }

        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();

        this.total = total;
        this.dtoList = dtoList;

        this.end =   (int)(Math.ceil(this.page / 10.0 )) *  10; // 한 페이지당 10개씩

        this.start = this.end - 9;

        int last =  (int)(Math.ceil((total/(double)size)));

        this.end =  end > last ? last: end;

        this.prev = this.start > 1;

        this.next =  total > this.end * this.size;

    }
}