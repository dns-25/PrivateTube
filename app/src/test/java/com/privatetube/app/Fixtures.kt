package com.privatetube.app

object Fixtures {
    fun read(name: String): String =
        checkNotNull(Fixtures::class.java.classLoader?.getResource("fixtures/$name")) { "missing fixture $name" }
            .readText(Charsets.UTF_8)
}
