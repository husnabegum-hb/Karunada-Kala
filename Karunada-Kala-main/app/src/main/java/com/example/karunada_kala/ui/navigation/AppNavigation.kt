package com.example.karunada_kala.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.res.stringResource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.karunada_kala.ui.auth.AuthScreen
import com.example.karunada_kala.ui.auth.AuthViewModel
import com.example.karunada_kala.ui.events.EventsScreen
import com.example.karunada_kala.ui.home.HomeScreen
import com.example.karunada_kala.ui.map.MapScreen
import com.example.karunada_kala.ui.profile.ProfileScreen
import com.example.karunada_kala.ui.artform.ArtFormScreen
import com.example.karunada_kala.ui.feed.FeedScreen
import com.example.karunada_kala.ui.feed.PostDetailScreen
import com.example.karunada_kala.ui.qa.QAForumScreen
import com.example.karunada_kala.ui.passport.PassportScreen
import com.example.karunada_kala.ui.userprofile.UserProfileScreen
import com.example.karunada_kala.ui.listings.ListingsScreen
import com.example.karunada_kala.ui.reviews.ReviewsScreen

@Composable
fun AppNavigation(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val userRole by authViewModel.userRole.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val isKannada by authViewModel.isKannadaMode.collectAsState()

    val showBottomBar = currentDestination?.route?.let { route ->
        route.contains("HomeRoute") || route.contains("MapRoute") ||
        route.contains("FeedRoute") || route.contains("PassportRoute") ||
        route.contains("BookingsRoute") || route.contains("ListingsRoute") ||
        route.contains("QAForumRoute")
    } ?: false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    // Common Tab
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = stringResource(id = com.example.karunada_kala.R.string.home)) },
                        label = { Text(stringResource(id = com.example.karunada_kala.R.string.home)) },
                        selected = currentDestination?.hierarchy?.any { it.route?.contains("HomeRoute") == true } == true,
                        onClick = {
                            navController.navigate(HomeRoute) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )

                    if (userRole == "STUDIO") {
                        NavigationBarItem(
                            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = stringResource(id = com.example.karunada_kala.R.string.my_listings)) },
                            label = { Text(stringResource(id = com.example.karunada_kala.R.string.my_listings)) },
                            selected = currentDestination?.hierarchy?.any { it.route?.contains("ListingsRoute") == true } == true,
                            onClick = {
                                navController.navigate(ListingsRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Email, contentDescription = stringResource(id = com.example.karunada_kala.R.string.qa_forum)) },
                            label = { Text(stringResource(id = com.example.karunada_kala.R.string.qa_forum)) },
                            selected = currentDestination?.hierarchy?.any { it.route?.contains("QAForumRoute") == true } == true,
                            onClick = {
                                navController.navigate(QAForumRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    } else {
                        // USER Tabs
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Share, contentDescription = stringResource(id = com.example.karunada_kala.R.string.feed)) },
                            label = { Text(stringResource(id = com.example.karunada_kala.R.string.feed)) },
                            selected = currentDestination?.hierarchy?.any { it.route?.contains("FeedRoute") == true } == true,
                            onClick = {
                                navController.navigate(FeedRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.Place, contentDescription = stringResource(id = com.example.karunada_kala.R.string.map)) },
                            label = { Text(stringResource(id = com.example.karunada_kala.R.string.map)) },
                            selected = currentDestination?.hierarchy?.any { it.route?.contains("MapRoute") == true } == true,
                            onClick = {
                                navController.navigate(MapRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.LocationOn, contentDescription = stringResource(id = com.example.karunada_kala.R.string.passport)) },
                            label = { Text(stringResource(id = com.example.karunada_kala.R.string.passport)) },
                            selected = currentDestination?.hierarchy?.any { it.route?.contains("PassportRoute") == true } == true,
                            onClick = {
                                navController.navigate(PassportRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.DateRange, contentDescription = stringResource(id = com.example.karunada_kala.R.string.my_bookings)) },
                            label = { Text(stringResource(id = com.example.karunada_kala.R.string.my_bookings)) },
                            selected = currentDestination?.hierarchy?.any { it.route?.contains("BookingsRoute") == true } == true,
                            onClick = {
                                navController.navigate(BookingsRoute(showOnlyMyBookings = false)) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = SplashRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<SplashRoute> {
                com.example.karunada_kala.ui.splash.SplashScreen(
                    onNavigateToHome = {
                        navController.navigate(HomeRoute) {
                            popUpTo(SplashRoute) { inclusive = true }
                        }
                    },
                    onNavigateToAuth = {
                        navController.navigate(AuthRoute) {
                            popUpTo(SplashRoute) { inclusive = true }
                        }
                    }
                )
            }
            composable<AuthRoute> {
                AuthScreen(
                    onNavigateToHome = {
                        navController.navigate(HomeRoute) {
                            popUpTo(AuthRoute) { inclusive = true }
                        }
                    }
                )
            }
            composable<HomeRoute> {
                HomeScreen(
                    userRole = userRole,
                    currentUser = currentUser,
                    onNavigateToProfile = { artisanId ->
                        navController.navigate(ProfileRoute(artisanId))
                    },
                    onNavigateToArtForm = { artFormId ->
                        navController.navigate(ArtFormRoute(artFormId))
                    },
                    onNavigateToUserProfile = {
                        navController.navigate(UserProfileRoute)
                    },
                    onNavigateToListings = {
                        navController.navigate(ListingsRoute)
                    }
                )
            }
            composable<MapRoute> {
                MapScreen(
                    onNavigateToProfile = { artisanId ->
                        navController.navigate(ProfileRoute(artisanId))
                    }
                )
            }
            composable<ProfileRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ProfileRoute>()
                ProfileScreen(onNavigateToArtForm = { id -> navController.navigate(ArtFormRoute(id)) })
            }
            composable<ArtFormRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ArtFormRoute>()
                ArtFormScreen(onNavigateToProfile = { id -> navController.navigate(ProfileRoute(id)) })
            }
            composable<FeedRoute> {
                FeedScreen(
                    onNavigateToPostDetail = { postId ->
                        navController.navigate(PostDetailRoute(postId))
                    }
                )
            }
            composable<PostDetailRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<PostDetailRoute>()
                PostDetailScreen()
            }
            composable<QAForumRoute> {
                QAForumScreen()
            }
            composable<PassportRoute> {
                PassportScreen()
            }
            composable<ListingsRoute> {
                ListingsScreen()
            }
            composable<BookingsRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<BookingsRoute>()
                EventsScreen()
            }
            composable<UserProfileRoute> {
                UserProfileScreen(
                    onNavigateToAuth = {
                        navController.navigate(AuthRoute) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToBookings = { navController.navigate(BookingsRoute(showOnlyMyBookings = true)) },
                    onNavigateToListings = { navController.navigate(ListingsRoute) }
                )
            }
            composable<ReviewsRoute> { backStackEntry ->
                val route = backStackEntry.toRoute<ReviewsRoute>()
                ReviewsScreen(
                    artisanId = route.artisanId,
                    artisanName = route.artisanName
                )
            }
        }
    }
}
