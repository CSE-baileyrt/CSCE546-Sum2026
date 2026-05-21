package com.example.universitylaptops.data
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LaptopDao {
    @Transaction
    @Query("SELECT * FROM laptop ORDER BY id DESC")
    fun observeAllLaptops(): Flow<List<LaptopWithRelations>>

    @Transaction
    @Query("SELECT * FROM laptop WHERE id = :laptopId")
    fun observeLaptop(laptopId: Long): Flow<LaptopWithRelations?>

    @Transaction
    @Query("""
            SELECT l.id AS laptopId,
                   l.brand AS brand,
                   l.price AS price,
                   l.screenSize AS screenSize,
                   s.firstName AS studentName,
                   s.lastName AS studentLastName
            FROM laptop l
            LEFT JOIN student_laptop sl ON sl.laptopId = l.id
            LEFT JOIN student s ON s.id = sl.studentId
            WHERE (:brandFilter IS NULL OR :brandFilter = '' OR l.brand = :brandFilter)
              AND (:lastNameFilter IS NULL OR :lastNameFilter = '' OR s.lastName LIKE '%' || :lastNameFilter || '%')
            GROUP BY l.id
            ORDER BY l.id DESC
        """)
    fun observeLaptopList(
        brandFilter: String?,
        lastNameFilter: String?
    ): Flow<List<LaptopDisplayItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLaptop(item: LaptopEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProcessor(item: ProcessorEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRam(item: RamEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHardDrives(items: List<HardDriveEntity>)

    @Delete
    suspend fun deleteLaptop(item: LaptopEntity)

    @Query("DELETE FROM hard_drive WHERE laptopOwnerId = :laptopId")
    suspend fun deleteHardDrivesForLaptop(laptopId: Long)

    @Query("DELETE FROM student_laptop WHERE laptopId = :laptopId")
    suspend fun deleteStudentLinksForLaptop(laptopId: Long)

    @Query("SELECT DISTINCT brand FROM laptop ORDER BY brand")
    fun observeBrands(): Flow<List<String>>
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM student ORDER BY lastName, firstName")
    fun observeStudents(): Flow<List<StudentEntity>>

    @Query("SELECT DISTINCT s.lastName FROM student s ORDER BY s.lastName")
    fun observeStudentLastNames(): Flow<List<String>>

    @Transaction
    @Query("SELECT * FROM student WHERE id = :studentId")
    fun observeStudent(studentId: Long): Flow<StudentWithLaptops?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertStudents(items: List<StudentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCrossRefs(items: List<StudentLaptopCrossRef>)

    @Query("DELETE FROM student_laptop WHERE laptopId = :laptopId")
    suspend fun deleteLaptopLinks(laptopId: Long)
}