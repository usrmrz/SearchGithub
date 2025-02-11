package dev.usrmrz.searchgithub.data.repository

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import dev.usrmrz.searchgithub.data.api.ApiEmptyResponse
import dev.usrmrz.searchgithub.data.api.ApiErrorResponse
import dev.usrmrz.searchgithub.data.api.ApiResponse
import dev.usrmrz.searchgithub.data.api.ApiSuccessResponse
import dev.usrmrz.searchgithub.domain.model.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

abstract class NetworkBoundResource<ResultType, RequestType> {

    fun asFlow(): Flow<Resource<ResultType>> = flow {
        // Загружаем данные из базы данных
        val dbData = loadFromDb().first()

        // Проверяем, нужно ли загружать данные из сети
        if(shouldFetch(dbData)) {
            emit(Resource.Loading(dbData))

            // Загружаем данные из сети
            when(val apiResponse = createCall().first()) {
                is ApiSuccessResponse -> {
                    // Сохраняем данные в базу данных
                    saveCallResult(processResponse(apiResponse))
                    // Загружаем обновлённые данные из базы данных
                    emit(Resource.Success(loadFromDb().first()))
                }
                is ApiEmptyResponse -> {
                    // Если ответ пустой, просто загружаем данные из базы данных
                    emit(Resource.Success(dbData))
                }
                is ApiErrorResponse -> {
                    // Обрабатываем ошибку
                    onFetchFailed()
                    emit(Resource.Error(apiResponse.errorMessage, dbData))
                }
            }
        } else {
            // Если данные не нужно загружать из сети, просто возвращаем данные из базы данных
            emit(Resource.Success(dbData))
        }
    }.catch { e ->
        // Обрабатываем исключения
        emit(Resource.Error(e.message ?: "Unknown error", null))
    }

    protected open fun onFetchFailed() {}

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>): RequestType = response.body

    @WorkerThread
    protected abstract suspend fun saveCallResult(item: RequestType)

    @MainThread
    protected abstract suspend fun shouldFetch(data: ResultType?): Boolean

    @MainThread
    protected abstract fun loadFromDb(): Flow<ResultType>

    @MainThread
    protected abstract suspend fun createCall(): Flow<ApiResponse<RequestType>>
}
