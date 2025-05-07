A simple Android app for discovering movies using TMDB API.

CURRENT FEATURES:
- Browse popular and top rated movies in both list and grid view
- View basic movie summary details
- Watch trailers (basic implementation)
- Read movie reviews
- Cached movie list when disconnected with the functionality of auto-refresh when connection is back

KNOWN LIMITATIONS:
- Some features may not work perfectly
- UI still being improved
- Performance optimizations needed

DEVELOPMENT SETUP:
1. Get API key from TMDB
2. create SECRETS.kt and put it under < app/src/main/java/com/ltu/moviedb/movievavigator/utils 	
   const val API_KEY = "your_key_here"

TECH USED:
- Kotlin
- Jetpack Compose
- Retrofit
- ExoPlayer

NOTE:
This is a work in progress. Expect bugs and missing features.

Version: 0.1-beta
