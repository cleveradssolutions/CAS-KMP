package com.cleveradssolutions.plugin.kmp

import com.cleversolutions.ads.TargetingOptions
import com.cleversolutions.ads.android.CAS as AndroidCAS

internal class AndroidTargetingOptions() : CASTargetingOptions {
    override var userId: String?
        get() = AndroidCAS.targetingOptions.userID
        set(value) {
            AndroidCAS.targetingOptions.userID = value
        }

    override var gender: CASGender
        get() = when (AndroidCAS.targetingOptions.gender) {
            TargetingOptions.GENDER_MALE -> CASGender.MALE
            TargetingOptions.GENDER_FEMALE -> CASGender.FEMALE
            else -> CASGender.UNKNOWN
        }
        set(value) {
            AndroidCAS.targetingOptions.gender = value.value
        }

    override var age: Int
        get() = AndroidCAS.targetingOptions.age
        set(value) {
            AndroidCAS.targetingOptions.age = value
        }

    override var locationCollectionEnabled: Boolean
        get() = AndroidCAS.targetingOptions.locationCollectionEnabled
        set(value) {
            AndroidCAS.targetingOptions.locationCollectionEnabled = value
        }

    override var keywords: Set<String>?
        get() = AndroidCAS.targetingOptions.keywords
        set(value) {
            AndroidCAS.targetingOptions.keywords = value
        }

    override var contentUrl: String?
        get() = AndroidCAS.targetingOptions.contentUrl
        set(value) {
            AndroidCAS.targetingOptions.contentUrl = value
        }
}