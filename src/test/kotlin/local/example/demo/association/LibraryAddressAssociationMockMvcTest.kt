/**
 *
 * Copyright 2018 paolo mococci
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package local.example.demo.association

import local.example.demo.BookHandlerDemoApplication
import local.example.demo.repository.AddressRepository
import local.example.demo.repository.LibraryRepository
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [BookHandlerDemoApplication::class])
@AutoConfigureMockMvc
class LibraryAddressAssociationMockMvcTest {

    val civic55: String = "{\"name\":\"Civic 55 Library\"}"
    val address55: String = "{\"location\":\"NewCity Central Avenue 55\"}"

    @Autowired
    val mockMvc: MockMvc? = null

    @Autowired val libraryRepository: LibraryRepository? = null
    @Autowired val addressRepository: AddressRepository? = null

    @Before
    fun `initialize`() {
        libraryRepository?.deleteAll()
        addressRepository?.deleteAll()
    }

    @Test
    @Throws(Exception::class)
    fun `one-to-one association library-address test`() {
        val addressMockMvcResult = mockMvc!!.perform(MockMvcRequestBuilders.post("/addresses")
                .content(address55).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated).andReturn()
        val libraryMockMvcResult = mockMvc!!.perform(MockMvcRequestBuilders.post("/libraries")
                .content(civic55).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated).andReturn()
        val addressResult = addressMockMvcResult.response.getHeader("Location")
        val libraryResult = libraryMockMvcResult.response.getHeader("Location")
        mockMvc!!.perform(MockMvcRequestBuilders.put(libraryResult!! + "/libraryAddress")
                .content(addressResult!!).contentType("text/uri-list"))
                .andExpect(MockMvcResultMatchers.status().isNoContent)
        mockMvc!!.perform(MockMvcRequestBuilders.get("$addressResult/library"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.name")
                        .value("Civic 55 Library"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._links.books.href")
                        .value(Matchers.containsString("libraries/2/books")))
    }
}
