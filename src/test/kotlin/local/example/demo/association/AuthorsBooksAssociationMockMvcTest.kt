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
import local.example.demo.repository.AuthorRepository
import local.example.demo.repository.BookRepository
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
class AuthorsBooksAssociationMockMvcTest {

    val jeremy78: String = "{\"nickname\":\"jeremy78\"}"
    val hummingBirdLife2017: String = "{\"title\":\"Humming-Bird Life 2017\"}"
    val robinSecretLife2018: String = "{\"title\":\"Robin Secret Life 2018\"}"

    @Autowired
    val mockMvc: MockMvc? = null

    @Autowired val authorRepository: AuthorRepository? = null
    @Autowired val bookRepository: BookRepository? = null

    @Before
    fun `initialize`() {
        authorRepository?.deleteAll()
        bookRepository?.deleteAll()
    }

    @Test
    @Throws(Exception::class)
    fun `many-to-many association authors-books test`() {
        val authorMockMvcResult = mockMvc!!.perform(MockMvcRequestBuilders.post("/authors")
                .content(jeremy78).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated).andReturn()
        val book1MockMvcResult = mockMvc!!.perform(MockMvcRequestBuilders.post("/books")
                .content(hummingBirdLife2017).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated).andReturn()
        val book2MockMvcResult = mockMvc!!.perform(MockMvcRequestBuilders.post("/books")
                .content(robinSecretLife2018).contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated).andReturn()
        val authorResult = authorMockMvcResult.response.getHeader("Location")
        val book1Result = book1MockMvcResult.response.getHeader("Location")
        val book2Result = book2MockMvcResult.response.getHeader("Location")
        mockMvc!!.perform(MockMvcRequestBuilders.put(authorResult!! + "/books")
                .content(book1Result + "\n" + book2Result).contentType("text/uri-list"))
                .andExpect(MockMvcResultMatchers.status().isNoContent)
        mockMvc!!.perform(MockMvcRequestBuilders.get("$authorResult/books"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.books[0].title")
                        .value("Humming-Bird Life 2017"))
                .andExpect(MockMvcResultMatchers.jsonPath("$._embedded.books[1].title")
                        .value("Robin Secret Life 2018"))
    }
}
