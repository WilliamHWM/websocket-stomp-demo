package cn.monitor4all.springbootwebsocketdemo

class Test {
    fun main() {
        val connectMessage = """
        CONNECT
        heart-beat:5000,5000
        accept-version:1.2
        host:127.0.0.1
    """.trimIndent()

        val subscription = """
        SUBSCRIBE
        id:0
        destination:/u/topic
        ack:client
    """.trimIndent()

        val send = """
        SEND
        destination:/app/chat.sendMessage
        content-type:application/json
 
        {"sender":"asdas","type":"JOIN"}
    """.trimIndent()

        fun String.generateWithBody(): String {
            val builder = StringBuilder()
            builder.append(this)
            builder.append('\u0000')
            return builder.toString().toByteArray().joinToString(separator = "") { String.format("%02x", it) }
        }

        fun String.generate(): String {
            val builder = StringBuilder()
            builder.append(this)
            builder.append('\n')
            builder.append('\n')
            builder.append('\u0000')
            return builder.toString().toByteArray().joinToString(separator = "") { String.format("%02x", it) }
        }

        println(connectMessage.generate())
        println(subscription.generate())
        println(send.generateWithBody())
    }

}