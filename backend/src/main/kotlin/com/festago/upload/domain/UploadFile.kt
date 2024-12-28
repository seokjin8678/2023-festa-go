package com.festago.upload.domain

import com.festago.common.util.UuidCreator
import com.festago.common.util.Validator.notNegative
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import java.net.URI
import java.time.LocalDateTime
import java.util.UUID
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.domain.Persistable
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.util.MimeType

@Entity
@EntityListeners(AuditingEntityListener::class)
class UploadFile(
    size: Long,
    location: URI,
    extension: FileExtension,
    createdAt: LocalDateTime,
) : Persistable<UUID> {

    @Id
    private val id: UUID = UuidCreator.create()

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar")
    var status: UploadStatus = UploadStatus.UPLOADED
        protected set

    var size: Long = size
        protected set

    @Convert(converter = URIStringConverter::class)
    var location: URI = location
        protected set

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar")
    var extension: FileExtension = extension
        protected set

    var ownerId: Long? = null
        protected set

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar")
    var ownerType: FileOwnerType? = null
        protected set

    var createdAt: LocalDateTime = createdAt
        protected set

    @LastModifiedDate
    var updatedAt: LocalDateTime? = null
        protected set

    /**
     * UploadFile을 생성한다. <br></br> UploadFile이 생성됐을 때, 파일은 어딘가에 업로드된 상태로 간주한다. <br></br> 따라서 기본 상태는 UPLOADED 이다. <br></br> 파일의 주인의
     * 생성 시점보다 파일이 더 먼저 생성될 수 있기에, ownerType, ownerId는 null이 될 수 있다. <br></br>
     */
    init {
        notNegative(size, "size")
    }

    /**
     * UPLOADED 상태의 파일을 ASSIGNED 상태로 변경한다. <br></br> ASSIGNED 상태의 파일은 주인이 정해졌지만, 해당 주인이 파일을 소유하고 있지 않는 상태이다. <br></br> 따라서
     * ASSIGNED 상태의 파일은 같은 주인이라도 여러 개가 생길 수 있다. <br></br> 이후 파일을 다른 상태로 변경하려면 renewalStatus() 또는 changeAbandoned()를 호출해야
     * 한다. <br></br>
     * 파일의 주인은 정해졌지만, 해당 파일이 공유되어 사용될 수 있으므로 UPLOADED 상태의 파일만 ASSIGNED 상태로 변경할 수 있다. <br></br>
     *
     * @param ownerId   파일 주인의 식별자
     * @param ownerType 파일 주인의 타입
     */
    fun changeAssigned(ownerId: Long, ownerType: FileOwnerType) {
        if (status == UploadStatus.UPLOADED) {
            this.status = UploadStatus.ASSIGNED
            this.ownerId = ownerId
            this.ownerType = ownerType
        }
    }

    /**
     * UPLOADED 상태의 파일을 ATTACHED 상태로 변경한다. <br></br> ATTACHED 상태의 파일은 주인이 해당 파일을 소유하고 있는 상태이다. <br></br> 따라서 ATTACHED 상태의 파일은
     * 주인이 가진 파일 개수를 초과할 수 없다. <br></br> 이후 파일을 다른 상태로 변경하려면 renewalStatus() 또는 changeAbandoned()를 호출해야 한다. <br></br>
     * 파일의 주인은 정해졌지만, 해당 파일이 공유되어 사용될 수 있으므로 UPLOADED 상태의 파일만 ATTACHED 상태로 변경할 수 있다. <br></br>
     *
     * @param ownerId   파일 주인의 식별자
     * @param ownerType 파일 주인의 타입
     */
    fun changeAttached(ownerId: Long, ownerType: FileOwnerType) {
        if (status == UploadStatus.UPLOADED) {
            this.status = UploadStatus.ATTACHED
            this.ownerId = ownerId
            this.ownerType = ownerType
        }
    }

    /**
     * 현재 파일을 ABANDONED 상태로 변경한다. <br></br> ABANDONED 상태의 파일은 더 이상 주인이 소유하고 있지 않는 것을 의미한다. <br></br> 따라서 해당 파일은 다시 다른 상태로 변경할 수
     * 없고, 삭제 대상이 된다. <br></br>
     */
    fun changeAbandoned() {
        status = UploadStatus.ABANDONED
    }

    /**
     * ASSIGNED 또는 ATTACHED 상태의 파일을 ATTACHED 또는 ABANDONED 상태로 변경한다. <br></br> 하지만 사용자가 파일 등록을 여러번 시도하여 ASSIGNED 상태의 파일이 다수
     * 생성될 수 있다. <br></br> 따라서 최종적으로 등록된 파일만 ATTACHED 상태로 변경하고 나머지는 ABANDONED 상태로 변경해야 한다. <br></br> 그렇기에 최종적으로 등록되야할 파일의 식별자
     * 목록을 받은 뒤, 식별자 목록에 현재 파일의 식별자가 있고, ASSIGNED 또는 ATTACHED 상태의 파일을 PRE_ATTACHED로 변경한다. <br></br> 그 뒤, PRE_ATTACHED 상태가 되지
     * 못한 파일은 사용자가 최종적으로 등록한 파일이 아니므로 ABANDONED 상태로 변경한다. <br></br> 그리고 PRE_ATTACHED 상태의 파일은 ATTACHED 상태로 변경한다. <br></br>
     * 해당 파일이 공유되어 사용될 수 있으므로 ownerId과 ownerType이 동일한 파일만 상태를 변경할 수 있다. <br></br>
     *
     * @param ownerId 파일 주인의 식별자
     * @param ownerType 파일 주인의 타입
     * @param ids 최종적으로 ATTACHED 상태를 가져야 할 파일의 식별자 목록
     */
    fun renewalStatus(ownerId: Long, ownerType: FileOwnerType, ids: Set<UUID>) {
        if (isNotOwner(ownerId, ownerType)) {
            return
        }
        if (ids.contains(id) && isAssignedOrAttached) {
            status = UploadStatus.PRE_ATTACHED
        }
        when (status) {
            UploadStatus.PRE_ATTACHED -> status = UploadStatus.ATTACHED
            UploadStatus.ASSIGNED, UploadStatus.ATTACHED -> status = UploadStatus.ABANDONED
            else -> {}
        }
    }

    private fun isNotOwner(ownerId: Long, ownerType: FileOwnerType): Boolean {
        return this.ownerId != ownerId || this.ownerType != ownerType
    }

    private val isAssignedOrAttached: Boolean
        get() = status == UploadStatus.ASSIGNED || status == UploadStatus.ATTACHED

    val mimeType: MimeType
        get() = extension.mimeType

    val uploadUri: URI
        get() = location.resolve("/$name")

    val name: String
        get() = "${createdAt.toLocalDate()}/${id}${extension.value}"

    override fun getId(): UUID = id

    override fun isNew(): Boolean = updatedAt == null
}
