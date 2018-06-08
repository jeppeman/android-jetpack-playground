package com.jeppeman.jetpackplayground.domain.interactor


interface UseCase<T> {
    suspend fun execute(): T
}