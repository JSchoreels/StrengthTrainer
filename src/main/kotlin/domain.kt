package be.jschoreels.strengthtrainer

import java.lang.IllegalArgumentException
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

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
        val datetime: LocalDateTime
) : VolumeMeasurable, Comparable<TrainingSession> {

    override fun volume() : Double = workout.volume()
    override fun equals(other: Any?): Boolean = other is TrainingSession && datetime.equals(other.datetime)
    override fun hashCode(): Int = datetime.hashCode()
    override fun compareTo(other: TrainingSession) = datetime.compareTo(other.datetime)
}

class TrainingSessionDuplicatedException : IllegalArgumentException("Training Session already exists")
class TrainingSessionNotFoundException : IllegalArgumentException("Training Session not found")
class TrainingSessionEditionFailureException(e : Throwable): IllegalArgumentException("Training Session could not be edited", e)

class TrainingLog {

    private val trainingSessions: SortedSet<TrainingSession> = sortedSetOf()

    @Throws(TrainingSessionDuplicatedException::class)
    fun add(trainingSession: TrainingSession) {
        require(trainingSessions.add(trainingSession)) {
            throw TrainingSessionDuplicatedException()
        }
    }

    @Throws(TrainingSessionNotFoundException::class)
    fun remove(trainingSession: TrainingSession) {
        require(trainingSessions.remove(trainingSession)) {
            throw TrainingSessionNotFoundException()
        }
    }

    @Throws(TrainingSessionEditionFailureException::class)
    fun edit(oldTrainingSession: TrainingSession, newTrainingSession: TrainingSession) {
        try {
            remove(oldTrainingSession)
            add(newTrainingSession)
        } catch (exception: TrainingSessionDuplicatedException) {
            add(oldTrainingSession)
            throw TrainingSessionEditionFailureException(exception)
        } catch (exception: TrainingSessionNotFoundException) {
            throw TrainingSessionEditionFailureException(exception)
        }
    }


    fun get(date : LocalDate) : List<TrainingSession> {
        return trainingSessions.filter {
            trainingSession -> trainingSession.datetime.toLocalDate().equals(date)
        }
    }

}