package com.example.maptracker

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.maptracker.data.local.AppDatabase
import com.example.maptracker.data.local.LocationDao
import com.example.maptracker.data.local.entity.LocationEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LocationDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var dao: LocationDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.locationDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveLocation() = runTest {
        val entity = LocationEntity(
            title = "Test Location",
            note = "Some note",
            latitude = 48.8566,
            longitude = 2.3522,
            timestamp = 1000L,
        )
        dao.insertLocation(entity)

        val locations = dao.getAllLocations().first()
        assertEquals(1, locations.size)
        assertEquals("Test Location", locations.first().title)
        assertEquals("Some note", locations.first().note)
    }

    @Test
    fun insertMultipleAndVerifyDescendingOrder() = runTest {
        dao.insertLocation(LocationEntity(title = "First", note = "", latitude = 0.0, longitude = 0.0, timestamp = 1000L))
        dao.insertLocation(LocationEntity(title = "Second", note = "", latitude = 0.0, longitude = 0.0, timestamp = 2000L))
        dao.insertLocation(LocationEntity(title = "Third", note = "", latitude = 0.0, longitude = 0.0, timestamp = 3000L))

        val locations = dao.getAllLocations().first()
        assertEquals(3, locations.size)
        assertEquals("Third", locations[0].title)
        assertEquals("First", locations[2].title)
    }

    @Test
    fun deleteLocation() = runTest {
        dao.insertLocation(LocationEntity(title = "To Delete", note = "", latitude = 0.0, longitude = 0.0, timestamp = 0L))

        val inserted = dao.getAllLocations().first().first()
        dao.deleteLocation(inserted)

        val remaining = dao.getAllLocations().first()
        assertTrue(remaining.isEmpty())
    }

    @Test
    fun getLocationById_returnsCorrectLocation() = runTest {
        dao.insertLocation(LocationEntity(title = "Alpha", note = "", latitude = 1.0, longitude = 1.0, timestamp = 0L))
        dao.insertLocation(LocationEntity(title = "Beta", note = "", latitude = 2.0, longitude = 2.0, timestamp = 0L))

        val all = dao.getAllLocations().first()
        val alphaId = all.first { it.title == "Alpha" }.id

        val found = dao.getLocationById(alphaId).first()
        assertEquals("Alpha", found?.title)
    }

    @Test
    fun getLocationById_returnsNullForUnknownId() = runTest {
        val result = dao.getLocationById(999L).first()
        assertNull(result)
    }

    @Test
    fun replaceOnConflict() = runTest {
        dao.insertLocation(LocationEntity(id = 1L, title = "Original", note = "", latitude = 0.0, longitude = 0.0, timestamp = 0L))
        dao.insertLocation(LocationEntity(id = 1L, title = "Updated", note = "", latitude = 0.0, longitude = 0.0, timestamp = 0L))

        val locations = dao.getAllLocations().first()
        assertEquals(1, locations.size)
        assertEquals("Updated", locations.first().title)
    }
}
