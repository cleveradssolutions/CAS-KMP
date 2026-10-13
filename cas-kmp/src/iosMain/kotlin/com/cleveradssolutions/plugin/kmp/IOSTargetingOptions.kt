package com.cleveradssolutions.plugin.kmp

import kotlinx.cinterop.ExperimentalForeignApi
import cocoapods.CleverAdsSolutions_Base.CAS as IosCAS

@OptIn(ExperimentalForeignApi::class)
internal class IOSTargetingOptions : CASTargetingOptions {

    override var userId: String?
        get() = IosCAS.targetingOptions().userID()
        set(value) {
            IosCAS.targetingOptions().setUserID(value)
        }

    override var gender: CASGender = CASGender.UNKNOWN
        set(value) {
            field = value
            IosCAS.targetingOptions().setGender(value.toIos())
        }

    override var age: Int
        get() = IosCAS.targetingOptions().age().toInt()
        set(value) {
            IosCAS.targetingOptions().setAge(value.toLong())
        }

    override var locationCollectionEnabled: Boolean
        get() = IosCAS.targetingOptions().locationCollectionEnabled()
        set(value) {
            IosCAS.targetingOptions().setLocationCollectionEnabled(value)
        }

    override var keywords: Set<String>? = null
        set(value) {
            field = value
            IosCAS.targetingOptions().setKeywords(value?.toList())
        }

    override var contentUrl: String?
        get() = IosCAS.targetingOptions().contentUrl()
        set(value) {
            IosCAS.targetingOptions().setContentUrl(value)
        }
}