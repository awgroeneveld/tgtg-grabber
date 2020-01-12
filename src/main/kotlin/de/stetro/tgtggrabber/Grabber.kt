package de.stetro.tgtggrabber

import de.stetro.tgtggrabber.api.IFTTTRepository
import de.stetro.tgtggrabber.api.TgtgRepository
import io.reactivex.Observable
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.TimeUnit

class Grabber : KoinComponent {

    private val tgtgRepository: TgtgRepository by inject()
    private val iftttRepository: IFTTTRepository by inject()

    fun run(username: String, password: String, iftttkey: String) {
        println("start")
        Observable.interval(0, 5, TimeUnit.MINUTES)
            .map { tgtgRepository.getFavorites(username, password) }
            .blockingSubscribe { favorites ->
                favorites.subscribe({
                    if (it.items.isNotEmpty()) {
                        if ((it.items[0].itemsAvailable > 0)) {
                            println("${it.items[0].displayName} ist verfügbar")
                        } else {
                            println("${it.items[0].displayName} ist ausverkauft")
                            iftttRepository.sendNotification(iftttkey)
                        }
                    }
                }, {
                    it.printStackTrace()
                })
            }
    }
}