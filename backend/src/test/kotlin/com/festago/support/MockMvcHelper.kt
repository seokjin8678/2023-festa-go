package com.festago.support

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

fun MockMvc.jsonGet(
    urlTemplate: String,
    vararg urlVariables: Any?,
    dsl: MockHttpServletRequestDsl.() -> Unit = {},
): ResultActionsDsl {
    return get(urlTemplate, *urlVariables) {
        contentType = MediaType.APPLICATION_JSON
        dsl()
    }
}

fun MockMvc.jsonPost(
    urlTemplate: String,
    vararg urlVariables: Any?,
    dsl: MockHttpServletRequestDsl.() -> Unit = {},
): ResultActionsDsl {
    return post(urlTemplate, *urlVariables) {
        contentType = MediaType.APPLICATION_JSON
        dsl()
    }
}

fun MockMvc.jsonDelete(
    urlTemplate: String,
    vararg urlVariables: Any?,
    dsl: MockHttpServletRequestDsl.() -> Unit = {},
): ResultActionsDsl {
    return delete(urlTemplate, *urlVariables) {
        contentType = MediaType.APPLICATION_JSON
        dsl()
    }
}

fun MockMvc.jsonPatch(
    urlTemplate: String,
    vararg urlVariables: Any?,
    dsl: MockHttpServletRequestDsl.() -> Unit = {},
): ResultActionsDsl {
    return patch(urlTemplate, *urlVariables) {
        contentType = MediaType.APPLICATION_JSON
        dsl()
    }
}

fun MockMvc.jsonPut(
    urlTemplate: String,
    vararg urlVariables: Any?,
    dsl: MockHttpServletRequestDsl.() -> Unit = {},
): ResultActionsDsl {
    return put(urlTemplate, *urlVariables) {
        contentType = MediaType.APPLICATION_JSON
        dsl()
    }
}

fun MockHttpServletRequestDsl.mockAuthHeader(value: String = "Bearer 1234") {
    return header(HttpHeaders.AUTHORIZATION, value)
}
