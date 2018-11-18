package streamsort.queue

class ListQueue[T] extends DataQueue[T] {

  var data: List[T] = List()

  def peek(): Option[T] = {
    data.headOption
  }

  def dequeue(): Option[T] = {
    val v = peek()
    data = data.drop(1)
    v
  }

  def enqueue(v: T): Unit = {
    data :+= v
  }

}
