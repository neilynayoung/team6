package com.google.firebase.encore.koreanfood

class KoreanFoodReview {

    // 회원 아이디, 후기 글, 사진 url
    var name: String? = null
    var reviewText: String? = null
    var photoUrl: String? = null

    constructor() {}

    constructor(name: String?, reviewText: String, photoUrl: String?){
        this.reviewText = reviewText
        this.name = name
        this.photoUrl = photoUrl
    }




}