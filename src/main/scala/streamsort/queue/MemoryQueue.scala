package streamsort.queue

import scala.collection.mutable
import scala.util.Try

class MemoryQueue[T] extends DataQueue[T] {

  var data: mutable.Queue[T] = mutable.Queue[T]()

  def peek(): Option[T] = {
    data.headOption
  }

  def dequeue(): Option[T] = {
    Try(data.dequeue).toOption
  }

  def enqueue(v: T): Unit = {
    data += v
  }

}
