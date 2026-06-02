package com.example.birthdayservicecall.data

import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class AgeResponse(
    val age: Int,
    @param:Json(name = "ageextended") val ageExtended: AgeExtendedResponse
)

data class AgeExtendedResponse(
    val years: Int,
    val months: Int,
    val days: Int
)

data class NamedayResponse(
    val success: Boolean = false,
    val message: String = "",
    val data: Map<String, String> = emptyMap()
)

data class ChuckNorrisJokeResponse(
    val categories: List<String> = emptyList(),
    @param:Json(name = "created_at") val createdAt: String = "",
    @param:Json(name = "icon_url") val iconUrl: String = "",
    val id: String = "",
    @param:Json(name = "updated_at") val updatedAt: String = "",
    val url: String = "",
    val value: String = ""
)

data class DayOffStatus(
    val code: Int,
    val description: String,
    val isDayOff: Boolean?
)

data class BirthdayLookupResult(
    val age: AgeResponse,
    val nameday: NamedayResponse,
    val dayOff: DayOffStatus,
    val joke: ChuckNorrisJokeResponse
)

class BirthdayRepository(
    private val digiDatesApi: DigiDatesApi = BirthdayNetwork.digiDatesApi,
    private val namedayApi: NamedayApi = BirthdayNetwork.namedayApi,
    private val isDayOffApi: IsDayOffApi = BirthdayNetwork.isDayOffApi,
    private val chuckNorrisApi: ChuckNorrisApi = BirthdayNetwork.chuckNorrisApi
) {
    suspend fun lookup(date: String, day: Int, month: Int): BirthdayLookupResult = coroutineScope {
        val age = async { digiDatesApi.getAge(date) }
        val nameday = async { namedayApi.getNameday(day.twoDigits(), month.twoDigits()) }
        val dayOff = async { isDayOffApi.getDayOff(date, countryCode = "ru").toDayOffStatus() }
        val joke = async { chuckNorrisApi.getRandomJoke() }

        BirthdayLookupResult(
            age = age.await(),
            nameday = nameday.await(),
            dayOff = dayOff.await(),
            joke = joke.await()
        )
    }
}

interface DigiDatesApi {
    @GET("api/v1/age/{date}")
    suspend fun getAge(@Path("date") date: String): AgeResponse
}

interface NamedayApi {
    @GET("api/V2/date")
    suspend fun getNameday(
        @Query("day") day: String,
        @Query("month") month: String
    ): NamedayResponse
}

interface IsDayOffApi {
    @GET("{date}")
    suspend fun getDayOff(
        @Path("date") date: String,
        @Query("cc") countryCode: String
    ): Response<ResponseBody>
}

interface ChuckNorrisApi {
    @GET("jokes/random")
    suspend fun getRandomJoke(): ChuckNorrisJokeResponse
}

private object BirthdayNetwork {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val digiDatesApi: DigiDatesApi = retrofit("https://digidates.de/")
        .create(DigiDatesApi::class.java)

    val namedayApi: NamedayApi = retrofit("https://nameday.abalin.net/")
        .create(NamedayApi::class.java)

    val isDayOffApi: IsDayOffApi = retrofit("https://isdayoff.ru/")
        .create(IsDayOffApi::class.java)

    val chuckNorrisApi: ChuckNorrisApi = retrofit("https://api.chucknorris.io/")
        .create(ChuckNorrisApi::class.java)

    private fun retrofit(baseUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
}

private fun Response<ResponseBody>.toDayOffStatus(): DayOffStatus {
    val rawCode = body()?.string() ?: errorBody()?.string()
    val code = rawCode?.trim()?.toIntOrNull() ?: code()

    return DayOffStatus(
        code = code,
        description = when (code) {
            0 -> "Working day"
            1 -> "Day off"
            2 -> "Shortened working day"
            4 -> "Working day by special decree"
            8 -> "Public holiday"
            100 -> "Date or country code error"
            101 -> "Data not found"
            199 -> "Service error"
            else -> "Unexpected response"
        },
        isDayOff = when (code) {
            1, 8 -> true
            0, 2, 4 -> false
            else -> null
        }
    )
}

private fun Int.twoDigits(): String = toString().padStart(2, '0')
