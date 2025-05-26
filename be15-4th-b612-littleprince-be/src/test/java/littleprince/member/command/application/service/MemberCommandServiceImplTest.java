package littleprince.member.command.application.service;

import littleprince.common.exception.BusinessException;
import littleprince.item.command.application.service.BadgeCommandService;
import littleprince.item.command.application.service.ItemCommandService;
import littleprince.member.command.application.dto.request.SignupRequest;
import littleprince.member.command.domain.aggregate.MemberDTO;
import littleprince.member.command.mapper.MemberCommandMapper;
import littleprince.member.exception.MemberErrorCode;
import littleprince.member.fixture.SignupRequestFixture;
import littleprince.member.query.dto.FindMemberDTO;
import littleprince.member.query.mapper.MemberQueryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberCommandServiceImplTest {

    @Mock
    private MemberQueryMapper memberQueryMapper;
    @Mock
    private MemberCommandMapper memberCommandMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ItemCommandService itemCommandService;
    @Mock
    private BadgeCommandService badgeCommandService;
    @InjectMocks
    private MemberCommandServiceImpl memberCommandService;

    @Test
    void 예외_회원가입_비밀번호_불일치() {
        SignupRequest request = SignupRequestFixture.passwordMismatchRequest();

        assertThatThrownBy(() -> memberCommandService.signup(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(MemberErrorCode.PASSWORD_MISMATCH.getMessage());
    }

    @Test
    void 예외_회원가입_중복된_이메일() {
        SignupRequest request = SignupRequestFixture.duplicateEmailRequest();

        /* 함수가 실행되면 FindMemberDTO를 던져줌!*/
        when(memberQueryMapper.findMemberByEmail(request.getEmail()))
                .thenReturn(Optional.of(new FindMemberDTO()));

        assertThatThrownBy(() -> memberCommandService.signup(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(MemberErrorCode.DUPLICATE_EMAIL_EXISTS.getMessage());
    }

    @Test
    void 정상_회원가입() {
        // given
        SignupRequest request = SignupRequestFixture.validRequest();

        when(memberQueryMapper.findMemberByEmail(request.getEmail()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("encoded1234");

        doAnswer(invocation -> {
            MemberDTO member = invocation.getArgument(0);
            member.setMemberId(1L);  // 임의의 ID 설정
            return null;
        }).when(memberCommandMapper).insertMember(any(MemberDTO.class));

        // when
        memberCommandService.signup(request);

        // then
        verify(memberCommandMapper, times(1)).insertMember(argThat(member ->
                member.getEmail().equals(request.getEmail()) &&
                        member.getPassword().equals("encoded1234")
        ));
    }
}

