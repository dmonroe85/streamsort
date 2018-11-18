package streamsort.merge

import org.scalatest.{FlatSpec, Matchers}
import streamsort.queue.ListQueue

import scala.collection.immutable.HashMap

class MergerTest extends FlatSpec with Matchers {

  behavior of "MergerTest"

  it should "createInput" in {
    val m = Merger[Int]()

    m.inputs should equal(HashMap())

    val lq1 = new ListQueue[Int]
    m.createInput(lq1)
    m.inputs should equal(HashMap(1 -> lq1))

    val lq2 = new ListQueue[Int]
    m.createInput(lq2)
    m.inputs should equal(HashMap(1 -> lq1, 2 -> lq2))
  }

  it should "addValue" in {
    val m = Merger(List(new ListQueue[Int]))

    m.inputs(1).peek() should equal(None)
    m.enqueue(1)(1234)

    m.inputs(1).peek() should equal(Some(1234))
  }

  it should "findMins" in {
    val lq1 = new ListQueue[Int]
    val lq2 = new ListQueue[Int]
    val m = Merger(List(lq1, lq2))

    m.findMins() should equal(Set())

    m.enqueue(1)(10)
    m.findMins() should equal(Set())
    m.findMins(10) should equal(Set(lq1))

    m.enqueue(2)(9)
    m.findMins() should equal(Set(lq2))

    val lq3 = new ListQueue[Int]
    m.createInput(lq3)
    m.enqueue(3)(9)
    m.findMins() should equal(Set(lq2, lq3))
  }

  it should "merge" in {
    val lq1 = new ListQueue[Int]
    val lq2 = new ListQueue[Int]
    val m = Merger(List(lq1, lq2))

    m.merge() should equal(List())

    m.enqueue(2)(20)
    m.merge() should equal(List())

    m.enqueue(1)(19)
    m.merge() should equal(List(19))

    m.enqueue(1)(20)
    m.merge() should equal(List(20, 20))

    m.enqueue(1)(21)
    m.enqueue(1)(21)
    m.enqueue(2)(21)
    m.merge() should equal(List(21, 21, 21))
  }

}
