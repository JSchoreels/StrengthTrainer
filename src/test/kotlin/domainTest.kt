package be.jschoreels.strengthtrainer

import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import java.time.LocalDate


class VolumeTest : FunSpec({
    test("Volume should be the multiplication of series by repetitions by weight") {
        Exercice("Deadlift", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 100.0)
                .volume() shouldBe 100.0
        Exercice("Deadlift", series = 1, repetitionsPerSeries = 5, weightPerRepetitions = 100.0)
                .volume() shouldBe 500.0
        Exercice("Deadlift", series = 5, repetitionsPerSeries = 5, weightPerRepetitions = 100.0)
                .volume() shouldBe 2500.0
    }

    val workout = Workout(
            listOf(
                    Exercice("Squat", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 100.0),
                    Exercice("Press", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 10.0),
                    Exercice("Deadlift", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 1.0)
            ))

    test("Volume of a Workout should be equal to the sum of all exercices") {
        workout.volume() shouldBe 111.0
    }

    test("Volume of a TrainingSession should be equal to the workout of that session"){
        TrainingSession(workout, LocalDate.MIN).volume() shouldBe workout.volume()
    }
})