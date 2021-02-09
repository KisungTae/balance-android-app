package com.beeswork.balance.internal.exception

import java.io.IOException
import java.lang.RuntimeException


class ChatIdNotFoundException: IOException()
class NoInternetConnectivityException: IOException()
class AccountIdNotFoundException: RuntimeException()
class IdentityTokenNotFoundException: RuntimeException()