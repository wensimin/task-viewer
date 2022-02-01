import java.lang.Thread.sleep
import java.nio.charset.Charset

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
                "exit" -> active = false
                else -> println("无效指令 $it")
            }
        }
    }
    println("指令进程已停止")
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
        println("监听进程已停止")
    }.start()
}


fun findCurrentProcess(): List<ProcessRecord> {
    val taskString =
        Runtime.getRuntime().exec("tasklist").inputStream.bufferedReader(Charset.forName("GBK")).use { it.readText() }
            .trim()

    return taskString.split("\n").let {
        it.subList(3, it.size)
    }.map {
        it.split("\\s{2,}".toRegex()).let { process ->
            if (process.size != 4) {
                println("$process error")
            }
            val pidAndSessionName = process[1].split("\\s".toRegex())
            if (pidAndSessionName.size != 2) {
                println("$pidAndSessionName error")
            }
            val pid = pidAndSessionName[0]
            val sessionName = pidAndSessionName[1]
            ProcessRecord(pid, process[0], sessionName, process[2], process[3].trim())
        }
    }.toList()
}
