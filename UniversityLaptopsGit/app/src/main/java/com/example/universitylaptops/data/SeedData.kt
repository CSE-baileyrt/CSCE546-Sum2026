package com.example.universitylaptops.data

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class AppDatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getInstance(context)
            val laptopDao = database.laptopDao()
            val studentDao = database.studentDao()
            SeedData.populate(database, laptopDao, studentDao)
        }
    }
}

object SeedData {
    private val brands = listOf("Dell", "HP", "Lenovo", "Acer", "Asus", "Apple", "MSI", "Samsung")
    private val processorMfr = listOf("Intel", "AMD", "Apple")
    private val driveMfr = listOf("Samsung", "Western Digital", "Seagate", "Crucial", "Kingston")
    private val firstNames = listOf(
        "Ava", "Noah", "Mia", "Liam", "Emma", "Olivia", "Sophia", "Ethan", "Lucas", "Amelia",
        "Isabella", "Mason", "Logan", "Zoe", "Elijah", "Charlotte", "James", "Harper", "Benjamin", "Evelyn"
    )
    private val lastNames = listOf(
        "Adams", "Baker", "Clark", "Davis", "Evans", "Foster", "Garcia", "Harris", "Irwin", "Jones",
        "Khan", "Lewis", "Moore", "Nguyen", "Owens", "Patel", "Quinn", "Reed", "Smith", "Turner"
    )

    suspend fun populate(db: AppDatabase, laptopDao: LaptopDao, studentDao: StudentDao) {
        val rng = Random(42)

        val processors = (1L..30L).map { id ->
            ProcessorEntity(
                id = id,
                manufacturer = processorMfr.random(rng),
                speed = listOf(1.8, 2.0, 2.4, 2.6, 3.0, 3.2, 3.6, 4.0).random(rng)
            )
        }
        processors.forEach { laptopDao.upsertProcessor(it) }

        val rams = (1L..20L).map { id ->
            RamEntity(
                id_manufacturer = id,
                capacity = listOf(8, 16, 32, 64).random(rng)
            )
        }
        rams.forEach { laptopDao.upsertRam(it) }

        val laptops = (1L..100L).map { id ->
            LaptopEntity(
                id = id,
                brand = brands.random(rng),
                price = listOf(499.99, 599.99, 699.99, 799.99, 899.99, 1099.99, 1299.99, 1499.99).random(rng),
                screenSize = listOf(13.3, 14.0, 15.6, 16.0, 17.3).random(rng),
                hasHDMI = rng.nextBoolean(),
                hasUSBC = rng.nextBoolean(),
                processorId = processors.random(rng).id,
                ramId = rams.random(rng).id_manufacturer
            )
        }
        laptops.forEach { laptopDao.upsertLaptop(it) }

        val hardDrives = buildList {
            var hdId = 1L
            laptops.forEach { laptop ->
                val driveCount = if (rng.nextBoolean()) 1 else 2
                repeat(driveCount) {
                    add(
                        HardDriveEntity(
                            id = hdId++,
                            manufacturer = driveMfr.random(rng),
                            capacity = listOf(256, 512, 1024, 2048).random(rng),
                            laptopOwnerId = laptop.id
                        )
                    )
                }
            }
        }
        laptopDao.upsertHardDrives(hardDrives)

        val students = (1L..50L).map { id ->
            StudentEntity(
                id = id,
                firstName = firstNames.random(rng),
                lastName = lastNames.random(rng),
                address = "${rng.nextInt(100, 9999)} Main St, Campus City"
            )
        }
        studentDao.upsertStudents(students)

        val crossRefs = buildList {
            students.forEach { student ->
                val laptopCount = rng.nextInt(1, 4)
                val assigned = laptops.shuffled(rng).take(laptopCount)
                assigned.forEach { laptop ->
                    add(StudentLaptopCrossRef(student.id, laptop.id))
                }
            }
        }
        studentDao.upsertCrossRefs(crossRefs)
    }
}