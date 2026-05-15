package com.example.karunada_kala.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object SplashRoute

@Serializable
object AuthRoute

@Serializable
object HomeRoute

@Serializable
object MapRoute

@Serializable
object EventsRoute

@Serializable
data class ProfileRoute(val artisanId: String)

@Serializable
data class ArtFormRoute(val artFormId: String)

@Serializable
object FeedRoute

@Serializable
object QAForumRoute

@Serializable
object PassportRoute

@Serializable
object ListingsRoute

@Serializable
data class BookingsRoute(val showOnlyMyBookings: Boolean = false)

@Serializable
data class PostDetailRoute(val postId: String)

@Serializable
object UserProfileRoute

@Serializable
data class ReviewsRoute(val artisanId: String, val artisanName: String)
