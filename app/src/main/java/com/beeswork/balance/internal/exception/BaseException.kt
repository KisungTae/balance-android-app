package com.beeswork.balance.internal.exception

import java.lang.RuntimeException

abstract class BaseException(val exceptionCode: Int): RuntimeException()