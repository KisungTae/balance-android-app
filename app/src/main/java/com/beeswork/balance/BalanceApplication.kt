package com.beeswork.balance

import android.app.Application
import android.content.Context
import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.network.BalanceService
import com.beeswork.balance.data.network.interceptor.ConnectivityInterceptor
import com.beeswork.balance.data.network.interceptor.ConnectivityInterceptorImpl
import com.beeswork.balance.data.network.rds.BalanceRDS
import com.beeswork.balance.data.network.rds.BalanceRDSImpl
import com.beeswork.balance.data.provider.PreferenceProvider
import com.beeswork.balance.data.provider.PreferenceProviderImpl
import com.beeswork.balance.data.repository.BalanceRepository
import com.beeswork.balance.data.repository.BalanceRepositoryImpl
import com.beeswork.balance.ui.chat.ChatViewModelFactory
import com.beeswork.balance.ui.clicked.ClickedViewModelFactory
import com.beeswork.balance.ui.match.MatchViewModelFactory
import com.beeswork.balance.ui.swipe.SwipeViewModelFactory
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.*


class BalanceApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@BalanceApplication))

        // Database
        bind() from singleton { BalanceDatabase(instance()) }

        // DAO
        bind() from singleton { instance<BalanceDatabase>().matchDAO() }
        bind() from singleton { instance<BalanceDatabase>().messageDAO() }
        bind() from singleton { instance<BalanceDatabase>().clickDAO() }
        bind() from singleton { instance<BalanceDatabase>().fcmTokenDAO() }
        bind() from singleton { instance<BalanceDatabase>().clickedDAO() }
        bind<BalanceRepository>() with singleton {
            BalanceRepositoryImpl(
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance()
            )


        }

        // Factory
        bind() from provider { MatchViewModelFactory(instance()) }
        bind() from factory { matchId: Int -> ChatViewModelFactory(matchId, instance()) }
        bind() from provider { SwipeViewModelFactory(instance()) }
        bind() from provider { ClickedViewModelFactory(instance()) }

        // Interceptor
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }

        // API
        bind() from singleton { BalanceService(instance()) }

        // NDS
        bind<BalanceRDS>() with singleton { BalanceRDSImpl(instance()) }

        // Provider
        bind<PreferenceProvider>() with singleton { PreferenceProviderImpl(instance()) }

        // FusedLocationProvider
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }


    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this);
    }
}


// TODO: 1. nav_graph shows no navhostfragments found...
//       2. sqlite storage limit exception handling
//       3. match viewholder override fun onClick(view: View?) handle nullable for view and matchid, try to use match instead of tag
//       4. cold boost start on AVD removes the Macbook noise what is it?
//       5. httpclient timeout setting extend the timeout seconds it is too short
//       6. put saving messages in onMessageReceived
//       7. can't find setMaxsize() in PagedList.Config.Builder()



