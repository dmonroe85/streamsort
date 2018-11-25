package streamsort.queue

import org.scalatest.{FlatSpec, Matchers}

class MemoryQueueTest extends FlatSpec with Matchers {

  behavior of "enqueue"

  it should "add new values to the end of the list" in {
    val q = new MemoryQueue[Int]()

    q.enqueue(1)
    q.enqueue(0)
    q.enqueue(3)

    q.data should equal(List(1, 0, 3))
  }


  behavior of "peek"

  it should "show the next value without modifying the Queue" in {
    val q = new MemoryQueue[Int]()

    q.peek should equal(None)

    q.enqueue(1)
    q.peek should equal(Some(1))

    q.enqueue(0)
    q.peek should equal(Some(1))

    q.enqueue(3)
    q.peek should equal(Some(1))

  }


  behavior of "dequeue"

  it should "remove and return the next value from the queue" in {

    val q = new MemoryQueue[Int]()

    q.dequeue() should equal (None)
    q.data should equal (List())

    q.enqueue(1)
    q.enqueue(0)
    q.enqueue(3)

    q.data should equal(List(1, 0, 3))

    q.dequeue() should equal(Some(1))
    q.data should equal(List(0, 3))

    q.dequeue() should equal(Some(0))
    q.data should equal(List(3))

    q.dequeue() should equal(Some(3))
    q.data should equal(List())

    q.dequeue() should equal(None)
    q.data should equal(List())
  }

}
