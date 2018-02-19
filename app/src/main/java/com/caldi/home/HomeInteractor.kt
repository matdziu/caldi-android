package com.caldi.home

import com.caldi.models.Event
import io.reactivex.Observable

class HomeInteractor {

    fun fetchEvents(): Observable<PartialHomeViewState> {
        return Observable.just(PartialHomeViewState.FetchingSucceeded(
                arrayListOf(Event("1", "AndroidCan 2017",
                        "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png"),
                        Event("1", "AndroidCan 2017",
                                "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png"),
                        Event("1", "AndroidCan 2017",
                                "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png"),
                        Event("1", "AndroidCan 2017",
                                "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png"),
                        Event("1", "AndroidCan 2017",
                                "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png"),
                        Event("1", "AndroidCan 2017",
                                "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png"),
                        Event("1", "AndroidCan 2017",
                                "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png"),
                        Event("1", "AndroidCan 2017",
                                "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png"),
                        Event("1", "AndroidCan 2017",
                                "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png"),
                        Event("1", "AndroidCan 2017",
                                "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png"),
                        Event("1", "AndroidCan 2017",
                                "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png"),
                        Event("1", "AndroidCan 2017",
                                "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png"),
                        Event("1", "AndroidCan 2017",
                                "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png"))))
    }
}