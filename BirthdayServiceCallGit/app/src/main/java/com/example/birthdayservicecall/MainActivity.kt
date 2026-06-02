package com.example.birthdayservicecall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.birthdayservicecall.data.BirthdayLookupResult
import com.example.birthdayservicecall.data.BirthdayRepository
import com.example.birthdayservicecall.ui.theme.BirthdayServiceCallTheme
import java.time.DateTimeException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BirthdayServiceCallTheme {
                BirthdayApp()
            }
        }
    }
}

@Composable
fun BirthdayApp(
    modifier: Modifier = Modifier,
    repository: BirthdayRepository = remember { BirthdayRepository() }
) {
    var day by rememberSaveable { mutableStateOf("") }
    var month by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableStateOf("") }
    var uiState by remember { mutableStateOf<BirthdayUiState>(BirthdayUiState.Idle) }
    val scope = rememberCoroutineScope()

    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                Text(
                    text = "Birthday lookup",
                    style = MaterialTheme.typography.headlineMedium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DateNumberField(
                        value = day,
                        onValueChange = { day = it.onlyDigits(2) },
                        label = "Day",
                        modifier = Modifier.weight(1f)
                    )
                    DateNumberField(
                        value = month,
                        onValueChange = { month = it.onlyDigits(2) },
                        label = "Month",
                        modifier = Modifier.weight(1f)
                    )
                    DateNumberField(
                        value = year,
                        onValueChange = { year = it.onlyDigits(4) },
                        label = "Year",
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState !is BirthdayUiState.Loading,
                    onClick = {
                        val birthDate = parseBirthDate(day, month, year)
                        when {
                            birthDate == null -> {
                                uiState = BirthdayUiState.Error("Enter a valid 4-digit birth date.")
                            }

                            birthDate.isAfter(LocalDate.now()) -> {
                                uiState = BirthdayUiState.Error("Birth date cannot be in the future.")
                            }

                            else -> {
                                scope.launch {
                                    uiState = BirthdayUiState.Loading
                                    uiState = try {
                                        BirthdayUiState.Success(
                                            repository.lookup(
                                                date = birthDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                                                day = birthDate.dayOfMonth,
                                                month = birthDate.monthValue
                                            )
                                        )
                                    } catch (error: Exception) {
                                        BirthdayUiState.Error(
                                            error.message ?: "Could not reach the birthday services."
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) {
                    if (uiState is BirthdayUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    }
                    Text(if (uiState is BirthdayUiState.Loading) "Calling APIs" else "Check birthday")
                }

                when (val state = uiState) {
                    BirthdayUiState.Idle -> Unit
                    BirthdayUiState.Loading -> Unit
                    is BirthdayUiState.Error -> Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    is BirthdayUiState.Success -> BirthdayResults(state.result)
                }
            }
        }
    }
}

@Composable
private fun DateNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
}

@Composable
private fun BirthdayResults(result: BirthdayLookupResult) {
    val age = result.age.ageExtended
    ResultCard(title = "Age") {
        Text(
            text = "${age.years} years, ${age.months} months, ${age.days} days",
            style = MaterialTheme.typography.bodyLarge
        )
    }

    ResultCard(title = "Nameday") {
        val namedays = result.nameday.data
            .filterValues { it.isNotBlank() }
            .toSortedMap()

        if (namedays.isEmpty()) {
            Text("No nameday found for this month and day.")
        } else {
            Text(
                text = namedays.entries.joinToString(separator = "\n") { (country, name) ->
                    "${country.uppercase(Locale.US)}: $name"
                }
            )
        }
    }

    ResultCard(title = "Russia day off") {
        Text("Status ${result.dayOff.code}: ${result.dayOff.description}")
        result.dayOff.isDayOff?.let { isDayOff ->
            Text(if (isDayOff) "This date was a day off." else "This date was not a day off.")
        }
    }

    ResultCard(title = "Chuck Norris joke") {
        Text(result.joke.value)
    }
}

@Composable
private fun ResultCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            content()
        }
    }
}

private sealed interface BirthdayUiState {
    data object Idle : BirthdayUiState
    data object Loading : BirthdayUiState
    data class Success(val result: BirthdayLookupResult) : BirthdayUiState
    data class Error(val message: String) : BirthdayUiState
}

private fun String.onlyDigits(maxLength: Int): String =
    filter { it.isDigit() }.take(maxLength)

private fun parseBirthDate(day: String, month: String, year: String): LocalDate? {
    if (year.length != 4) return null

    return try {
        LocalDate.of(
            year.toInt(),
            month.toInt(),
            day.toInt()
        )
    } catch (_: DateTimeException) {
        null
    } catch (_: NumberFormatException) {
        null
    }
}

@Preview(showBackground = true)
@Composable
fun BirthdayAppPreview() {
    BirthdayServiceCallTheme {
        BirthdayApp()
    }
}
