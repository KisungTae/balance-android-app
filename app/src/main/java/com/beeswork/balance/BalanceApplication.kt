package com.beeswork.balance

import android.app.Application
import android.content.Context
import com.beeswork.balance.data.database.BalanceDatabase
import com.beeswork.balance.data.network.api.BalanceAPI
import com.beeswork.balance.data.network.interceptor.ConnectivityInterceptor
import com.beeswork.balance.data.network.interceptor.ConnectivityInterceptorImpl
import com.beeswork.balance.data.network.rds.BalanceRDS
import com.beeswork.balance.data.network.rds.BalanceRDSImpl
import com.beeswork.balance.internal.provider.preference.PreferenceProvider
import com.beeswork.balance.internal.provider.preference.PreferenceProviderImpl
import com.beeswork.balance.data.database.repository.BalanceRepository
import com.beeswork.balance.data.database.repository.BalanceRepositoryImpl
import com.beeswork.balance.data.database.repository.chat.ChatRepository
import com.beeswork.balance.data.database.repository.chat.ChatRepositoryImpl
import com.beeswork.balance.data.database.repository.click.ClickRepository
import com.beeswork.balance.data.database.repository.click.ClickRepositoryImpl
import com.beeswork.balance.data.database.repository.match.MatchRepository
import com.beeswork.balance.data.database.repository.match.MatchRepositoryImpl
import com.beeswork.balance.data.database.repository.profile.ProfileRepository
import com.beeswork.balance.data.database.repository.profile.ProfileRepositoryImpl
import com.beeswork.balance.data.database.repository.setting.SettingRepository
import com.beeswork.balance.data.database.repository.setting.SettingRepositoryImpl
import com.beeswork.balance.data.database.repository.swipe.SwipeRepository
import com.beeswork.balance.data.database.repository.swipe.SwipeRepositoryImpl
import com.beeswork.balance.data.network.rds.chat.ChatRDS
import com.beeswork.balance.data.network.rds.chat.ChatRDSImpl
import com.beeswork.balance.data.network.rds.click.ClickRDS
import com.beeswork.balance.data.network.rds.click.ClickRDSImpl
import com.beeswork.balance.data.network.rds.match.MatchRDS
import com.beeswork.balance.data.network.rds.match.MatchRDSImpl
import com.beeswork.balance.data.network.rds.report.ReportRDS
import com.beeswork.balance.data.network.rds.report.ReportRDSImpl
import com.beeswork.balance.data.network.rds.setting.SettingRDS
import com.beeswork.balance.data.network.rds.setting.SettingRDSImpl
import com.beeswork.balance.data.network.rds.swipe.SwipeRDS
import com.beeswork.balance.data.network.rds.swipe.SwipeRDSImpl
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapper
import com.beeswork.balance.internal.mapper.chat.ChatMessageMapperImpl
import com.beeswork.balance.internal.mapper.match.MatchMapper
import com.beeswork.balance.internal.mapper.match.MatchMapperImpl
import com.beeswork.balance.data.network.service.stomp.StompClientImpl
import com.beeswork.balance.internal.mapper.click.ClickMapper
import com.beeswork.balance.internal.mapper.click.ClickMapperImpl
import com.beeswork.balance.internal.mapper.swipe.CardMapper
import com.beeswork.balance.internal.mapper.swipe.CardMapperImpl
import com.beeswork.balance.internal.mapper.swipe.SwipeFilterMapper
import com.beeswork.balance.internal.mapper.swipe.SwipeFilterMapperImpl
import com.beeswork.balance.ui.balancegame.BalanceGameDialogViewModelFactory
import com.beeswork.balance.ui.chat.ChatViewModelFactory
import com.beeswork.balance.ui.chat.ChatViewModelFactoryParameter
import com.beeswork.balance.ui.click.ClickViewModelFactory
import com.beeswork.balance.ui.mainviewpager.MainViewPagerViewModelFactory
import com.beeswork.balance.ui.match.MatchViewModelFactory
import com.beeswork.balance.ui.swipe.SwipeFilterDialogViewModelFactory
import com.beeswork.balance.ui.swipe.SwipeViewModelFactory
import com.google.android.gms.location.LocationServices
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.*
import java.util.concurrent.TimeUnit

