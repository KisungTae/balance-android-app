package com.beeswork.balance

import android.app.Application
import android.content.Context
import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.network.BalanceService
import com.beeswork.balance.data.network.interceptor.ConnectivityInterceptor
import com.beeswork.balance.data.network.interceptor.ConnectivityInterceptorImpl
import com.beeswork.balance.data.network.rds.BalanceRDS
import com.beeswork.balance.data.network.rds.BalanceRDSImpl
import com.beeswork.balance.internal.provider.PreferenceProvider
import com.beeswork.balance.internal.provider.PreferenceProviderImpl
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.database.repository.BalanceRepositoryImpl
import com.beeswork.balance.ui.balancegame.BalanceGameDialogViewModelFactory
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

        // Repository
        bind<BalanceRepository>() with singleton {
            BalanceRepositoryImpl(
                instance(),
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
        bind() from factory { chatId: Long -> ChatViewModelFactory(chatId, instance()) }
        bind() from provider { SwipeViewModelFactory(instance()) }
        bind() from provider { ClickedViewModelFactory(instance()) }
        bind() from provider { BalanceGameDialogViewModelFactory(instance()) }

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
//       8. remove clicked if match
//       9. add ACCOUNT_BLOCKED_EXCEPTION and add it in when clause of network call response
//      10. change click, no answer in baalance game but post answer to servers and wati for the response, if success then
//      11. Location update should be one when current location is out of saved location by 1 km
//      12. exception class changes - DONE
//      13. when receving notification, then just call the corresponding API
//      14. finish clicked
//      15. swipe fragment should have update on number of clickeds, and chat
//      16. change the flow of balanceGame
//      17. add localization and exception Message in network call, need my own message not the exception.message
//      18. consider after click, give match result or not
//      19. chat algorithm, message came then increment unreadMessageCount in Match and if it is added to the message list, then adapter in chat fragment calls minus the unreadMessageCount of match
//      20. refresh button for matches and clicked to sync whole list using fetchedAt = 2020-01-01
//      21. check if udpatedAt of match or clicked is vlaid date before put fetchedAt to sharedprefrerence
//      22. change cardbeingfetched for fetchCards to something else
//      23. if accountBlockedException then logs him out on repository level
//      24. click the same button in the nativigation bar throws exception of lifecycler to the same observer


