package littleprince.plan.query.dto.response;

import littleprince.common.domain.StatusType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LongDetailDTO {
    private Long taskId;
    private Long projectId;
    private Long memberId;
    private String content;
    private StatusType isChecked;
}
