package streamsort.merge

import org.scalatest.{FlatSpec, Matchers}
import streamsort.queue.MemoryQueue

import scala.collection.immutable.HashMap

class MergerTest extends FlatSpec with Matchers {

  behavior of "MergerTest"

  it should "createInput with unique, sequential integer keys" in {
    val m = Merger[Int]()

    m.inputs should equal(HashMap())

    val lq1 = new MemoryQueue[Int]
    m.createInput(lq1)
    m.inputs should equal(HashMap(1 -> lq1))

    val lq2 = new MemoryQueue[Int]
    m.createInput(lq2)
    m.inputs should equal(HashMap(1 -> lq1, 2 -> lq2))
  }

  it should "addValue to the appropriate queue" in {
    val m = Merger(List(new MemoryQueue[Int]))

    m.inputs(1).peek() should equal(None)
    m.enqueue(1)(1234)

    m.inputs(1).peek() should equal(Some(1234))
  }

  def build2QueueMerger(): Merger[Int] = {
    val lq1 = new MemoryQueue[Int]
    val lq2 = new MemoryQueue[Int]
    Merger(List(lq1, lq2))
  }

  it should "findMins returning an empty output when not all queues have data" in {
    val m = build2QueueMerger()
    m.findMins() should equal(Set())
    m.enqueue(1)(10)
    m.findMins() should equal(Set())
  }

  it should "findMins returning the matching queue when looking for a specific value" in {
    val m = build2QueueMerger()
    m.enqueue(1)(10)
    m.findMins(10) should equal(m.getQs(List(1)))
  }

  it should "findMins returning the min-value queues" in {
    val m = build2QueueMerger()
    m.enqueue(1)(10)
    m.enqueue(2)(9)
    m.findMins() should equal(m.getQs(List(2)))

    val lq3 = new MemoryQueue[Int]
    m.createInput(lq3)
    m.enqueue(3)(9)
    m.findMins() should equal(m.getQs(List(2, 3)))
  }

  it should "merge returning empty for no values or insufficient values queued" in {
    val m = build2QueueMerger()

    m.merge() should equal(List())

    m.enqueue(2)(20)
    m.merge() should equal(List())
  }

  it should "merge returning min values for sufficiently populated queues" in {
    val m = build2QueueMerger()
    m.enqueue(2)(20)
    m.enqueue(1)(19)
    m.merge() should equal(List(19))

    m.enqueue(1)(20)
    m.merge() should equal(List(20, 20))
  }

  it should "merge returning ordered values as long as queues are sufficiently populated" in {
    val m = build2QueueMerger()

    m.enqueue(1)(21)
    m.enqueue(1)(21)
    m.enqueue(2)(21)
    m.merge() should equal(List(21, 21, 21))

    m.enqueue(1)(22)
    m.enqueue(1)(22)
    m.enqueue(2)(23)
    m.enqueue(1)(23)
    m.enqueue(1)(24)
    m.enqueue(2)(25)
    m.merge() should equal(List(22, 22, 23, 23, 24))
  }

}
