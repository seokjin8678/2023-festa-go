package com.festago.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.festago.support.FestivalFixture;
import com.festago.support.MemberFixture;
import com.festago.support.MemberTicketFixture;
import com.festago.support.StageFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class MemberTicketRepositoryTest {

    @Autowired
    MemberTicketRepository memberTicketRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    StageRepository stageRepository;

    @Autowired
    FestivalRepository festivalRepository;

    @Test
    void 회원의_ID로_에매한_티켓을_모두_조회() {
        // given
        Member member1 = memberRepository.save(MemberFixture.member().build());
        Member member2 = memberRepository.save(MemberFixture.member().build());

        Festival festival = festivalRepository.save(FestivalFixture.festival().build());
        Stage stage1 = stageRepository.save(StageFixture.stage().festival(festival).build());
        Stage stage2 = stageRepository.save(StageFixture.stage().festival(festival).build());

        memberTicketRepository.save(MemberTicketFixture.memberTicket().stage(stage1).owner(member1).build());
        memberTicketRepository.save(MemberTicketFixture.memberTicket().stage(stage2).owner(member1).build());
        memberTicketRepository.save(MemberTicketFixture.memberTicket().stage(stage1).owner(member2).build());

        // when
        List<MemberTicket> memberTickets = memberTicketRepository.findAllByOwnerId(member1.getId());

        // then
        assertThat(memberTickets).hasSize(2);
    }
}
