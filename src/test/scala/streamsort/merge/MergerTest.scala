package streamsort.merge

import org.scalatest.{FlatSpec, Matchers}
import streamsort.TestUtil
import streamsort.queue.MemoryQueue

import scala.collection.immutable.HashMap

class MergerTest extends FlatSpec with Matchers {

  behavior of "createInput"

  it should "create an input with unique, sequential integer keys" in {
    val m = Merger[Int]()

    m.inputs should equal(HashMap())

    val lq1 = new MemoryQueue[Int]
    m.createInput(lq1)
    m.inputs should equal(HashMap(1 -> lq1))

    val lq2 = new MemoryQueue[Int]
    m.createInput(lq2)
    m.inputs should equal(HashMap(1 -> lq1, 2 -> lq2))
  }


  behavior of "addValue"

  it should "add a value to the appropriate queue" in {
    val m = Merger(List(new MemoryQueue[Int]))

    m.inputs(1).peek() should equal(None)
    m.enqueue(1)(1234)

    m.inputs(1).peek() should equal(Some(1234))
  }


  behavior of "findMins"

  it should "return an empty output when not all queues have data" in {
    val m = TestUtil.build2QueueMerger[Int]()
    m.findMins() should equal(Set())
    m.enqueue(1)(10)
    m.findMins() should equal(Set())
  }

  it should "return the matching queue when looking for a specific value" in {
    val m = TestUtil.build2QueueMerger[Int]()
    m.enqueue(1)(10)
    m.findMins(10) should equal(m.getQs(List(1)))
  }

  it should "return the min-value queues" in {
    val m = TestUtil.build2QueueMerger[Int]()
    m.enqueue(1)(10)
    m.enqueue(2)(9)
    m.findMins() should equal(m.getQs(List(2)))

    val lq3 = new MemoryQueue[Int]
    m.createInput(lq3)
    m.enqueue(3)(9)
    m.findMins() should equal(m.getQs(List(2, 3)))
  }


  behavior of "merge"

  it should "return empty for no values or insufficient values queued" in {
    val m = TestUtil.build2QueueMerger[Int]()

    m.merge() should equal(List())

    m.enqueue(2)(20)
    m.merge() should equal(List())
  }

  it should "return min values only for sufficiently populated queues" in {
    val m = TestUtil.build2QueueMerger[Int]()
    m.enqueue(2)(20)
    m.enqueue(1)(19)
    m.merge() should equal(List(19))

    m.enqueue(1)(20)
    m.merge() should equal(List(20, 20))
  }

  it should "return all ordered values as long as queues are sufficiently populated" in {
    val m = TestUtil.build2QueueMerger[Int]()

    m.enqueue(1)(21)
    m.enqueue(1)(21)
    m.enqueue(2)(21)
    m.merge() should equal(List(21, 21, 21))

    m.enqueue(1)(22)
    m.enqueue(2)(23)
    m.enqueue(2)(24)
    m.enqueue(1)(22)
    m.enqueue(2)(25)
    m.enqueue(1)(23)
    m.enqueue(1)(24)

    m.merge() should equal(List(22, 22, 23, 23, 24, 24))
    // When didn't return 25 because there's nothing left in 1, so we can't return anything else yet
  }

  it should "allow sequential merge to return all available ordered values" in {
    val m = TestUtil.build2QueueMerger[Int](Some(x => x + 1))

    // Haven't processed anything, this should be empty
    m.merge() should equal(List())

    m.enqueue(1)(22)
    m.enqueue(1)(24)
    // Don't know what the first value in the second queue will be, can't make a decision yet
    m.merge() should equal(List())

    m.enqueue(2)(23)
    // Can dequeue finally
    m.merge() should equal(List(22, 23, 24))

    m.enqueue(2)(25)
    m.enqueue(2)(26)
    // Continue from the last value in memory
    m.merge() should equal(List(25, 26))
  }

}
