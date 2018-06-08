package com.jeppeman.jetpackplayground.domain

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Failure(val message: String? = null, val throwable: Throwable? = null) : Result<Nothing>()

    inline fun doOnFailure(crossinline block: (error: Throwable?) -> Unit): Result<T> {
        return try {
            when (this) {
                is Success -> this
                is Failure -> {
                    block(throwable)
                    this
                }
            }
        } catch (exception: Exception) {
            Failure(throwable = exception)
        }
    }

    inline fun doOnResult(crossinline block: () -> Unit): Result<T> {
        try {
            block()
        } catch (exception: Exception) {
            return Failure(throwable = exception)
        }
        return this
    }

    inline fun doOnSuccess(crossinline block: (T) -> Unit): Result<T> {
        return try {
            when (this) {
                is Success -> {
                    block(data)
                    this
                }
                is Failure -> this
            }
        } catch (exception: Exception) {
            Failure(throwable = exception)
        }
    }

    inline fun <TTo : Any> flatMap(crossinline block: (T) -> Result<TTo>): Result<TTo> {
        return try {
            when (this) {
                is Success -> block(data)
                is Failure -> this
            }
        } catch (exception: Exception) {
            Failure(throwable = exception)
        }
    }

    inline fun <TTo : Any> map(crossinline block: (T) -> TTo): Result<TTo> {
        return try {
            when (this) {
                is Success -> Success(block(data))
                is Failure -> this
            }
        } catch (exception: Exception) {
            Failure(throwable = exception)
        }
    }

    companion object {
        fun empty() = Unit.asResult()

        inline fun <R : Any> from(crossinline block: () -> R): Result<R> {
            return try {
                Success(block())
            } catch (exception: Exception) {
                Failure(throwable = exception)
            }
        }

        suspend inline fun <R : Any> fromSuspending(crossinline block: suspend () -> R): Result<R> {
            return try {
                Success(block())
            } catch (exception: Exception) {
                Failure(throwable = exception)
            }
        }

        inline fun <T1 : Any, T2 : Any, R : Any> zip(
                first: Result<T1>,
                second: Result<T2>,
                crossinline block: (T1, T2) -> R
        ): Result<R> {
            return try {
                when {
                    first is Success && second is Success -> Success(
                            block(
                                    first.data,
                                    second.data
                            )
                    )
                    first is Failure -> first
                    second is Failure -> second
                    else -> Failure()
                }
            } catch (exception: Exception) {
                Failure(throwable = exception)
            }
        }
    }
}

fun <T : Any> T.asResult() = Result.Success(this)
fun <TError : Throwable> TError.asResult() = Result.Failure(throwable = this)