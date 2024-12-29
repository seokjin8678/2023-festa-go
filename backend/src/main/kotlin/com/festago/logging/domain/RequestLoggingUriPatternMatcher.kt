package com.festago.logging.domain

interface RequestLoggingUriPatternMatcher {

    /**
     * HttpMethod에 대해 Pattern에 추가될 URI Path를 추가하는 메서드
     *
     * 어플리케이션이 실행될 때, 하나의 스레드에서 접근하는 것을 가정으로 설계했기에 Thread Safe 하지 않음.
     *
     * 따라서 다른 Bean에서 해당 클래스를 의존하여, 이 메서드를 호출하는 것에 주의할 것
     *
     * @param method 패턴에 추가할 HttpMethod
     * @param path   패턴에 추가할 URI
     * @param policy 패턴의 상세 로깅 정책
     */
    fun addPattern(method: String, path: String, policy: RequestLoggingPolicy = RequestLoggingPolicy.DEFAULT)

    /**
     * HttpMethod와 Path이 등록된 패턴에 일치하는지 검사하는 메서드
     *
     * @param method 패턴에 일치하는지 검사할 HttpMethod
     * @param path   패턴에 일치하는지 검사할 경로. 예시: "/api/v1/festival"
     * @return method에 대한 path가 등록된 패턴에 일치하면 RequestLoggingPolicy, 아니면 null
     */
    fun match(method: String, path: String): RequestLoggingPolicy?
}
