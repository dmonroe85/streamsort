package streamsort.last

case class MemoryLastValue[T](var last: Option[T] = None) extends LastValue[T] {

  def get: T = { last.get }

  def put(v: T): Unit = { last = Some(v)}

  def nonEmpty: Boolean = { last.nonEmpty }

}
