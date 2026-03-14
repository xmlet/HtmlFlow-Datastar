package pt.isel.utils

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicReference

class EventBus<T : Any>(
    initialValue: T? = null,
) {
    private val subscribers = mutableSetOf<LinkedBlockingQueue<T>>()
    val currentValue = AtomicReference<T>(initialValue)

    fun subscribe(): LinkedBlockingQueue<T> {
        val queue = LinkedBlockingQueue<T>()
        synchronized(subscribers) {
            subscribers.add(queue)
        }
        return queue
    }

    fun unsubscribe(queue: LinkedBlockingQueue<T>) {
        synchronized(subscribers) {
            subscribers.remove(queue)
        }
    }

    fun publish(value: T) {
        synchronized(subscribers) {
            currentValue.getAndSet(value)
            subscribers.forEach { queue ->
                queue.offer(value)
            }
        }
    }
}
