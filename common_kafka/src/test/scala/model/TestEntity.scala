package model

class TestEntity() {
  private var count = 0L

  def this(count: Long) {
    this()
    this.count = count
  }

  def getCount: Long = count
}