package com.festago.support

import jakarta.persistence.Id
import java.util.concurrent.atomic.AtomicLong

abstract class AbstractMemoryRepositoryKt<T>(
    protected val memory: MutableMap<Long, T> = mutableMapOf(),
    private val autoIncrement: AtomicLong = AtomicLong(),
) {

    fun save(entity: T): T {
        val fields = entity!!.javaClass.declaredFields
        for (field in fields) {
            if (field.isAnnotationPresent(Id::class.java)) {
                field.isAccessible = true
                val id = autoIncrement.incrementAndGet()
                field.set(entity, id)
                memory[id] = entity
                return entity
            }
        }
        throw IllegalArgumentException("해당 엔티티에 @Id 어노테이션이 붙은 식별자가 존재하지 않습니다.");
    }

    fun findById(id: Long): T? {
        return memory[id]
    }

    fun existsById(id: Long): Boolean {
        return memory.containsKey(id)
    }

    fun deleteById(id: Long) {
        memory.remove(id)
    }

    fun count(): Long {
        return memory.size.toLong()
    }
}