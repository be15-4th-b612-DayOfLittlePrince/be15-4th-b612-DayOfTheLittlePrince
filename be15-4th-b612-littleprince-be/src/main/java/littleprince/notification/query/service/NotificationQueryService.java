package littleprince.notification.query.service;

import littleprince.notification.query.dto.NotificationDto;
import littleprince.notification.query.dto.response.NotificationListResponse;
import littleprince.notification.query.dto.response.NotificationResponse;
import littleprince.notification.query.mapper.NotificationQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationQueryService {

    private final NotificationQueryMapper notificationQueryMapper;

    public NotificationListResponse getByMemberId(Long memberId, int page, int size) {
        int offset = page * size;

        List<NotificationDto> dtos =
                notificationQueryMapper.findByMemberIdWithPaging(memberId, offset, size);

        List<NotificationResponse> notifications = dtos.stream()
                .map(dto -> new NotificationResponse(dto.getNotificationId(),dto.getTemplate(), dto.getCreatedAt(), dto.getIsRead(), dto.getCategoryId()))
                .collect(Collectors.toList());

        long unreadCount = notificationQueryMapper.countUnreadByMemberId(memberId);
        long totalCount = notificationQueryMapper.countByMemberId(memberId);

        return new NotificationListResponse(notifications, unreadCount, totalCount);
    }
}
