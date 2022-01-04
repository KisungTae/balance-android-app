package com.beeswork.balance.data.database.converter

import androidx.room.TypeConverter
import java.nio.ByteBuffer
import java.util.*


object UUIDConverter {

//    @TypeConverter
//    @JvmStatic
//    fun toUUID(bytes: ByteArray?): UUID? {
//        return bytes?.let {
//            val byteBuffer = ByteBuffer.wrap(it)
//            return UUID(byteBuffer.long, byteBuffer.long)
//        }
//    }
//
//    @TypeConverter
//    @JvmStatic
//    fun fromUUID(uuid: UUID?): ByteArray? {
//        return uuid?.let {
//            val byteBuffer = ByteBuffer.wrap(ByteArray(16))
//            byteBuffer.putLong(it.mostSignificantBits)
//            byteBuffer.putLong(it.leastSignificantBits)
//            return byteBuffer.array()
//        }
//    }

    @TypeConverter
    @JvmStatic
    fun toUUID(uuid: String?): UUID? {
        return uuid?.let {
            UUID.fromString(it)
        }

//        return bytes?.let {
//            val byteBuffer = ByteBuffer.wrap(it)
//            return UUID(byteBuffer.long, byteBuffer.long)
//        }
    }

    @TypeConverter
    @JvmStatic
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
//        return uuid?.let {
//            val byteBuffer = ByteBuffer.wrap(ByteArray(16))
//            byteBuffer.putLong(it.mostSignificantBits)
//            byteBuffer.putLong(it.leastSignificantBits)
//            return byteBuffer.array()
//        }
    }
}