//TODO: decide timeout duration
const val NETWORK_READ_TIMEOUT = 10L
const val NETWORK_CONNECTION_TIMEOUT = 10L
const val NETWORK_WRITE_TIMEOUT = 10L

class BalanceApplication : Application(), KodeinAware {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    override val kodein = Kodein.lazy {
        import(androidXModule(this@BalanceApplication))

        // Mapper
        bind<MatchMapper>() with singleton { MatchMapperImpl() }
        bind<ChatMessageMapper>() with singleton { ChatMessageMapperImpl() }
        bind<ClickMapper>() with singleton { ClickMapperImpl() }
        bind<SwipeFilterMapper>() with singleton { SwipeFilterMapperImpl() }
        bind<CardMapper>() with singleton { CardMapperImpl() }


        // Database
        bind() from singleton { BalanceDatabase(instance()) }

        // DAO
        bind() from singleton { instance<BalanceDatabase>().matchDAO() }
        bind() from singleton { instance<BalanceDatabase>().chatMessageDAO() }
        bind() from singleton { instance<BalanceDatabase>().swipeDAO() }
        bind() from singleton { instance<BalanceDatabase>().fcmTokenDAO() }
        bind() from singleton { instance<BalanceDatabase>().clickDAO() }
        bind() from singleton { instance<BalanceDatabase>().profileDAO() }
        bind() from singleton { instance<BalanceDatabase>().locationDAO() }
        bind() from singleton { instance<BalanceDatabase>().photoDAO() }
        bind() from singleton { instance<BalanceDatabase>().swipeFilterDAO() }

        // API
        bind() from singleton { BalanceAPI(instance()) }

        // RDS
        bind<ReportRDS>() with singleton { ReportRDSImpl(instance()) }
        bind<ChatRDS>() with singleton { ChatRDSImpl(instance()) }
        bind<MatchRDS>() with singleton { MatchRDSImpl(instance()) }
        bind<ClickRDS>() with singleton { ClickRDSImpl(instance()) }
        bind<SettingRDS>() with singleton { SettingRDSImpl(instance()) }
        bind<SwipeRDS>() with singleton { SwipeRDSImpl(instance()) }



        bind<BalanceRDS>() with singleton { BalanceRDSImpl(instance()) }


        // Repository
        bind<SwipeRepository>() with singleton { SwipeRepositoryImpl(instance(), instance(), instance(), instance()) }
        bind<ProfileRepository>() with singleton { ProfileRepositoryImpl(instance(), instance()) }
        bind<SettingRepository>() with singleton {
            SettingRepositoryImpl(
                instance(),
                instance(),
                instance(),
                instance()
            )
        }
        bind<ClickRepository>() with singleton {
            ClickRepositoryImpl(
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                applicationScope
            )
        }

        bind<ChatRepository>() with singleton {
            ChatRepositoryImpl(
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                applicationScope
            )
        }

        bind<MatchRepository>() with singleton {
            MatchRepositoryImpl(
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                instance(),
                applicationScope
            )
        }


