package dev.polek.photobrowser.api

import org.junit.Test
import org.assertj.core.api.Assertions.*

class FlickrPhotoUrlTest {

    @Test
    fun `test URL for photo with original secret`() {
        val photo = FlickrPhoto(
            id = "50662768647",
            title = "IMG_5019",
            server = "65535",
            owner = "164133858@N03",
            secret = "eff68119ae",
            originalSecret = "dd40b2c8b0"
        )

        assertThat(photo.url()).isEqualTo("https://live.staticflickr.com/65535/50662768647_dd40b2c8b0_h.jpg")
    }

    @Test
    fun `test URL for photo without original secret`() {
        val photo = FlickrPhoto(
            id = "50662767652",
            title = "Office bathroom",
            server = "65535",
            owner = "34711376@N03",
            secret = "8894dabbc6",
            originalSecret = null
        )

        assertThat(photo.url()).isEqualTo("https://live.staticflickr.com/65535/50662767652_8894dabbc6_b.jpg")
    }
}
