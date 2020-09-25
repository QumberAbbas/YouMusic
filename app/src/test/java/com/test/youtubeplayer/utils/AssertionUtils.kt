package com.test.youtubeplayer.utils

import org.junit.Assert

fun assertTrue(value: Boolean?) {
    if (value == null) {
        throw AssertionError()
    }
    Assert.assertTrue(value)
}