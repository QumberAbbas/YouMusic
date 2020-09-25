# YouMusic
YouTube Music Application [Using Youtube Data API]

## Technology
- Kotlin
- Kotlin Coroutines
- MVVM Architecture
- Android Architecture componenets
    - ViewModel
    - Live Data
    - DataBinding
- Room
- Dagger HILT
- Glide (Image Loading Library)
- Gson
- Lottie (Animation Loader)
- Mockito (For Unit Testing)

## Screens

### 1. Authentication screen
User can login usign the Google SignIn and proceed to the Main Screen.

#### Authentication
App uses the Google authentication to authenticate the user.

### 2. Playlist Activity
This screen shows the list of playlists from the currently logged in user's channel.

YouTube data API expects at least a single channel for the current user. If the user does not have a channel, it throws 404 error. It's manadatory that user should have created a channel in his account to use the app.


### 3. Playlist Details Activity
User can select any of the playlist been retreieved and on selecting that user can play any playlist video.

### 4. Search Activity
From the play list screen, user can navigate to the search screen. With a search term, the search can be performed

### 5. Video Player Activity
Plays the videos selected from search & play list videos screen. It uses the `[android-youtube-player](https://github.com/PierfrancescoSoffritti/android-youtube-player)` which uses the `Youtube's iFrame player` internally.

## Local Storage (Room)
App caches the playlists and its videos locally in database. Though the search feature of the app always directly communicates with the server.

## Testing
Mocktio is used for unit testing

