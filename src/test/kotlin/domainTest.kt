package be.jschoreels.strengthtrainer

import io.kotlintest.matchers.instanceOf
import io.kotlintest.matchers.types.shouldBeInstanceOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.FunSpec
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals

val workout = Workout(
        listOf(
                Exercice("Squat", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 100.0),
                Exercice("Press", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 10.0),
                Exercice("Deadlift", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 1.0)
        ))

val workoutEvening = Workout(
        listOf(
                Exercice("Curl", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 100.0),
                Exercice("Press", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 10.0),
                Exercice("Jogging", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 1.0)
        ))

val workoutAlternate = Workout(
        listOf(
                Exercice("ChinUp", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 100.0),
                Exercice("CleanPress", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 100.0),
                Exercice("Curl", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 100.0)
        ))

val date = LocalDate.of(2018, 1, 1)
val datetimeMorning = date.atTime(10, 30)
val trainingSessionMorning = TrainingSession(workout, datetimeMorning)
val trainingSessionMorningAlternate = TrainingSession(workoutAlternate, datetimeMorning)
val datetimeEvening = date.atTime(18, 30)
val trainingSessionEvening = TrainingSession(workoutEvening, datetimeEvening)

class VolumeTest : FunSpec({
    test("Volume should be the multiplication of series by repetitions by weight") {
        Exercice("Deadlift", series = 1, repetitionsPerSeries = 1, weightPerRepetitions = 100.0)
                .volume() shouldBe 100.0
        Exercice("Deadlift", series = 1, repetitionsPerSeries = 5, weightPerRepetitions = 100.0)
                .volume() shouldBe 500.0
        Exercice("Deadlift", series = 5, repetitionsPerSeries = 5, weightPerRepetitions = 100.0)
                .volume() shouldBe 2500.0
    }



    test("Volume of a Workout should be equal to the sum of all exercices") {
        workout.volume() shouldBe 111.0
    }

    test("Volume of a TrainingSession should be equal to the workout of that session") {
        trainingSessionMorning.volume() shouldBe workout.volume()
    }

})

class TrainingSessionTest : FunSpec({

    test("Two Sessions are the same if datetime is equals") {
        assertEquals(trainingSessionMorning, trainingSessionMorning)
        assertEquals(trainingSessionMorning, trainingSessionMorningAlternate)
        assertNotEquals(trainingSessionMorning, trainingSessionEvening)
    }

    test("Two Sessions hashcode are the same if datetime is equals") {
        assertEquals(trainingSessionMorning.hashCode(), trainingSessionMorning.hashCode())
        assertEquals(trainingSessionMorning.hashCode(), trainingSessionMorningAlternate.hashCode())
        assertNotEquals(trainingSessionMorning.hashCode(), trainingSessionEvening.hashCode())
    }

    test("One session is is less than another if datetime is") {
        assert(trainingSessionMorning.compareTo(trainingSessionMorning) == 0)
        assert(trainingSessionMorning.compareTo(trainingSessionMorningAlternate) == 0)
        assert(trainingSessionMorning < trainingSessionEvening)
        assert(trainingSessionEvening > trainingSessionMorning)
    }

})

class TrainingLogTest : FunSpec({

    test("Adding an item should increase the number of training record") {
        val trainingLog = TrainingLog()
        trainingLog.add(trainingSessionMorning)
        trainingLog.get(date).size shouldBe 1
    }

    test("Duplicate should not be added") {
        val trainingLog = TrainingLog()
        trainingLog.add(trainingSessionMorning)
        assertFailsWith(TrainingSessionDuplicatedException::class) { trainingLog.add(trainingSessionMorning) }
        trainingLog.get(date).size shouldBe 1
    }

    test("Two sessions the same days should be returned by the date") {
        val trainingLog = TrainingLog()
        trainingLog.add(trainingSessionMorning)
        trainingLog.add(trainingSessionEvening)
        trainingLog.get(date).size shouldBe 2
    }

    test("Removing one session over two") {
        val trainingLog = TrainingLog()
        trainingLog.add(trainingSessionMorning)
        trainingLog.add(trainingSessionEvening)
        trainingLog.remove(trainingSessionMorning)
        trainingLog.get(date).size shouldBe 1
    }

    test("Removing a no more existing session should throw an exception") {
        val trainingLog = TrainingLog()
        trainingLog.add(trainingSessionMorning)
        trainingLog.remove(trainingSessionMorning)
        assertFailsWith(TrainingSessionNotFoundException::class) { trainingLog.remove(trainingSessionMorning) }
        trainingLog.get(date).size shouldBe 0
    }

    test("Editing a session") {
        val trainingLog = TrainingLog()
        trainingLog.add(trainingSessionMorning)
        trainingLog.edit(trainingSessionMorning, trainingSessionMorningAlternate)
        trainingLog.get(date).size shouldBe 1
        trainingLog.get(date).get(0).workout shouldBe workoutAlternate
    }

    test("Editing a session that does not exist should fail but not alter log") {
        val trainingLog = TrainingLog()
        trainingLog.add(trainingSessionMorning)
        val exception: TrainingSessionEditionFailureException = assertFailsWith(TrainingSessionEditionFailureException::class) {
            trainingLog.edit(trainingSessionMorningAlternate.copy(datetime = datetimeEvening), trainingSessionMorning)
        }
        exception.cause.shouldBeInstanceOf<TrainingSessionNotFoundException>()
        trainingLog.get(date).size shouldBe 1
        trainingLog.get(date).get(0).workout shouldBe workout
    }

    test("Editing a session to a new one already existing should fail but not alter log") {
        val trainingLog = TrainingLog()
        trainingLog.add(trainingSessionMorning)
        trainingLog.add(trainingSessionEvening)
        val exception: TrainingSessionEditionFailureException = assertFailsWith(TrainingSessionEditionFailureException::class) {
            trainingLog.edit(trainingSessionMorning, trainingSessionEvening)
        }
        exception.cause.shouldBeInstanceOf<TrainingSessionDuplicatedException>()
        trainingLog.get(date).size shouldBe 2
        trainingLog.get(date).get(0).workout shouldBe workout
        trainingLog.get(date).get(1).workout shouldBe workoutEvening

    }
})