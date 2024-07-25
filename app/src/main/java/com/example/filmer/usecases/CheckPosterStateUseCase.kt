package com.example.filmer.usecases

import android.util.Log
import com.example.filmer.data.PreferenceProvider
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait
import javax.inject.Inject
import kotlin.random.Random

class CheckPosterStateUseCase @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val preferences: PreferenceProvider
) {
    operator fun invoke(): Flow<Long?> = channelFlow {
        val flowMutex = Mutex(true)
        remoteConfig.fetch().addOnCompleteListener() { task ->

            var localStartPosterId = preferences.getStartPosterID()
            var localStartPosterCount = preferences.getStartPosterCount()
            var localStartPosterStartCount = preferences.getStartPosterStartCount()
            var localStartPosterPercentage = preferences.getStartPosterPercentage()

            if (task.isSuccessful) {
                remoteConfig.activate()
                val remoteStartPosterID = remoteConfig.getLong("filmStartPoster")
                val remoteStartPosterViewCount = remoteConfig.getLong("filmStartPosterViewCount")
                val remoteStartPosterPercentage =
                    remoteConfig.getDouble("filmStartPosterViewPercentage").toFloat()

                Log.d("TAG", "load finished $remoteStartPosterID  $remoteStartPosterViewCount  $remoteStartPosterPercentage")

                if (remoteStartPosterID != localStartPosterId) { // check film ID changed
                    localStartPosterId = remoteStartPosterID
                    localStartPosterCount = 0
                    localStartPosterStartCount = remoteStartPosterViewCount
                    localStartPosterPercentage = remoteStartPosterPercentage
                    preferences.saveStartPosterData(
                        remoteStartPosterID,
                        0,
                        remoteStartPosterViewCount,
                        remoteStartPosterPercentage
                    )
                }


                if (remoteStartPosterViewCount != localStartPosterStartCount) { // check views count changed
                    localStartPosterCount = remoteStartPosterViewCount - localStartPosterStartCount
                    localStartPosterStartCount = remoteStartPosterViewCount
                    preferences.saveStartPosterData(
                        localStartPosterId,
                        localStartPosterCount,
                        localStartPosterStartCount,
                        localStartPosterPercentage
                    )
                }



                if (remoteStartPosterPercentage != localStartPosterPercentage) { // check show percentage changed
                    localStartPosterPercentage = remoteStartPosterPercentage
                    preferences.saveStartPosterData(
                        localStartPosterId,
                        localStartPosterCount,
                        localStartPosterStartCount,
                        localStartPosterPercentage
                    )
                }
            }

            if (localStartPosterStartCount < 0 || localStartPosterStartCount <= localStartPosterCount || localStartPosterPercentage <= 0f) {
                runBlocking {
                    send(null)
                }
            } else {
                if (Random.nextDouble(0.0, 1.0).toFloat() <= localStartPosterPercentage) {
                    runBlocking {
                        send(localStartPosterId)
                    }
                    preferences.saveStartPosterData(
                        localStartPosterId,
                        localStartPosterCount + 1,
                        localStartPosterStartCount,
                        localStartPosterPercentage
                    )
                }
            }
            flowMutex.unlock()
        }.addOnFailureListener {
            runBlocking {
                send(null)
            }
            flowMutex.unlock()
        }
        flowMutex.withLock {

        }
    }
}