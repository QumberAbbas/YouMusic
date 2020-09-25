package com.test.youtubeplayer.base.models

interface ListResult<T> {
    val listModels: List<T>?
    val nextPageToken: String
}