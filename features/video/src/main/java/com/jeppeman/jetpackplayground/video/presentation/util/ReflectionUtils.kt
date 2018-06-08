package com.jeppeman.jetpackplayground.video.presentation.util

import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KFunction
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.full.NoSuchPropertyException
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.jvm.isAccessible

fun Any.getFunctionRecursive(methodName: String): KFunction<*> {
    var cls: Class<*>? = this::class.java
    while (cls != null) {
        try {
            return cls.kotlin.declaredFunctions.first { function -> function.name == methodName }
        } catch (exception: Exception) {
            cls = cls.superclass
        }
    }

    throw NoSuchMethodException(methodName)
}

fun KFunction<*>.callOrThrowInner(vararg args: Any): Any? {
    return try {
        call(*args)
    } catch (exception: InvocationTargetException) {
        throw exception.targetException ?: exception.cause ?: exception
    }
}

inline fun <reified T> Any.invokeMethod(methodName: String, vararg params: Any): T {
    return getFunctionRecursive(methodName).run {
        isAccessible = true
        callOrThrowInner(*(arrayOf(this@invokeMethod, *params)))
    } as T
}

fun Any.invokeMethod(methodName: String, vararg params: Any) {
    getFunctionRecursive(methodName).apply {
        isAccessible = true
        callOrThrowInner(*(arrayOf(this@invokeMethod, *params)))
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> Any.getProperty(propertyName: String): T {
    var cls: Class<*>? = this::class.java
    while (cls != null) {
        try {
            return (cls.kotlin
                    .declaredMemberProperties
                    .first { p -> p.name == propertyName } as KProperty<T>)
                    .getter
                    .apply { isAccessible = true }
                    .call(this)
        } catch (exception: Exception) {
            cls = cls.superclass
        }
    }

    throw NoSuchPropertyException()
}

fun Any.setProperty(propertyName: String, value: Any) {
    var cls: Class<*>? = this::class.java
    while (cls != null) {
        try {
            return (cls.kotlin
                    .declaredMemberProperties
                    .first { p -> p.name == propertyName } as KMutableProperty1<*, *>)
                    .setter
                    .apply { isAccessible = true }
                    .call(this, value)
        } catch (exception: Exception) {
            cls = cls.superclass
        }
    }

    throw NoSuchPropertyException()
}