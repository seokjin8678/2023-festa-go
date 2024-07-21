package com.festago.common.util

import com.festago.common.exception.UnexpectedException
import com.festago.common.exception.ValidException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
object Validator {
    /**
     * 문자열이 null 또는 공백인지 검사합니다.
     *
     * @param input     검증할 문자열
     * @param fieldName 예외 메시지에 출력할 필드명
     * @throws ValidException input이 null 또는 공백이면
     */
    @JvmStatic
    fun notBlank(input: String?, fieldName: String?) {
        contract {
            returns() implies (input != null)
        }
        if (input.isNullOrBlank()) {
            throw ValidException("${fieldName}은/는 null 또는 공백이 될 수 없습니다.")
        }
    }

    /**
     * 문자열의 최대 길이를 검증합니다. null 값은 무시됩니다. 최대 길이가 0 이하이면 예외를 던집니다. 문자열의 길이가 maxLength 이하이면 예외를 던지지 않습니다.
     *
     * @param input     검증할 문자열
     * @param maxLength 검증할 문자열의 최대 길이
     * @param fieldName 예외 메시지에 출력할 필드명
     * @throws UnexpectedException 최대 길이가 0 이하이면
     * @throws ValidException      input의 길이가 maxLength 초과하면
     */
    @JvmStatic
    fun maxLength(input: CharSequence?, maxLength: Int, fieldName: String?) {
        if (maxLength <= 0) {
            throw UnexpectedException("최대 길이는 0 이하일 수 없습니다.")
        }
        // avoid NPE
        if (input == null) {
            return
        }
        if (input.length > maxLength) {
            throw ValidException("${fieldName}의 길이는 ${maxLength}글자 이하여야 합니다.")
        }
    }

    /**
     * 문자열의 최소 길이를 검증합니다. null 값은 무시됩니다. 최소 길이가 0 이하이면 예외를 던집니다. 문자열의 길이가 minLength보다 이상이면 예외를 던지지 않습니다.
     *
     * @param input     검증할 문자열
     * @param minLength 검증할 문자열의 최소 길이
     * @param fieldName 예외 메시지에 출력할 필드명
     * @throws UnexpectedException maxLength가 0 이하이면
     * @throws ValidException      input의 길이가 minLength 미만이면
     */
    @JvmStatic
    fun minLength(input: CharSequence?, minLength: Int, fieldName: String?) {
        if (minLength <= 0) {
            throw UnexpectedException("최소 길이는 0 이하일 수 없습니다.")
        }
        // avoid NPE
        if (input == null) {
            return
        }
        if (input.length < minLength) {
            throw ValidException("${fieldName}의 길이는 ${minLength}글자 이상이어야 합니다.")
        }
    }

    /**
     * 객체가 null인지 검사합니다.
     *
     * @param obj    검증할 객체
     * @param fieldName 예외 메시지에 출력할 필드명
     * @throws ValidException object가 null 이면
     */
    @JvmStatic
    fun notNull(obj: Any?, fieldName: String?) {
        contract {
            returns() implies (obj != null)
        }
        if (obj == null) {
            throw ValidException("${fieldName}은/는 null이 될 수 없습니다.")
        }
    }

    /**
     * 값의 최대 값을 검증합니다.
     *
     * @param value     검증할 값
     * @param maxValue  검증할 값의 최대 값
     * @param fieldName 예외 메시지에 출력할 필드명
     * @throws ValidException value가 maxValue 초과하면
     */
    @JvmStatic
    fun maxValue(value: Int, maxValue: Int, fieldName: String?) {
        if (value > maxValue) {
            throw ValidException("${fieldName}은/는 $maxValue 이하여야 합니다.")
        }
    }

    /**
     * 값의 최대 값을 검증합니다.
     *
     * @param value     검증할 값
     * @param maxValue  검증할 값의 최대 값
     * @param fieldName 예외 메시지에 출력할 필드명
     * @throws ValidException value가 maxValue 초과하면
     */
    fun maxValue(value: Long, maxValue: Long, fieldName: String?) {
        if (value > maxValue) {
            throw ValidException("${fieldName}은/는 $maxValue 이하여야 합니다.")
        }
    }

