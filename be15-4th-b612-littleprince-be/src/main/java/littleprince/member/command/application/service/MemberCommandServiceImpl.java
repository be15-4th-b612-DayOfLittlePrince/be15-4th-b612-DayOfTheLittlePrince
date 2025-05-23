package littleprince.member.command.application.service;


import littleprince.common.exception.BusinessException;
import littleprince.item.Exception.ItemErrorCode;
import littleprince.item.command.application.service.BadgeCommandService;
import littleprince.item.command.application.service.ItemCommandService;
import littleprince.item.query.mapper.GetBadgeCommandMapper;
import littleprince.item.query.mapper.GetBadgeQueryMapper;
import littleprince.member.command.application.dto.constant.MemberLevel;
import littleprince.member.command.application.dto.request.PlanetNameRequest;
import littleprince.member.command.application.dto.request.SignupRequest;
import littleprince.member.command.application.dto.response.ExpResponse;
import littleprince.member.command.application.repository.MemberRepository;
import littleprince.member.command.domain.aggregate.Member;
import littleprince.member.command.domain.aggregate.MemberDTO;
import littleprince.member.command.mapper.ExpHistoryCommandMapper;
import littleprince.member.command.mapper.MemberCommandMapper;
import littleprince.member.exception.MemberErrorCode;
import littleprince.member.query.dto.FindMemberDTO;
import littleprince.member.query.mapper.MemberQueryMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberCommandServiceImpl implements MemberCommandService {
    private final int MAX_LEVEL = 10;

    private final MemberQueryMapper memberQueryMapper;
    private final MemberCommandMapper memberCommandMapper;
    private final PasswordEncoder passwordEncoder;
    private final GetBadgeCommandMapper getBadgeCommandMapper;
    private final ExpHistoryCommandMapper expHistoryCommandMapper;
    private final GetBadgeQueryMapper getBadgeQueryMapper;
    private final ItemCommandService itemCommandService;
    private final BadgeCommandService badgeCommandService;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void signup(SignupRequest request) {
        /* 비밀번호와 비밀번호 확인 일치 확인 */
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(MemberErrorCode.PASSWORD_MISMATCH);
        }

        /* 1. DB에서 email 중복 확인 */
        Optional<FindMemberDTO> foundMember = memberQueryMapper.findMemberByEmail(request.getEmail());

        /* 중복인 경우 에러 응답*/
        if (foundMember.isPresent()) {
            throw new BusinessException(MemberErrorCode.DUPLICATE_EMAIL_EXISTS);
        }

        String encryptPassword = passwordEncoder.encode(request.getPassword());

        MemberDTO member = MemberDTO.builder()
                .email(request.getEmail())
                .password(encryptPassword)
                .build();

        /* 2. 회원 가입 완료 */
        memberCommandMapper.insertMember(member);
        
        /* 3. 기본 아이템, 칭호 지급 */
        itemCommandService.addItem(member.getMemberId(), 0);
        badgeCommandService.addDefaultBadge(member.getMemberId());
    }

    @Override
    @Transactional
    public ExpResponse addExp(Long memberId, int amount) {
        MemberDTO member = memberQueryMapper.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        int beforeLevel = member.getLevel();
        int currentLevel = member.getLevel();
        int currentExp = member.getExp() + amount;
        boolean levelUp = false;

        expHistoryCommandMapper.insertExpHistory(memberId, amount);


        /* 2. 추가 이후에도 레벨업이 남아있다면? 남은 만큼 추가 */
        log.info("시작 전 경험치 : {}, 시작 전 레벨 : {}", member.getExp(), member.getLevel());
        log.info("currentExp: {}, 레벨업 요구 레벨: {}", member.getExp(), MemberLevel.getTotalExpByLevel(currentLevel + 1));
        while(currentLevel < MAX_LEVEL && currentExp > MemberLevel.getTotalExpByLevel(currentLevel + 1)){
            // 2.1. currentExp 획득량 만큼 감소 시켜주기!
            currentExp -= MemberLevel.getTotalExpByLevel(currentLevel + 1);
            // 2.2. currentLevel 1 올려주기
            currentLevel += 1;
            /* 2.3. 레벨업 했으면 아이템, 칭호 지급해주기!*/
            itemCommandService.addItem(memberId, currentLevel);
            badgeCommandService.addBadge(memberId, currentLevel);
        }

        if(currentLevel != beforeLevel){
            levelUp = true;
        }

        log.info("현재 레벨 : {}, 현재 경험치 : {}", currentLevel, currentExp);

        member.setLevel(currentLevel);
        member.setExp(currentExp);
        memberCommandMapper.updateLevelAndExp(member);

        return ExpResponse.builder()
                .memberId(memberId)
                .updatedExp(currentExp)
                .updatedLevel(currentLevel)
                .levelUp(levelUp)
                .build();
    }

    @Override
    @Transactional
    public ExpResponse addExp(Long memberId, int amount, Long projectId) {
        MemberDTO member = memberQueryMapper.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));
        int beforeLevel = member.getLevel();
        int currentLevel = member.getLevel();
        int currentExp = member.getExp() + amount;
        boolean levelUp = false;

        expHistoryCommandMapper.insertExpHistoryId(memberId, amount, projectId);


        /* 2. 추가 이후에도 레벨업이 남아있다면? 남은 만큼 추가 */
        log.info("시작 전 경험치 : {}, 시작 전 레벨 : {}", member.getExp(), member.getLevel());
        log.info("currentExp: {}, 레벨업 요구 레벨: {}", member.getExp(), MemberLevel.getTotalExpByLevel(currentLevel + 1));
        while(currentLevel < MAX_LEVEL && currentExp > MemberLevel.getTotalExpByLevel(currentLevel + 1)){
            // 2.1. currentExp 획득량 만큼 감소 시켜주기!
            currentExp -= MemberLevel.getTotalExpByLevel(currentLevel + 1);
            // 2.2. currentLevel 1 올려주기
            currentLevel += 1;
            /* 2.3. 레벨업 했으면 아이템, 칭호 지급해주기!*/
            itemCommandService.addItem(memberId, currentLevel);
            badgeCommandService.addBadge(memberId, currentLevel);
        }

        if(currentLevel != beforeLevel){
            levelUp = true;
        }

        log.info("현재 레벨 : {}, 현재 경험치 : {}", currentLevel, currentExp);

        member.setLevel(currentLevel);
        member.setExp(currentExp);
        memberCommandMapper.updateLevelAndExp(member);

        return ExpResponse.builder()
                .memberId(memberId)
                .updatedExp(currentExp)
                .updatedLevel(currentLevel)
                .levelUp(levelUp)
                .build();
    }

    @Transactional
    public void changePlanetName(Long memberId, PlanetNameRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ItemErrorCode.ITEM_NOT_FOUND));

        String newPlanetName = request.getPlanetName();

        if (newPlanetName == null || newPlanetName.trim().isEmpty()) {
            throw new BusinessException(MemberErrorCode.PLANET_NAME_REQUIRED);
        }

        member.changePlanetName(newPlanetName);
    }

}
