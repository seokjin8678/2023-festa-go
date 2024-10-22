package com.festago.support.fixture;

import com.festago.stage.domain.StageQueryInfo;
import java.util.Collections;

public class StageQueryInfoFixture extends BaseFixture {

    private Long stageId;
    private String artistInfo;

    private StageQueryInfoFixture() {
    }

    public static StageQueryInfoFixture builder() {
        return new StageQueryInfoFixture();
    }

    public StageQueryInfoFixture stageId(Long stageId) {
        this.stageId = stageId;
        return this;
    }

    public StageQueryInfoFixture artistInfo(String artistInfo) {
        this.artistInfo = artistInfo;
        return this;
    }

    public StageQueryInfo build() {
        return StageQueryInfo.of(stageId, Collections.emptyList(), ignore -> artistInfo);
    }
}