    /**
     * 값의 최소 값을 검증합니다.
     *
     * @param value     검증할 값
     * @param minValue  검증할 값의 최소 값
     * @param fieldName 예외 메시지에 출력할 필드명
     * @throws ValidException value가 minValue 미만이면
     */
    @JvmStatic
    fun minValue(value: Int, minValue: Int, fieldName: String?) {
        if (value < minValue) {
            throw ValidException("${fieldName}은/는 $minValue 이상이어야 합니다.")
        }
    }

    /**
     * 값의 최소 값을 검증합니다.
     *
     * @param value     검증할 값
     * @param minValue  검증할 값의 최소 값
     * @param fieldName 예외 메시지에 출력할 필드명
     * @throws ValidException value가 minValue 미만이면
     */
    fun minValue(value: Long, minValue: Long, fieldName: String?) {
        if (value < minValue) {
            throw ValidException("${fieldName}은/는 $minValue 이상이어야 합니다.")
        }
    }

    /**
     * 값이 음수인지 검증합니다.
     *
     * @param value     검증할 값
     * @param fieldName 예외 메시지에 출력할 필드명
     * @throws ValidException value가 음수이면
     */
    @JvmStatic
    fun notNegative(value: Int, fieldName: String?) {
        if (value < 0) {
            throw ValidException("${fieldName}은/는 음수가 될 수 없습니다.")
        }
    }

    /**
     * 값이 음수인지 검증합니다.
     *
     * @param value     검증할 값
     * @param fieldName 예외 메시지에 출력할 필드명
     * @throws ValidException value가 음수이면
     */
    @JvmStatic
    fun notNegative(value: Long, fieldName: String?) {
        if (value < 0) {
            throw ValidException("${fieldName}은/는 음수가 될 수 없습니다.")
        }
    }

    /**
     * 컬렉션의 최대 size를 검증합니다.
     *
     * @param collection 검증할 컬렉션
     * @param maxSize    검증할 컬렉션의 최대 원소 수
     * @param fieldName  예외 메시지에 출력할 필드명
     * @throws UnexpectedException 최대 크기가 0 이하이면
     * @throws ValidException      collecion의 size가 maxSize를 초과하면
     */
    @JvmStatic
    fun maxSize(collection: Collection<*>, maxSize: Int, fieldName: String?) {
        if (maxSize <= 0) {
            throw UnexpectedException("최대 size는 0 이하일 수 없습니다.")
        }
        if (collection.size > maxSize) {
            throw ValidException("${fieldName}의 size는 $maxSize 이하여야 합니다.")
        }
    }

    /**
     * 컬렉션의 최소 size를 검증합니다.
     *
     * @param collection 검증할 컬렉션
     * @param minSize    검증할 컬렉션의 최소 원소 수
     * @param fieldName  예외 메시지에 출력할 필드명
     * @throws UnexpectedException 최대 크기가 0 이하이면
     * @throws ValidException      collecion의 size가 minSize 미만이면
     */
    @JvmStatic
    fun minSize(collection: Collection<*>, minSize: Int, fieldName: String?) {
        if (minSize <= 0) {
            throw UnexpectedException("최대 size는 0 이하일 수 없습니다.")
        }
        if (collection.size < minSize) {
            throw ValidException("${fieldName}의 size는 $minSize 이상이어야 합니다.")
        }
    }

    /**
     * 리스트에 중복이 있는지 검사합니다. HashSet을 사용하여 중복을 검사하므로, 리스트의 원소 타입은 반드시 equals, hashCode 메서드를 구현해야 합니다.
     *
     * @param list      검증할 리스트
     * @param fieldName 예외 메시지에 출력할 필드명
     */
    @JvmStatic
    fun notDuplicate(list: List<*>?, fieldName: String?) {
        // avoid NPE
        if (list == null || list.isEmpty()) {
            return
        }
        if (HashSet(list).size != list.size) {
            throw ValidException("${fieldName}에 중복된 값이 있습니다.")
        }
    }
}
