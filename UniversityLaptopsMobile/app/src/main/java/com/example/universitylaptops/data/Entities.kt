package com.example.universitylaptops.data

import androidx.room.*

@Entity(tableName = "processor")
data class ProcessorEntity(
    @PrimaryKey val id: Long,
    val manufacturer: String,
    val speed: Double
)

@Entity(tableName = "ram")
data class RamEntity(
    @PrimaryKey val id_manufacturer: Long,
    val capacity: Int
)

@Entity(tableName = "laptop")
data class LaptopEntity(
    @PrimaryKey val id: Long,
    val brand: String,
    val price: Double,
    val screenSize: Double,
    val hasHDMI: Boolean,
    val hasUSBC: Boolean,
    val processorId: Long,
    val ramId: Long
)

@Entity(tableName = "hard_drive")
data class HardDriveEntity(
    @PrimaryKey val id: Long,
    val manufacturer: String,
    val capacity: Int,
    val laptopOwnerId: Long
)

@Entity(tableName = "student")
data class StudentEntity(
    @PrimaryKey val id: Long,
    val firstName: String,
    val lastName: String,
    val address: String
)

@Entity(
    tableName = "student_laptop",
    primaryKeys = ["studentId", "laptopId"]
)
data class StudentLaptopCrossRef(
    val studentId: Long,
    val laptopId: Long
)


data class LaptopWithRelations(
    @Embedded val laptop: LaptopEntity,
    @Relation(
        parentColumn = "processorId",
        entityColumn = "id"
    )
    val processor: ProcessorEntity,
    @Relation(
        parentColumn = "ramId",
        entityColumn = "id_manufacturer"
    )
    val ram: RamEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "laptopOwnerId"
    )
    val hardDrives: List<HardDriveEntity>
)

data class StudentWithLaptops(
    @Embedded val student: StudentEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = StudentLaptopCrossRef::class,
            parentColumn = "studentId",
            entityColumn = "laptopId"
        )
    )
    val laptops: List<LaptopEntity>
)


data class LaptopDisplayItem(
    val laptopId: Long,
    val brand: String,
    val price: Double,
    val screenSize: Double,
    val studentName: String?,
    val studentLastName: String?
)