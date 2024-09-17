// package de.schaumburg.schaumbooks.book;

// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.mockito.ArgumentMatchers.any;
// import static org.mockito.Mockito.when;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.MockitoAnnotations;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.http.MediaType;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// import com.fasterxml.jackson.databind.ObjectMapper;

// @WebMvcTest(BookController.class)
// public class BookControllerSecurityTest {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     BookService bookService;

//     @InjectMocks
//     private BookController bookController;

//     private Book validBook;
//     private Book updatedBook;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
//         mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();

//         validBook = new Book(1L, "Title", "Publisher", "ISBN-1234", BookStatus.AVAILABLE, null);
//         updatedBook = new Book(1L, "updated title", "updated verlag", "123-123", BookStatus.AVAILABLE, null);
//     }

//     @Test
//     // @WithMockUser(username = "admin", roles = { "ADMIN" })
//     void shouldAllowAdminToUpdateBook() throws Exception {
//         when(bookService.updateBook(any(Long.class), any(Book.class))).thenReturn(validBook);

//         mockMvc.perform(put("/books/1")
//                 .contentType("application/json")
//                 .content(asJsonString(updatedBook)).with(user("admin").roles("ADMIN")))
//                 .andExpect(status().isOk())
//                 .andExpect(jsonPath("$.title").value("Title"));
//     }

//     @Test
//     @WithMockUser(username = "user", roles = { "USER" })
//     void shouldDenyUserToUpdateBook() throws Exception {
//         mockMvc.perform(put("/books/1")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(
//                         "{\"title\": \"Updated Title\", \"publisher\": \"Updated Publisher\", \"isbn\": \"ISBN-1234\", \"status\": \"AVAILABLE\"}"))
//                 .andExpect(status().isForbidden());
//     }

//     private static String asJsonString(final Object obj) {
//         try {
//             return new ObjectMapper().writeValueAsString(obj);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }
// }
