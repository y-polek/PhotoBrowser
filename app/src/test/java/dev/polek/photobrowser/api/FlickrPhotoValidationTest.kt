package dev.polek.photobrowser.api

import org.assertj.core.api.Assertions
import org.junit.Test

class FlickrPhotoValidationTest {

    @Test
    fun `photo is invalid if ID is null`() {
        val photo = FlickrPhoto(
            id = null,
            title = "Office bathroom",
            server = "65535",
            owner = "34711376@N03",
            secret = "8894dabbc6",
            originalSecret = "dd40b2c8b0"
        )

        Assertions.assertThat(photo.isValid).isFalse
    }

    @Test
    fun `photo is valid if TITLE is null`() {
        val photo = FlickrPhoto(
            id = "50662767652",
            title = null,
            server = "65535",
            owner = "34711376@N03",
            secret = "8894dabbc6",
            originalSecret = "dd40b2c8b0"
        )

        Assertions.assertThat(photo.isValid).isTrue
    }

    @Test
    fun `photo is invalid if SERVER is null`() {
        val photo = FlickrPhoto(
            id = "50662767652",
            title = "Office bathroom",
            server = null,
            owner = "34711376@N03",
            secret = "8894dabbc6",
            originalSecret = "dd40b2c8b0"
        )

        Assertions.assertThat(photo.isValid).isFalse
    }

    @Test
    fun `photo is invalid if OWNER is null`() {
        val photo = FlickrPhoto(
            id = "50662767652",
            title = "Office bathroom",
            server = "65535",
            owner = null,
            secret = "8894dabbc6",
            originalSecret = "dd40b2c8b0"
        )

        Assertions.assertThat(photo.isValid).isFalse
    }

    @Test
    fun `photo is invalid if SECRET and ORIGINAL_SECRET is null`() {
        val photo = FlickrPhoto(
            id = "50662767652",
            title = "Office bathroom",
            server = "65535",
            owner = "34711376@N03",
            secret = null,
            originalSecret = null
        )

        Assertions.assertThat(photo.isValid).isFalse
    }

    @Test
    fun `photo is valid if SECRET is null and ORIGINAL_SECRET is non-null`() {
        val photo = FlickrPhoto(
            id = "50662767652",
            title = "Office bathroom",
            server = "65535",
            owner = "34711376@N03",
            secret = null,
            originalSecret = "dd40b2c8b0"
        )

        Assertions.assertThat(photo.isValid).isTrue
    }

    @Test
    fun `photo is valid if ORIGINAL_SECRET is null and SECRET is non-null`() {
        val photo = FlickrPhoto(
            id = "50662767652",
            title = "Office bathroom",
            server = "65535",
            owner = "34711376@N03",
            secret = "8894dabbc6",
            originalSecret = null
        )

        Assertions.assertThat(photo.isValid).isTrue
    }
}
