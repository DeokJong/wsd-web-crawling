package com.wsd.web.wsd_web_crawling.applications.dto.ApplicationPost;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 구인 정보 요청을 위한 데이터 전송 객체입니다.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationPostRequest {

    /**
     * 구인 정보 ID를 나타냅니다.
     */
    @NotNull
    @Min(1)
    @Max(9999999999L)
    @Schema(description = "구인 정보 ID", example = "1")
    private Long postingId;
}
