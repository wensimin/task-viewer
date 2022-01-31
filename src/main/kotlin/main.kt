import java.lang.Thread.sleep
import kotlin.streams.toList

var active = true
var currentProcess = emptyList<ProcessRecord>()
var showRecord = false
var quickRecord = emptyList<ProcessRecord>()
fun main() {
    listenProcess()
    listenCommand()
}

fun listenCommand() {
    while (active) {
        println("请输入指令")
        readLine().let {
            when (it) {
                "show" -> showRecord = true
                "hide" -> showRecord = false
                "save" -> saveRecord()
                "new" -> logNewRecord()
                else -> println("无效指令 $it")
            }
        }
    }
}

fun logNewRecord() {
    (currentProcess - quickRecord).forEach { println("new process $it") }
}

fun saveRecord() {
    quickRecord = currentProcess
    println("saved")
}

fun listenProcess() {
    Thread {
        while (active) {
            val newProcess = findCurrentProcess()
            val exitedProcess = currentProcess - newProcess
            val startedProcess = newProcess - currentProcess
            currentProcess = newProcess
            if (showRecord) {
                println("now process count: ${currentProcess.size}")
                println("exited count ${exitedProcess.size} started count ${startedProcess.size}")
                exitedProcess.forEach { println("exit $it") }
                startedProcess.forEach { println("start $it") }
            }
            sleep(1000)
        }
    }.start()
}

fun findCurrentProcess(): List<ProcessRecord> {
    return ProcessHandle.allProcesses().map {
        ProcessRecord(
            it.pid(),
            it.info().command().orElse(null),
            it.parent().map(ProcessHandle::pid).orElse(null),
            it.info().user().orElse(null),
            it.info().startInstant().orElse(null)
        )
    }.toList()
}
