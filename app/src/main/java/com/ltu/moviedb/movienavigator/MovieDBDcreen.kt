package com.ltu.moviedb.movienavigator

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ltu.moviedb.movienavigator.ui.screens.MovieDetailScreen
import com.ltu.moviedb.movienavigator.ui.screens.MovieGridScreen
import com.ltu.moviedb.movienavigator.ui.screens.MovieListScreen
import com.ltu.moviedb.movienavigator.ui.screens.ThirdScreen
import com.ltu.moviedb.movienavigator.viewmodel.MovieDBViewModel
import com.ltu.moviedb.movienavigator.viewmodel.SelectedMovieUiState

enum class MovieDBScreen(@StringRes val title: Int) {
    List(title = R.string.app_name),
    Grid(title = R.string.app_name),
    Detail(title = R.string.movie_detail),
    Third(title = R.string.third_screen)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDBAppBar(
    currentScreen: MovieDBScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onViewToggle: () -> Unit,
    modifier: Modifier = Modifier,
    movieDBViewModel: MovieDBViewModel
) {
    var menuExpanded by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { menuExpanded = !menuExpanded }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(R.string.menu_description)
                )
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        movieDBViewModel.getPopularMovies()
                        menuExpanded = false
                    },
                    text = { Text(stringResource(R.string.popular_movies)) }
                )
                DropdownMenuItem(
                    onClick = {
                        movieDBViewModel.getTopRatedMovies()
                        menuExpanded = false
                    },
                    text = { Text(stringResource(R.string.top_rated_movies)) }
                )
                DropdownMenuItem(
                    onClick = {
                        movieDBViewModel.getSavedMovies()
                        menuExpanded = false
                    },
                    text = { Text(stringResource(R.string.saved)) }
                )

                if (currentScreen == MovieDBScreen.List || currentScreen == MovieDBScreen.Grid) {
                    DropdownMenuItem(
                        onClick = {
                            onViewToggle()
                            menuExpanded = false
                        },
                        text = {
                            Text(
                                if (currentScreen == MovieDBScreen.List)
                                    stringResource(R.string.grid_view)
                                else
                                    stringResource(R.string.list_view)
                            )
                        }
                    )
                }
            }
        }
    )
}

@Composable
fun MovieDBApp(
    navController: NavHostController = rememberNavController()
) {
    val movieDBViewModel: MovieDBViewModel = viewModel(factory = MovieDBViewModel.Factory)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MovieDBScreen.valueOf(
        backStackEntry?.destination?.route ?: MovieDBScreen.List.name
    )
    val connectionState by movieDBViewModel.connectionState.collectAsState()

    val onViewToggle = {
        when (currentScreen) {
            MovieDBScreen.List -> navController.navigate(MovieDBScreen.Grid.name) {
                popUpTo(MovieDBScreen.List.name) { inclusive = true }
            }
            MovieDBScreen.Grid -> navController.navigate(MovieDBScreen.List.name) {
                popUpTo(MovieDBScreen.Grid.name) { inclusive = true }
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            MovieDBAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                onViewToggle = onViewToggle,
                movieDBViewModel = movieDBViewModel
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MovieDBScreen.List.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = MovieDBScreen.List.name) {
                MovieListScreen(
                    movieListUiState = movieDBViewModel.movieListUiState,
                    connectionState = connectionState,
                    onMovieListItemClicked = {
                        movieDBViewModel.setSelectedMovie(it)
                        navController.navigate(MovieDBScreen.Detail.name)
                    },
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                )
            }
            composable(route = MovieDBScreen.Grid.name) {
                MovieGridScreen(
                    movieListUiState = movieDBViewModel.movieListUiState,
                    connectionState = connectionState,
                    onMovieListItemClicked = {
                        movieDBViewModel.setSelectedMovie(it)
                        navController.navigate(MovieDBScreen.Detail.name)
                    },
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                )
            }
            composable(route = MovieDBScreen.Detail.name) {
                MovieDetailScreen(
                    selectedMovieUiState = movieDBViewModel.selectedMovieUiState,
                    modifier = Modifier,
                    onNavigateToThirdScreen = {
                        navController.navigate(MovieDBScreen.Third.name)
                    },
                    movieDBViewModel = movieDBViewModel
                )
            }
            composable(route = MovieDBScreen.Third.name) {
                val selectedMovie = (movieDBViewModel.selectedMovieUiState as? SelectedMovieUiState.Success)?.movie
                ThirdScreen(
                    movieId = selectedMovie?.id,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}