        bind<BalanceRepository>() with singleton {
            BalanceRepositoryImpl(
                instance(),
                instance(),
                instance(),
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

        // StompClient
        bind() from singleton { StompClientImpl(applicationScope, instance(), instance()) }

        // Provider
        bind<PreferenceProvider>() with singleton { PreferenceProviderImpl(instance()) }

        // Factory
        bind() from provider { MatchViewModelFactory(instance(), instance(), instance()) }
        bind() from factory { param: ChatViewModelFactoryParameter ->
            ChatViewModelFactory(
                param,
                instance(),
                instance(),
                instance()
            )
        }
        bind() from provider { SwipeViewModelFactory(instance(), instance(), instance()) }
        bind() from provider { ClickViewModelFactory(instance(), instance()) }
        bind() from provider { BalanceGameDialogViewModelFactory(instance()) }
        bind() from provider { SwipeFilterDialogViewModelFactory(instance(), instance()) }
        bind() from provider { MainViewPagerViewModelFactory(instance(), instance(), instance()) }


        // Interceptor
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }

        bind<OkHttpClient>() with singleton {
            OkHttpClient.Builder()
                .addInterceptor(instance())
                .readTimeout(NETWORK_READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(NETWORK_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .connectTimeout(NETWORK_CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .build()
        }


        // FusedLocationProvider
        bind() from provider { LocationServices.getFusedLocationProviderClient(instance<Context>()) }


    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

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
//      25. when swipe in clicked or try to get in chat with deleted account, then you should remove clicked or match from the list
//          let the users delete the deleted account in clicked and match but when users click on them, let them know this account is deleted
//      26. users should be enabled in android app after saving their first profile, if disabled, then they should be directed to profile setup page
//      27. consider interval and fastest interval for location request
//      28. you retrieve 15 cards, but remove them all in clicked and match comparison, then it will pass zero list then, reset layout will display change the logic
//      29. remove android:usesCleartextTraffic="true" in manifest
//      30. when comparing cards against match and clicked, you should remove yourself as well
//      31. implement loading page in the card's picture, when users click left or right then it goes to next picutre, then loadin gpage when loading image from AWs
//      32. implement listClick
//      33. listClick, listClicked, listMatches, uses the fetchedAt from database not store it in sharedPreference
//      34. move preferences to database, just store information in database rather than sharedPreference
//      35. top right of swipe is settings not filter, settings include filters
//      36. changing email is in settings not in profile
//      37. photopucker recyclerview has scrolling disable it
//      38. check if cropped image stays in data/usr/,,../cache folder
//      39. when fnished cropping image, saveLocation is called why
//      40. check what is returned from puploading images to s3
//      41. change url and other logic related to s3 becuase I deploy cloudFront, acl:public-read in lambda
//      42. cropped image when there is not enough space in memory or storage, handle exception outofstorage or osmething
//      43.             val exceptionResponse =
//                Gson().fromJson(response.errorBody()?.string(), ExceptionResponse::class.java) throws error when no error body
//      44. when done crop image, savelocation fired why and prevent it
//      45. compressor and cropedimage temp files in cache
//      46. modify ratio on photopicker and cropped image to final card
//      47. delete cropped image when start app or end app
//      48. delete image on cards, when siwped, glide remove cached iamges
//      49. implement coroutine exception handler
//      50. implement global exception handler
//      51. implement account not found and blocked exception they should be in baseACtivty or somthing
//      52. click response result change to enum type
//      53. when typeing messag ein chat, let user know if the message exceeds x bytes
//      54. listener for message received via notification and then observer in chatFragment, if in chat then connect()
//      55. include message and id and other stuffs to save it to databsae in app
//      56. SocketFactory().create() already tries to connect to endpoint?
//      57. keep click list don't delete because if match is deleted, then matchedId should be kept in somewhere, then click is a place to keep
//      58. use flow in repository refer to https://shivamdhuria.medium.com/what-the-flows-build-an-android-app-using-flows-and-live-data-using-mvvm-architecture-4d3ab807b4dd
//      59. fetch matches come with last message to update recent message and message's createdAt,
//          so that updating recentmessage compares the createdAt, and flag the match to indicate there is new message
//          add lastFetchMatchAt account table in server, whenvery fetch match update the lastFetchMatchAt then query based on the lastFetchMatchAt
//          and then fetchMAtch response includes the lastFetchMatchAt field to be saved in preference or account table in android so that
//          lastFetchMAtchAt included in the next reuqest to fetch matches
//      60. fetchChatmessges with lastFetchaedMessageAt not id of Chatmessages, and lastFetchedAt - 1hour to safely retrieve all chatMessages
//          lastFetchChatMessatAT updated with the createdat of last fetchedChatMessages
//      61. fetchMatch will retreive match with messages with body empty but last message will have the body to be displayed in the match list
//          match livedata will be triggered when new item inserted in chatMessage because of foreighkey s make sure query will include unread message count
//      62. match lastChatMessageId instead of lastReadAt
//      63. encrypt identity token in sharedpreference
//      64. sharedpreference uuid convert throw errors then go back to login page and then re authorize? then authrozie should sync id and identitytoken
//      65. when deleted and reinstalled, then fetch matches, clicked, click, chatmessages (2weeks)
//      66. create table to save current insertion offsetdatetime, and then insert chatmesages with additional updatedAt column,
//          so query chatmessage.updatedat > current insertiondatetime, so then you can retrieve only the rows that have been udpated
//      67. when app deleted, then save the each match's lastreadchatmessagecreatedat or id
//      68. when sending reuqwest to list matches, lastchatmessagecreatedat = lastcyced(has id) message's createdat
//      69, match not deleted, not blocked, not unmatched, accountupdatedat > last account view at, then flash the pircture orsomething
//      70. put dummy chatmmessage start and end of chatmessage table so that I can know if it is begining or end
//      71. when click on profile in chat, udpate accountViewedAt on match and compare with accoutnUpdatedAT then flash profile picture if accountviewdat < accountupdatdAt
//      72. when exception or error from websocket, then make sending in chatmessage to error
//      73. when delete match, delete chatmessages, and match itself but in transaction, add matchedId in click so that cards that I already liked never be shown
//          --> just keep swipedId in clicked no swap around between matches and clicked
//      74. clickd, and match notification save them to database even if it retreives when clicked on each tab like match tab or licked tab, so that cards tab get notified and display numbers of liked or match or messages
//      75. when retrieve chatmessages in chatroom, update lastchatmessageid of match to the most recent message Id and previous lastchatmessageId to scroll to should be passed through action arg from match, but when save chatmessag throuh web socket then lastchatmssageid = the saved chatmessage's id
//      76. make item_tail and item_head that take up no space, so that easily scroll to the item because they are in the list without space taken up, Item_tail should have text message saying let's talk or date
//      77. when delete match, post it to server so that when app deleted and reinstalled, sync matches
//      78. consider saving fitler information to the server for when app deleted and reinstalled
//      79. if click throws exception of MatchExsits or ClickExitst then remove clicked card
//      80. clickedlist only returns valid accounts, so remoe clicked if not found in the returned clicked list
//      81. create table for fetchMatches profile to update status of fetchMatches, observer from chatViewMode, so that if fetchMatches fails, then chatviewmodel fetch ChatMessages
//      82. when ack to websocket, update lastchatmessageread
//      83. reinstall creates new preference with default values,
//      84. enter a chat and if there are chatmessages with sending status then resend it and then the server will check duplicates and then just erutnr as like it is a new mssgae
//      85. when ge SwipeAlreadyClicked exception, then call listSwipes(clicked=false), if it's siwpeAlreadtMatchedException then call listMatches
//      86. fetchClickers should remove deleted
//      87. match profilePicture make it circle
//      88. refactor notification and intent in mainActivity
//      89. put viewmodel for dialogFragment
//      90. put the photo upload logic in super class, and profileDialog, and photoFragment in profileViewPager extend the photoSuperClass to prevent code duplication
//          refer to https://www.youtube.com/watch?v=pwZQRw08mP0
//      91. when moved to mainviewpager, pressed on back button, then it recreates the mainviewpager fragment
//      92. when moved to chatFragment, back button, recreates the mainviewpager fragment and the chat ui stays in the screen
//      93. overflow menu in tool bar animation is not smooth
//      94. matchRepository has flow of newly added match, so socket pass new match to match repository and it saves the new match and then postValue on the flow of newly added match observable
//      95. chatRepository has flow of newly added chatMessage (sent fetched, received) so that we don't need chatInsertedAt in matchProfile
//      96. Offsetdatetime.now() should habve timezone.UTC
//      97. setImageResource and glide should be done in background thread
//      98. match should have flow for new match
//      99. custom emoji
//          https://github.com/hani-momanii/SuperNova-Emoji/blob/master/supernova-emoji-library/src/main/java/hani/momanii/supernova_emoji_library/emoji/Cars.java
//          https://developer.android.com/guide/topics/ui/look-and-feel/emoji-compat#using-emojicompat-with-custom-widgets
//     100. account circle icon should have circle so that I don't need to circlecrop
//     101. lstmatches error on chat and match fragments
//     102. hide keyboard when touch outside
//     103. fetchMatches() after inserting all the chatMessages, check if chatMessage.id < current latest chatMessage.id and chatMessage.status == Sending, then update the status to error to be removed or retry
//      104. think about putting fcm token table to profile as variable
//      105. display profile photo in chatmessage snackbar if any defined in chatMessageAdapter profilePhotoEndPoint
//      106. at first login, save fcm token to server to update the updatedAt so that if even multiple devices, server can use the most recent token
//      107. hide keyboard when touched outside
//      108. chat enter slide does not animate mainvewipager fragment
//      109. reportDiloag has a viewmodel to report?
//      110. if location is not synched, then put some ! mark in the setting so that users can manually trigger synchroniation