package com.jumao

import org.junit._
import Assert._

@Test
class AppTest {

    @Test
    def testOK() = assertTrue(true)

    println(Tuple1(1,2))
    println(List(1,2,3,4).mkString)
    println("|-12".replace("|","").toInt)
    val a = List(8,2,3,4)
    val list = a.drop(1)
    println(list)

//    @Test
//    def testKO() = assertTrue(false)

}


