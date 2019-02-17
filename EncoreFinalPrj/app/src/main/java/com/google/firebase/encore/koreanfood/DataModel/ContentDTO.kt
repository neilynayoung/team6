package com.google.firebase.encore.koreanfood.DataModel

data class ContentDTO (var review: String? = null,
                       var photoUrl: String? = null,
                       var uid: String? = null,
                       var userId: String? = null,
                       var timestamp: Long? = null,
                       var key: Int = 1)