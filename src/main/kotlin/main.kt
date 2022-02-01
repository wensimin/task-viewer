import java.nio.charset.Charset

var active = true
var recordProcess = mutableMapOf<String, MutableList<ProcessRecord>>()
fun main() {
    listenCommand()
}

fun listenCommand() {
    while (active) {
        println("请输入指令")
        readLine().let {
            when (it) {
                "show" -> logProcess()
                "add" -> addRecord()
                "new" -> logNewRecord()
                "exit" -> active = false
                else -> println("无效指令 $it")
            }
        }
    }
    println("指令进程已停止")
}

fun logProcess() {
    findNowProcess().forEach { (k, e) ->
        println("name $k, pid ${e.joinToString(",") { it.pid }}")
    }
}


fun logNewRecord() {
    findNowProcess().let { nowProcess ->
        (nowProcess.keys - recordProcess.keys).forEach { k ->
            println("new process name :$k pid ${nowProcess[k]!!.joinToString(",") { it.pid }}")
        }
    }

}

fun addRecord() {
    recordProcess += findNowProcess()
    println("added, all count ${recordProcess.size}")
}


fun findNowProcess(): MutableMap<String, MutableList<ProcessRecord>> {
    val taskString =
        Runtime.getRuntime().exec("tasklist").inputStream.bufferedReader(Charset.forName("GBK")).use { it.readText() }
            .trim()
    val rs = mutableMapOf<String, MutableList<ProcessRecord>>()
    taskString.split("\n").let {
        it.subList(3, it.size)
    }.forEach {
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
            val name = process[0]
            if (rs[name] == null) {
                rs[name] = mutableListOf(ProcessRecord(pid, name, sessionName, process[2], process[3].trim()))
            } else {
                rs[name]!!.add(ProcessRecord(pid, name, sessionName, process[2], process[3].trim()))
            }
        }
    }
    return rs
}
