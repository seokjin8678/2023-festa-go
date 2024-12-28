package com.festago.support.fixture;

import com.festago.admin.domain.Admin;

public class AdminFixture extends BaseFixture {

    private Long memberId = 0L;
    private String username;
    private String password = "123456";

    private AdminFixture() {
    }

    public static AdminFixture builder() {
        return new AdminFixture();
    }

    public AdminFixture memberId(Long memberId) {
        this.memberId = memberId;
        return this;
    }

    public AdminFixture username(String username) {
        this.username = username;
        return this;
    }

    public AdminFixture password(String password) {
        this.password = password;
        return this;
    }

    public Admin build() {
        return new Admin(
            null,
            memberId,
            uniqueValue("admin", username),
            password
        );
    }
}
