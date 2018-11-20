package streamsort

import streamsort.merge.Merger
import streamsort.queue.MemoryQueue

object TestUtil {

  def build2QueueMerger[T](nextInSequence: Option[T => T] = None)
                          (implicit ord: Ordering[T]): Merger[T] = {
    val lq1 = new MemoryQueue[T]()
    val lq2 = new MemoryQueue[T]()
    Merger(List(lq1, lq2), nextInSequence)
  }

}
