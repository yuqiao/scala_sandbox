package com.taobao.wangxin.playgroud

abstract class Animal(name: String) {
  def speak:Unit
}

class Dog(name: String) extends Animal(name) {  
  override def speak {
    println(name + " says Woof")
  }
}

class Cat(name: String) extends Animal(name) {
  override def speak {
    println(name + " says Meow")
  }
}

