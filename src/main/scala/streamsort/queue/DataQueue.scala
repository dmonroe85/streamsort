package streamsort.queue

trait DataQueue[T] {

  def peek(): Option[T]

  def dequeue(): Option[T]

  def enqueue(v: T): Unit

}
