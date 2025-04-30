package com.ltu.moviedb.movienavigator.database

import com.ltu.moviedb.movienavigator.model.Movie
import com.ltu.moviedb.movienavigator.utils.Genre

class Movies {
    fun getMovies(): List<Movie>{
        return listOf(
            Movie(
                1,
                "A Minecraft Movie",
                "/yFHHfHcUgGAxziP1C3lLt0q2T4s.jpg",
                "/is9bmV6uYXu7LjZGJczxrjJDlv8.jpg",
                "2025-03-31",
                "By day, they're invisible—valets, hostesses, and bartenders at a luxury hotel. By night, they're the Carjackers, a crew of skilled drivers who track and rob wealthy clients on the road. As they plan their ultimate heist, the hotel director hires a ruthless hitman, to stop them at all costs. With danger closing in, can Nora, Zoe, Steve, and Prestance pull off their biggest score yet?",
                //Genre.getGenreNames(listOf(10751, 35, 12, 14)),
                //"https://www.themoviedb.org/movie/950387-a-minecraft-movie",
                //"tt3566834"
            ),
            Movie(
                2,
                "Captain America: Brave New World",
                "/pzIddUEMWhWzfvLI3TwxUG2wGoi.jpg",
                "/gsQJOfeW45KLiQeEIsom94QPQwb.jpg",
                "2025-02-12",
                "When a group of radical activists take over an energy company's annual gala, seizing 300 hostages, an ex-soldier turned window cleaner suspended 50 storeys up on the outside of the building must save those trapped inside, including her younger brother.",
                //Genre.getGenreNames(listOf(28,53,878)),
                //"https://www.themoviedb.org/movie/822119-captain-america-brave-new-world",
                //"tt14513804"
            ),
            Movie(
                3,
                "G20",
                "/tSee9gbGLfqwvjoWoCQgRZ4Sfky.jpg",
                "/sNx1A3822kEbqeUxvo5A08o4N7o.jpg",
                "2025-04-09",
                "After the G20 Summit is overtaken by terrorists, President Danielle Sutton must bring all her statecraft and military experience to defend her family and her fellow leaders.",
                //Genre.getGenreNames(listOf(28,9648,18)),
                //"https://www.themoviedb.org/movie/1045938-g20",
                //"tt23476986"

            ),
            Movie(
                4,
                "Novocaine",
                "/xmMHGz9dVRaMY6rRAlEX4W0Wdhm.jpg",
                "/zksO4lVnRKRoaSYzh2EDn2Z3Pel.jpg",
                "2025-03-12",
                "When the girl of his dreams is kidnapped, everyman Nate turns his inability to feel pain into an unexpected strength in his fight to get her back.",
                //Genre.getGenreNames(listOf(28,35,53)),
                //"https://www.themoviedb.org/movie/1195506-novocaine",
                //"tt29603959"
            ),
            Movie(
                5,
                "Gunslingers",
                "/O7REXWPANWXvX2jhQydHjAq2DV.jpg",
                "/ce3prrjh9ZehEl5JinNqr4jIeaB.jpg",
                "2025-04-11",
                "When the most wanted man in America surfaces in a small Kentucky town, his violent history -- and a blood-thirsty mob seeking vengeance and a king’s ransom -- soon follow. As brothers face off against one another and bullets tear the town to shreds, this lightning-fast gunslinger makes his enemies pay the ultimate price for their greed.",
                //Genre.getGenreNames(listOf(37,28)),
                //"https://www.themoviedb.org/movie/1293286-gunslingers",
                //"tt24850708"
            )
        )
    }
}