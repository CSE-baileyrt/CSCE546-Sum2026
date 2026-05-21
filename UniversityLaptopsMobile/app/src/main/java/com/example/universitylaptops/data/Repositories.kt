package com.example.universitylaptops.data

import kotlinx.coroutines.flow.Flow

class LaptopRepository(
    private val laptopDao: LaptopDao,
    private val studentDao: StudentDao
) {
    fun observeLaptopList(brand: String?, lastName: String?): Flow<List<LaptopDisplayItem>> =
        laptopDao.observeLaptopList(brand, lastName)

    fun observeLaptop(laptopId: Long) = laptopDao.observeLaptop(laptopId)
    fun observeBrands() = laptopDao.observeBrands()
    fun observeLastNames() = studentDao.observeStudentLastNames()
    fun observeAllStudents() = studentDao.observeStudents()

    suspend fun saveLaptop(
        laptop: LaptopEntity,
        processor: ProcessorEntity,
        ram: RamEntity,
        hardDrives: List<HardDriveEntity>,
        selectedStudentIds: List<Long>
    ) {
        laptopDao.upsertProcessor(processor)
        laptopDao.upsertRam(ram)
        laptopDao.upsertLaptop(laptop)
        laptopDao.deleteHardDrivesForLaptop(laptop.id)
        laptopDao.upsertHardDrives(hardDrives)
        studentDao.deleteLaptopLinks(laptop.id)
        studentDao.upsertCrossRefs(selectedStudentIds.map { studentId ->
            StudentLaptopCrossRef(studentId, laptop.id)
        })
    }

    suspend fun deleteLaptop(laptopId: Long) {
        laptopDao.deleteHardDrivesForLaptop(laptopId)
        studentDao.deleteLaptopLinks(laptopId)
    }
}