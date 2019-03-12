package be.jschoreels.strengthtrainer

import java.time.LocalDate

interface VolumeMeasurable {
    fun volume(): Double
}


data class Exercice (
        val type: String,
        val series: Int,
        val repetitionsPerSeries: Int,
        val weightPerRepetitions: Double
) : VolumeMeasurable {

    override fun volume(): Double = series * repetitionsPerSeries * weightPerRepetitions
}

data class Workout(
        val exercises: List<Exercice>
) : VolumeMeasurable {

    override fun volume() = exercises.map(Exercice::volume).sum()
}

data class TrainingSession(
        val workout: Workout,
        val date: LocalDate
) : VolumeMeasurable {

    override fun volume() : Double = workout.volume()
}


fun main(args: Array<String>) {
    val trainingSession = TrainingSession(
            Workout(
                    listOf(
                            Exercice(type = "Squat", series = 3, repetitionsPerSeries = 5, weightPerRepetitions = 80.0),
                            Exercice("Press", 3, 5, 52.5),
                            Exercice("Deadlift", 1,  5,  120.0)
                    )
            ),
            LocalDate.of(2019, 3, 11)
    )
    println(trainingSession)
    println("Volume is ${trainingSession.volume()}")
}