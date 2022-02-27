package com.beeswork.balance.data.database.common

class QueryResult<T>(
    val data: T?,
    val queryType: QueryType
) {

    fun isInsert(): Boolean {
        return this.queryType == QueryType.INSERT
    }

    fun isUpdate(): Boolean {
        return this.queryType == QueryType.UPDATE
    }

    fun isNone(): Boolean {
        return this.queryType == QueryType.NONE
    }

    companion object {
        fun<T> insert(data: T): QueryResult<T> {
            return QueryResult(data, QueryType.INSERT)
        }

        fun<T> update(data: T): QueryResult<T> {
            return QueryResult(data, QueryType.UPDATE)
        }

        fun<T> none(): QueryResult<T> {
            return QueryResult(null, QueryType.NONE)
        }
    }


    enum class QueryType {
        INSERT,
        UPDATE,
        NONE
    }
}