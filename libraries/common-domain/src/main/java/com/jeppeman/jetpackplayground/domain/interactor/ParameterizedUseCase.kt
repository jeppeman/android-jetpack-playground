package com.jeppeman.jetpackplayground.domain.interactor

interface ParameterizedUseCase<T, TParams> {
    suspend fun execute(params: TParams): T
}