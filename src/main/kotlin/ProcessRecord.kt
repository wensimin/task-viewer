import java.time.Instant

data class ProcessRecord(
    val pid: Long,
    val command: String?,
    val parent: Long?,
    val user: String?,
    val startInstant: Instant?
)
