package com.kubernet.bytefollow.model

import java.math.BigInteger

data class Stats(
    val followerCount : BigInteger,
    val followingCount: BigInteger,
    val heart: BigInteger,
    val heartCount: BigInteger,
    val videoCount: BigInteger,
    val diggCount: BigInteger
)