package com.louiserennick.countrytreasurehunt

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Model class for a single treasure hunt location
data class LocationEntity(
    val id: Long,
    val name: String,
    val imageUrl: String,
    var isFound: Boolean
)

// SQLite helper class for managing the local database
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "treasure_hunt.db", null, 2) {

    override fun onCreate(db: SQLiteDatabase?) {
        // SQL query to create the locations table
        val createTableQuery = """
            CREATE TABLE locations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                image_url TEXT NOT NULL,
                is_found INTEGER NOT NULL DEFAULT 0
            );
        """
        db?.execSQL(createTableQuery)

        // Initial data for 10 locations
        val sampleLocations = listOf(
            Triple("Library", "https://picsum.photos/id/1015/400/300", false),
            Triple("Town Hall", "https://picsum.photos/id/1011/400/300", false),
            Triple("Museum", "https://picsum.photos/id/1025/400/300", false),
            Triple("Train Station", "https://picsum.photos/id/1003/400/300", false),
            Triple("Park", "https://picsum.photos/id/103/400/300", false),
            Triple("Coffee Shop", "https://picsum.photos/id/1074/400/300", false),
            Triple("Art Gallery", "https://picsum.photos/id/1080/400/300", false),
            Triple("Clock Tower", "https://picsum.photos/id/106/400/300", false),
            Triple("Statue", "https://picsum.photos/id/1062/400/300", false),
            Triple("Market", "https://picsum.photos/id/1084/400/300", false)
        )

        // Insert sample data into the table
        sampleLocations.forEach { (name, imageUrl, isFound) ->
            val values = ContentValues().apply {
                put("name", name)
                put("image_url", imageUrl)
                put("is_found", if (isFound) 1 else 0)
            }
            db?.insert("locations", null, values)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Recreate the database if version is bumped
        db?.execSQL("DROP TABLE IF EXISTS locations")
        onCreate(db)
    }

    // Get all saved locations
    fun getAllLocations(): List<LocationEntity> {
        val locations = mutableListOf<LocationEntity>()
        val db = readableDatabase
        val cursor = db.query("locations", null, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow("id"))
                val name = getString(getColumnIndexOrThrow("name"))
                val imageUrl = getString(getColumnIndexOrThrow("image_url"))
                val isFound = getInt(getColumnIndexOrThrow("is_found")) == 1
                locations.add(LocationEntity(id, name, imageUrl, isFound))
            }
        }
        cursor.close()
        return locations
    }

    // Get how many locations have been found
    fun countFoundLocations(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM locations WHERE is_found = 1", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    // Mark a location as found by its ID
    fun markAsFound(id: Long) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("is_found", 1)
        }
        db.update("locations", values, "id = ?", arrayOf(id.toString()))
    }

    // Reset all locations to 'not found'
    fun resetAllLocations() {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("is_found", 0)
        }
        db.update("locations", values, null, null)
    }
}
