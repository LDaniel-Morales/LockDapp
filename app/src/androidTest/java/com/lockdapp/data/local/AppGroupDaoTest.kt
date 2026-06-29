package com.lockdapp.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppGroupDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: AppGroupDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = db.appGroupDao()
    }

    @After
    fun teardown() { db.close() }

    @Test
    fun upsert_generatesId_and_findById_returns_it() = runTest {
        val id = dao.upsert(AppGroupEntity(name = "Redes sociales", packages = listOf("com.instagram.android")))
        val entity = dao.findById(id)
        assertEquals("Redes sociales", entity?.name)
        assertEquals(listOf("com.instagram.android"), entity?.packages)
    }

    @Test
    fun observeAll_emits_inserted_items() = runTest {
        dao.upsert(AppGroupEntity(name = "Juegos", packages = listOf("com.game.one")))
        dao.upsert(AppGroupEntity(name = "Redes sociales", packages = listOf("com.instagram.android")))
        val list = dao.observeAll().first()
        assertEquals(2, list.size)
        // ordered by name
        assertEquals("Juegos", list[0].name)
        assertEquals("Redes sociales", list[1].name)
    }

    @Test
    fun upsert_updates_existing_row() = runTest {
        val id = dao.upsert(AppGroupEntity(name = "Grupo", packages = listOf("com.a")))
        dao.upsert(AppGroupEntity(id = id, name = "Grupo actualizado", packages = listOf("com.a", "com.b")))
        val entity = dao.findById(id)
        assertEquals("Grupo actualizado", entity?.name)
        assertEquals(listOf("com.a", "com.b"), entity?.packages)
    }

    @Test
    fun deleteById_removes_entity() = runTest {
        val id = dao.upsert(AppGroupEntity(name = "Temporal", packages = emptyList()))
        dao.deleteById(id)
        assertNull(dao.findById(id))
    }

    @Test
    fun observeAll_is_empty_initially() = runTest {
        val list = dao.observeAll().first()
        assertEquals(0, list.size)
    }

    @Test
    fun packages_list_persists_correctly_via_converter() = runTest {
        val pkgs = listOf("com.instagram.android", "com.twitter.android", "com.tiktok.android")
        val id = dao.upsert(AppGroupEntity(name = "Sociales", packages = pkgs))
        val entity = dao.findById(id)
        assertEquals(pkgs, entity?.packages)
    }
}
