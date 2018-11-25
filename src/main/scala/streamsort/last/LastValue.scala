package streamsort.last

/**
  * Stores the last value merged; this also provides a way to initialize a merger.
  */
trait LastValue[T] {

  def get: T

  def put(v: T): Unit

  def nonEmpty: Boolean

}
