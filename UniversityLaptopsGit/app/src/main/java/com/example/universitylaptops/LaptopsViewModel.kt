package com.example.universitylaptops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.universitylaptops.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LaptopsViewModel(
    private val repository: LaptopRepository
) : ViewModel() {

    private val brandFilter = MutableStateFlow<String?>(null)
    private val lastNameFilter = MutableStateFlow<String?>(null)

    val brands = repository.observeBrands().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val lastNames = repository.observeLastNames().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val laptopList = combine(brandFilter, lastNameFilter) { brand, lastName ->
        brand to lastName
    }.flatMapLatest { (brand, lastName) ->
        repository.observeLaptopList(brand, lastName)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setBrandFilter(value: String?) { brandFilter.value = value }
    fun setLastNameFilter(value: String?) { lastNameFilter.value = value }

    fun observeLaptop(laptopId: Long) = repository.observeLaptop(laptopId)

    fun observeAllStudents() = repository.observeAllStudents()

    fun saveLaptop(
        laptop: LaptopEntity,
        processor: ProcessorEntity,
        ram: RamEntity,
        hardDrives: List<HardDriveEntity>,
        selectedStudentIds: List<Long>
    ) {
        viewModelScope.launch {
            repository.saveLaptop(laptop, processor, ram, hardDrives, selectedStudentIds)
        }
    }

    fun deleteLaptop(laptopId: Long) {
        viewModelScope.launch {
            repository.deleteLaptop(laptopId)
        }
    }
}