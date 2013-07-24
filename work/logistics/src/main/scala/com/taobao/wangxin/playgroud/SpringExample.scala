package com.taobao.wangxin.playgroud

import org.springframework.context.support.ClassPathXmlApplicationContext

object ScalaSpringExample
{
  def main(args: Array[String]) {
    
    // open/read the application context file
    val ctx = new ClassPathXmlApplicationContext("applicationContext.xml")

    // instantiate our dog and cat objects from the application context
    val dog = ctx.getBean("dog").asInstanceOf[Animal]
    val cat = ctx.getBean("cat").asInstanceOf[Animal]

    // let them speak
    dog.speak
    cat.speak

  }
}
